package org.examemulator.domain.questionnaire.question;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.examemulator.domain.cerfication.Certification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "QUESTION_CONCEPT", uniqueConstraints = { @UniqueConstraint(columnNames = { "NAME", "CERTIFICATION_ID" }) })
public class QuestionConcept {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "CERTIFICATION_ID", referencedColumnName = "ID", nullable = false)
    private Certification certification;

    // ------------------------------------------------------------------------------
    QuestionConcept() {
	super();
    }

    public QuestionConcept(final String name, final Certification certification) {
	super();
	this.name = name;
	this.certification = certification;
    }

    // ------------------------------------------------------------------------------

    public Long getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public Certification getCertification() {
	return certification;
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
