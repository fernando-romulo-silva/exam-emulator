package org.examemulator.gui.preexame;

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
import static org.examemulator.util.FileUtil.extractedExamName;
import static org.examemulator.util.FileUtil.getQtyFiles;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.examemulator.domain.pretest.PreExam;
import org.examemulator.domain.pretest.PreQuestion;
import org.examemulator.gui.components.RangeSlider;
import org.examemulator.service.PretestService;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PreExameController {

    private final PreExameView view;

    private final PretestService service;

    private final Logger logger;

    private final List<PreQuestion> toExamQuestions = new ArrayList<>();

    private PreExam exam;

    private PreQuestion selectedQuestion;

    private String currentFolder;

    @Inject
    PreExameController(final PreExameGui gui, final PretestService service, final Logger logger) {
	super();
	this.view = gui.getView();
	this.service = service;
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

    private void updateQuestionLabel() {
	final var component = Stream.of(view.pQuestions.getComponents()) //
			.filter(comp -> Objects.equals(comp.getName(), selectedQuestion.getOrder().toString())) //
			.findFirst();

	if (component.isPresent() && component.get() instanceof JLabel label) {

//	    if (selectedQuestion.isMarked()) {
//		label.setForeground(Color.ORANGE);
//	    } else if (selectedQuestion.isAnswered()) {
//		label.setForeground(Color.BLUE);
//	    } else {
//		label.setForeground(Color.BLACK);
//	    }
	}
    }

    private void createButtonActions() {

	view.rangeQuestions.addChangeListener(event -> {
	    final var slider = (RangeSlider) event.getSource();
	    view.lblRangeLow.setText(String.valueOf(slider.getValue()));
	    view.lblUpper.setText(String.valueOf(slider.getUpperValue()));
	});

	view.btnNext.addActionListener(event -> {
	    final var nextQuestionOptional = nextQuestion(exam.getQuestions(), selectedQuestion);
	    if (nextQuestionOptional.isPresent()) {
		selectedQuestion = nextQuestionOptional.get();
	    }
	    loadPanelQuestion();
	});

	view.btnPrevious.addActionListener(event -> {
	    final var previousQuestionOptional = previousQuestion(exam.getQuestions(), selectedQuestion);
	    if (previousQuestionOptional.isPresent()) {
		selectedQuestion = previousQuestionOptional.get();
	    }
	    loadPanelQuestion();
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

		exam = service.createExam(currentFolder);

		view.examPanel.setBorder(BorderFactory.createTitledBorder(extractedExamName(currentFolder)));

		view.btnNewExam.setEnabled(true);
		view.btnSave.setEnabled(true);
		view.btnNewExam.setEnabled(true);

//		view.textFieldName.setEnabled(true);
//		view.textQuantity.setEnabled(true);

		view.rangeQuestions.setEnabled(true);
		view.rangeQuestions.setMinimum(1);
		view.rangeQuestions.setMaximum(getQtyFiles(currentFolder));
		view.rangeQuestions.setValue(1);
		view.rangeQuestions.setUpperValue(getQtyFiles(currentFolder));

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

	final var panelQuestionPanel = new JPanel();
	panelQuestionPanel.setLayout(new BorderLayout());
	panelQuestionPanel.setBorder(createTitledBorder("Question " + leftPad(selectedQuestion.getOrder().toString(), 2, '0')));
	panelQuestionPanel.add(createScrollHtmlTextToShow(txt), CENTER);
	panelQuestionPanel.revalidate();
	panelQuestionPanel.repaint();

	view.btnPrevious.setEnabled(hasPreviousQuestion(exam.getQuestions(), selectedQuestion));
	view.btnNext.setEnabled(hasNextQuestion(exam.getQuestions(), selectedQuestion));

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

		if (event.getClickCount() == 1 && event.getButton() == MouseEvent.BUTTON1 && Objects.nonNull(exam)) {
		    selectQuestion(Integer.valueOf(text));
		    loadPanelQuestion();
		}
	    }
	};

	for (final var question : exam.getQuestions()) {

	    final var label = new JLabel(leftPad(question.getOrder().toString(), 2, '0'));
	    label.setName(question.getOrder().toString());
	    view.pQuestions.add(label);
	    label.addMouseListener(questionLabelListener);
	}
    }
    
    private void selectFirstQuestion() {
	
	if (exam.getQuestions().isEmpty()) {
	    return;
	}
	
	selectedQuestion = exam.getQuestions().get(0);
	view.btnPrevious.setEnabled(hasPreviousQuestion(exam.getQuestions(), selectedQuestion));
	view.btnNext.setEnabled(hasNextQuestion(exam.getQuestions(), selectedQuestion));
    }
    
    private void selectQuestion(int order) {
	
	final var questionOptional = exam.getQuestions() //
			.stream() //
			.filter(question -> Objects.equals(question.getOrder(), order)) //
			.findFirst();
	
	if (questionOptional.isEmpty()) {
	    return;
	}
	
	selectedQuestion = questionOptional.get();
	
	view.btnPrevious.setEnabled(hasPreviousQuestion(exam.getQuestions(), selectedQuestion));
	view.btnNext.setEnabled(hasNextQuestion(exam.getQuestions(), selectedQuestion));
    }
}