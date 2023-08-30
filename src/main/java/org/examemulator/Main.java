package org.examemulator;

import static java.lang.Runtime.getRuntime;

import org.examemulator.gui.main.MainController;
import org.examemulator.util.database.HsqldbServer;
import org.examemulator.util.datasource.JndiHelper;

import com.formdev.flatlaf.FlatLightLaf;

import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(final String... args) {

	HsqldbServer.start();

	JndiHelper.registerDataSource();
	
	FlatLightLaf.setup();

	final var container = SeContainerInitializer.newInstance().initialize();

	final var mainController = container.select(MainController.class).get();

	mainController.loadCertificationFromFolder("/home/fernando/Development/workspaces/eclipse-workspace/certifications-technologies/docker-dca-certification");
	mainController.show();

	getRuntime().addShutdownHook(new Thread(() -> {

	    if (container.isRunning()) {
		container.close();
	    }

	    HsqldbServer.stop();
	}));

    }
}
