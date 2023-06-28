package org.examemulator.domain.questionnaire.question;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "OPTION", uniqueConstraints = @UniqueConstraint(columnNames = { "LETTER", "QUESTION_ID" }))
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "LETTER")
    private String letter;

    @Lob
    @Column(name = "TEXT")
    private String value;

    @Column(name = "CORRECT")
    private Boolean correct = Boolean.FALSE;

    Option() {
	super();
    }

    public Option(final String letter, final String value, final Boolean correct) {
	super();
	this.letter = letter;
	this.value = value;
	this.correct = correct;
    }

    void update(final Option updatedOption) {
	this.value = updatedOption.value;
	this.correct = updatedOption.correct;
    }

    public Boolean isCorrect() {
	return correct;
    }

    public String getLetter() {
	return letter;
    }

    public String getValue() {
	return value;
    }

    // ----------------------------------------------------------

    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    @Override
    public boolean equals(final Object obj) {

	final boolean result;

	if (this == obj) {
	    result = true;

	} else if (obj instanceof Option other) {
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
