package org.examemulator.domain.questionnaire.question;

import java.util.Optional;

import org.examemulator.domain.cerfication.Certification;

import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;

@Repository
public interface QuestionConceptRepository extends PageableRepository<QuestionConcept, Long> {

    Optional<QuestionConcept> findByCertificationAndName(final Certification certification, final String name);

}