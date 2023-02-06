package org.examemulator.gui;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class GuiProducer {

    
    @Produces
    @RequestScoped
    public ExamView createExamView() {
	
	return new ExamView();
    }

}
