package org.examemulator.domain.cerfication;

import java.util.Optional;

import org.examemulator.util.domain.GenericRepository;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@Repository
@ApplicationScoped
public class CertificationRepository extends GenericRepository<Certification, Long> {

    public Optional<Certification> findByName(final String name) {

	final var query = entityManager.createQuery("select c from Certification c where c.name = :name", Certification.class);
	query.setParameter("name", name);

	try {
	    
	    return Optional.of(query.getSingleResult());

	} catch (final NoResultException ex) {
	    return Optional.empty();
	}
    }
}
