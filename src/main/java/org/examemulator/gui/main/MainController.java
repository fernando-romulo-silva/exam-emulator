package org.examemulator.gui.main;

import static java.util.Objects.nonNull;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;
import static javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import static org.examemulator.domain.exam.ExamStatus.FINISHED;
import static org.examemulator.domain.exam.ExamStatus.INITIAL;
import static org.examemulator.domain.exam.ExamStatus.RUNNING;
import static org.examemulator.infra.util.DomainUtil.QUESTION_MIN_ATTEMPT;
import static org.examemulator.infra.util.TableModelField.fieldOf;
import static org.examemulator.infra.util.gui.ControllerUtil.alignTableModel;
import static org.examemulator.infra.util.gui.ControllerUtil.createQuestionDialog;
import static org.examemulator.infra.util.gui.TableCellRendererUtil.DATE_TIME_TABLE_CELL_RENDERER;
import static org.examemulator.infra.util.gui.TableCellRendererUtil.ENUM_TABLE_CELL_RENDERER;
import static org.examemulator.infra.util.gui.TableCellRendererUtil.NUMBER_TABLE_CELL_RENDERER;
import static org.examemulator.infra.util.gui.TableCellRendererUtil.ORDER_TABLE_CELL_RENDERER;
import static org.examemulator.infra.util.gui.TableCellRendererUtil.PERCENT_TABLE_CELL_RENDERER;

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
import java.util.Optional;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.examemulator.application.CertificationService;
import org.examemulator.application.ExamService;
import org.examemulator.application.LoadFromFileService;
import org.examemulator.application.QuestionnaireService;
import org.examemulator.application.QuestionnaireSetService;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.gui.components.MainJTreeTreeNode;
import org.examemulator.gui.exam.ExamController;
import org.examemulator.gui.main.MainView.MainGui;
import org.examemulator.gui.questionnaire.QuestionnaireController;
import org.examemulator.gui.statitics.StatiticsController;
import org.examemulator.infra.dto.QuestionDTO;
import org.examemulator.infra.util.FileUtil;
import org.examemulator.infra.util.TableModelField;
import org.jboss.weld.exceptions.IllegalArgumentException;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MainController {

    private static final String LABEL_TABLE_ORDER = "Order";

    private final CertificationService certificationService;

    private final QuestionnaireSetService questionnaireSetService;

    private final QuestionnaireService questionnaireService;

    private final ExamService examService;

    private final Logger logger;

    private final MainView view;

    private final QuestionnaireController questionnaireController;

    private final ExamController examController;

    private final StatiticsController statiticsController;

    private final List<Exam> exams = new ArrayList<>();

    private final List<QuestionDTO> questions = new ArrayList<>();

    private final LoadFromFileService loadFromFileService;

    private Certification selectedCertification;

    private QuestionnaireSet selectedQuestionnaireSet;

    private Questionnaire selectedQuestionnaire;

    private Exam selectedExam;

    @Inject
    MainController( //
		    final MainGui mainGui, //
		    final QuestionnaireController questionnaireController, //
		    final ExamController examController, //
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
	this.examController = examController;
    }

    @PostConstruct
    void init() {
	initActions();
    }

    public void show() {
	initView();
	invokeLaterView();
	
	view.pack();
	view.setLocationRelativeTo(null);
    }

    public void show(final Component lastView) {
	view.pack();
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
	loadTreeData();
	loadExamData();
	loadQuestionData();
	statisticData();
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

		    if (isLeftMouseButton(event) && event.getClickCount() == 1 && !event.isConsumed()) {

			final var certificationString = (String) selectedNode.getUserObject();
			final var certificationId = toLong(trim(substringBefore(certificationString, "-")));
			selectedCertification = certificationService.findById(certificationId).orElse(null);

			selectedQuestionnaireSet = null;
			selectedQuestionnaire = null;

			if (nonNull(selectedCertification)) {
			    loadExamData();
			    loadQuestionData();
			    statisticData();
			}
		    }

		} else if (Objects.equals(type, QuestionnaireSet.class)) {

		    if (isLeftMouseButton(event) && event.getClickCount() == 1 && !event.isConsumed()) {

			final var questionnaireSetString = (String) selectedNode.getUserObject();
			final var questionnaireSetOrder = toInt(trim(substringBefore(questionnaireSetString, "-")));
			selectedQuestionnaireSet = questionnaireSetService.findByCertificationAndOrder(selectedCertification, questionnaireSetOrder).orElse(null);

			selectedQuestionnaire = null;

			if (nonNull(selectedQuestionnaireSet)) {
			    loadExamData();
			    loadQuestionData();
			    statisticData();
			}
		    }

		} else if (Objects.equals(type, Questionnaire.class)) {

		    final var selectedParentNode = (MainJTreeTreeNode) selectedNode.getParent();
		    final var questionnaireSetString = (String) selectedParentNode.getUserObject();
		    final var questionnaireSetOrder = toInt(trim(substringBefore(questionnaireSetString, "-")));
		    selectedQuestionnaireSet = questionnaireSetService.findByCertificationAndOrder(selectedCertification, questionnaireSetOrder).orElse(null);

		    final var questionnaireString = (String) selectedNode.getUserObject();
		    final var questionnaireOrder = toInt(trim(substringBefore(questionnaireString, "-")));
		    selectedQuestionnaire = questionnaireService.findByQuestionnaireSetAndOrder(selectedQuestionnaireSet, questionnaireOrder).orElse(null);

		    if (isLeftMouseButton(event) && !event.isConsumed() && event.getClickCount() == 2 && nonNull(selectedQuestionnaire)) {
			view.setVisible(false);
			questionnaireController.show(view, selectedQuestionnaire);
			return;
		    }

		    if (isLeftMouseButton(event) && !event.isConsumed() && event.getClickCount() == 1 && nonNull(selectedQuestionnaire)) {
			loadExamData();
			loadQuestionData();
			statisticData();
		    }

		}
	    }
	});

	view.examTable.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mousePressed(final MouseEvent event) {

		final var table = (JTable) event.getSource();
		final var currentRow = table.getSelectedRow();
		final var finalCurrentRow = table.convertRowIndexToModel(currentRow);

		if (isLeftMouseButton(event) && event.getClickCount() == 2 && !event.isConsumed() && finalCurrentRow != -1) {
		    final var column = 0;
		    final var valueAt = table.getModel().getValueAt(finalCurrentRow, column);

		    if (nonNull(valueAt)) {
			selectedExam = exams.stream() //
					.filter(cert -> Objects.equals(cert.getId(), toLong(valueAt.toString()))) //
					.findAny() //
					.orElse(null);

			if (nonNull(selectedExam) && FINISHED.equals(selectedExam.getStatus())) {
			    view.setVisible(false);
			    statiticsController.show(selectedExam, view);
			} else if (nonNull(selectedExam) && (Objects.equals(RUNNING, selectedExam.getStatus()) || Objects.equals(INITIAL, selectedExam.getStatus()))) {
			    view.setVisible(false);
			    examController.show(selectedExam, view);
			}
		    }
		}
	    }
	});

	view.questionsTable.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mousePressed(final MouseEvent event) {
		final var table = (JTable) event.getSource();
		final var tableModel = (AbstractTableModel) table.getModel();

		if (isLeftMouseButton(event) && event.getClickCount() == 2) {

		    final var selectedRow = table.getSelectedRow();
		    final var finalSelectedRow = table.convertRowIndexToModel(selectedRow);

		    final Optional<QuestionDTO> optionalQuestion;

		    if (tableModel.getColumnCount() == 8) { // without questionnaire name and set name

			final var questionOrder = tableModel.getValueAt(finalSelectedRow, 1);

			optionalQuestion = questions.stream() //
					.filter(q -> Objects.equals(q.questionOrder(), questionOrder)) //
					.findFirst();

		    } else if (tableModel.getColumnCount() == 9) {

			final var questionnaireName = tableModel.getValueAt(finalSelectedRow, 0);
			final var questionOrder = tableModel.getValueAt(finalSelectedRow, 2);

			optionalQuestion = questions.stream() //
					.filter(q -> Objects.equals(q.questionOrder(), questionOrder) //
							&& Objects.equals(q.questionnaireName(), questionnaireName)) //
					.findFirst();

		    } else {

			final var setName = tableModel.getValueAt(finalSelectedRow, 0);
			final var questionnaireName = tableModel.getValueAt(finalSelectedRow, 1);
			final var questionOrder = tableModel.getValueAt(finalSelectedRow, 3);

			optionalQuestion = questions.stream() //
					.filter(q -> Objects.equals(q.questionOrder(), questionOrder) //
							&& Objects.equals(q.questionnaireName(), questionnaireName) //
							&& Objects.equals(q.questionnaireSetName(), setName)) //
					.findFirst();
		    }

		    final var id = optionalQuestion.isPresent() //
				    ? optionalQuestion.get().idQuestion() //
				    : StringUtils.EMPTY;

		    final var questionOptional = questionnaireService.findById(id);

		    if (questionOptional.isEmpty()) {
			return;
		    }

		    final var question = questionOptional.get();
		    
		    final var currentActiveQuestion = question.isActive();
		    
		    final var activeQuestion = createQuestionDialog(view, questionOptional.get());
		    
		    if (currentActiveQuestion == activeQuestion) {
			return;
		    }
		    
		    if (activeQuestion) {
			question.active();
		    } else {
			question.inactive();
		    }
		    
		    questionnaireService.updateQuestion(question);
		    
		} else if (isRightMouseButton(event) && event.getClickCount() == 2) {

		    if (questions.isEmpty()) {
			showMessageDialog(view, "You need select at least one question!", "Error!", ERROR_MESSAGE);
			return;
		    }

		    final var selectedRows = table.getSelectedRows();
		    final var selectedIds = new ArrayList<String>();

		    if (tableModel.getColumnCount() == 8) { // without questionnaire name and set name

			for (final var selectedRow : selectedRows) {

			    final var finalSelectedRow = table.convertRowIndexToModel(selectedRow);

			    final var questionOrder = tableModel.getValueAt(finalSelectedRow, 1);

			    final var optionalQuestion = questions.stream() //
					    .filter(q -> Objects.equals(q.questionOrder(), questionOrder)) //
					    .findFirst();

			    if (optionalQuestion.isPresent()) {
				selectedIds.add(optionalQuestion.get().idQuestion());
			    }
			}

		    } else if (tableModel.getColumnCount() == 9) { // without set name

			for (final var selectedRow : selectedRows) {

			    final var finalSelectedRow = table.convertRowIndexToModel(selectedRow);

			    final var questionnaireName = tableModel.getValueAt(finalSelectedRow, 0);
			    final var questionOrder = tableModel.getValueAt(finalSelectedRow, 2);

			    final var optionalQuestion = questions.stream() //
					    .filter(q -> Objects.equals(q.questionOrder(), questionOrder) //
							    && Objects.equals(q.questionnaireName(), questionnaireName)) //
					    .findFirst();

			    if (optionalQuestion.isPresent()) {
				selectedIds.add(optionalQuestion.get().idQuestion());
			    }
			}

		    } else { // full

			for (final var selectedRow : selectedRows) {

			    final var finalSelectedRow = table.convertRowIndexToModel(selectedRow);

			    final var setName = tableModel.getValueAt(finalSelectedRow, 0);
			    final var questionnaireName = tableModel.getValueAt(finalSelectedRow, 1);
			    final var questionOrder = tableModel.getValueAt(finalSelectedRow, 3);

			    final var optionalQuestion = questions.stream() //
					    .filter(q -> Objects.equals(q.questionOrder(), questionOrder) //
							    && Objects.equals(q.questionnaireName(), questionnaireName) //
							    && Objects.equals(q.questionnaireSetName(), setName)) //
					    .findFirst();

			    if (optionalQuestion.isPresent()) {
				selectedIds.add(optionalQuestion.get().idQuestion());
			    }
			}
		    }

		    final var questionsFromDb = questionnaireService.findByIds(selectedIds).toList();
		    view.setVisible(false);
		    
		    final var examName = examService.getNextExamDynamicNameBy(selectedCertification, selectedQuestionnaireSet, selectedQuestionnaire);
		    examController.show(examName, selectedCertification.getMinScorePercent(), questionsFromDb, view);
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
	view.trData.getSelectionModel().setSelectionMode(SINGLE_TREE_SELECTION);
    }

    private void loadExamData() {

	exams.clear();

	if (ObjectUtils.allNotNull(selectedCertification, selectedQuestionnaireSet, selectedQuestionnaire)) {

	    exams.addAll(examService.findExamBy(selectedCertification, selectedQuestionnaireSet, selectedQuestionnaire).toList());

	} else if (ObjectUtils.allNotNull(selectedCertification, selectedQuestionnaireSet)) {

	    exams.addAll(examService.findExamBy(selectedCertification, selectedQuestionnaireSet).toList());

	} else if (ObjectUtils.allNotNull(selectedCertification)) {

	    exams.addAll(examService.findExamBy(selectedCertification).toList());
	}

	selectedExam = null;

	final var tableFields = List.of( //
			fieldOf("id", NUMBER_TABLE_CELL_RENDERER), //
			fieldOf("name", true), //
			fieldOf("start", DATE_TIME_TABLE_CELL_RENDERER), // 
			fieldOf("finish", DATE_TIME_TABLE_CELL_RENDERER), // 
			fieldOf("status", ENUM_TABLE_CELL_RENDERER), //
			fieldOf("type", ENUM_TABLE_CELL_RENDERER), //
			fieldOf("shuffleQuestions", "Shuffled"), //
			fieldOf("result", ENUM_TABLE_CELL_RENDERER)//
	);

	alignTableModel(view.examTable, Exam.class, exams, tableFields);
	view.examTable.getSelectionModel().setSelectionMode(SINGLE_SELECTION);
    }

    private void loadQuestionData() {

	questions.clear();

	final List<TableModelField> tableFields;
	
	final var fieldOfValue = fieldOf("value", true);

	final var fieldOfQuestionOrder = fieldOf("questionOrder", LABEL_TABLE_ORDER, ORDER_TABLE_CELL_RENDERER);
	final var fieldOfQtyCorrect = fieldOf("qtyCorrect", "Correct", NUMBER_TABLE_CELL_RENDERER);
	final var fieldOfQtyIncorrect = fieldOf("qtyIncorrect", "Incorrect", NUMBER_TABLE_CELL_RENDERER);
	final var fieldOfQtyMarked = fieldOf("qtyMarked", "Marked", NUMBER_TABLE_CELL_RENDERER);

	final var fieldOfQtyTotal = fieldOf("qtyTotal", "Attempts", NUMBER_TABLE_CELL_RENDERER);
	final var fieldOfPercCorrect = fieldOf("percCorrect", "% Correct", PERCENT_TABLE_CELL_RENDERER);
	final var fieldOfPercIncorrect = fieldOf("percIncorrect", "% Incorrect", PERCENT_TABLE_CELL_RENDERER);

	//final var fieldOfId = fieldOf("idQuestion");
	
	if (ObjectUtils.allNotNull(selectedCertification, selectedQuestionnaireSet, selectedQuestionnaire)) {

	    tableFields = List.of( //
			    fieldOfValue, //
			    fieldOfQuestionOrder, //
			    fieldOfQtyMarked, // 
			    fieldOfQtyCorrect, //
			    fieldOfQtyIncorrect, //
			    fieldOfQtyTotal, //
			    fieldOfPercCorrect, //
			    fieldOfPercIncorrect
	    );

	    questions.addAll(questionnaireService.findByCertificationAndQuestionnaireSetAndQuestionnaire(selectedCertification, selectedQuestionnaireSet, selectedQuestionnaire).toList());

	} else {

	    final var fieldOfQuestionnaireName = fieldOf("questionnaireName", "Questionnaire");

	    if (ObjectUtils.allNotNull(selectedCertification, selectedQuestionnaireSet)) {

		tableFields = List.of( //
				fieldOfQuestionnaireName, //
				fieldOfValue, //
				fieldOfQuestionOrder, //
				fieldOfQtyMarked, fieldOfQtyCorrect, //
				fieldOfQtyIncorrect, //
				fieldOfQtyTotal, //
				fieldOfPercCorrect, //
				fieldOfPercIncorrect
		);

		questions.addAll(questionnaireService.findByCertificationAndQuestionnaireSet(selectedCertification, selectedQuestionnaireSet).toList());

	    } else if (ObjectUtils.allNotNull(selectedCertification)) {
		tableFields = List.of( //
				fieldOf("questionnaireSetName", "Set"), //
				fieldOfQuestionnaireName, //
				fieldOfValue, //
				fieldOfQuestionOrder, //
				fieldOfQtyMarked, //
				fieldOfQtyCorrect, //
				fieldOfQtyIncorrect, //
				fieldOfQtyTotal, //
				fieldOfPercCorrect, //
				fieldOfPercIncorrect
		);

		questions.addAll(questionnaireService.findByCertification(selectedCertification).toList());
	    } else {
		tableFields = List.of();
	    }
	}

	view.questionsTable.getSelectedRows();

	alignTableModel(view.questionsTable, QuestionDTO.class, questions, tableFields);

	view.questionsTable.getSelectionModel().setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
    }
    
    public void statisticData() {
	
	view.lblStatistic.setText(EMPTY);
	
	final var qtExams = exams.stream().count();
	final var qtExamsPassed = exams.stream().filter(Exam::isPassed).count();
	final var qtExamsFailed = exams.stream().filter(Exam::isFailed).count();
	final var qtExamsUndefined = exams.stream().filter(Exam::isUndefined).count();
	
	final var qtQuestions = questions.stream().count();
	final var qtQuestionsReady = questions.stream().filter(QuestionDTO::ready).count();
	final var qtQuestionsUnready = qtQuestions - qtQuestionsReady; 
	
	final var qtQuestionsAttemps = questions.stream().filter(question -> question.qtyTotal() >= QUESTION_MIN_ATTEMPT).count();
	final var qtQuestionsNonAttemps = questions.stream().filter(question -> question.qtyTotal() < QUESTION_MIN_ATTEMPT).count();
	
	final var msgLayout = """
			<html>
			The selection has {0} exam(s), and of them, {1} exam(s) has passed. <br />
			There is(are) {2} failed exam(s), and {3} is(are) undefined. <br />
			<br />
			The selection has {4} question(s) with a minimum of attempts ({5} attempts).<br />
			And {6} question(s) without a minimum of attempts. <br />
			<br />
			The selection has {7} question(s) and {8} is(are) ready (score >= 90%). <br />
			And {9} is(are) unready (score &lt; 90%). <br />
			</html>
			""";
	
	final var msg = MessageFormat.format( //
			msgLayout, //
			qtExams,
			qtExamsPassed,
			qtExamsFailed,
			qtExamsUndefined,
			qtQuestionsAttemps,
			QUESTION_MIN_ATTEMPT,
			qtQuestionsNonAttemps,
			qtQuestions,
			qtQuestionsReady,
			qtQuestionsUnready
	);
	
	view.lblStatistic.setText(msg);
	
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

	final var questionnaireSetFolders = FileUtil.readFolders(questionnairesSetPath);
	if (questionnaireSetFolders.isEmpty()) {
	    throw new IllegalArgumentException("The 'Questionnaires' does not have any folders, questionnaires set!");
	}
	
	final var certification = loadFromFileService.loadCertification(certificationPath);

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
