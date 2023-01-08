package org.examemulator.domain;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

public class Question implements Comparable<Question> {

    private final String id;

    private final String value;

    private final String explanation;

    private final List<Option> options = new ArrayList<>();

    private final List<String> correctOptions = new ArrayList<>();

    private final List<String> answers = new ArrayList<>();

    private final Integer order;

    private QuestionType type = QuestionType.UNDEFINED;

    private boolean marked;

    private boolean answered;

    // ------------------------------------------------------------------------------
    private Question(final Builder builder) {
	this.id = builder.id;
	this.value = builder.value;
	this.explanation = builder.explanation;
	this.order = builder.order;
	this.options.addAll(builder.options);
	this.correctOptions.addAll(builder.correctOptions);
    }

    // ------------------------------------------------------------------------------

    public void selectAnswer(final String answer) {

	if (StringUtils.isEmpty(answer)) {
	    throw new IllegalArgumentException("Empty answer is not allowed!");
	}

	if (answers.contains(answer)) {
	    return;
	}

	final var isValidOption = options.stream() //
			.map(o -> o.getId()) //
			.noneMatch(os -> Objects.equals(os, answer));

	if (isValidOption && !Objects.equals(answer, "N/A")) {
	    final var validValues = options //
			    .stream() //
			    .map(o -> o.getId()) //
			    .collect(joining(", "));

	    throw new IllegalArgumentException("Invalid answer. It can be [" + validValues + "]");
	}

	answers.add(answer);

	answered = !answers.isEmpty();
    }

    public void deselectAnswer(final String answer) {

	if (StringUtils.isEmpty(answer)) {
	    throw new IllegalArgumentException("Empty answer is not allowed!");
	}

	if (!answers.contains(answer)) {
	    return;
	}

	answers.remove(answer);

	answered = !answers.isEmpty();
    }

    public void mark(final boolean value) {
	this.marked = value;
    }

    void defineToDiscrete(final boolean discrete) {
	if (discrete) {
	    if (correctOptions.size() == 1) {
		this.type = QuestionType.DOSC;
	    } else {
		this.type = QuestionType.DOMC;
	    }

	} else {
	    if (correctOptions.size() == 1) {
		this.type = QuestionType.SC;
	    } else {
		this.type = QuestionType.MC;
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

    public List<Option> getOptions() {
	return options;
    }

    public List<String> getCorrectOptions() {
	return correctOptions;
    }

    public List<String> getAnswers() {
	return answers;
    }

    public boolean isMarked() {
	return marked;
    }

    public boolean isAnswered() {
	return answered;
    }

    public boolean isCorrect() {

	if (!answered) {
	    return false;
	}

	if (answered) {
	    return answers.containsAll(correctOptions);
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

	public String id;

	public String value;

	public String explanation;

	public boolean discrete = false;

	public Integer order;

	public List<Option> options;

	public List<String> correctOptions;

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
	    return nonNull(id) //
			    && nonNull(value) //
			    && nonNull(explanation) //
			    && nonNull(order);
	}
    }

}
