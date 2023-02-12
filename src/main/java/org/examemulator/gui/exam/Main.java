package org.examemulator.gui.exam;

import org.examemulator.gui.exam.ExamController;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(final String... args) {
	
	try (final var container = SeContainerInitializer.newInstance().initialize()) {
//	    container.select(ExamGui.class).get();
	    container.getBeanManager().getEvent().fire(new ExamGui.BootEvent());
	}
//        seContainer.getBeanManager().fireEvent(new BootEvent());
    }
}
