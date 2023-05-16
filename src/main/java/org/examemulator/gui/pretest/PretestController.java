package org.examemulator.gui.pretest;

import static javax.swing.JFileChooser.DIRECTORIES_ONLY;

import java.io.File;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
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
	
	view.btnNew.addActionListener(event -> {
	    
	    final var chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new File("."));
	    chooser.setDialogTitle("select an exam folder");
	    chooser.setFileSelectionMode(DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
	    chooser.showOpenDialog(view);

	    view.questionInternPanel.removeAll();
	    view.questionInternPanel.revalidate();
	    view.questionInternPanel.repaint();

	    view.pQuestions.removeAll();
	    view.pQuestions.revalidate();
	    view.pQuestions.repaint();

	    currentFolder = chooser.getSelectedFile().getAbsolutePath();

	    if (Objects.nonNull(currentFolder)) {

		view.examPanel.setBorder(BorderFactory.createTitledBorder(service.extractedExamName(currentFolder)));

		view.btnNewExam.setEnabled(true);
		view.textMinScore.setEnabled(true);
		view.textDiscrete.setEnabled(true);
		view.cbMode.setEnabled(true);
		view.spinnerTimeDuration.setEnabled(true);
		view.chckbxShuffleQuestions.setEnabled(true);
		view.chckbxShuffleOptions.setEnabled(true);

		view.rangeQuestions.setEnabled(true);
		view.rangeQuestions.setMinimum(1);
		view.rangeQuestions.setMaximum(service.getQtyFiles(currentFolder));
		view.rangeQuestions.setValue(1);
		view.rangeQuestions.setUpperValue(service.getQtyFiles(currentFolder));

		view.btn.setEnabled(false);
		view.btnNext.setEnabled(false);
		view.btnPrevious.setEnabled(false);
		view.btnStatistics.setEnabled(false);

		view.revalidate();
		view.repaint();
		view.setVisible(true);
	    }
	    
	});
	
	
	
    }
}