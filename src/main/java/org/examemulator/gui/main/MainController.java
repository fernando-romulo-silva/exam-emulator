package org.examemulator.gui.main;

import static java.util.Objects.nonNull;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.SwingConstants.CENTER;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import static org.examemulator.util.ControllerUtil.alignColumns;
import static org.examemulator.util.ControllerUtil.createTableModel;
import static org.examemulator.util.ControllerUtil.TableModelField.fieldOf;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.ObjectUtils;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamStatus;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.gui.components.MainJTreeTreeNode;
import org.examemulator.gui.main.MainView.MainGui;
import org.examemulator.gui.questionnaire.QuestionnaireController;
import org.examemulator.gui.statitics.StatiticsController;
import org.examemulator.service.CertificationService;
import org.examemulator.service.ExamService;
import org.examemulator.service.LoadFromFileService;
import org.examemulator.service.QuestionnaireService;
import org.examemulator.service.QuestionnaireSetService;
import org.examemulator.util.FileUtil;
import org.jboss.weld.exceptions.IllegalArgumentException;
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

    private final QuestionnaireController questionnaireController;

    private final StatiticsController statiticsController;

    private Certification selectedCertification;
    private QuestionnaireSet selectedQuestionnaireSet;
    private Questionnaire selectedQuestionnaire;

    private Exam selectedExam;
    private final List<Exam> exams = new ArrayList<>();

    private final List<Question> questions = new ArrayList<>();

    private final LoadFromFileService loadFromFileService;

    @Inject
    MainController( //
		    final MainGui mainGui, //
		    final QuestionnaireController questionnaireController, //
		    final StatiticsController statiticsController, // 
		    final CertificationService certificationService, //
		    final QuestionnaireSetService questionnaireSetService, //
		    final QuestionnaireService questionnaireService, //
		    final ExamService examService, //
		    final LoadFromFileService loadFromFileService, //
		    final Logger logger) {
	super();
	this.certificationService = certificationService;
	this.questionnaireSetService = questionnaireSetService;
	this.questionnaireService = questionnaireService;
	this.examService = examService;
	this.loadFromFileService = loadFromFileService;
	this.logger = logger;
	this.view = mainGui.getView();
	this.questionnaireController = questionnaireController;
	this.statiticsController = statiticsController;
    }

    @PostConstruct
    void init() {
	initActions();
    }

    public void show() {
	initView();
	invokeLaterView();
    }

    public void show(final Component lastView) {
	view.setLocationRelativeTo(lastView);
	invokeLaterView();
    }

    private void invokeLaterView() {
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

	loadTreeData();
	loadExamData();
	loadQuestionData();
    }

    private void initActions() {

	view.trData.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(final MouseEvent event) {
            
		final var path = view.trData.getPathForLocation(event.getX(), event.getY());
		
		if (path == null) {
		    return;
		}

		final var selectedNode = (MainJTreeTreeNode) path.getLastPathComponent();
		final var type = selectedNode.getType();
		

		if (Objects.equals(type, Certification.class)) {
		    
		    if (SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 1) {
			
			final var certificationString = (String) selectedNode.getUserObject();
			final var certificationId = toLong(trim(substringBefore(certificationString, "-")));
			selectedCertification = certificationService.findById(certificationId).orElse(null);

			 loadExamData();
		    }
		    
		} else if (Objects.equals(type, QuestionnaireSet.class)) {
		    
		    if (SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 1) {
			
			final var questionnaireSetString = (String) selectedNode.getUserObject();
			final var questionnaireSetOrder = toInt(trim(substringBefore(questionnaireSetString, "-")));
			selectedQuestionnaireSet = questionnaireSetService.findByCertificationAndOrder(selectedCertification, questionnaireSetOrder).orElse(null);
		    }		    

		} else if (Objects.equals(type, Questionnaire.class)) {
		    
		    final var questionnaireString = (String) selectedNode.getUserObject();
		    final var questionnaireOrder = toInt(trim(substringBefore(questionnaireString, "-")));
		    selectedQuestionnaire = questionnaireService.findByQuestionnaireSetAndOrder(selectedQuestionnaireSet, questionnaireOrder).orElse(null);
		    
		    if (SwingUtilities.isLeftMouseButton(event) && !event.isConsumed() && event.getClickCount() == 2 && nonNull(selectedQuestionnaire)) {
			view.setVisible(false);
			questionnaireController.show(view, selectedQuestionnaire);
			return;
		    }

		    if (SwingUtilities.isLeftMouseButton(event) && !event.isConsumed() && event.getClickCount() == 1 && nonNull(selectedQuestionnaire)) {
			loadQuestionData();
		    }
		    
		}		    
            }
        });
	
	view.examTable.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mousePressed(final MouseEvent event) {

		final var table = (JTable) event.getSource();
		final var currentRow = table.getSelectedRow();

		if (event.getClickCount() == 2 && !event.isConsumed() && currentRow != -1) {
		    final var column = 0;
		    final var valueAt = table.getModel().getValueAt(currentRow, column);

		    if (Objects.nonNull(valueAt)) {
			selectedExam = exams.stream() //
					.filter(cert -> Objects.equals(cert.getId(), toLong(valueAt.toString()))) //
					.findAny() //
					.orElse(null);

			if (Objects.nonNull(selectedExam) && ExamStatus.FINISHED.equals(selectedExam.getStatus())) {
			    view.setVisible(false);
			    statiticsController.show(selectedExam, view);
			}
		    }

		}
	    }
	});

	view.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(final WindowEvent windowEvent) {
		if (showConfirmDialog(view, "Are you sure you want to leave?", "Exit Application", YES_NO_OPTION, QUESTION_MESSAGE) == YES_OPTION) {
		    System.exit(0);
		}
	    }
	});
    }

    private void loadTreeData() {
	
	selectedCertification = null;
	final var certifications = certificationService.findAll().toList();

	final var root = new DefaultMutableTreeNode("Certifications");
	
	for (final var certification : certifications) {
	    
	    final var cert = leftPad(certification.getId().toString(), 2, '0').concat("-").concat(certification.getName());
	    
	    final var curCertification = new MainJTreeTreeNode(cert, Certification.class);
	    root.add(curCertification);
	    
	    final var questionnaireSets = questionnaireSetService.findByCertification(certification).toList();
	    
	    for (final var questionnaireSet : questionnaireSets) {
		
		final var qset = leftPad(questionnaireSet.getOrder().toString(), 2, '0').concat("-").concat(questionnaireSet.getName());
		
		final var curQuestionnaireSet = new MainJTreeTreeNode(qset, QuestionnaireSet.class);
		curCertification.add(curQuestionnaireSet);
		
		final var questionnaires = questionnaireService.findByQuestionnaireSet(questionnaireSet).toList();
		
		for (final var questionnaire : questionnaires) {
		    final var qu = leftPad(questionnaire.getOrder().toString(), 2, '0').concat("-").concat(questionnaire.getName());
		    final var curQuestionnaire = new MainJTreeTreeNode(qu, Questionnaire.class);
		    curQuestionnaireSet.add(curQuestionnaire);
		}
	    }
	}
	
	view.trData.setModel(new DefaultTreeModel(root));
    }

    private void loadExamData() {

	exams.clear();

	if (ObjectUtils.anyNotNull(selectedCertification)) {
	    exams.addAll(examService.findByCertification(selectedCertification).toList());
	}

	selectedExam = null;
	final var fields = List.of(fieldOf("id"), fieldOf("status"), fieldOf("type"), fieldOf("shuffleQuestions"), fieldOf("result"));
	view.examTable.setModel(createTableModel(Exam.class, exams, fields));
	view.examTable.getSelectionModel().setSelectionMode(SINGLE_SELECTION);

	alignColumns(view.examTable, List.of(0), CENTER);
    }

    private void loadQuestionData() {
	questions.clear();

	if (ObjectUtils.anyNotNull(selectedQuestionnaire)) {

	    questions.addAll(selectedQuestionnaire.getQuestions());

	} else if (ObjectUtils.anyNotNull(selectedCertification, selectedQuestionnaire)) {
	    questions.addAll(questionnaireService.findByCertification(selectedCertification).toList());
	}

	view.questionsTable.setModel(createTableModel(Question.class, questions, List.of(fieldOf("order", LABEL_TABLE_ORDER), fieldOf("name"), fieldOf("value"))));
	view.questionsTable.getSelectionModel().setSelectionMode(MULTIPLE_INTERVAL_SELECTION);

	alignColumns(view.questionsTable, List.of(0), CENTER);
    }

    public void loadCertificationFromFolder(final String certificationDir) {

	final var certificationPath = Paths.get(certificationDir);

	if (Files.notExists(certificationPath)) {
	    throw new IllegalArgumentException(MessageFormat.format("Certification folder ''{0}'' does not exist", certificationPath));
	}

	final var questionnairesSetPath = certificationPath.resolve("Questionnaires");
	if (Files.notExists(certificationPath) || !Files.isDirectory(certificationPath)) {
	    throw new IllegalArgumentException("Certification folder 'Questionnaires' does not exist!");
	}

	final var certification = loadFromFileService.loadCertification(certificationPath);

	final var questionnaireSetFolders = FileUtil.readFolders(questionnairesSetPath);
	if (questionnaireSetFolders.isEmpty()) {
	    throw new IllegalArgumentException("The 'Questionnaires' does not have any folders, questionnaires set!");
	}

	for (final var quetionnaireSetFolder : questionnaireSetFolders) {

	    final var questionnaireSet = loadFromFileService.loadQuestionnaireSet(quetionnaireSetFolder, certification);

	    final var questionnairesPaths = FileUtil.readFolders(quetionnaireSetFolder);

	    for (final var questionnairePath : questionnairesPaths) {

		final var questionsFiles = loadFromFileService.loadQuestions(questionnairePath, certification);
		loadFromFileService.loadQuestionnaire(questionnairePath, questionsFiles, questionnaireSet);
	    }
	}
    }
}
