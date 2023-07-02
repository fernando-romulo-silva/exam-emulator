package org.examemulator.domain.questionnaire;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table( //
	name = "QUESTIONNARIE", // 
	uniqueConstraints = {  //
		@UniqueConstraint(name = "QUESTIONNARIE_UC_NAM_SETID", columnNames = { "NAME", "SET_ID" }), //
		@UniqueConstraint(name = "QUESTIONNARIE_UC_NOR_SETID", columnNames = { "NUM_ORDER", "SET_ID" }) //
	} //
)
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "NUM_ORDER")
    private Integer order;

    @ManyToOne
    @JoinColumn(name = "SET_ID", referencedColumnName = "ID")
    private QuestionnaireSet set;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "CERTIFICATION_ID", referencedColumnName = "ID", nullable = false)
    private Certification certification;

    @JoinColumn(name = "QUESTIONNAIRE_ID")
    @OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
    private final List<Question> questions = new ArrayList<>();

    Questionnaire() {
	super();
    }

    public Questionnaire( //
		    final String name, //
		    final String description, //
		    final Integer order,
		    final QuestionnaireSet set, //
		    final List<Question> questions) {
	super();
	this.name = name;
	this.description = description;
	this.set = set;
	this.order = order;
	this.certification = set.getCertification();
	this.questions.addAll(questions);
    }
    
    // ------------------------------------------
    public void update( //
		    final Integer order, //
		    final String name, //
		    final String description,
		    final QuestionnaireSet set, //
		    final List<Question> questions) {
	
	this.order = order;
	this.name = name;
	this.description = description;
	this.set = set;
	this.certification = set.getCertification();
	
	removeOptions(questions);
	
	questions.stream().forEach(this::updateQuestion);
    }
    
    private void removeOptions(final List<Question> questionsUpdated) {
	
	if (this.questions.size() > questionsUpdated.size()) { // one or more options was removed
		
	    final var currentOrders = questions.stream().map(Question::getOrder).toList();
	    
	    final var newOrders = questionsUpdated.stream().map(Question::getOrder).toList();
	    
	    final var differences = CollectionUtils.removeAll(currentOrders, newOrders);
	    
	    final var toRemove = new ArrayList<Question>();
	    
	    for (final var difference : differences) {
		
		final var optionalQuestion = this.questions.stream() //
			.filter(o -> Objects.equals(o.getOrder(), difference)) //
			.findFirst();
		
		if (optionalQuestion.isPresent()) {
		    toRemove.add(optionalQuestion.get());
		}
	    }
	    
	    if (!toRemove.isEmpty()) {
		questions.removeAll(toRemove);
	    }
	}
    }
    
    private void updateQuestion(final Question updatedQuestion) {
	
	final var optinalQuestion = this.questions.stream()
		.filter(question -> Objects.equals(question.getName(), updatedQuestion.getName()))
		.findFirst();
	
	if (optinalQuestion.isPresent()) {
	    final var currentQuestion = optinalQuestion.get();
	    currentQuestion.update(updatedQuestion);
	} else {
	    this.questions.add(updatedQuestion);
	}
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
    
    public Integer getOrder() {
	return order;
    }

    public QuestionnaireSet getSet() {
	return set;
    }

    public Certification getCertification() {
	return certification;
    }

    public List<Question> getQuestions() {
	return Collections.unmodifiableList(questions) //
			.stream() //
			.sorted((question1, question2) -> question1.getOrder().compareTo(question2.getOrder())) //
			.toList();
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

	} else if (obj instanceof final Questionnaire other) {
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
