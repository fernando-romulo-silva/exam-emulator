package org.examemulator;

import org.examemulator.gui.main.MainController;
import org.examemulator.util.database.HsqldbServer;

import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(final String... args) {

	final var container = SeContainerInitializer.newInstance().initialize();

	HsqldbServer.start();

	final var mainController = container.select(MainController.class).get();
	mainController.loadCertificationFromFolder("/home/fernando/Development/workspaces/eclipse-workspace/certifications-technologies/docker-dca-certification");
	mainController.show();

	Runtime.getRuntime().addShutdownHook(new Thread(() -> {

	    // Both OpenWebBeans and Weld seem to shutdown on their own.
	    // Weld even prints a stack trace if we close it.
	    if (container.isRunning()) {
		container.close();
	    }

	    HsqldbServer.stop();
	}));
    }
}
