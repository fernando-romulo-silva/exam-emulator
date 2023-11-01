package org.examemulator.infra.persistence;

import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.question.QuestionRepository;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.infra.dto.QuestionDTO;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuestionRepositoryImp extends GenericRepository<Question, String> implements QuestionRepository {

    @Override
    @SuppressWarnings("unchecked")
    public Stream<QuestionDTO> findByCertificationAndQuestionnaireSetAndQuestionnaire(final Certification certification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire) {
	
	final var query = entityManager.createNamedQuery("QuestionByCertificationAndQuestionnaireSetAndQuestionnaire");
	query.setParameter(1, certification.getId());
	query.setParameter(2, questionnaireSet.getId());
	query.setParameter(3, questionnaire.getId());
	
	return query.getResultStream();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Stream<QuestionDTO> findByCertificationAndQuestionnaireSet(final Certification certification, final QuestionnaireSet questionnaireSet) {
	
	final var query = entityManager.createNamedQuery("QuestionByCertificationAndQuestionnaireSet");
	query.setParameter(1, certification.getId());
	query.setParameter(2, questionnaireSet.getId());
	
	return query.getResultStream();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Stream<QuestionDTO> findByCertification(final Certification certification) {
	
	final var query = entityManager.createNamedQuery("QuestionByCertification");
	query.setParameter(1, certification.getId());
	
	return query.getResultStream();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Question update(final Question entity) {
	return super.update(entity);
    }  
}
