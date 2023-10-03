package org.examemulator.domain.questionnaire;

import java.util.Optional;
import java.util.stream.Stream;

import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.infra.persistence.GenericRepository;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@Repository
@ApplicationScoped
public class QuestionnaireRepository extends GenericRepository<Questionnaire, Long> {
    
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
}
