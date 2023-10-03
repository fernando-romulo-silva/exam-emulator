package org.examemulator.domain.exam;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface ExamRepository extends CrudRepository<Exam, Long>{

    Stream<Exam> findExamBy(Certification certification);

    Stream<Exam> findExamBy(Certification certification, QuestionnaireSet questionnaireSet);

    Stream<Exam> findExamBy(Certification certification, QuestionnaireSet questionnaireSet, Questionnaire questionnaire);

    BigDecimal getNextExamNumberBy(String baseName);

    BigDecimal getNextExamRetryNumberBy(String baseName);

    int deleteExamQuestionsOptions(Long idExam);

    int deleteExamQuestions(Long idExam);

    int deleteExam(Long idExam);

}