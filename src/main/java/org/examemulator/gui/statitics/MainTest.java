package org.examemulator.gui.statitics;

import java.lang.annotation.Annotation;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.se.SeContainerInitializer;

public class MainTest {

    public static void main(final String... args) {
	
	try (final var container = SeContainerInitializer.newInstance().initialize()) {
	    
	  final var t =  container.select(StatiticsGui.class).get();
	  System.out.println(t);
	  
    
	}
	
	
//        seContainer.getBeanManager().fireEvent(new BootEvent());
	
//	new ExamController();
        
    }

}
