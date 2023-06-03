package org.examemulator.domain.pretest;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import org.examemulator.domain.base.InquiryInterface;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRE_QUESTION")
public class PreQuestion implements Comparable<PreQuestion>, InquiryInterface  {
    
    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "EXPLANATION")
    private String explanation;
    
    @Column(name = "ORDER")
    private Integer order;

    @JoinColumn(name = "PRE_QUESTION_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PreOption> options = new ArrayList<>();
    
    PreQuestion() {
	super();
    }

    private PreQuestion(final Builder builder) {
	this.id = UUID.randomUUID().toString();
	this.name = builder.name;
	this.value = builder.value;
	this.explanation = builder.explanation;
	this.order = builder.order;
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

    public List<PreOption> getOptions() {
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
			.filter(PreOption::isCorrect) //
			.map(PreOption::getLetter) //
			.toList();
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

	} else if (obj instanceof PreQuestion other) {
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
    public int compareTo(final PreQuestion another) {
	return this.order.compareTo(another.order);
    }
    
    // ------------------------------------------------------------------------------
    public static final class Builder {


	public String name;

	public String value;

	public String explanation;

	public Integer order;

	public List<PreOption> options;

	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);
	    return this;
	}

	public PreQuestion build() {

	    if (!checkParams()) {
		throw new IllegalStateException("");
	    }

	    return new PreQuestion(this);
	}

	private boolean checkParams() {
	    return nonNull(name) //
			    && nonNull(value) //
			    && nonNull(explanation) //
			    && nonNull(order);
	}
    }

}
