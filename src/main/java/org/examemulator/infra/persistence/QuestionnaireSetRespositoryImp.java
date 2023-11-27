package org.examemulator.infra.persistence;

import java.util.Optional;
import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.domain.questionnaire.set.QuestionnaireSetRespository;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class QuestionnaireSetRespositoryImp extends GenericRepository<QuestionnaireSet, Long> implements QuestionnaireSetRespository {

    @Override
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
    
    @Override
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

    @Override
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
    
    @SuppressWarnings("unchecked")
    @Override
    public QuestionnaireSet update(QuestionnaireSet entity) {
	return super.update(entity);
    } 
    
    @Override
    public Page<QuestionnaireSet> findAll(Pageable pageable) {
	return null;
    }
}
