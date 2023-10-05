package org.examemulator.domain.questionnaire.question;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.examemulator.domain.InquiryInterface;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "QUESTION", uniqueConstraints = { @UniqueConstraint(name = "QUESTION_UC_NUOR_QUID", columnNames = { "NUM_ORDER", "QUESTIONNAIRE_ID" }) })
public class Question implements Comparable<Question>, InquiryInterface {

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "NAME")
    private String name;

    @Lob
    @Column(name = "VALUE")
    private String value;

    @Lob
    @Column(name = "EXPLANATION")
    private String explanation;

    @ManyToOne
    @JoinColumn(name = "CONCEPT_ID", referencedColumnName = "ID")
    private QuestionConcept concept;

    @Column(name = "NUM_ORDER")
    private Integer order;

    @JoinColumn(name = "QUESTION_ID")
    @OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
    private final List<Option> options = new ArrayList<>();

    Question() {
	super();
    }

    private Question(final Builder builder) {
	this.id = UUID.randomUUID().toString();
	this.name = builder.name;
	this.value = builder.value;
	this.explanation = builder.explanation;
	this.order = builder.order;
	this.concept = builder.concept;
	this.options.addAll(builder.options);
    }

    // ------------------------------------------------------------------------------
    public void update(final Question updatedQuestion) {

	this.name = updatedQuestion.name;
	this.value = updatedQuestion.value;
	this.explanation = updatedQuestion.explanation;
	this.order = updatedQuestion.order;
	this.concept = updatedQuestion.concept;

	removeOptions(updatedQuestion.options);

	updatedQuestion.options.stream().forEach(this::updateOption);

    }

    private void removeOptions(final List<Option> optionsUpdated) {

	if (this.options.size() > optionsUpdated.size()) { // one or more options was removed

	    final var currentLetters = options.stream().map(Option::getLetter).toList();

	    final var newLetters = optionsUpdated.stream().map(Option::getLetter).toList();

	    final var differences = CollectionUtils.removeAll(currentLetters, newLetters);

	    final var toRemove = new ArrayList<Option>();

	    for (final var didfference : differences) {

		final var optionalOption = this.options.stream() //
				.filter(o -> Objects.equals(o.getLetter(), didfference)) //
				.findFirst();

		if (optionalOption.isPresent()) {
		    toRemove.add(optionalOption.get());
		}
	    }

	    if (!toRemove.isEmpty()) {
		options.removeAll(toRemove);
	    }
	}
    }

    private void updateOption(final Option updatedOption) {

	final var optinalOption = this.options.stream() //
			.filter(option -> Objects.equals(option.getLetter(), updatedOption.getLetter())) //
			.findFirst();

	if (optinalOption.isPresent()) {
	    final var currentQuestion = optinalOption.get();
	    currentQuestion.update(updatedOption);
	} else {
	    this.options.add(updatedOption);
	}
    }

    // ------------------------------------------------------------------------------

    public String getValue() {
	return value;
    }

    public Integer getOrder() {
	return order;
    }

    public String getExplanation() {
	return explanation;
    }

    public List<Option> getOptions() {
	return unmodifiableList(options)
			.stream()
			.sorted((o1, o2) -> o1.getLetter().compareTo(o2.getLetter()))
			.toList();
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public List<String> getCorrectOptions() {
	return options.stream() //
			.filter(Option::isCorrect) //
			.map(Option::getLetter) //
			.sorted() //
			.toList();
    }

    @Override
    public int getOptionsAmount() {
	return options.size();
    }

    public Optional<QuestionConcept> getConcept() {
	return ofNullable(concept);
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

	public String name;

	public String value;

	public String explanation;

	public Integer order;

	public QuestionConcept concept;

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
