package org.examemulator.service;

import java.util.List;

import org.examemulator.domain.exam.Exam;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ExamService {

    @Inject
    private EntityManager entityManager;
    
    @Transactional
    public void save(final Exam exam) {

	entityManager.persist(exam);
    }

    public List<Exam> getAll() {
	return entityManager.createQuery("select p from Exam p", Exam.class).getResultList();
    }
    
}
