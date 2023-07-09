package org.examemulator.service;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

import java.util.Optional;
import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.domain.questionnaire.set.QuestionnaireSetRespository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(SUPPORTS)
public class QuestionnaireSetService {

    private final QuestionnaireSetRespository repository;

    @Inject
    QuestionnaireSetService(final QuestionnaireSetRespository repository) {
	super();
	this.repository = repository;
    }
    
    public record QuestionnaireSetDTO(Integer order, String name, String description) {}

    @Transactional(REQUIRED)
    public QuestionnaireSet readOrSaveQuestionnaireSet(final QuestionnaireSetDTO data, final Certification certification) {

	final var optionalQuestionnaireSet = repository.findByCertificationAndOrder(certification, data.order());

	if (optionalQuestionnaireSet.isEmpty()) {
	    final var questionnaireSetTemp = new QuestionnaireSet(data.name(), data.description(), data.order(), certification);
	    return repository.save(questionnaireSetTemp);
	}

	final var questionnaireSet = optionalQuestionnaireSet.get();
	questionnaireSet.update(data.name(), data.description(), data.order());
	return repository.update(questionnaireSet);
    }
    
    public Stream<QuestionnaireSet> findByCertification(final Certification certification) {
	return repository.findByCertification(certification);
    }
    
    public Stream<QuestionnaireSet> findAll() {
	return repository.findAll();
    }
    
    public Optional<QuestionnaireSet> findByCertificationAndOrder(final Certification certification, final Integer order) {
	return repository.findByCertificationAndOrder(certification, order);
    }
}
