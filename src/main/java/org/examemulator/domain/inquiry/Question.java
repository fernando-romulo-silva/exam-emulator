package org.examemulator.domain.inquiry;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.CollectionUtils.containsAny;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;
import static org.examemulator.util.DomainUtil.DISCRET_LIST;
import static org.examemulator.util.FileUtil.WORDS_ALL;
import static org.examemulator.util.FileUtil.WORDS_NONE;

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
import org.examemulator.domain.exam.Option;
import org.examemulator.domain.exam.QuestionType;

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
public final class Question implements InquiryInterface, Comparable<Question> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private String id;
    
    @OneToOne
    @JoinColumn(name = "PRE_QUESTION", referencedColumnName = "ID")
    private final PreQuestion preQuestion;
    
    @JoinColumn(name = "QUESTION_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Option> options = new ArrayList<>();

    @Column(name = "SHUFFLE_OPTIONS")
    private final boolean shuffleOptions;
    
    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private QuestionType type = QuestionType.UNDEFINED;

    @Column(name = "ORDER")
    private Integer order;

    @Column(name = "MARKED")
    private boolean marked;

    // ------------------------------------------------------------------------------
    private Question(final Builder builder) {
	this.id = UUID.randomUUID().toString();
	this.shuffleOptions = builder.shuffleOptions;
	this.preQuestion = builder.preQuestion;
//	this.type = builder.type;
	
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
			.map(Option::getValue) //
			.filter(answer -> containsAny(List.of(answer), words))
			.findAny();
	
	final var lastOptions = new ArrayList<Option>();
	
	if (answersOptional.isPresent()) {
	    
	    lastOptions.addAll(options.stream()
			    		.filter(option -> containsAny(List.of(option.getValue()), words))
			    		.toList());

	    options.removeIf(option -> containsAny(List.of(option.getValue()), words));
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
    
    public void selectAnswer(final String answer) {

	if (StringUtils.isEmpty(answer)) {
	    throw new IllegalArgumentException("Empty answer is not allowed!");
	}
	
	final var answers = getAnswers();

	if (answers.contains(answer)) {
	    return;
	}

	final var isValidOption = options.stream() //
			.map(Option::getLetter) //
			.noneMatch(os -> Objects.equals(os, answer));

	if (isValidOption && !Objects.equals(answer, "N/A")) {
	    final var validValues = options //
			    .stream() //
			    .map(Option::getLetter) //
			    .collect(joining(", "));

	    throw new IllegalArgumentException("Invalid answer. It can be [" + validValues + "]");
	}
	
	final var optionSelected = options.stream()
			.filter(option -> Objects.equals(option.getLetter(), answer))
			.findFirst();
	
	if (optionSelected.isPresent()) {
	    optionSelected.get().select();
	}
    }

    public void deselectAnswer(final String answer) {

	if (StringUtils.isEmpty(answer)) {
	    throw new IllegalArgumentException("Empty answer is not allowed!");
	}

	if (!getAnswers().contains(answer)) {
	    return;
	}
	
	final var optionSelected = options.stream()
			.filter(option -> Objects.equals(option.getLetter(), answer))
			.findFirst();
	
	if (optionSelected.isPresent()) {
	    optionSelected.get().unselect();
	}
    }

    public void mark(final boolean value) {
	this.marked = value;
    }

    public boolean isDiscrete() {
	return DISCRET_LIST.contains(type);
    }
   
    // ------------------------------------------------------------------------------

    public String getName() {
	return preQuestion.getName();
    }
    
    public String getValue() {
	return preQuestion.getValue();
    }

    public Integer getOrder() {
	return order;
    }
    
    public String getExplanation() {
	return preQuestion.getExplanation();
    }

    public QuestionType getType() {
	return type;
    }
    
    public void setOrder(final Integer order) {
	this.order = order;
    }

    public List<Option> getOptions() {
	return options;
    }
    
    public int getOptionsAmount() {
	return options.size();
    }

    public List<String> getCorrectOptions() {
	return options.stream() //
			.filter(Option::isCorrect) //
			.map(Option::getLetter) //
			.toList();
    }

    public List<String> getAnswers() {
	return options.stream()
			.filter(Option::isSelected)
			.map(Option::getLetter)
			.toList();
    }

    public boolean isMarked() {
	return marked;
    }

    public boolean isAnswered() {
	return !getAnswers().isEmpty();
    }

    public boolean isCorrect() {

	if (!isAnswered()) {
	    return false;
	}
	
	final var answers = getAnswers(); 

	final var correctOptions = getCorrectOptions();

	if (isEqualCollection(answers, correctOptions)) {
	    return true;
	}

	final var answersText = options.stream() //
			.map(Option::getValue) //
			.toList();

	if (CollectionUtils.containsAny(answersText, WORDS_ALL)) {

	    if (options.size() == answers.size()) {
		return true;
	    }

	    final var remainderOptions = options.stream() //
			    .filter(option -> !correctOptions.contains(option.getLetter())) //
			    .map(Option::getLetter) //
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

	} else if (obj instanceof Question other) {
	    result = Objects.equals(id, other.id);

	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public String toString() {
	final var sbToString = new StringBuilder(76);

	sbToString.append("Question [id=").append(id) //
			.append(", name=").append(getName()) //
			.append(", type=").append(type) //
			.append(", order=").append(order) //
			.append(']');

	return sbToString.toString();
    }

    @Override
    public int compareTo(final Question another) {
	return this.order.compareTo(another.order);
    }

    // ------------------------------------------------------------------------------

    public static final class Builder {

	public PreQuestion preQuestion;
	
	public boolean shuffleOptions = true;
	
	public boolean discrete = false;
	
	public Integer order;
	
//	private QuestionType type = QuestionType.UNDEFINED;
	
	private List<Option> options;
	
	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);
	    return this;
	}
	
	public Builder with(final InquiryInterface question) {
	    
	    if (question instanceof PreQuestion q) {
		return with(q);
	    } else if (question instanceof Question q) {
		return with(q);
	    }
	    
	    return this;
	}
	
	public Builder with(final Question question) {
	    this.preQuestion = question.preQuestion;
	    this.shuffleOptions = question.shuffleOptions;
	    this.options = question.getOptions().stream().map(Option::new).toList();
	    return this;
	}
	
	public Builder with(final PreQuestion question) {
	    this.preQuestion = question;
	    this.options = question.getOptions().stream().map(Option::new).toList();
	    return this;
	}	

	public Question build() {
	    
	    if (!checkParams()) {
		throw new IllegalStateException("");
	    }

	    return new Question(this);
	}

	private boolean checkParams() {
	    return nonNull(preQuestion) && CollectionUtils.isEmpty(options);
	}
    }

}
