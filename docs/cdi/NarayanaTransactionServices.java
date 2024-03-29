package org.examemulator.infra.transaction.jta;

import org.jboss.weld.transaction.spi.TransactionServices;

import com.arjuna.ats.jta.cdi.TransactionContext;
import com.arjuna.ats.jta.common.JTAEnvironmentBean;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;

public class NarayanaTransactionServices implements TransactionServices {
    
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery event, final BeanManager manager) {
	final var jtaEnvironmentBean = com.arjuna.ats.jta.common.jtaPropertyManager.getJTAEnvironmentBean();
	event.addContext(new TransactionContext(com.arjuna.ats.jta.TransactionManager::transactionManager, jtaEnvironmentBean::getTransactionSynchronizationRegistry));
    }
    
    /**
     * Returns the {@link UserTransaction} present in this environment by invoking the {@link com.arjuna.ats.jta.UserTransaction#userTransaction()} method and returning its result.
     *
     * <p>
     * This method never returns {@code null}.
     * </p>
     *
     * <p>
     * The return value of this method is used as the backing implementation of the <a href="http://docs.jboss.org/cdi/spec/2.0/cdi-spec.html#additional_builtin_beans">built-in {@code UserTransaction}
     * CDI bean</a>.
     * </p>
     *
     * @return the non-{@code null} {@link UserTransaction} present in this environment
     *
     * @see com.arjuna.ats.jta.UserTransaction#userTransaction()
     */
    @Override
    public final UserTransaction getUserTransaction() {
	// We don't want to use, e.g.,
	// CDI.current().select(UserTransaction.class).get() here because
	// CDI containers like Weld are obliged per the specification to
	// automatically provide a bean for UserTransaction. Weld uses
	// the return value of this method to create such a bean and we
	// obviously need to avoid the infinite loop.
	final Instance<JTAEnvironmentBean> jtaEnvironmentBeans = CDI.current().select(JTAEnvironmentBean.class);
	
	assert jtaEnvironmentBeans != null;
	
	final JTAEnvironmentBean jtaEnvironmentBean;
	
	if (jtaEnvironmentBeans.isUnsatisfied()) {
	    jtaEnvironmentBean = com.arjuna.ats.jta.common.jtaPropertyManager.getJTAEnvironmentBean();
	} else {
	    jtaEnvironmentBean = jtaEnvironmentBeans.get();
	}
	
	assert jtaEnvironmentBean != null;
	return jtaEnvironmentBean.getUserTransaction();
    }

    /**
     * Returns {@code true} if the current {@link Transaction} {@linkplain Transaction#getStatus() has a status} indicating that it is active.
     *
     * <p>
     * This method returns {@code true} if the current {@link Transaction} {@linkplain Transaction#getStatus() has a status} equal to one of the following values:
     * </p>
     *
     * <ul>
     *
     * <li>{@link Status#STATUS_ACTIVE}</li>
     *
     * <li>{@link Status#STATUS_COMMITTING}</li>
     *
     * <li>{@link Status#STATUS_MARKED_ROLLBACK}</li>
     *
     * <li>{@link Status#STATUS_PREPARED}</li>
     *
     * <li>{@link Status#STATUS_PREPARING}</li>
     *
     * <li>{@link Status#STATUS_ROLLING_BACK}</li>
     *
     * </ul>
     *
     * @return {@code true} if the current {@link Transaction} {@linkplain Transaction#getStatus() has a status} indicating that it is active; {@code false} otherwise
     *
     * @exception RuntimeException if an invocation of the {@link Transaction#getStatus()} method resulted in a {@link SystemException}
     *
     * @see Status
     */
    @Override
    public final boolean isTransactionActive() {
	final boolean returnValue;
	final Instance<Transaction> transactions = CDI.current().select(Transaction.class);
	assert transactions != null;
	if (!transactions.isUnsatisfied()) {
	    final Transaction transaction = transactions.get();
	    assert transaction != null;
	    boolean temp = false;
	    try {
		final int status = transaction.getStatus();
		temp = status == Status.STATUS_ACTIVE || status == Status.STATUS_COMMITTING || status == Status.STATUS_MARKED_ROLLBACK || status == Status.STATUS_PREPARED
				|| status == Status.STATUS_PREPARING || status == Status.STATUS_ROLLING_BACK;
	    } catch (final SystemException e) {
		throw new RuntimeException(e.getMessage(), e);
	    } finally {
		returnValue = temp;
	    }
	} else {
	    returnValue = false;
	}
	return returnValue;
    }

    /**
     * Registers the supplied {@link Synchronization} with the current {@link Transaction}.
     *
     * @exception RuntimeException if an invocation of the {@link TransactionManager#getTransaction()} method resulted in a {@link SystemException}, or if an invocation of the
     *                             {@link Transaction#registerSynchronization(Synchronization)} method resulted in either a {@link SystemException} or a {@link RollbackException}
     *
     * @see Transaction#registerSynchronization(Synchronization)
     */
    @Override
    public final void registerSynchronization(final Synchronization synchronization) {
	final CDI<Object> cdi = CDI.current();
	final Instance<Transaction> transactionInstance = cdi.select(Transaction.class);
	
	Transaction transaction = null;
	
	if (transactionInstance.isUnsatisfied()) {
	    Instance<TransactionManager> transactionManagerInstance = cdi.select(TransactionManager.class);
	    assert transactionManagerInstance != null;
	    final TransactionManager transactionManager;
	    
	    if (transactionManagerInstance.isUnsatisfied()) {
		transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
	    } else {
		transactionManager = transactionManagerInstance.get();
	    }
	    
	    if (transactionManager != null) {
		try {
		    transaction = transactionManager.getTransaction();
		} catch (final SystemException e) {
		    throw new RuntimeException(e.getMessage(), e);
		}
	    }
	    
	} else {
	    transaction = transactionInstance.get();
	}
	
	if (transaction != null) {
	    try {
		transaction.registerSynchronization(synchronization);
	    } catch (final SystemException | RollbackException e) {
		throw new RuntimeException(e.getMessage(), e);
	    }
	}
    }

    /**
     * Releases any internal resources acquired during the lifespan of this object.
     */
    @Override
    public synchronized final void cleanup() {

    }
}
