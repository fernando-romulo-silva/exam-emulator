package org.examemulator.domain.questionnaire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "QUESTIONNARIE", uniqueConstraints = { //
	@UniqueConstraint(columnNames = { "NAME", "CERTIFICATION_ID" }) //
})
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name = "SET_ID", referencedColumnName = "ID")
    private QuestionnaireSet set;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CERTIFICATION_ID", referencedColumnName = "ID", nullable = false)
    private Certification certification;

    @JoinColumn(name = "QUESTIONNAIRE_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Question> questions = new ArrayList<>();

    Questionnaire() {
	super();
    }

    public Questionnaire(final String name, final String description, final QuestionnaireSet set, final List<Question> questions) {
	super();
	this.name = name;
	this.description = description;
	this.set = set;
	this.certification = set.getCertification();
	this.questions.addAll(questions);
    }

    // ------------------------------------------

    public Long getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public QuestionnaireSet getSet() {
	return set;
    }
    
    public Certification getCertification() {
        return certification;
    }    

    public List<Question> getQuestions() {
	return Collections.unmodifiableList(questions);
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

	} else if (obj instanceof Questionnaire other) {
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
