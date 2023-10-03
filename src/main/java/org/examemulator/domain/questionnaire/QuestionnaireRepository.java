package org.examemulator.domain.questionnaire;

import java.util.Optional;
import java.util.stream.Stream;

import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface QuestionnaireRepository extends CrudRepository<Questionnaire, Long> {

    Optional<Questionnaire> findByQuestionnaireSetAndOrder(final QuestionnaireSet questionnaireSet, final Integer order);

    Stream<Questionnaire> findByQuestionnaireSet(final QuestionnaireSet questionnaireSet);

    Questionnaire update(final Questionnaire questionnaire);

}