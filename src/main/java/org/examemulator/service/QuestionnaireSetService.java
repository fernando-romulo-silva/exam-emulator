package org.examemulator.service;

import java.util.stream.Stream;

import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.domain.questionnaire.set.QuestionnaireSetRespository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuestionnaireSetService {

    private final QuestionnaireSetRespository repository;

    @Inject
    QuestionnaireSetService(final QuestionnaireSetRespository repository) {
	super();
	this.repository = repository;
    }
    
    public Stream<QuestionnaireSet> getAll() {
	return repository.findAll();
    }
}
