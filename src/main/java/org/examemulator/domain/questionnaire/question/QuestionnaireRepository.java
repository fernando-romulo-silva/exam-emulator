package org.examemulator.domain.questionnaire.question;

import java.util.Optional;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.util.domain.GenericRepository;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@Repository
@ApplicationScoped
public class QuestionnaireRepository extends GenericRepository<QuestionConcept, Long> {
    
    public Optional<QuestionConcept> findByNameAndCertification(final String name, final Certification certification) {
	
	final var qlString = """
				select q 
				  from QuestionConcept q 
				 where q.name = :name 
				   and q.certification = :certification
					""";
	
	final var query = entityManager.createQuery(qlString, QuestionConcept.class);
	query.setParameter("name", name);
	query.setParameter("certification", certification);

	try {
	    
	    return Optional.of(query.getSingleResult());

	} catch (final NoResultException ex) {
	    return Optional.empty();
	}
    }
}
