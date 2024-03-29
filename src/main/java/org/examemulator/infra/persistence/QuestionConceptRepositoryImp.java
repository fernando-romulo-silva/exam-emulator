package org.examemulator.infra.persistence;

import java.util.Optional;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.question.QuestionConcept;
import org.examemulator.domain.questionnaire.question.QuestionConceptRepository;

import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class QuestionConceptRepositoryImp extends GenericRepository<QuestionConcept, Long> implements QuestionConceptRepository {
    
    @Override
    public Optional<QuestionConcept> findByCertificationAndName(final Certification certification, final String name) {
	
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
    
    @Override
    public Page<QuestionConcept> findAll(Pageable pageable) {
	return null;
    }
}
