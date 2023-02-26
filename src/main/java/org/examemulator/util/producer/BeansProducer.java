package org.examemulator.util.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class BeansProducer {

    @Produces
    public Logger producer(final InjectionPoint ip) {
	return LoggerFactory.getLogger(ip.getMember().getDeclaringClass().getName());
    }

//    @Produces
//    @ApplicationScoped
//    public EntityManagerFactory produceEntityManagerFactory() {
//
//	return Persistence.createEntityManagerFactory("defaultPU");
//    }
//
//    @Produces
//    private TransactionManager tmProducer() throws Exception {
//
//	TransactionManager transactionManager = new TransactionManagerImple();
//	return transactionManager;
//    }
//
//    @Produces
//    public UserTransaction getUserTransaction(TransactionManager tm) {
//	UserTransaction utx = new UserTransactionImple();
//	return utx;
//    }

}
