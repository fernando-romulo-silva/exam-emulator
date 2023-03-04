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

    public static final String DATABASE_NAME = "examEmulator";

    public static final int HSQLDB_PORT = 9137;

    public static final String HSQLDB_URL = "jdbc:hsqldb:hsql://localhost:" + HSQLDB_PORT + "/" + DATABASE_NAME;

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

	try (final var serverSocket = new ServerSocket(HSQLDB_PORT)) {
	    serverSocket.setReuseAddress(true);
	} catch (final IOException e) {
	    throw new IllegalArgumentException("Server port isn't open!");
	}

	hsqlServer.setSilent(true);
	hsqlServer.setTrace(false);

	final var dataBasesFolder = "database";

	hsqlServer.setPort(HSQLDB_PORT);
	hsqlServer.setDatabaseName(0, DATABASE_NAME);
	hsqlServer.setDatabasePath(0, "file:" + dataBasesFolder + separator + DATABASE_NAME);

	hsqlServer.start();

	logger.info("HSqlDB Server Started");
    }

}
