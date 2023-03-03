package org.examemulator.service;

import java.util.List;

import org.examemulator.domain.ExamGroup;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ExamGroupService {

    @Inject
    private EntityManager entityManager;

    @Transactional
    public void save(final ExamGroup examGroup) {

	entityManager.persist(examGroup);
    }

    public List<ExamGroup> getAll() {

	return entityManager.createQuery("select p from ExamGroup p", ExamGroup.class).getResultList();

    }

}
