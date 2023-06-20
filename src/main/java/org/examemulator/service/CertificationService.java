package org.examemulator.service;

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

    @Transactional
    public Certification readOrSaveCertification(final FolderStruc data) {

	final var optionalCertification = certificationRepository.findByName(data.certificationName());

	if (optionalCertification.isEmpty()) {
	    final var certificationTemp = new Certification(data.certificationName());
	    return certificationRepository.save(certificationTemp);
	}
	
	return optionalCertification.get();
    }
    
    @Transactional(value = SUPPORTS)
    public Stream<Certification> findAll() {
	return certificationRepository.findAll();
    }
}
