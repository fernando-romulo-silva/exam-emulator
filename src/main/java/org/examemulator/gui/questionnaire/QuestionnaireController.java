package org.examemulator.gui.questionnaire;

import static java.awt.BorderLayout.CENTER;
import static java.util.stream.Collectors.joining;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
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

import org.examemulator.domain.questionnaire.Question;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.gui.components.RangeSlider;
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
	createButtonActions();
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

//    private void updateQuestionLabel() {
//	final var component = Stream.of(view.pQuestions.getComponents()) //
//			.filter(comp -> Objects.equals(comp.getName(), selectedQuestion.getOrder().toString())) //
//			.findFirst();
//
//	if (component.isPresent() && component.get() instanceof JLabel label) {
//
//	    if (selectedQuestion.isMarked()) {
//		label.setForeground(Color.ORANGE);
//	    } else if (selectedQuestion.isAnswered()) {
//		label.setForeground(Color.BLUE);
//	    } else {
//		label.setForeground(Color.BLACK);
//	    }
//	}
//    }

    private void createButtonActions() {

	view.rangeQuestions.addChangeListener(event -> {
	    final var slider = (RangeSlider) event.getSource();
	    view.lblRangeLow.setText(String.valueOf(slider.getValue()));
	    view.lblUpper.setText(String.valueOf(slider.getUpperValue()));
	});

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

	    currentFolder = chooser.getSelectedFile().getAbsolutePath();

	    if (Objects.nonNull(currentFolder)) {

		questionnaire = loadFromFileService.loadQuestionnaire(currentFolder);

		view.contentPane.setBorder(createTitledBorder(questionnaire.getName()));

		view.btnNewExam.setEnabled(true);
		view.btnNewExam.setEnabled(true);
		view.textDescription.setText(questionnaire.getDescription());
		
		final var set = questionnaire.getSet();
		view.textSet.setText(set.getName());
		
		final var certification = questionnaire.getCertification();
		view.textCertification.setText(certification.getName());

//		view.textFieldName.setEnabled(true);
//		view.textQuantity.setEnabled(true);

		view.rangeQuestions.setEnabled(true);
		view.rangeQuestions.setMinimum(1);
		view.rangeQuestions.setMaximum(questionnaire.getQuestions().size());
		view.rangeQuestions.setValue(1);
		view.rangeQuestions.setUpperValue(questionnaire.getQuestions().size());
		
		view.textQuantity.setValue(questionnaire.getQuestions().size());

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
	    
	    toExamQuestions.addAll(questionnaire.getQuestions());
	    
	    examController.show(questionnaire.getName(), view, toExamQuestions);
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

	final var questionLabelListener = new MouseAdapter() {

	    @Override
	    public void mouseClicked(final MouseEvent event) {
		final var labelEvent = (JLabel) event.getSource();
		final var text = labelEvent.getText();

		if (event.getClickCount() == 1 && event.getButton() == MouseEvent.BUTTON1 && Objects.nonNull(questionnaire)) {
		    selectQuestion(Integer.valueOf(text));
		    loadPanelQuestion();
		}
	    }
	};

	for (final var question : questionnaire.getQuestions()) {

	    final var label = new JLabel(leftPad(question.getOrder().toString(), 2, '0'));
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