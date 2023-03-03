package org.examemulator.util.database;

import static java.io.File.separator;
import static org.hsqldb.Database.CLOSEMODE_IMMEDIATELY;

import java.io.IOException;
import java.net.ServerSocket;

import org.hsqldb.Server;
import org.slf4j.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HsqldbServer {
    
    public static final String PATH = "/";
    
    public static final String HSQLDB_URL = "jdbc:hsqldb:hsql://localhost:";
    
    private final Server hsqlServer;

    private final Logger logger;
    
    @Inject
    HsqldbServer(final Logger logger) {
	super();
	this.logger = logger;
	this.hsqlServer = new Server();
    }
    
    public void stop() {
        
	hsqlServer.shutdownCatalogs(CLOSEMODE_IMMEDIATELY);
        hsqlServer.stop();
        hsqlServer.shutdown();

        logger.info("HSqlDB Server stopped");
    }
    
    public void start() {

        try (final var serverSocket = new ServerSocket(2527)) {
            serverSocket.setReuseAddress(true);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Server port isn't open!");
        }

        hsqlServer.setSilent(true);
        hsqlServer.setTrace(false);
        
        final var dataBasesFolder = "database";
        final var dataBaseName = "examEmulator";
        
        hsqlServer.setPort(9137);
        hsqlServer.setDatabaseName(0, dataBaseName);
        
	hsqlServer.setDatabasePath(0, "file:" + dataBasesFolder  + separator + dataBaseName);

        hsqlServer.start();
        
        logger.info("HSqlDB Server Started");
    }

}
