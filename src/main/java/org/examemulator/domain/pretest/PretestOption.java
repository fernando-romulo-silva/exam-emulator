package org.examemulator.domain.pretest;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRETEST_QUESTION")
public class PretestOption {

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
    
    
    PretestOption(){
	super();
    }
    
    PretestOption(final String letter, final String text, final Boolean correct) {
	super();
	this.letter = letter;
	this.text = text;
	this.correct = correct;
    }

    public Boolean isCorrect() {
	return correct;
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

	} else if (obj instanceof PretestOption other) {
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
