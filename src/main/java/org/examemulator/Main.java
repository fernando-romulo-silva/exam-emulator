package org.examemulator;


import org.examemulator.gui.exam.ExamController;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(final String... args) {
	
	try (final var container = SeContainerInitializer.newInstance().initialize()) {
	    
	    System.out.println("bla");
	}
	
	
//        seContainer.getBeanManager().fireEvent(new BootEvent());
	
//	new ExamController();
        
    }
}
