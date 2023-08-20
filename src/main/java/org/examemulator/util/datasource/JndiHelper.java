package org.examemulator.util.datasource;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class JndiHelper {

    private static InitialContext INITIAL_CONTEXT;
    
    public static void registerDataSource() {

	try {
	    
	    final var env = new Hashtable<String, String>();
	    System.setProperty("java.naming.factory.initial", "org.osjava.sj.MemoryContextFactory");
	    System.setProperty("org.osjava.sj.jndi.shared", "true");

	    env.put("java.naming.factory.initial", "org.osjava.sj.MemoryContextFactory");
	    env.put("org.osjava.sj.jndi.shared", "true");
//	    env.put("jndi.syntax.direction", "left_to_right");
//	    env.put("jndi.syntax.separator", "/");

	    INITIAL_CONTEXT = new InitialContext(env);
	    
	    final var config = new HikariConfig();

	    config.setJdbcUrl("jdbc:hsqldb:hsql://localhost:9137/examEmulator");
	    config.setUsername("sa");
	    config.setPassword("");
	    config.setDataSourceJNDI("HikariDataSource");
	    config.setMaximumPoolSize(1);
	    config.setMinimumIdle(1);

	    config.addDataSourceProperty("cachePrepStmts", "true");
	    config.addDataSourceProperty("prepStmtCacheSize", "250");
	    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

	    final var dataSource = new HikariDataSource(config);
	    
	    INITIAL_CONTEXT.bind("HikariDataSource", dataSource);

	} catch (final NamingException ex) {
	    throw new IllegalStateException(ex);
	}
    }

}
