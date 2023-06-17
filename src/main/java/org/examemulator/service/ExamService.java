package org.examemulator.service;

import java.util.stream.Stream;

import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ExamService {

    private final ExamRepository examRepository;
    
    @Inject
    ExamService(ExamRepository examRepository) {
	super();
	this.examRepository = examRepository;
    }

    @Transactional
    public void save(final Exam exam) {

	examRepository.save(exam);
    }

    public Stream<Exam> getAll() {
	return examRepository.findAll();
    }
}
