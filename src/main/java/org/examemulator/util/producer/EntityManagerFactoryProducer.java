package org.examemulator.util.producer;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class EntityManagerFactoryProducer {

    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    public void postConstruct() {
        entityManagerFactory = Persistence.createEntityManagerFactory("defaultPU");
    }

    @Produces
    @ApplicationScoped
    public EntityManagerFactory createEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void close(@Disposes final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }
}
