package org.examemulator;


import org.examemulator.gui.ExamController;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(final String... args) {
        final var seContainer = SeContainerInitializer.newInstance().initialize();
	
        seContainer.getBeanManager().fireEvent(new BootEvent());
	
//	new ExamController();
    }
}
