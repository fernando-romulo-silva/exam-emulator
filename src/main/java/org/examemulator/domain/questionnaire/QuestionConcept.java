package org.examemulator.domain.questionnaire;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "QUESTION_CONCEPT")
public class QuestionConcept {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", unique = true, nullable = false)
    private String name;

    // ------------------------------------------------------------------------------

    QuestionConcept() {
	super();
    }

    public QuestionConcept(final String name) {
	super();
	this.name = name;
    }

    // ------------------------------------------------------------------------------

    public Long getId() {
	return id;
    }

    public String getName() {
	return name;
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

	} else if (obj instanceof QuestionConcept other) {
	    result = Objects.equals(id, other.id);

	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }
}
