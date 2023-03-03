package org.examemulator;

import org.examemulator.domain.ExamGroup;
import org.examemulator.gui.exam.ExamController;
import org.examemulator.service.ExamGroupService;
import org.examemulator.util.database.HsqldbServer;

import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {
    
    public static void main(final String... args) {

	execute01();
    }

    static void executeApplication() {
	final var container = SeContainerInitializer.newInstance().initialize();

//	   final var beanManager = container.getBeanManager();
//	   beanManager.getEvent().fire(new ExamGui.BootEvent() {});

	final var examController = container.select(ExamController.class).get();
	examController.show();
	
	Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	    
	    // Both OpenWebBeans and Weld seem to shutdown on their own.
	    // Weld even prints a stack trace if we close it.
	    if (container.isRunning()) {
		container.close();
	    }
	    
	}));
    }
    
    static void execute01() {
	final var container = SeContainerInitializer.newInstance().initialize();
	
	final var examGroup = new ExamGroup("group1", 1);
	
	final var hsqldbServer = container.select(HsqldbServer.class).get(); 
	
	hsqldbServer.start();
	
	final var examGroupService = container.select(ExamGroupService.class).get();
	
	examGroupService.save(examGroup);
	
	System.out.println(examGroupService.getAll());
	
	hsqldbServer.stop();
    }
    
    
}
