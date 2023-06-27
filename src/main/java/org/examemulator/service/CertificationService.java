package org.examemulator.service;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.cerfication.CertificationRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CertificationService {
    
    private final CertificationRepository certificationRepository;

    @Inject
    CertificationService(final CertificationRepository certificationRepository) {
	super();
	this.certificationRepository = certificationRepository;
    }

    @Transactional(REQUIRED)
    public Certification readOrSaveCertification(final String name) {

	final var optionalCertification = certificationRepository.findByName(name);

	if (optionalCertification.isEmpty()) {
	    final var certificationTemp = new Certification(name);
	    return certificationRepository.save(certificationTemp);
	}
	
	return optionalCertification.get();
    }
    
    @Transactional(SUPPORTS)
    public Stream<Certification> findAll() {
	return certificationRepository.findAll();
    }
}
