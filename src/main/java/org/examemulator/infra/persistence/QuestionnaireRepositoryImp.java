package org.examemulator.infra.persistence;

import java.util.Optional;
import java.util.stream.Stream;

import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.QuestionnaireRepository;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class QuestionnaireRepositoryImp extends GenericRepository<Questionnaire, Long> implements QuestionnaireRepository {
    
    @Override
    public Optional<Questionnaire> findByQuestionnaireSetAndOrder(final QuestionnaireSet questionnaireSet, final Integer order) {
	
	final var qlString = """
				select q 
				  from Questionnaire q 
				 where q.order = :order
				   and q.set = :set
					""";
	
	final var query = entityManager.createQuery(qlString, Questionnaire.class);
	query.setParameter("order", order);
	query.setParameter("set", questionnaireSet);

	try {
	    
	    return Optional.of(query.getSingleResult());

	} catch (final NoResultException ex) {
	    return Optional.empty();
	}
    }    
    
    @Override
    public Stream<Questionnaire> findByQuestionnaireSet(final QuestionnaireSet questionnaireSet) {
	
	final var qlString = """
				select q 
				  from Questionnaire q 
				 where q.certification = :certification
				   and q.set = :set 
					""";
	
	final var query = entityManager.createQuery(qlString, Questionnaire.class);
	query.setParameter("set", questionnaireSet);
	query.setParameter("certification", questionnaireSet.getCertification());
	
	return query.getResultStream();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Questionnaire update(Questionnaire entity) {
	return super.update(entity);
    }
    
    @Override
    public Page<Questionnaire> findAll(Pageable pageable) {
	return null;
    }
}
