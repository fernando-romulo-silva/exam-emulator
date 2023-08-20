package org.examemulator.util.producer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class BeansProducer {
    
    private InitialContext initContext;

    @Produces
    public Logger logProducer(final InjectionPoint ip) {
	return LoggerFactory.getLogger(ip.getMember().getDeclaringClass().getName());
    }

    @Produces
    public DataSource dataSourcerProducer() throws NamingException {
	 
	final var envContext = (Context) initContext.lookup("java:/comp/env");
	 
	 final var dataSource = (DataSource) envContext.lookup("HikariDataSource");
	 
	 return dataSource;
	
//	final var config = new HikariConfig();
//
//	config.setJdbcUrl("jdbc:hsqldb:hsql://localhost:9137/examEmulator");
//	config.setUsername("sa");
//	config.setPassword("");
//	config.setDataSourceClassName("org.hsqldb.jdbc.JDBCDataSource");
//	config.setDataSourceJNDI("java:jboss/mydatasource");
//	
//	config.addDataSourceProperty("cachePrepStmts", "true");
//	config.addDataSourceProperty("prepStmtCacheSize", "250");
//	config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//	
//	return new HikariDataSource(config);
    }

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
