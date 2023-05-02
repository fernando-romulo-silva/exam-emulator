package org.examemulator.domain;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;
import static org.examemulator.util.FileUtil.WORDS_ALL;
import static org.examemulator.util.FileUtil.WORDS_NONE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "QUESTION")
public class Question implements Comparable<Question> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "EXPLANATION")
    private String explanation;

    @JoinColumn(name = "QUESTION_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Option> options = new ArrayList<>();

    @Column(name = "ANSWERS", nullable = false)
    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "QUESTION_ANSWER", joinColumns = @JoinColumn(name = "QUESTION_ID"))
    private final List<String> answers = new ArrayList<>();

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private QuestionType type = QuestionType.UNDEFINED;

    @Column(name = "ORDER")
    private Integer order;

    @Column(name = "MARKED")
    private boolean marked;

    // ------------------------------------------------------------------------------

    private Question(final Builder builder) {
	this.id = builder.id;
	this.name = builder.name;
	this.value = builder.value;
	this.explanation = builder.explanation;
	this.order = builder.order;
	this.options.addAll(builder.options);
    }

    // ------------------------------------------------------------------------------
    
    void shuffleOptions() {
	
	final var words = ListUtils.union(WORDS_ALL, WORDS_NONE);
	
	final var answersOptional = options.stream() //
			.map(Option::getText) //
			.filter(answer -> CollectionUtils.containsAny(List.of(answer), words))
			.findAny();
	
	final var lastOptions = new ArrayList<Option>();
	
	if (answersOptional.isPresent()) {
	    
	    lastOptions.addAll(options.stream()
			    		.filter(option -> CollectionUtils.containsAny(List.of(option.getText()), words))
			    		.toList());

	    options.removeIf(option -> CollectionUtils.containsAny(List.of(option.getText()), words));
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

    public void selectAnswer(final String answer) {

	if (StringUtils.isEmpty(answer)) {
	    throw new IllegalArgumentException("Empty answer is not allowed!");
	}

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

	answers.add(answer);
    }

    public void deselectAnswer(final String answer) {

	if (StringUtils.isEmpty(answer)) {
	    throw new IllegalArgumentException("Empty answer is not allowed!");
	}

	if (!answers.contains(answer)) {
	    return;
	}

	answers.remove(answer);
    }

    public void mark(final boolean value) {
	this.marked = value;
    }

    void defineToDiscrete(final boolean discrete) {

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

    public String getValue() {
	return value;
    }

    public String getExplanation() {
	return explanation;
    }

    public QuestionType getType() {
	return type;
    }

    public Integer getOrder() {
	return order;
    }
    
    void setOrder(final Integer order) {
	this.order = order;
    }

    public List<Option> getOptions() {
	return options;
    }

    public List<String> getCorrectOptions() {
	return options.stream() //
			.filter(Option::isCorrect) //
			.map(Option::getLetter) //
			.toList();
    }

    public List<String> getAnswers() {
	return answers;
    }

    public boolean isMarked() {
	return marked;
    }

    public boolean isAnswered() {
	return !answers.isEmpty();
    }

    public boolean isCorrect() {

	if (!isAnswered()) {
	    return false;
	}

	final var correctOptions = getCorrectOptions();

	if (isEqualCollection(answers, correctOptions)) {
	    return true;
	}

	final var answersText = options.stream() //
			.map(Option::getText) //
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

//	if (CollectionUtils.containsAny(answersText, WORDS_ABOVE)) {
//
//	    final var index = ListIterate.detectIndex(options, user -> WORDS_ABOVE.contains(user.getText()));
//
//	    final var subListAbove = options.subList(0, index).stream().map(Option::getLetter).toList();
//
//	    if (isEqualCollection(answers, subListAbove)) {
//		return true;
//	    }
//
//	    final var subListAboveInclusive = options.subList(0, index + 1) //
//			    .stream() //
//			    .map(Option::getLetter) //
//			    .toList();
//
//	    return isEqualCollection(answers, subListAboveInclusive);
//	}

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
			.append(", name=").append(name) //
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

	public Long id;

	public String name;

	public String value;

	public String explanation;

	public boolean discrete = false;

	public Integer order;

	public List<Option> options;

	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);
	    return this;
	}

	public Question build() {

	    if (!checkParams()) {
		throw new IllegalStateException("");
	    }

	    return new Question(this);
	}

	private boolean checkParams() {
	    return nonNull(name) //
			    && nonNull(value) //
			    && nonNull(explanation) //
			    && nonNull(order);
	}
    }

}
