package org.examemulator.domain.cerfication;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository
public interface CertificationRepository extends CrudRepository<Certification, Long> {

    Optional<Certification> findByName(final String name);

}