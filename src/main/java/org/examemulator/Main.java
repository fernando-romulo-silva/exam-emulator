package org.examemulator;

import org.examemulator.gui.exam.ExamController;

import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {
    
    public static void main(final String... args) {

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
}
