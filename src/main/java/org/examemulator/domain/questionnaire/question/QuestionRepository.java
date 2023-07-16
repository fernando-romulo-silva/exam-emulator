package org.examemulator.domain.questionnaire.question;

import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.util.domain.GenericRepository;
import org.examemulator.util.dto.QuestionDTO;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;

@Repository
@ApplicationScoped
public class QuestionRepository extends GenericRepository<Question, Long> {

    @SuppressWarnings("unchecked")
    public Stream<QuestionDTO> findByCertificationAndQuestionnaireSetAndQuestionnaire(final Certification certification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire) {
	
	final var query = entityManager.createNamedQuery("QuestionByCertificationAndQuestionnaireSetAndQuestionnaire");
	query.setParameter(1, certification.getId());
	query.setParameter(2, questionnaireSet.getId());
	query.setParameter(3, questionnaire.getId());
	
	return query.getResultStream();
    }
    
    @SuppressWarnings("unchecked")
    public Stream<QuestionDTO> findByCertificationAndQuestionnaireSet(final Certification certification, final QuestionnaireSet questionnaireSet) {
	
	final var query = entityManager.createNamedQuery("QuestionByCertificationAndQuestionnaireSet");
	query.setParameter(1, certification.getId());
	query.setParameter(2, questionnaireSet.getId());
	
	return query.getResultStream();
    }
    
    @SuppressWarnings("unchecked")
    public Stream<QuestionDTO> findByCertification(final Certification certification) {
	
	final var query = entityManager.createNamedQuery("QuestionByCertification");
	query.setParameter(1, certification.getId());
	
	return query.getResultStream();
    }
}
