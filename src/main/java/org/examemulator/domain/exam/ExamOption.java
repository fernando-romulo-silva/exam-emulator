package org.examemulator.domain.exam;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.examemulator.domain.questionnaire.Option;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXAM_QUESTION_OPTION")
public class ExamOption {

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "LETTER")
    private String letter;

    @OneToOne
    @JoinColumn(name = "OPTION_ID", referencedColumnName = "ID")
    private Option option;

    @Column(name = "SELECTED")
    private Boolean selected = Boolean.FALSE;

    ExamOption() {
	super();
    }

    ExamOption(final Option preOption) {
	super();
	this.id = UUID.randomUUID().toString();
	this.option = preOption;
	this.letter = preOption.getLetter();
    }

    ExamOption(final ExamOption option) {
	super();
	this.id = UUID.randomUUID().toString();
	this.option = option.getOption();
	this.letter = option.getLetter();
    }

    public void select() {
	selected = true;
    }

    public void unselect() {
	selected = false;
    }

    public Option getOption() {
	return option;
    }

    public String getValue() {
	return option.getValue();
    }

    public Boolean isCorrect() {
	return option.isCorrect();
    }

    public Boolean isSelected() {
	return selected;
    }

    void setLetter(final String letter) {
	this.letter = letter;
    }

    public String getLetter() {
	return letter;
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

	} else if (obj instanceof ExamOption other) {
	    result = Objects.equals(id, other.id);

	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this) //
			.append("id", id) //
			.append("preOption", option) //
			.append("selected", selected) //
			.toString();
    }
}
