package org.examemulator;

import static java.lang.Runtime.getRuntime;

import org.examemulator.application.ExamService;
import org.examemulator.gui.main.MainController;
import org.examemulator.infra.database.HsqldbServer;
import org.examemulator.infra.datasource.JndiHelper;

import com.formdev.flatlaf.FlatLightLaf;

import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

	public static void main(final String... args) {

		HsqldbServer.start();

		JndiHelper.registerDataSource();

		FlatLightLaf.setup();

		final var container = SeContainerInitializer.newInstance().initialize();

		final var service = container.select(ExamService.class).get();
		service.updateAll();

		final var mainController = container.select(MainController.class).get();
		// mainController.loadCertificationFromFolder("/home/fernando/Development/workspaces/vscode-workspace/personal/exam-emulator/src/test/resources/certifications/food-certification");
		// "/home/fernando/Development/workspaces/vscode-workspace/personal/certifications-technologies/edu-1202-spring-professional"
		mainController.loadCertificationFromFolder(
				"/home/fernando/Development/workspaces/vscode-workspace/personal/certifications-technologies/edu-1202-spring-professional");
		mainController.show();

		getRuntime().addShutdownHook(new Thread(() -> {

			if (container.isRunning()) {
				container.close();
			}

			HsqldbServer.stop();
		}));

	}
}
