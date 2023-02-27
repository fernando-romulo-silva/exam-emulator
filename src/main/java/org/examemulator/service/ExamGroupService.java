package org.examemulator.service;

import org.examemulator.domain.ExamGroup;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class ExamGroupService {
    
    @Inject
    private EntityManager entityManager;

    @Transactional(TxType.REQUIRED)
    public void save(final ExamGroup examGroup) {
	
	entityManager.persist(examGroup);
    }

}
