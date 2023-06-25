package org.examemulator.gui.main;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.SwingConstants.CENTER;
import static org.examemulator.util.ControllerUtil.alignColumns;
import static org.examemulator.util.ControllerUtil.createTableModel;
import static org.examemulator.util.ControllerUtil.TableModelField.fieldOf;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.ObjectUtils;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.gui.main.MainView.MainGui;
import org.examemulator.service.CertificationService;
import org.examemulator.service.ExamService;
import org.examemulator.service.QuestionnaireService;
import org.examemulator.service.QuestionnaireSetService;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MainController {

    private static final String LABEL_TABLE_ORDER = "   Order  ";

    private final CertificationService certificationService;

    private final QuestionnaireSetService questionnaireSetService;

    private final QuestionnaireService questionnaireService;

    private final ExamService examService;

    private final Logger logger;

    private final MainView view;

    private Certification selectedCertification;
    private final List<Certification> certifications = new ArrayList<>();

    private QuestionnaireSet selectedQuestionnaireSet;
    private final List<QuestionnaireSet> questionnaireSets = new ArrayList<>();

    private Questionnaire selectedQuestionnaire;
    private final List<Questionnaire> questionnaires = new ArrayList<>();

    private Exam selectedExam;
    private final List<Exam> exams = new ArrayList<>();
    
    private final List<Question> questions = new ArrayList<>();

    @Inject
    MainController( //
		    final MainGui mainGui, //
		    final CertificationService certificationService, //
		    final QuestionnaireSetService questionnaireSetService, //
		    final QuestionnaireService questionnaireService, //
		    final ExamService examService, //
		    final Logger logger) {
	super();
	this.certificationService = certificationService;
	this.questionnaireSetService = questionnaireSetService;
	this.questionnaireService = questionnaireService;
	this.examService = examService;
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

	loadCertificationTable();
	loadQuestionnaireSetTable();
	loadQuestionnaireTable();
	loadExamTable();
	loadQuestionTable();
    }

    private void initActions() {

	view.certificantionTable.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mousePressed(final MouseEvent event) {

		final var table = (JTable) event.getSource();
		final var currentRow = table.getSelectedRow();

		if (event.getClickCount() == 1 && !event.isConsumed() && currentRow != -1 && event.getButton() == MouseEvent.BUTTON1) {
		    final var column = 0; // id
		    final var valueAt = table.getModel().getValueAt(currentRow, column);

		    if (Objects.nonNull(valueAt)) {
			selectCertification(valueAt.toString());
			loadQuestionnaireSetTable();
			loadQuestionnaireTable();
			loadQuestionTable();
		    }
		    
		} else if (event.getClickCount() == 1 && !event.isConsumed() && currentRow != -1 && event.getButton() == MouseEvent.BUTTON2) {
		    view.certificantionTable.getSelectionModel().clearSelection();
		    view.questionnaireSetTable.getSelectionModel().clearSelection();
		    view.questionnaireTable.getSelectionModel().clearSelection();
		    view.questionsTable.getSelectionModel().clearSelection();
		}
	    }
	});

	view.questionnaireSetTable.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mousePressed(final MouseEvent event) {

		final var table = (JTable) event.getSource();
		final var currentRow = table.getSelectedRow();

		if (event.getClickCount() == 1 && !event.isConsumed() && currentRow != -1) {
		    final var column = 0;
		    final var valueAt = table.getModel().getValueAt(currentRow, column);

		    if (Objects.nonNull(valueAt)) {
			selectQuestionnaireSet(valueAt.toString());
			loadQuestionnaireTable();
		    }
		}
	    }
	});
	
	view.questionnaireTable.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mousePressed(final MouseEvent event) {

		final var table = (JTable) event.getSource();
		final var currentRow = table.getSelectedRow();

		if (event.getClickCount() == 1 && !event.isConsumed() && currentRow != -1) {
		    final var column = 0;
		    final var valueAt = table.getModel().getValueAt(currentRow, column);

		    if (Objects.nonNull(valueAt)) {
			selectQuestionnaire(valueAt.toString());
			loadQuestionTable();
		    }
		}
	    }
	});
	
    }

    private boolean selectCertification(final String id) {

	final var value = Long.valueOf(id);

	final var certificationOptional = certifications.stream() //
			.filter(cert -> Objects.equals(cert.getId(), value)) //
			.findAny();

	if (certificationOptional.isPresent()) {
	    selectedCertification = certificationOptional.get();
	    return true;
	}

	return false;
    }

    private boolean selectQuestionnaireSet(final String order) {

	final var value = Integer.valueOf(order);

	final var questionnaireSetOptional = questionnaireSets.stream() //
			.filter(cert -> Objects.equals(cert.getOrder(), value)) //
			.findAny();

	if (questionnaireSetOptional.isPresent()) {
	    selectedQuestionnaireSet = questionnaireSetOptional.get();
	    return true;
	}

	return false;
    }
    
    private boolean selectQuestionnaire(final String order) {

	final var value = Integer.valueOf(order);

	final var questionnaireOptional = questionnaires.stream() //
			.filter(cert -> Objects.equals(cert.getOrder(), value)) //
			.findAny();

	if (questionnaireOptional.isPresent()) {
	    selectedQuestionnaire = questionnaireOptional.get();
	    return true;
	}

	return false;
    }

    private void loadCertificationTable() {
	certifications.clear();
	selectedCertification = null;

	certifications.addAll(certificationService.findAll().toList());
	view.certificantionTable.setModel(createTableModel(Certification.class, certifications, List.of(fieldOf("id", "   Identifier  "), fieldOf("name"))));
	view.certificantionTable.getSelectionModel().setSelectionMode(SINGLE_SELECTION);

	alignColumns(view.certificantionTable, List.of(0), CENTER);

    }

    private void loadQuestionnaireSetTable() {

	questionnaireSets.clear();

	if (Objects.nonNull(selectedCertification)) {
	    questionnaireSets.addAll(questionnaireSetService.findByCertification(selectedCertification).toList());
	}

	selectedQuestionnaireSet = null;
	view.questionnaireSetTable.setModel(createTableModel(QuestionnaireSet.class, questionnaireSets, List.of(fieldOf("order", LABEL_TABLE_ORDER), fieldOf("name"), fieldOf("description"))));
	view.questionnaireSetTable.getSelectionModel().setSelectionMode(SINGLE_SELECTION);
	
	alignColumns(view.questionnaireSetTable, List.of(0), CENTER);
    }

    private void loadQuestionnaireTable() {

	questionnaires.clear();

	if (ObjectUtils.anyNotNull(selectedCertification, selectedQuestionnaireSet)) {
	    questionnaires.addAll(questionnaireService.findByCertificationAndQuestionnaireSet(selectedCertification, selectedQuestionnaireSet).toList());
	}

	selectedQuestionnaire = null;
	view.questionnaireTable.setModel(createTableModel(Questionnaire.class, questionnaires, List.of(fieldOf("order", LABEL_TABLE_ORDER), fieldOf("name"), fieldOf("Description"))));
	view.questionnaireTable.getSelectionModel().setSelectionMode(SINGLE_SELECTION);
	
	alignColumns(view.questionnaireTable, List.of(0), CENTER);
    }

    private void loadExamTable() {

	exams.clear();

	if (ObjectUtils.anyNotNull(selectedCertification, selectedQuestionnaireSet)) {
	    exams.addAll(examService.findByCertification(selectedCertification).toList());
	}

	selectedExam = null;
	view.examTable.setModel(createTableModel(Exam.class, exams, List.of(fieldOf("Id"), fieldOf("Name"))));
	view.examTable.getSelectionModel().setSelectionMode(SINGLE_SELECTION);
	
	alignColumns(view.examTable, List.of(0), CENTER);
    }
    
    private void loadQuestionTable() {
	questions.clear();

	if (ObjectUtils.anyNotNull(selectedQuestionnaire)) {
	    
	    questions.addAll(selectedQuestionnaire.getQuestions());
	    
	} else if (ObjectUtils.anyNotNull(selectedCertification, selectedQuestionnaire)) {
	    questions.addAll(questionnaireService.findByCertification(selectedCertification).toList());
	}
	
	view.questionsTable.setModel(createTableModel(Question.class, questions, List.of(fieldOf("order", LABEL_TABLE_ORDER), fieldOf("name"))));
	view.questionsTable.getSelectionModel().setSelectionMode(SINGLE_SELECTION);
	
	alignColumns(view.questionsTable, List.of(0), CENTER);
    }

}
