package org.examemulator.service;

import java.util.List;

import org.examemulator.domain.pretest.PretestGroup;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ExamGroupService {

    @Inject
    private EntityManager entityManager;
    
    @Transactional
    public void save(final PretestGroup examGroup) {

	entityManager.persist(examGroup);
    }

    public List<PretestGroup> getAll() {
	return entityManager.createQuery("select p from ExamGroup p", PretestGroup.class).getResultList();
    }
}
