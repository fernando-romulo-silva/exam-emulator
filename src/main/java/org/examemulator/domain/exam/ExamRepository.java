package org.examemulator.domain.exam;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.util.domain.GenericRepository;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;

@Repository
@ApplicationScoped
public class ExamRepository extends GenericRepository<Exam, Long> {

    @SuppressWarnings("unchecked")
    public Stream<Exam> findExamBy(final Certification certification) {
	
	final var sqlString = """
			SELECT DISTINCT ex.*
			  FROM EXAM ex
		         INNER JOIN EXAM_QUESTION exq on exq.EXAM_QUESTION_ID = ex.ID
		         INNER JOIN QUESTION q on q.id = exq.QUESTION_ID
		         INNER JOIN QUESTIONNARIE qre on qre.id = q.QUESTIONNAIRE_ID
		         WHERE qre.CERTIFICATION_ID = ?
			""";

	final var query = entityManager.createNativeQuery(sqlString, Exam.class);
	query.setParameter(1, certification.getId());

	return query.getResultStream();
    }
    
    @SuppressWarnings("unchecked")
    public Stream<Exam> findExamBy(final Certification certification, final QuestionnaireSet questionnaireSet) {
	
	final var sqlString = """
			SELECT DISTINCT ex.*
			  FROM EXAM ex
		         INNER JOIN EXAM_QUESTION exq on exq.EXAM_QUESTION_ID = ex.ID
		         INNER JOIN QUESTION q on q.id = exq.QUESTION_ID
		         INNER JOIN QUESTIONNARIE qre on qre.id = q.QUESTIONNAIRE_ID
		         WHERE qre.CERTIFICATION_ID = ?
		           AND UPPER(ex.name) LIKE UPPER(?)
			""";

	final var query = entityManager.createNativeQuery(sqlString, Exam.class);
	query.setParameter(1, certification.getId());
	query.setParameter(2, '%' + questionnaireSet.getName() + '%');

	return query.getResultStream();
    }
    
    @SuppressWarnings("unchecked")
    public Stream<Exam> findExamBy(final Certification certification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire) {
	
	final var sqlString = """
			SELECT DISTINCT ex.*
			  FROM EXAM ex
		         INNER JOIN EXAM_QUESTION exq on exq.EXAM_QUESTION_ID = ex.ID
		         INNER JOIN QUESTION q on q.id = exq.QUESTION_ID
		         INNER JOIN QUESTIONNARIE qre on qre.id = q.QUESTIONNAIRE_ID
		         WHERE qre.CERTIFICATION_ID = ?
		           AND UPPER(ex.name) LIKE UPPER(?)
		           AND UPPER(ex.name) LIKE UPPER(?)
			""";

	final var query = entityManager.createNativeQuery(sqlString, Exam.class);
	query.setParameter(1, certification.getId());
	query.setParameter(2, '%' + questionnaireSet.getName() + '%');
	query.setParameter(3, '%' + questionnaire.getName() + '%');

	return query.getResultStream();
    }     
    
    public BigDecimal getNextExamNumberBy(final String baseName) {
	
	final var sqlString = """
                        SELECT COUNT(*) + 1
                          FROM EXAM AS e
                         WHERE UPPER(e.NAME) LIKE UPPER(?)
                           AND UPPER(e.NAME) NOT LIKE UPPER('% retry %')					
			"""; 
	
	final var query = entityManager.createNativeQuery(sqlString);
	query.setParameter(1, baseName + '%');
	
	return (BigDecimal) query.getSingleResult();
    }
    
    public BigDecimal getNextExamRetryNumberBy(final String baseName) {
	
	final var sqlString = """
                        SELECT COUNT(*) + 1
                          FROM EXAM AS e
                         WHERE UPPER(e.NAME) LIKE UPPER(?)
                           AND UPPER(e.NAME) LIKE UPPER('% retry %')					
			"""; 

	final var query = entityManager.createNativeQuery(sqlString);
	query.setParameter(1, baseName + '%');
	
	return (BigDecimal) query.getSingleResult();
    }
}
