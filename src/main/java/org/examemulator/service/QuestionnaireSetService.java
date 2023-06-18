package org.examemulator.service;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;

import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.domain.questionnaire.set.QuestionnaireSetRespository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(value = SUPPORTS)
public class QuestionnaireSetService {

    private final QuestionnaireSetRespository repository;

    @Inject
    QuestionnaireSetService(final QuestionnaireSetRespository repository) {
	super();
	this.repository = repository;
    }
    
    public Stream<QuestionnaireSet> findByCertification(final Certification certification) {
	return repository.findByCertification(certification);
    }
    
    public Stream<QuestionnaireSet> findAll() {
	return repository.findAll();
    }
}
