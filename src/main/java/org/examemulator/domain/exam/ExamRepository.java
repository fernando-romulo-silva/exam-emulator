package org.examemulator.domain.exam;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface ExamRepository extends CrudRepository<Exam, Long> {

    Stream<Exam> findExamBy(final Certification certification);

    Stream<Exam> findExamBy(final Certification certification, final QuestionnaireSet questionnaireSet);

    Stream<Exam> findExamBy(final Certification certification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire);

    BigDecimal getNextExamNumberBy(final String baseName);

    BigDecimal getNextExamRetryNumberBy(final String baseName);

    int deleteExamQuestionsOptions(final Long idExam);

    int deleteExamQuestions(final Long idExam);

    int deleteExam(final Long idExam);

    Exam update(final Exam entity);

    List<Exam> findAllFinishedExamsHasQuestion(final Question question);

}