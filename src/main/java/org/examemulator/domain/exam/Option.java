package org.examemulator.domain.exam;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.examemulator.domain.pretest.PreOption;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "QUESTION_OPTION")
public class Option {

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "LETTER")
    private String letter;

    @OneToOne
    @JoinColumn(name = "PRE_OPTION_ID", referencedColumnName = "ID")
    private PreOption preOption;

    @Column(name = "SELECTED")
    private Boolean selected = Boolean.FALSE;

    Option() {
	super();
    }

    Option(final PreOption preOption) {
	super();
	this.id = UUID.randomUUID().toString();
	this.preOption = preOption;
	this.letter = preOption.getLetter();
    }

    Option(final Option option) {
	super();
	this.id = UUID.randomUUID().toString();
	this.preOption = option.getPreOption();
	this.letter = option.getLetter();
    }

    public void select() {
	selected = true;
    }

    public void unselect() {
	selected = false;
    }

    public PreOption getPreOption() {
	return preOption;
    }

    public String getValue() {
	return preOption.getValue();
    }

    public Boolean isCorrect() {
	return preOption.isCorrect();
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

	} else if (obj instanceof Option other) {
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
			.append("preOption", preOption) //
			.append("selected", selected) //
			.toString();
    }
}
