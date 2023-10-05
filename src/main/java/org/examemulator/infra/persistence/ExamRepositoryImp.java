package org.examemulator.infra.persistence;

import static org.apache.commons.lang3.StringUtils.replace;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamRepository;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExamRepositoryImp extends GenericRepository<Exam, Long> implements ExamRepository {

    @Override
    @SuppressWarnings("unchecked")
    public Stream<Exam> findExamBy(final Certification certification) {
	
	final var certificationName = certification.getName();
	final var selectionNameSize = Integer.toString(certificationName.length() + 4);
	
	final var sqlString = """
			SELECT DISTINCT ex.*
			  FROM EXAM ex
		         INNER JOIN EXAM_QUESTION exq on exq.EXAM_ID = ex.ID
		         INNER JOIN QUESTION q on q.id = exq.QUESTION_ID
		         INNER JOIN QUESTIONNARIE qre on qre.id = q.QUESTIONNAIRE_ID
		         WHERE qre.CERTIFICATION_ID = ?
		         ORDER BY CASE SUBSTRING(UPPER(ex.name), $1, 7) 
			             WHEN 'DYNAMIC' THEN 0
        		             ELSE 1
    		                  END ASC,
		               ex.name		         
			""";

	final var query = entityManager.createNativeQuery(replace(sqlString, "$1", selectionNameSize), Exam.class);
	query.setParameter(1, certification.getId());
	return query.getResultStream();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Stream<Exam> findExamBy(final Certification certification, final QuestionnaireSet questionnaireSet) {
	
	final var questionnaireSetName = questionnaireSet.getName();
	final var selectionNameSize = Integer.toString(questionnaireSetName.length() + 4);	
	
	final var sqlString = """
			SELECT DISTINCT ex.*
			  FROM EXAM ex
		         INNER JOIN EXAM_QUESTION exq on exq.EXAM_ID = ex.ID
		         INNER JOIN QUESTION q on q.id = exq.QUESTION_ID
		         INNER JOIN QUESTIONNARIE qre on qre.id = q.QUESTIONNAIRE_ID
		         WHERE qre.CERTIFICATION_ID = ?
		           AND UPPER(ex.name) LIKE UPPER(?)
		         ORDER BY CASE SUBSTRING(UPPER(ex.name), $1, 7) 
			             WHEN 'DYNAMIC' THEN 0
        		             ELSE 1
    		                  END ASC,
		               ex.name
			""";

	final var query = entityManager.createNativeQuery(replace(sqlString, "$1", selectionNameSize), Exam.class);
	query.setParameter(1, certification.getId());
	query.setParameter(2, '%' + questionnaireSet.getName() + '%');

	return query.getResultStream();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Stream<Exam> findExamBy(final Certification certification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire) {

	final var questionnaireSetName = questionnaireSet.getName();
	final var questionnaireName = questionnaire.getName();
	final var selectionNameSize = Integer.toString(questionnaireSetName.length() + 4 + questionnaireName.length() + 4);
	
	final var sqlString = """
			SELECT DISTINCT ex.*
			  FROM EXAM ex
		         INNER JOIN EXAM_QUESTION exq on exq.EXAM_ID = ex.ID
		         INNER JOIN QUESTION q on q.id = exq.QUESTION_ID
		         INNER JOIN QUESTIONNARIE qre on qre.id = q.QUESTIONNAIRE_ID
		         WHERE qre.CERTIFICATION_ID = ?
		           AND UPPER(ex.name) LIKE UPPER(?)
		           AND UPPER(ex.name) LIKE UPPER(?)
		         ORDER BY CASE SUBSTRING(UPPER(ex.name), $1, 7) 
			             WHEN 'DYNAMIC' THEN 0
        		             ELSE 1
    		                  END ASC,
		               ex.name
			""";

	final var query = entityManager.createNativeQuery(replace(sqlString, "$1", selectionNameSize), Exam.class);
	query.setParameter(1, certification.getId());
	query.setParameter(2, '%' + questionnaireSet.getName() + '%');
	query.setParameter(3, '%' + questionnaire.getName() + '%');

	return query.getResultStream();
    }     
    
    @Override
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
    
    @Override
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
    
    @Override
    public int deleteExamQuestionsOptions(final Long idExam) {
	final var dmlString = """
                                  DELETE 
                                    FROM EXAM_QUESTION_OPTION AS eqo
                                   WHERE EXISTS (
                                  		  SELECT 1
                                  		    FROM EXAM_QUESTION AS eq
                                  		   WHERE EQ.ID = eqo.EXAM_QUESTION_ID
                                  		     AND eq.EXAM_ID = ?
                                                )					
			""";
	
	final var dml = entityManager.createNativeQuery(dmlString);
	dml.setParameter(1, idExam);
	
	return dml.executeUpdate();
    }
    
    @Override
    public int deleteExamQuestions(final Long idExam) {
	final var dmlString = """
                                  DELETE 
                                    FROM EXAM_QUESTION AS eq
                                   WHERE eq.EXAM_ID = ?				
			""";
	
	final var dml = entityManager.createNativeQuery(dmlString);
	dml.setParameter(1, idExam);
	
	return dml.executeUpdate();
    }
    
    @Override
    public int deleteExam(final Long idExam) {
	final var dmlString = """
                                  DELETE 
                                    FROM EXAM AS e
                                   WHERE E.ID = ?				
			""";
	
	final var dml = entityManager.createNativeQuery(dmlString);
	dml.setParameter(1, idExam);
	
	return dml.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Exam update(Exam entity) {
	return super.update(entity);
    }    
}
