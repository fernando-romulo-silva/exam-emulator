package org.examemulator.gui.exam;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.ORANGE;
import static java.awt.Dialog.ModalityType.DOCUMENT_MODAL;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.BoxLayout.Y_AXIS;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.LF;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.examemulator.domain.exam.ExamStatus.RUNNING;
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
import static org.examemulator.util.domain.DomainUtil.DISCRET_LIST;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamOption;
import org.examemulator.domain.exam.ExamQuestion;
import org.examemulator.domain.exam.ExamStatus;
import org.examemulator.domain.inquiry.InquiryInterface;
import org.examemulator.gui.exam.ExamView.ExamGui;
import org.examemulator.gui.statitics.StatiticsController;
import org.examemulator.service.ExamService;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ExamController {

    private final ExamView view;

    private final ExamService service;

    private final Logger logger;

    private final StatiticsController statiticsController;

    private List<? extends InquiryInterface> availableQuestions;

    private String name;

    private Exam exam;

    private ExamQuestion selectedQuestion;

    private Timer timer;

    @Inject
    ExamController(//
		    final ExamGui gui, //
		    final ExamService service, //
		    final StatiticsController statiticsController, //
		    final Logger logger) {
	super();
	this.view = gui.getView();
	this.service = service;
	this.statiticsController = statiticsController;
	this.logger = logger;
    }

    @PostConstruct
    void init() {
	initView();
	creatButtonActions();
    }

    public void show(final String name, final Component lastView, final List<? extends InquiryInterface> availableQuestions) {

	this.availableQuestions = availableQuestions;
	this.name = name;

	view.contentPane.setBorder(createTitledBorder(name));

	for (var i = 1; i <= availableQuestions.size(); i++) {
	    final var value = Integer.toString(i);
	    final var label = new JLabel(leftPad(value, 2, '0'));
	    label.setName(value);
	    view.pQuestions.add(label);
	}

	SwingUtilities.invokeLater(() -> {

	    logger.info("starting swing-event: {}", name);

	    view.setLocationRelativeTo(lastView);

	    view.revalidate();
	    view.repaint();
	    view.setVisible(true);

	    logger.info("finished swing-event: {}", name);
	});
    }

    private void initView() {

	view.questionInternPanel.removeAll();
	view.questionInternPanel.revalidate();
	view.questionInternPanel.repaint();

	view.pQuestions.removeAll();
	view.pQuestions.revalidate();
	view.pQuestions.repaint();

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

    // =================================================================================================================

    private void creatButtonActions() {

	view.btnStart.addActionListener(event -> {

	    final var discretPercent = BigDecimal.valueOf((Integer) view.textDiscrete.getValue());

	    final var minScorePercent = BigDecimal.valueOf((Integer) view.textMinScore.getValue());

	    final var practiceMode = equalsIgnoreCase("Practice", (String) view.cbMode.getSelectedItem());

	    final var shuffleQuestions = view.chckbxShuffleQuestions.isSelected();
	    final var shuffleOptions = view.chckbxShuffleOptions.isSelected();

	    exam = new Exam.Builder().with($ -> {
		$.name = this.name;
		$.practiceMode = practiceMode;
		$.discretPercent = discretPercent;
		$.minScorePercent = minScorePercent;
		$.randomOrder = false;
		$.shuffleQuestions = shuffleQuestions;
		$.shuffleOptions = shuffleOptions;
		$.questions = availableQuestions;
	    }).build();

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

	    if (nonNull(timer)) {
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

		if (nonNull(timer)) {
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

		if (nonNull(timer)) {
//		    timer = new Timer(MILLISECOND, createTimerAction(duration, view.lblClock, () -> view.btnFinish.doClick()));
//		    timer.set

		    timer.restart();
		}
	    }
	});

	view.btnFinish.addActionListener(event -> {

	    exam.finish();

	    if (nonNull(timer)) {
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

	view.btnStatistics.addActionListener(event -> {
	    view.setVisible(false);
	    statiticsController.show(exam, view);
	});

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

	view.btnCheckAnswer.addActionListener(event -> {

	    final var dialogTitle = "Question ".concat(leftPad(selectedQuestion.getOrder().toString(), 2, '0')).concat("'s Answer");
	    final var answerDialog = new JDialog(view, dialogTitle, DOCUMENT_MODAL);
	    answerDialog.setBounds(132, 132, 300, 200);
	    answerDialog.setSize(600, 500);
	    answerDialog.setLocationRelativeTo(view);

	    final var dialogContainer = answerDialog.getContentPane();
	    dialogContainer.setLayout(new BorderLayout());

	    final var correctOptions = "Correct Answer(s): ".concat(extractedOptions(selectedQuestion.getCorrectOptions()));

	    final String vs;
	    if (DISCRET_LIST.contains(selectedQuestion.getType())) {
		vs = LF.concat(LF).concat(selectedQuestion.getOptions().stream() //
				.filter(ExamOption::isCorrect) //
				.map(ExamOption::getValue) //
				.collect(joining(LF.concat(LF))));
	    } else {
		vs = EMPTY;
	    }

	    final var explanation = LF.concat(LF).concat("Explanation:").concat(LF).concat(selectedQuestion.getExplanation());

	    final var text = correctOptions.concat(vs).concat(explanation);

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
		label.setForeground(ORANGE);
	    } else if (selectedQuestion.isAnswered()) {
		label.setForeground(BLUE);
	    } else {
		label.setForeground(BLACK);
	    }
	}
    }

    private void loadPanelQuestion() {

	view.questionInternPanel.removeAll();

	final var optionalConcept = selectedQuestion.getQuestion().getConcept();
	final var conceptName = optionalConcept.isPresent() //
			? " (".concat(optionalConcept.get().getName()).concat(")") //
			: "";

	final var questionText = LF.concat(selectedQuestion.getValue()).concat(LF);
	
	final var panelQuestionPanel = new JPanel();
	panelQuestionPanel.setLayout(new BoxLayout(panelQuestionPanel, Y_AXIS));
	panelQuestionPanel.setBorder(createTitledBorder("Question ".concat(leftPad(selectedQuestion.getOrder().toString(), 2, '0').concat(conceptName))));
	panelQuestionPanel.add(createScrollTextToShow(questionText));
	panelQuestionPanel.revalidate();
	panelQuestionPanel.repaint();

	final var groupOptionsQuestionPanel = new JPanel(new BorderLayout());

	if (selectedQuestion.isAnswered() && DISCRET_LIST.contains(selectedQuestion.getType())) {
	    groupOptionsQuestionPanel.add(new JLabel("You alrealdy answered it"), NORTH);
	} else if (DISCRET_LIST.contains(selectedQuestion.getType())) {
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

	final MouseAdapter questionLabelListener = new MouseAdapter() {

	    @Override
	    public void mouseClicked(final MouseEvent event) {
		final var labelEvent = (JLabel) event.getSource();
		final var text = labelEvent.getText();

		if (event.getButton() == BUTTON1 && nonNull(exam) && exam.getStatus() == RUNNING) {
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
