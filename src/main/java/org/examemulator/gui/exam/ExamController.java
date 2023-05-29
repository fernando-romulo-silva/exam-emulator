package org.examemulator.gui.exam;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.examemulator.domain.exam.QuestionType.DISCRETE_MULTIPLE_CHOICE;
import static org.examemulator.domain.exam.QuestionType.DISCRETE_SINGLE_CHOICE;
import static org.examemulator.gui.GuiUtil.MILLISECOND;
import static org.examemulator.gui.GuiUtil.createDiscreteOptions;
import static org.examemulator.gui.GuiUtil.createIndiscreteOptions;
import static org.examemulator.gui.GuiUtil.createScrollTextToShow;
import static org.examemulator.gui.GuiUtil.createTimerAction;
import static org.examemulator.gui.GuiUtil.extractedOptions;
import static org.examemulator.util.ControllerUtil.hasNextQuestion;
import static org.examemulator.util.ControllerUtil.hasPreviousQuestion;
import static org.examemulator.util.ControllerUtil.nextQuestion;
import static org.examemulator.util.ControllerUtil.previousQuestion;
import static org.examemulator.util.FileUtil.extractedExamName;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamStatus;
import org.examemulator.domain.exam.Option;
import org.examemulator.domain.exam.Question;
import org.examemulator.domain.exam.QuestionType;
import org.examemulator.gui.statitics.StatiticsController;
import org.examemulator.service.ExamService;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ExamController {

    private final List<QuestionType> discreteList = List.of(DISCRETE_MULTIPLE_CHOICE, DISCRETE_SINGLE_CHOICE);

    private final ExamView view;

    private final ExamService service;

    private final Logger logger;
    
    private final StatiticsController statiticsController;

    private Exam exam;

    private Question selectedQuestion;

    private String currentFolder;

    private Timer timer;

    @Inject
    ExamController(
		    final ExamGui gui, 
		    final ExamService service, 
		    final StatiticsController statiticsController,
		    final Logger logger) {
	super();
	this.view = gui.getView();
	this.service = service;
	this.statiticsController = statiticsController;
	this.logger = logger;
    }

    @PostConstruct
    void init() {
	creatButtonActions();

	view.btnStart.setEnabled(false);
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

    // =================================================================================================================

    private void creatButtonActions() {

	view.btnStart.addActionListener(event -> {

	    final var discretPercent = BigDecimal.valueOf((Integer) view.textDiscrete.getValue());

	    final var mimScore = BigDecimal.valueOf((Integer) view.textMinScore.getValue());

	    final var practiceMode = equalsIgnoreCase("Practice", (String) view.cbMode.getSelectedItem());
	    
	    final var shuffleQuestions = view.chckbxShuffleQuestions.isSelected();
	    final var shuffleOptions = view.chckbxShuffleOptions.isSelected();
	    
	    exam = service.createExam(currentFolder, practiceMode, discretPercent, mimScore, shuffleQuestions, shuffleOptions);

	    view.btnStart.setEnabled(false);
	    view.btnStatistics.setEnabled(false);
	    view.btnPrevious.setEnabled(false);

	    view.textMinScore.setEnabled(false);
	    view.textDiscrete.setEnabled(false);
	    view.cbMode.setEnabled(false);
	    view.spinnerTimeDuration.setEnabled(false);
	    view.chckbxShuffleQuestions.setEnabled(false);
	    view.chckbxShuffleOptions.setEnabled(false);

	    view.btnPauseProceed.setEnabled(true);
	    view.chckbxMark.setEnabled(true);
	    view.btnFinish.setEnabled(true);
	    view.btnNext.setEnabled(true);
	    view.btnCheckAnswer.setEnabled(true);

	    final var duration = (Integer) view.spinnerTimeDuration.getValue();

	    if (Objects.nonNull(timer)) {
		timer.stop();
	    }

	    timer = null;

	    if (exam.isPracticeMode()) {
		view.btnCheckAnswer.setVisible(true);
		view.lblDuration.setVisible(false);
		view.spinnerTimeDuration.setVisible(false);
	    } else {
		view.btnCheckAnswer.setVisible(false);
		timer = new Timer(MILLISECOND, createTimerAction(duration, view.lblClock, () -> view.btnFinish.doClick()));
		timer.start();
	    }

	    exam.begin();

	    selectedQuestion = exam.getQuestions().get(0);

	    loadNumbersPanel();

	    loadPanelQuestion();
	});

	view.btnPauseProceed.addActionListener(event -> {

	    
	    if (exam.getStatus().equals(ExamStatus.RUNNING)) {
		
		exam.pause();
		view.btnPauseProceed.setText("Proceed");
		
		view.btnStart.setEnabled(false);
		view.btnStatistics.setEnabled(false);
		view.btnPrevious.setEnabled(false);

		view.lblClock.setText(StringUtils.EMPTY);

		view.btnPrevious.setEnabled(false);
		view.chckbxMark.setEnabled(false);
		view.btnPauseProceed.setEnabled(true);
		view.chckbxShuffleQuestions.setEnabled(false);
		view.chckbxShuffleOptions.setEnabled(false);

		view.btnFinish.setEnabled(false);
		view.btnNext.setEnabled(false);
		view.btnCheckAnswer.setEnabled(false);

		if (Objects.nonNull(timer)) {
		    timer.stop();
		}

	    } else if (exam.getStatus().equals(ExamStatus.PAUSED)) {
		
		view.btnStart.setEnabled(false);
		view.btnStatistics.setEnabled(false);
		view.btnPrevious.setEnabled(false);

		view.textMinScore.setEnabled(false);
		view.textDiscrete.setEnabled(false);
		view.cbMode.setEnabled(false);
		view.spinnerTimeDuration.setEnabled(false);
		view.chckbxShuffleQuestions.setEnabled(false);
		view.chckbxShuffleOptions.setEnabled(false);

		view.btnPauseProceed.setEnabled(true);
		view.chckbxMark.setEnabled(true);
		view.btnFinish.setEnabled(true);
		view.btnNext.setEnabled(true);
		view.btnCheckAnswer.setEnabled(true);		
		
		
		exam.proceed();
		view.btnPauseProceed.setText("Pause");

		if (Objects.nonNull(timer)) {
//		    timer = new Timer(MILLISECOND, createTimerAction(duration, view.lblClock, () -> view.btnFinish.doClick()));
//		    timer.set

		    
		    timer.restart();
		}
	    }
	    
	});
	
	view.btnFinish.addActionListener(event -> {

	    exam.finish();

	    if (Objects.nonNull(timer)) {
		timer.stop();
	    }

	    view.lblClock.setText(StringUtils.EMPTY);

	    view.btnCheckAnswer.setEnabled(false);
	    view.btnStart.setEnabled(false);
	    view.btnFinish.setEnabled(false);
	    view.btnPrevious.setEnabled(false);
	    view.btnNext.setEnabled(false);
	    view.chckbxMark.setEnabled(false);
	    view.btnPauseProceed.setEnabled(false);
	    view.chckbxShuffleQuestions.setEnabled(false);
	    view.chckbxShuffleOptions.setEnabled(false);

	    view.btnStatistics.setEnabled(true);

	    view.questionInternPanel.removeAll();
	    view.questionInternPanel.revalidate();
	    view.questionInternPanel.repaint();
	});

	view.btnStatistics.addActionListener(event -> statiticsController.show(exam, view));

	view.btnNext.addActionListener(event -> {
	    updateQuestionLabel();
	    final var nextQuestionOptional = nextQuestion(exam.getQuestions(), selectedQuestion);
	    if (nextQuestionOptional.isPresent()) {
		selectedQuestion = nextQuestionOptional.get();
	    }
	    loadPanelQuestion();
	});

	view.btnPrevious.addActionListener(event -> {
	    updateQuestionLabel();
	    final var previousQuestionOptional = previousQuestion(exam.getQuestions(), selectedQuestion);
	    if (previousQuestionOptional.isPresent()) {
		selectedQuestion = previousQuestionOptional.get();
	    }
	    loadPanelQuestion();
	});

	view.mntmNew.addActionListener(event -> {

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

		view.examPanel.setBorder(BorderFactory.createTitledBorder(extractedExamName(currentFolder)));

		view.btnStart.setEnabled(true);
		view.textMinScore.setEnabled(true);
		view.textDiscrete.setEnabled(true);
		view.cbMode.setEnabled(true);
		view.spinnerTimeDuration.setEnabled(true);
		view.chckbxShuffleQuestions.setEnabled(true);
		view.chckbxShuffleOptions.setEnabled(true);

		view.btnCheckAnswer.setEnabled(false);
		view.btnFinish.setEnabled(false);
		view.btnNext.setEnabled(false);
		view.btnPrevious.setEnabled(false);
		view.btnStatistics.setEnabled(false);
		view.btnPauseProceed.setEnabled(false);

		view.revalidate();
		view.repaint();
		view.setVisible(true);
	    }
	});

	view.btnCheckAnswer.addActionListener(event -> {

	    final var dialogTitle = "Question " + leftPad(selectedQuestion.getOrder().toString(), 2, '0') + "'s Answer";
	    final var answerDialog = new JDialog(view, dialogTitle, Dialog.ModalityType.DOCUMENT_MODAL);
	    answerDialog.setBounds(132, 132, 300, 200);
	    answerDialog.setSize(600, 500);
	    answerDialog.setLocationRelativeTo(view);

	    final var dialogContainer = answerDialog.getContentPane();
	    dialogContainer.setLayout(new BorderLayout());

	    final var correctOptions = "Correct Answer(s): " + extractedOptions(selectedQuestion.getCorrectOptions());

	    final String vs;
	    if (discreteList.contains(selectedQuestion.getType())) {
		vs = "\n\n" + selectedQuestion.getOptions().stream() //
				.filter(Option::isCorrect) //
				.map(Option::getText) //
				.collect(Collectors.joining("\n"));
	    } else {
		vs = "";
	    }

	    final var explanation = "\n" + selectedQuestion.getExplanation();

	    final var text = correctOptions + vs + explanation;

	    dialogContainer.add(createScrollTextToShow(text), CENTER);

	    final var okButton = new JButton("Ok");
	    okButton.addActionListener(okEvent -> answerDialog.setVisible(false));

	    final var panel = new JPanel(new FlowLayout());
	    panel.add(okButton);

	    dialogContainer.add(panel, SOUTH);
	    answerDialog.setVisible(true);
	});

	view.chckbxMark.addItemListener(event -> {
	    final var checkEvent = (JCheckBox) event.getSource();
	    selectedQuestion.mark(checkEvent.isSelected());
	});
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

    private void updateQuestionLabel() {
	final var component = Stream.of(view.pQuestions.getComponents()) //
			.filter(comp -> Objects.equals(comp.getName(), selectedQuestion.getOrder().toString())) //
			.findFirst();

	if (component.isPresent() && component.get() instanceof JLabel label) {

	    if (selectedQuestion.isMarked()) {
		label.setForeground(Color.ORANGE);
	    } else if (selectedQuestion.isAnswered()) {
		label.setForeground(Color.BLUE);
	    } else {
		label.setForeground(Color.BLACK);
	    }
	}
    }

    private void loadPanelQuestion() {
	
	view.questionInternPanel.removeAll();

	final var panelQuestionPanel = new JPanel();
	panelQuestionPanel.setLayout(new BoxLayout(panelQuestionPanel, Y_AXIS));
	panelQuestionPanel.setBorder(BorderFactory.createTitledBorder("Question " + leftPad(selectedQuestion.getOrder().toString(), 2, '0')));
	panelQuestionPanel.add(createScrollTextToShow("\n" + selectedQuestion.getValue() + "\n"));
	panelQuestionPanel.revalidate();
	panelQuestionPanel.repaint();

	final var groupOptionsQuestionPanel = new JPanel(new BorderLayout());

	if (selectedQuestion.isAnswered() && discreteList.contains(selectedQuestion.getType())) {
	    groupOptionsQuestionPanel.add(new JLabel("You alrealdy answered it"), NORTH);
	} else if (discreteList.contains(selectedQuestion.getType())) {
	    groupOptionsQuestionPanel.add(createDiscreteOptions(selectedQuestion), CENTER);
	} else {
	    groupOptionsQuestionPanel.add(createIndiscreteOptions(selectedQuestion), CENTER);
	}

	view.chckbxMark.setSelected(selectedQuestion.isMarked());

	view.btnPrevious.setEnabled(hasPreviousQuestion(exam.getQuestions(), selectedQuestion));
	view.btnNext.setEnabled(hasNextQuestion(exam.getQuestions(), selectedQuestion));

	view.questionInternPanel.add(panelQuestionPanel);
	view.questionInternPanel.add(groupOptionsQuestionPanel);

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

		if (event.getButton() == MouseEvent.BUTTON1 && Objects.nonNull(exam) && exam.getStatus() == ExamStatus.RUNNING) {
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
}
