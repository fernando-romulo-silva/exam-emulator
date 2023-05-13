package org.examemulator;

import org.examemulator.domain.pretest.PretestGroup;
import org.examemulator.gui.exam.ExamController;
import org.examemulator.service.ExamGroupService;
import org.examemulator.util.database.HsqldbServer;

import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {
    
    public static void main(final String... args) {

	executeApplication();
    }

    static void executeApplication() {
	final var container = SeContainerInitializer.newInstance().initialize();

//	   final var beanManager = container.getBeanManager();
//	   beanManager.getEvent().fire(new ExamGui.BootEvent() {});
	
	HsqldbServer.start();

	final var examController = container.select(ExamController.class).get();
	examController.show();
	
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
	
	final var examGroup = new PretestGroup("group1");
	
	final var examGroupService = container.select(ExamGroupService.class).get();
	
	examGroupService.save(examGroup);
	
	System.out.println(examGroupService.getAll());
	
	 HsqldbServer.stop();
	
    }
    
    
}
