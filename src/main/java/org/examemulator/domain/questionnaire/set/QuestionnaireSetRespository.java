package org.examemulator.domain.questionnaire.set;

import java.util.Optional;
import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;

import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;

@Repository
public interface QuestionnaireSetRespository extends PageableRepository<QuestionnaireSet, Long> {

    Optional<QuestionnaireSet> findByNameAndCertification(final String name, final Certification certification);

    Optional<QuestionnaireSet> findByCertificationAndOrder(final Certification certification, final Integer order);

    Stream<QuestionnaireSet> findByCertification(final Certification certification);
    
    QuestionnaireSet update(QuestionnaireSet entity);

}