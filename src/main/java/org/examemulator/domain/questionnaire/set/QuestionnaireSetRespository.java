package org.examemulator.domain.questionnaire.set;

import java.util.Optional;
import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.infra.util.domain.GenericRepository;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@Repository
@ApplicationScoped
public class QuestionnaireSetRespository extends GenericRepository<QuestionnaireSet, Long> {

    public Optional<QuestionnaireSet> findByNameAndCertification(final String name, final Certification certification) {

	final var qlString = """
			select q
			  from QuestionnaireSet q
			 where q.name = :name
			   and q.certification = :certification
				""";

	final var query = entityManager.createQuery(qlString, QuestionnaireSet.class);
	query.setParameter("name", name);
	query.setParameter("certification", certification);

	try {

	    return Optional.of(query.getSingleResult());

	} catch (final NoResultException ex) {
	    return Optional.empty();
	}
    }
    
    public Optional<QuestionnaireSet> findByCertificationAndOrder(final Certification certification, final Integer order) {

	final var qlString = """
			select q
			  from QuestionnaireSet q
			 where q.order = :order
			   and q.certification = :certification
				""";

	final var query = entityManager.createQuery(qlString, QuestionnaireSet.class);
	query.setParameter("order", order);
	query.setParameter("certification", certification);

	try {

	    return Optional.of(query.getSingleResult());

	} catch (final NoResultException ex) {
	    return Optional.empty();
	}
    }

    public Stream<QuestionnaireSet> findByCertification(final Certification certification) {

	final var qlString = """
			select q
			  from QuestionnaireSet q
			 where q.certification = :certification
				""";

	final var query = entityManager.createQuery(qlString, QuestionnaireSet.class);
	query.setParameter("certification", certification);

	return query.getResultStream();

    }
}
