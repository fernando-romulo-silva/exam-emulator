package org.examemulator.util.database;

import static java.io.File.separator;
import static org.hsqldb.Database.CLOSEMODE_IMMEDIATELY;

import java.io.IOException;
import java.net.ServerSocket;

import org.hsqldb.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HsqldbServer {

    public static final String DATABASE_NAME = "examEmulator";

    public static final int HSQLDB_PORT = 9137;

    public static final String HSQLDB_URL = "jdbc:hsqldb:hsql://localhost:" + HSQLDB_PORT + "/" + DATABASE_NAME;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HsqldbServer.class);

    private static final Server SERVER = new Server();
    
    private HsqldbServer() {
	throw new UnsupportedOperationException("You can't instantiate this class!");
    }

    public static void stop() {
	
	if (SERVER.isNotRunning()) {
	   return;
	}

	SERVER.shutdownCatalogs(CLOSEMODE_IMMEDIATELY);
	SERVER.stop();
	SERVER.shutdown();

	LOGGER.info("HSqlDB Server stopped");
    }

    public static void start() {

	try (final var serverSocket = new ServerSocket(HSQLDB_PORT)) {
	    serverSocket.setReuseAddress(true);
	} catch (final IOException e) {
	    throw new IllegalArgumentException("Server port isn't open!");
	}

	SERVER.setSilent(true);
	SERVER.setTrace(false);

	final var dataBasesFolder = "database";

	SERVER.setPort(HSQLDB_PORT);
	SERVER.setDatabaseName(0, DATABASE_NAME);
	SERVER.setDatabasePath(0, "file:" + dataBasesFolder + separator + DATABASE_NAME);

	SERVER.start();

	LOGGER.info("HSqlDB Server Started");
    }

}
