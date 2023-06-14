package org.examemulator.domain.questionnaire.question;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.examemulator.domain.inquiry.InquiryInterface;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "QUESTION")
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
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
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
	return Collections.unmodifiableList(options);
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