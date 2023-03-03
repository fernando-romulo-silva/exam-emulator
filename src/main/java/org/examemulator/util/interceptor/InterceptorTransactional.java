package org.examemulator.util.interceptor;

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
public class InterceptorTransactional {

    @Inject
    private EntityManager entityManager;

    @AroundInvoke
    public Object contextInterceptor(final InvocationContext context) throws Exception {

        final var transaction = entityManager.getTransaction();
	
        transaction.begin();
        
        final var object = context.proceed();
        
        transaction.commit();
        
        return object;
    }
}