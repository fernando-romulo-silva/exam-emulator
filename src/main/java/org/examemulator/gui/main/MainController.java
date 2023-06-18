package org.examemulator.gui.main;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.gui.main.MainView.MainGui;
import org.examemulator.service.CertificationService;
import org.examemulator.service.ExamService;
import org.examemulator.service.QuestionnaireSetService;
import org.examemulator.util.ControllerUtil;
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
    
    private final List<Certification> certifications = new ArrayList<>();

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
	initActions();
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
    
    private void initView() {
//	view.questionInternPanel.removeAll();
//	view.questionInternPanel.revalidate();
//	view.questionInternPanel.repaint();
//
//	view.pQuestions.removeAll();
//	view.pQuestions.revalidate();
//	view.pQuestions.repaint();
	
	certifications.clear(); 
	certifications.addAll(certificationService.findAll().toList());
	final var model = ControllerUtil.createTableModel(Certification.class, certifications);
	view.certificantionTable.setModel(model);
	final var cellSelectionModel = view.certificantionTable.getSelectionModel();
	cellSelectionModel.setSelectionMode(SINGLE_SELECTION);
//	model.fireTableDataChanged();
	
//	view.certificantionTable.addMouseListener(()(event) -> {});
	
//	int column = 0;
//	int row = view.certificantionTable.getSelectedRow();
    }
    
    private void initActions() {
	
	    
	    view.certificantionTable.addMouseListener(
			    
				new MouseAdapter() {
				     
				    @Override
				    public void mousePressed(final MouseEvent event) {
					
					final var table = (JTable) event.getSource();
				        final var currentRow = table.getSelectedRow();
				        
				        if (event.getClickCount() == 2 && table.getSelectedRow() != -1) {
				            final var row = 0; // id
				            
				            final var value = Long.valueOf(table.getModel().getValueAt(row, currentRow).toString());
				            final var certificationOptional = certifications.stream()
				        		    .filter(cert -> Objects.equals(cert.getId(), value))
				        		    .findAny();
				            
				            if (certificationOptional.isPresent()) {
						logger.info(certificationOptional.get().toString());
					    }
				            
				        }
				    }
				});
	    
	    
	
    }
    
    
}
