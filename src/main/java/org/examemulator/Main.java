package org.examemulator;

import org.examemulator.domain.pretest.PreGroup;
import org.examemulator.gui.preexame.PreExameController;
import org.examemulator.service.ExamGroupService;
import org.examemulator.util.database.HsqldbServer;

import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(final String... args) {

	executeApplication02();
    }

    static void executeApplication02() {
	final var container = SeContainerInitializer.newInstance().initialize();

	HsqldbServer.start();

	final var preExamController = container.select(PreExameController.class).get();
	preExamController.show();

	Runtime.getRuntime().addShutdownHook(new Thread(() -> {

	    // Both OpenWebBeans and Weld seem to shutdown on their own.
	    // Weld even prints a stack trace if we close it.
	    if (container.isRunning()) {
		container.close();
	    }

	    HsqldbServer.stop();
	}));
    }

    static void execute01() {

	HsqldbServer.start();

	final var container = SeContainerInitializer.newInstance().initialize();

	final var examGroup = new PreGroup("group1", null, null);

	final var examGroupService = container.select(ExamGroupService.class).get();

	examGroupService.save(examGroup);

	System.out.println(examGroupService.getAll());

	HsqldbServer.stop();

    }

}
