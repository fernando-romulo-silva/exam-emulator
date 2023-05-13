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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRETEST")
public class Pretest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @OneToOne
    @JoinColumn(name = "PRETEST_GROUP_ID", referencedColumnName = "ID")
    private PretestGroup group;

    @JoinColumn(name = "PRETEST_QUESTION_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PretestQuestion> questions = new ArrayList<>();
    
    Pretest() {
	super();
    }
    
    Pretest(final String name, final PretestGroup group) {
	super();
	this.name = name;
	this.group = group;
    }

    public Long getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public List<PretestQuestion> getQuestions() {
	return Collections.unmodifiableList(questions);
    }
    
    public void addQuestion(final PretestQuestion question) {
	questions.add(question);
    }

    // -----------------------------------------------------------------------
    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    @Override
    public boolean equals(final Object obj) {

	final boolean result;

	if (this == obj) {
	    result = true;

	} else if (obj instanceof Pretest other) {
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
			.append(", name=").append(name) //
			.append(']');

	return sbToString.toString();
    }
}
