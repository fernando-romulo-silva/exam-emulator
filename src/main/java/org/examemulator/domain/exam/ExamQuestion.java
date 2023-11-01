package org.examemulator.domain.exam;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.CollectionUtils.containsAny;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;
import static org.apache.commons.lang3.StringUtils.chop;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.examemulator.domain.exam.ExamQuestionStatus.ANSWERED;
import static org.examemulator.domain.exam.ExamQuestionStatus.FINISHED;
import static org.examemulator.infra.util.DomainUtil.DISCRET_LIST;
import static org.examemulator.infra.util.FileUtil.WORDS_ALL;
import static org.examemulator.infra.util.FileUtil.WORDS_NONE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.examemulator.domain.InquiryInterface;
import org.examemulator.domain.questionnaire.question.Question;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXAM_QUESTION")
public final class ExamQuestion implements InquiryInterface, Comparable<ExamQuestion> {

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @OneToOne
    @JoinColumn(name = "QUESTION_ID", referencedColumnName = "ID")
    private Question question;

    @JoinColumn(name = "EXAM_QUESTION_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ExamOption> options = new ArrayList<>();

    @Column(name = "SHUFFLE_OPTIONS")
    private Boolean shuffleOptions = FALSE;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private QuestionType type = QuestionType.UNDEFINED;

    @Column(name = "NUM_ORDER")
    private Integer order;

    @Column(name = "MARKED")
    private Boolean marked = FALSE;
    
    @Column(name = "CORRECT")
    private Boolean correct = FALSE;
    
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ExamQuestionStatus status = ExamQuestionStatus.UNANSWERED;

    ExamQuestion() {
	super();
    }

    // ------------------------------------------------------------------------------
    private ExamQuestion(final Builder builder) {
	this.id = UUID.randomUUID().toString();
	this.shuffleOptions = builder.shuffleOptions;
	this.question = builder.preQuestion;
	this.options.addAll(builder.options);

	this.order = Objects.nonNull(builder.order) //
			? builder.order //
			: builder.preQuestion.getOrder(); //

	shuffleOptions(shuffleOptions);
	defineType(builder.discrete);
    }

    // ------------------------------------------------------------------------------

    private void shuffleOptions(boolean shuffleOptions) {

	if (!shuffleOptions) {
	    return;
	}

	final var words = ListUtils.union(WORDS_ALL, WORDS_NONE);

	final var answersOptional = options.stream() //
			.map(ExamOption::getValue) //
			.map(this::extractedDots)
			.filter(answer -> containsAny(List.of(answer), words)) //
			.findAny();

	final var lastOptions = new ArrayList<ExamOption>();

	if (answersOptional.isPresent()) {

	    final var list = options.stream()
			    .filter(option -> containsAny(List.of(extractedDots(option.getValue())), words))
			    .toList();
	    
	    lastOptions.addAll(list);

	    options.removeIf(option -> containsAny(List.of(extractedDots(option.getValue())), words));
	}

	Collections.shuffle(options, new Random(System.nanoTime()));

	if (answersOptional.isPresent()) {
	    options.addAll(lastOptions);
	}

	int number = 'A';

	for (final var option : options) {
	    option.setLetter(Character.toString((char) number));
	    number++;
	}
    }

    private String extractedDots(String answer) {
	return endsWith(trim(answer), ".") ? chop(trim(answer)) : answer;
    }

    private void defineType(final boolean discrete) {

	final var correctOptions = getCorrectOptions();

	if (discrete) {
	    if (correctOptions.size() == 1) {
		this.type = QuestionType.DISCRETE_SINGLE_CHOICE;
	    } else {
		this.type = QuestionType.DISCRETE_MULTIPLE_CHOICE;
	    }

	} else {
	    if (correctOptions.size() == 1) {
		this.type = QuestionType.SINGLE_CHOICE;
	    } else {
		this.type = QuestionType.MULTIPLE_CHOICE;
	    }
	}
    }

    // ------------------------------------------------------------------------------
    
    public String getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public boolean isShuffleOptions() {
        return shuffleOptions;
    }
    
    public ExamQuestionStatus getStatus() {
        return status;
    }
    
    public boolean isSameOrderQuestion() {
	return Objects.equals(order, question.getOrder());
    }

    public void selectAnswer(final String answer) {

	if (status == FINISHED) {
	    throw new IllegalStateException("You can't select an answer to finalized exam question!");
	}
	
	if (StringUtils.isEmpty(answer)) {
	    throw new IllegalArgumentException("Empty answer is not allowed!");
	}
	
	if (Objects.equals(answer, "N/A")) {
	    status = ANSWERED;
	}

	final var answers = getAnswers();

	if (answers.contains(answer)) {
	    return;
	}

	final var isValidOption = options.stream() //
			.map(ExamOption::getLetter) //
			.noneMatch(os -> Objects.equals(os, answer));

	if (isValidOption && !Objects.equals(answer, "N/A")) {
	    final var validValues = options //
			    .stream() //
			    .map(ExamOption::getLetter) //
			    .collect(joining(", "));

	    throw new IllegalArgumentException("Invalid answer. It can be [" + validValues + "]");
	}

	final var optionSelected = options.stream() //
			.filter(option -> Objects.equals(option.getLetter(), answer)) //
			.findFirst();

	if (optionSelected.isPresent()) {
	    optionSelected.get().select();
	    status = ANSWERED;
	}
    }

    public void deselectAnswer(final String answer) {

	if (status == FINISHED) {
	    throw new IllegalStateException("You can't deselect an answer to finalized exam question!");
	}	
	
	if (StringUtils.isEmpty(answer)) {
	    throw new IllegalArgumentException("Empty answer is not allowed!");
	}

	if (!getAnswers().contains(answer)) {
	    return;
	}

	final var optionSelected = options.stream().filter(option -> Objects.equals(option.getLetter(), answer)).findFirst();

	if (optionSelected.isPresent()) {
	    optionSelected.get().unselect();
	}
    }

    public void mark(final boolean value) {
	
	if (status == FINISHED) {
	    throw new IllegalStateException("You can't mark an answer to finalized exam question!");
	}	
	
	this.marked = value;
    }
    
    void finalizeExamQuestion() {
	status = FINISHED;
	correct = isCorrect();
    }
    
    void updateResult() {
	correct = isCorrect();
    }

    // ------------------------------------------------------------------------------
    
    
    public boolean isDiscrete() {
	return DISCRET_LIST.contains(type);
    }

    public String getName() {
	return question.getName();
    }

    public String getValue() {
	return question.getValue();
    }

    public Integer getOrder() {
	return order;
    }

    public String getExplanation() {
	return question.getExplanation();
    }

    public QuestionType getType() {
	return type;
    }

    public void setOrder(final Integer order) {
	this.order = order;
    }

    public List<ExamOption> getOptions() {
	return options.stream() //
			.sorted((o1, o2) -> o1.getLetter().compareTo(o2.getLetter())) //
			.toList();
    }

    @Override
    public int getOptionsAmount() {
	return options.size();
    }

    public List<String> getCorrectOptions() {
	return options.stream() //
			.filter(ExamOption::isCorrect) //
			.map(ExamOption::getLetter) //
			.sorted(Comparable::compareTo)
			.toList();
    }

    public List<String> getAnswers() {
	return options.stream() //
			.filter(ExamOption::isSelected) //
			.map(ExamOption::getLetter) //
			.sorted(Comparable::compareTo)
			.toList();
    }

    public boolean isMarked() {
	return marked;
    }

    public boolean isAnswered() {
	return status == ANSWERED || status == FINISHED;
    }

    public boolean isCorrect() {
	
	if (!question.isActive()) {
	    return true;
	}
	
	if (Objects.equals(FALSE, isAnswered())) {
	    return false;
	}

	if (Objects.equals(TRUE, isAnswered()) && getAnswers().isEmpty()) {
	    return false;
	}

	final var answers = getAnswers();

	final var correctOptions = getCorrectOptions();

	if (isEqualCollection(answers, correctOptions)) {
	    return true;
	}

	final var answersText = options.stream() //
			.map(ExamOption::getValue) //
			.map(this::extractedDots) //
			.toList();

	if (CollectionUtils.containsAny(answersText, WORDS_ALL)) {

	    if (options.size() == answers.size()) {
		return true;
	    }

	    final var remainderOptions = options.stream() //
			    .filter(option -> !correctOptions.contains(option.getLetter())) //
			    .map(ExamOption::getLetter) //
			    .toList();

	    return isEqualCollection(answers, remainderOptions);
	    
	} 

	return false;
    }

    // ------------------------------------------------------------------------------

    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    @Override
    public boolean equals(final Object obj) {

	final boolean result;

	if (this == obj) {
	    result = true;

	} else if (obj instanceof ExamQuestion other) {
	    result = Objects.equals(id, other.id);

	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this) //
			.append("id", id) //
			.append("name", getName()) //
			.append("type", type) //
			.append("order", order) //
			.toString();
    }

    @Override
    public int compareTo(final ExamQuestion another) {
	return this.order.compareTo(another.order);
    }

    // ------------------------------------------------------------------------------

    static final class Builder {

	public Question preQuestion;

	public boolean shuffleOptions = true;

	public boolean discrete = false;

	public Integer order;

	private List<ExamOption> options;

	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);
	    return this;
	}

	public Builder with(final InquiryInterface inquiry) {

	    if (inquiry instanceof Question preQuestion) {
		return with(preQuestion);
	    } else if (inquiry instanceof ExamQuestion question) {
		return with(question);
	    }

	    return this;
	}

	public Builder with(final ExamQuestion question) {
	    this.preQuestion = question.question;
	    this.shuffleOptions = question.shuffleOptions;
	    this.options = question.getOptions().stream().map(ExamOption::new).toList();
	    return this;
	}

	public Builder with(final Question question) {
	    this.preQuestion = question;
	    this.options = question.getOptions().stream().map(ExamOption::new).toList();
	    return this;
	}

	public ExamQuestion build() {

	    if (!checkParams()) {
		throw new IllegalStateException("");
	    }

	    return new ExamQuestion(this);
	}

	private boolean checkParams() {
	    return nonNull(preQuestion) && CollectionUtils.isNotEmpty(options);
	}
    }
}
