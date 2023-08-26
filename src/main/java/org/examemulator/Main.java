package org.examemulator;

import org.examemulator.gui.main.MainController;
import org.examemulator.util.database.HsqldbServer;
import org.examemulator.util.datasource.JndiHelper;

import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(final String... args) throws Exception {

	HsqldbServer.start();

	JndiHelper.registerDataSource();

	final var container = SeContainerInitializer.newInstance().initialize();

	final var mainController = container.select(MainController.class).get();

	mainController.loadCertificationFromFolder("/home/fernando/Development/workspaces/eclipse-workspace/certifications-technologies/docker-dca-certification");
	mainController.show();

	Runtime.getRuntime().addShutdownHook(new Thread(() -> {

	    if (container.isRunning()) {
		container.close();
	    }

	    HsqldbServer.stop();
	}));

    }
}
