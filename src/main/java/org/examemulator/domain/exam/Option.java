package org.examemulator.domain.exam;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXAM_OPTION")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    
    @Column(name = "LETTER")
    private String letter;

    @Column(name = "TEXT")
    private String text;

    @Column(name = "CORRECT")
    private Boolean correct = Boolean.FALSE;
    
    @Column(name = "SELECTED")
    private Boolean selected = Boolean.FALSE;

    Option() {
	super();
    }
    
    public Option(final Long id, final String letter, final String text, final boolean correct) {
	this(letter, text, correct);
	this.id = id;
    }

    public Option(final String letter, final String text, final boolean correct) {
	super();
	this.text = text;
	this.letter = letter;
	this.correct = correct; 
    }
    
    public void select() {
	selected = true;
    }
    
    public void unselect() {
	selected = false;
    }

    public String getText() {
	return text;
    }

    public Boolean isCorrect() {
	return correct;
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
	final var sbToString = new StringBuilder(76);

	sbToString.append("Option [id=").append(id) //
			.append(", correct=").append(correct) //
			.append(", text=").append(text) //
			.append(']');

	return sbToString.toString();
    }
}
