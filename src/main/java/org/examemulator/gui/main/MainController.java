package org.examemulator.gui.main;

import javax.swing.SwingUtilities;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.gui.main.MainView.MainGui;
import org.examemulator.service.CertificationService;
import org.examemulator.service.ExamService;
import org.examemulator.service.QuestionnaireSetService;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MainController {

    private final ExamService examService;

    private final CertificationService certificationService;

    private final QuestionnaireSetService questionnaireSetService;

    private final Logger logger;

    private final MainView view;

    private Certification selectedCertification;

    private QuestionnaireSet selectedQuestionnaireSet;

    @Inject
    MainController( //
		    final MainGui mainGui, //
		    final ExamService examService, //
		    final CertificationService certificationService, //
		    final QuestionnaireSetService questionnaireSetService, //
		    final Logger logger) {
	super();
	this.examService = examService;
	this.certificationService = certificationService;
	this.questionnaireSetService = questionnaireSetService;
	this.logger = logger;
	this.view = mainGui.getView();
    }

    @PostConstruct
    void init() {
	// initActions();
    }

    private void initView() {
//	view.questionInternPanel.removeAll();
//	view.questionInternPanel.revalidate();
//	view.questionInternPanel.repaint();
//
//	view.pQuestions.removeAll();
//	view.pQuestions.revalidate();
//	view.pQuestions.repaint();
    }

    public void show() {
	
	initView();
	
	SwingUtilities.invokeLater(() -> {

	    logger.info("starting swing-event: {}", this.getClass().getSimpleName());

	    view.revalidate();
	    view.repaint();
	    view.setVisible(true);

	    logger.info("finished swing-event: {}", this.getClass().getSimpleName());
	});

    }
}
