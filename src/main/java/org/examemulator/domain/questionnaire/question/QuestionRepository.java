package org.examemulator.domain.questionnaire.question;

import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.infra.dto.QuestionDTO;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface QuestionRepository extends CrudRepository<Question, String> {

    Stream<QuestionDTO> findByCertificationAndQuestionnaireSetAndQuestionnaire(final Certification certification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire);

    Stream<QuestionDTO> findByCertificationAndQuestionnaireSet(final Certification certification, final QuestionnaireSet questionnaireSet);

    Stream<QuestionDTO> findByCertification(final Certification certification);

    Question update(final Question entity);
}