package org.examemulator.service;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(value = SUPPORTS)
public class ExamService {

    private final ExamRepository examRepository;
    
    @Inject
    ExamService(ExamRepository examRepository) {
	super();
	this.examRepository = examRepository;
    }

    @Transactional(value = REQUIRED)
    public void save(final Exam exam) {

	examRepository.save(exam);
    }

    public Stream<Exam> getAll() {
	return examRepository.findAll();
    }

    public Stream<Exam> findByCertification(final Certification selectedCertification) {
	return examRepository.findByCertification(selectedCertification);
    }
}
