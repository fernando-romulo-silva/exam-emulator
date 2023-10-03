package org.examemulator.infra.persistence;

import java.util.Optional;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.cerfication.CertificationRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class CertificationRepositoryImp extends GenericRepository<Certification, Long> implements CertificationRepository {

    @Override
    public Optional<Certification> findByName(final String name) {

	final var qlString = """
				select c 
				  from Certification c 
				 where c.name = :name
					""";
	
	final var query = entityManager.createQuery(qlString, Certification.class);
	query.setParameter("name", name);

	try {
	    
	    return Optional.of(query.getSingleResult());

	} catch (final NoResultException ex) {
	    return Optional.empty();
	}
    }
}
