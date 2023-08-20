package org.examemulator.util.transaction.nonjta;

import org.slf4j.Logger;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Named
@Interceptor
@Transactional
public class TransactionInterceptor {

    @Inject
    private EntityManager entityManager;

    @Inject
    private Logger logger;

    @AroundInvoke
    public Object contextInterceptor(final InvocationContext context) throws Exception {

	Object result = null;

	final var transaction = entityManager.getTransaction();

	try {

	    transaction.begin();

	    result = context.proceed();

	    transaction.commit();

	} catch (final Exception exTransaction) {
	    try {
		if (transaction.isActive()) {
		    transaction.rollback();
		    logger.debug("Rolled back transaction");
		}
	    } catch (final Exception exRollback) {
		logger.warn("Rollback of transaction failed", exRollback);
	    }

	    throw exTransaction;

	} finally {
	    if (entityManager.isOpen()) {
		entityManager.clear();
//		entityManager.close();
	    }
	}

	return result;
    }
}