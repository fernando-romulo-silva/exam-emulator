package org.examemulator.gui.pretest;

import javax.swing.SwingUtilities;

import org.examemulator.domain.pretest.Pretest;
import org.examemulator.domain.pretest.PretestQuestion;
import org.examemulator.gui.components.RangeSlider;
import org.examemulator.service.PretestService;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PretestController {
    
    private final PretestView view;

    private final PretestService service;

    private final Logger logger;
    
    private Pretest exam;

    private PretestQuestion selectedQuestion;

    private String currentFolder;

    @Inject
    PretestController(
		    final PretestGui gui, 
		    final PretestService service, 
		    final Logger logger) {
	super();
	this.view = gui.getView();
	this.service = service;
	this.logger = logger;
    }
    
    @PostConstruct
    void init() {
    }
    
    public void show() {
	
	
	
	final var name = "ExamController";
	SwingUtilities.invokeLater(() -> {
	    
	    logger.info("starting swing-event: {}", name);
	    
	    view.revalidate();
	    view.repaint();
	    view.setVisible(true);
	    
	    logger.info("finished swing-event: {}", name);
	});
    }
    
    public void createButtonsActions() {
	
	view.rangeQuestions.addChangeListener(event -> {
	    final var slider = (RangeSlider) event.getSource();
	    view.lblRangeLow.setText(String.valueOf(slider.getValue()));
	    view.lblUpper.setText(String.valueOf(slider.getUpperValue()));
	});
    }
}