package org.examemulator.domain.pretest;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRETEST_QUESTION")
public class PretestQuestion implements Comparable<PretestQuestion>  {
    
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
    
    @Column(name = "ORDER")
    private Integer order;

    @JoinColumn(name = "QUESTION_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PretestOption> options = new ArrayList<>();
    
    
    PretestQuestion() {
	super();
    }

    private PretestQuestion(final Builder builder) {
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

    public String getExplanation() {
	return explanation;
    }

    public List<PretestOption> getOptions() {
	return Collections.unmodifiableList(options);
    }

    public List<String> getCorrectOptions() {
	return options.stream() //
			.filter(PretestOption::isCorrect) //
			.map(PretestOption::getLetter) //
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

	} else if (obj instanceof PretestQuestion other) {
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
    public int compareTo(final PretestQuestion another) {
	return this.order.compareTo(another.order);
    }
    
    // ------------------------------------------------------------------------------
    public static final class Builder {


	public String name;

	public String value;

	public String explanation;

	public Integer order;

	public List<PretestOption> options;

	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);
	    return this;
	}

	public PretestQuestion build() {

	    if (!checkParams()) {
		throw new IllegalStateException("");
	    }

	    return new PretestQuestion(this);
	}

	private boolean checkParams() {
	    return nonNull(name) //
			    && nonNull(value) //
			    && nonNull(explanation) //
			    && nonNull(order);
	}
    }

}
