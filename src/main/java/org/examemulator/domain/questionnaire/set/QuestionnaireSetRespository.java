package org.examemulator.domain.questionnaire.set;

import java.util.Optional;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.util.domain.GenericRepository;

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
}
