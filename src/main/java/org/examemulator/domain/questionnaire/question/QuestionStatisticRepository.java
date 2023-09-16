package org.examemulator.domain.questionnaire.question;

import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.util.domain.GenericRepository;
import org.examemulator.util.dto.QuestionStatisticDTO;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;

@Repository
@ApplicationScoped
public class QuestionStatisticRepository extends GenericRepository<Question, String> {

    @SuppressWarnings("unchecked")
    public Stream<QuestionStatisticDTO> findByCertificationAndQuestionnaireSetAndQuestionnaire(final Certification certification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire) {
	
	final var query = entityManager.createNamedQuery("QuestionStatisticByCertificationAndQuestionnaireSetAndQuestionnaire");
	query.setParameter(1, certification.getId());
	query.setParameter(2, questionnaireSet.getId());
	query.setParameter(3, questionnaire.getId());
	
	return query.getResultStream();
    }
    
    @SuppressWarnings("unchecked")
    public Stream<QuestionStatisticDTO> findByCertificationAndQuestionnaireSet(final Certification certification, final QuestionnaireSet questionnaireSet) {
	
	final var query = entityManager.createNamedQuery("QuestionStatisticByCertificationAndQuestionnaireSet");
	query.setParameter(1, certification.getId());
	query.setParameter(2, questionnaireSet.getId());
	
	return query.getResultStream();
    }
    
    @SuppressWarnings("unchecked")
    public Stream<QuestionStatisticDTO> findByCertification(final Certification certification) {
	
	final var query = entityManager.createNamedQuery("QuestionStatisticByCertification");
	query.setParameter(1, certification.getId());
	
	return query.getResultStream();
    } 
}
