package org.examemulator.util.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Named
@ApplicationScoped
public class EntityManagerProducer {

    @Produces
    @ApplicationScoped
    public EntityManager createEntityManager(final EntityManagerFactory entityManagerFactory) {
	return entityManagerFactory.createEntityManager();
    }

    public void close(@Disposes final EntityManager entityManager) {
	entityManager.close();
    }
}
