package org.examemulator.gui.questionnaire;

import static java.awt.BorderLayout.CENTER;
import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.util.stream.Collectors.joining;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.examemulator.gui.GuiUtil.TAG_BR;
import static org.examemulator.gui.GuiUtil.TAG_BR_BR;
import static org.examemulator.gui.GuiUtil.TAG_CLOSE_B;
import static org.examemulator.gui.GuiUtil.TAG_OPEN_B;
import static org.examemulator.gui.GuiUtil.convertTextToHtml;
import static org.examemulator.gui.GuiUtil.createScrollHtmlTextToShow;
import static org.examemulator.gui.GuiUtil.extractedOptions;
import static org.examemulator.util.ControllerUtil.hasNextQuestion;
import static org.examemulator.util.ControllerUtil.hasPreviousQuestion;
import static org.examemulator.util.ControllerUtil.nextQuestion;
import static org.examemulator.util.ControllerUtil.previousQuestion;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.gui.exam.ExamController;
import org.examemulator.gui.questionnaire.QuestionnaireView.PreExameGui;
import org.examemulator.service.LoadFromFileService;
import org.examemulator.service.QuestionnaireService;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuestionnaireController {

    private final QuestionnaireView view;

    private final QuestionnaireService service;

    private final LoadFromFileService loadFromFileService;

    private final ExamController examController;

    private final Logger logger;

    private final List<Question> toExamQuestions = new ArrayList<>();

    private Questionnaire questionnaire;

    private Question selectedQuestion;

    private String currentFolder;

    @Inject
    QuestionnaireController( //
		    final PreExameGui gui, //
		    final QuestionnaireService service, //
		    final LoadFromFileService loadFromFileService, //
		    final ExamController examController, //
		    final Logger logger) {
	super();
	this.view = gui.getView();
	this.service = service;
	this.loadFromFileService = loadFromFileService;
	this.examController = examController;
	this.logger = logger;
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
	view.questionInternPanel.removeAll();
	view.questionInternPanel.revalidate();
	view.questionInternPanel.repaint();

	view.pQuestions.removeAll();
	view.pQuestions.revalidate();
	view.pQuestions.repaint();
    }

    private void initActions() {

	view.btnNext.addActionListener(event -> {

	    final var nextQuestionOptional = nextQuestion(questionnaire.getQuestions(), selectedQuestion);
	    if (nextQuestionOptional.isPresent()) {
		selectedQuestion = nextQuestionOptional.get();
	    }
	    loadPanelQuestion();
	});

	view.btnPrevious.addActionListener(event -> {

	    final var previousQuestionOptional = previousQuestion(questionnaire.getQuestions(), selectedQuestion);
	    if (previousQuestionOptional.isPresent()) {
		selectedQuestion = previousQuestionOptional.get();
	    }
	    loadPanelQuestion();
	});

	view.btnLoad.addActionListener(event -> {

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

	    currentFolder = Objects.nonNull(chooser.getSelectedFile()) //
			    ? chooser.getSelectedFile().getAbsolutePath() //
			    : StringUtils.EMPTY;

	    if (isNotBlank(currentFolder)) {

//		questionnaire = loadFromFileService.loadQuestionnaire(currentFolder);
		toExamQuestions.addAll(questionnaire.getQuestions());

		view.contentPane.setBorder(createTitledBorder(questionnaire.getName()));

		view.rdbtnAll.setEnabled(true);
		view.rdbtnNone.setEnabled(true);
		
		view.btnNewExam.setEnabled(true);
		view.btnNewExam.setEnabled(true);
		view.textDescription.setText(questionnaire.getDescription());

		final var set = questionnaire.getSet();
		view.textSet.setText(set.getName());

		final var certification = questionnaire.getCertification();
		view.textCertification.setText(certification.getName());

		view.textOrder.setText(StringUtils.leftPad(questionnaire.getOrder().toString(), 2, "0"));

		view.btnNext.setEnabled(false);
		view.btnPrevious.setEnabled(false);

		view.revalidate();
		view.repaint();
		view.setVisible(true);

		loadNumbersPanel();
		selectFirstQuestion();
		loadPanelQuestion();
	    }
	});

	view.btnNewExam.addActionListener(event -> {
	    view.setVisible(false);

	    if (toExamQuestions.isEmpty()) {
		toExamQuestions.addAll(questionnaire.getQuestions());
	    }

	    examController.show(questionnaire.getName(), view, toExamQuestions);
	});
	
	view.rdbtnAll.addActionListener(event -> {
	    toExamQuestions.addAll(questionnaire.getQuestions());
	    loadNumbersPanel();
	});
	
	view.rdbtnNone.addActionListener(event -> {
	    toExamQuestions.clear();
	    loadNumbersPanel();
	});

    }

    private void loadPanelQuestion() {
	view.questionInternPanel.removeAll();

	final var question = TAG_BR.concat(TAG_OPEN_B).concat(convertTextToHtml(selectedQuestion.getValue())).concat(TAG_CLOSE_B);

	final var options = selectedQuestion.getOptions() //
			.stream() //
			.map(option -> option.getLetter().concat(") ").concat(TAG_BR).concat(convertTextToHtml(option.getValue()))) //
			.collect(joining(TAG_BR_BR));

	final var correctOptions = TAG_OPEN_B.concat("Correct Answer(s): ").concat(TAG_CLOSE_B).concat(extractedOptions(selectedQuestion.getCorrectOptions()));

	final var explanation = TAG_OPEN_B.concat("Explanation: ").concat(TAG_CLOSE_B).concat(TAG_BR_BR).concat(convertTextToHtml(selectedQuestion.getExplanation()));

	final var txt = question.concat(TAG_BR_BR) //
			.concat(options).concat(TAG_BR_BR) //
			.concat(correctOptions).concat(TAG_BR_BR) //
			.concat(explanation);

	final var optionalConcept = selectedQuestion.getConcept();
	final var conceptName = optionalConcept.isPresent() //
			? " (".concat(optionalConcept.get().getName()).concat(")") //
			: "";

	final var panelQuestionPanel = new JPanel();
	panelQuestionPanel.setLayout(new BorderLayout());
	panelQuestionPanel.setBorder(createTitledBorder("Question " + leftPad(selectedQuestion.getOrder().toString(), 2, '0').concat(conceptName)));
	panelQuestionPanel.add(createScrollHtmlTextToShow(txt), CENTER);
	panelQuestionPanel.revalidate();
	panelQuestionPanel.repaint();

	view.btnPrevious.setEnabled(hasPreviousQuestion(questionnaire.getQuestions(), selectedQuestion));
	view.btnNext.setEnabled(hasNextQuestion(questionnaire.getQuestions(), selectedQuestion));

	view.questionInternPanel.add(panelQuestionPanel);

	view.questionInternPanel.revalidate();
	view.questionInternPanel.repaint();
    }

    private void loadNumbersPanel() {
	view.pQuestions.removeAll();
	view.pQuestions.revalidate();
	view.pQuestions.repaint();

	final var questionLabelListener = new QuestionLabelListener();

	for (final var question : questionnaire.getQuestions()) {

	    final var label = new JLabel(leftPad(question.getOrder().toString(), 2, '0'));
	    
	    if (toExamQuestions.contains(question)) {
		label.setForeground(BLUE);
	    } else {
		label.setForeground(BLACK);
	    }
	    
	    label.setName(question.getOrder().toString());
	    view.pQuestions.add(label);
	    label.addMouseListener(questionLabelListener);
	}
    }

    private void selectFirstQuestion() {

	if (questionnaire.getQuestions().isEmpty()) {
	    return;
	}

	selectedQuestion = questionnaire.getQuestions().get(0);
	view.btnPrevious.setEnabled(hasPreviousQuestion(questionnaire.getQuestions(), selectedQuestion));
	view.btnNext.setEnabled(hasNextQuestion(questionnaire.getQuestions(), selectedQuestion));
    }

    private class QuestionLabelListener extends MouseAdapter {

	@Override
	public void mouseClicked(final MouseEvent event) {
	    final var label = (JLabel) event.getSource();
	    final var text = label.getText();

	    if (event.getClickCount() == 1 && event.getButton() == MouseEvent.BUTTON1 && Objects.nonNull(questionnaire)) {

		selectQuestion(Integer.valueOf(text));
		loadPanelQuestion();

	    } else if (event.getClickCount() == 2 && !event.isConsumed() && event.getButton() == MouseEvent.BUTTON1 && Objects.nonNull(questionnaire)) {

		selectQuestion(Integer.valueOf(text));

		if (toExamQuestions.contains(selectedQuestion)) {
		    toExamQuestions.remove(selectedQuestion);
		    label.setForeground(BLACK);
		} else {
		    toExamQuestions.add(selectedQuestion);
		    label.setForeground(BLUE);
		}

		label.repaint();
	    }

	    if (toExamQuestions.isEmpty()) {
		view.rdbtnNone.setSelected(true);
	    } else {

		final var questionnaireQuestionSize = Objects.nonNull(questionnaire) ? questionnaire.getQuestions().size() : 0;

		if (toExamQuestions.size() < questionnaireQuestionSize) {
		    view.rdbtnAny.setSelected(true);
		} else {
		    view.rdbtnAll.setSelected(true);
		}
	    }
	}

	private void selectQuestion(int order) {

	    final var questionOptional = questionnaire.getQuestions() //
			    .stream() //
			    .filter(question -> Objects.equals(question.getOrder(), order)) //
			    .findFirst();

	    if (questionOptional.isEmpty()) {
		return;
	    }

	    selectedQuestion = questionOptional.get();

	    view.btnPrevious.setEnabled(hasPreviousQuestion(questionnaire.getQuestions(), selectedQuestion));
	    view.btnNext.setEnabled(hasNextQuestion(questionnaire.getQuestions(), selectedQuestion));
	}
    }
}