package org.examemulator.domain.pretest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public PretestQuestion(final String name, final String value, final String explanation, final Integer order) {
	super();
	this.name = name;
	this.value = value;
	this.explanation = explanation;
	this.order = order;
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
}
