package org.examemulator.domain.cerfication;

import java.util.Optional;

import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;

@Repository
public interface CertificationRepository extends PageableRepository<Certification, Long> {

    Optional<Certification> findByName(final String name);

}