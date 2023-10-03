package org.examemulator.gui.exam;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.GRAY;
import static java.awt.Dialog.ModalityType.DOCUMENT_MODAL;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.LF;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.examemulator.domain.exam.ExamStatus.PAUSED;
import static org.examemulator.domain.exam.ExamStatus.RUNNING;
import static org.examemulator.domain.exam.ExamType.EXAM;
import static org.examemulator.domain.exam.ExamType.PRACTICE;
import static org.examemulator.infra.util.domain.DomainUtil.DISCRET_LIST;
import static org.examemulator.infra.util.gui.ControllerUtil.hasNextQuestion;
import static org.examemulator.infra.util.gui.ControllerUtil.hasPreviousQuestion;
import static org.examemulator.infra.util.gui.ControllerUtil.nextQuestion;
import static org.examemulator.infra.util.gui.ControllerUtil.previousQuestion;
import static org.examemulator.infra.util.gui.GuiUtil.MILLISECOND;
import static org.examemulator.infra.util.gui.GuiUtil.createDiscreteOptions;
import static org.examemulator.infra.util.gui.GuiUtil.createIndiscreteOptions;
import static org.examemulator.infra.util.gui.GuiUtil.createScrollTextToShow;
import static org.examemulator.infra.util.gui.GuiUtil.createTimerAction;
import static org.examemulator.infra.util.gui.GuiUtil.extractedOptions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.StringUtils;
import org.examemulator.application.ExamService;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamOption;
import org.examemulator.domain.exam.ExamQuestion;
import org.examemulator.domain.inquiry.InquiryInterface;
import org.examemulator.gui.exam.ExamView.ExamGui;
import org.examemulator.gui.main.MainController;
import org.examemulator.gui.statitics.StatiticsController;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ExamController {

    private static final String FINISH_THIS_EXAM_MSG = "Are you sure you want to finish this Exam?";

    private final ExamView view;

    private final ExamService service;

    private final Logger logger;

    private final StatiticsController statiticsController;

    private final MainController mainController;

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
		    final MainController mainController, //
		    final Logger logger) {
	super();
	this.view = gui.getView();
	this.service = service;
	this.statiticsController = statiticsController;
	this.mainController = mainController;
	this.logger = logger;
    }

    @PostConstruct
    void init() {
	initActions();
    }

    public void show(final String name, final Component lastView, final List<? extends InquiryInterface> availableQuestions) {

	this.availableQuestions = new ArrayList<>(availableQuestions);
	this.name = name;

	view.contentPane.setBorder(createTitledBorder(name));

	initView();

	for (var i = 1; i <= availableQuestions.size(); i++) {
	    final var value = Integer.toString(i);
	    final var label = new JLabel(leftPad(value, 2, '0'));
	    label.setName(value);
	    view.pQuestions.add(label);
	}

	SwingUtilities.invokeLater(() -> {

	    logger.info("starting swing-event: {}", this.getClass().getSimpleName());

	    view.setLocationRelativeTo(lastView);

	    view.revalidate();
	    view.repaint();
	    view.setVisible(true);

	    logger.info("finished swing-event: {}", this.getClass().getSimpleName());
	});
    }

    public void show(final Exam selectedExam, final Component lastView) {

	this.availableQuestions = new ArrayList<>(selectedExam.getQuestions());
	this.name = selectedExam.getName();

	this.exam = selectedExam;
	view.contentPane.setBorder(createTitledBorder(name));

	final var questionLabelListener = new QuestionLabelListener();

	view.pQuestions.removeAll();

	for (var i = 1; i <= availableQuestions.size(); i++) {
	    final var value = Integer.toString(i);
	    final var label = new JLabel(leftPad(value, 2, '0'));
	    label.setName(value);
	    view.pQuestions.add(label);

	    final var tempQuestion = selectedExam.getQuestions().get(i - 1);

	    if (tempQuestion.isMarked()) {
		label.setForeground(BLUE);
	    } else if (tempQuestion.isAnswered()) {
		label.setForeground(GRAY);
	    } else {
		label.setForeground(BLACK);
	    }

	    label.addMouseListener(questionLabelListener);
	    label.repaint();
	}

	if (Objects.equals(exam.getType(), PRACTICE)) {
	    view.btnCheckAnswer.setVisible(true);
	    view.lblDuration.setVisible(false);
	    view.spinnerTimeDuration.setVisible(false);
	} else {
	    view.btnCheckAnswer.setVisible(false);
	    timer = new Timer(MILLISECOND, createTimerAction(exam.getDuration().intValue(), view.lblClock, () -> view.btnFinish.doClick()));
	    timer.start();
	}

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
	view.btnFinish.setEnabled(true);
	view.btnNext.setEnabled(true);
	view.btnCheckAnswer.setEnabled(true);

	SwingUtilities.invokeLater(() -> {

	    logger.info("starting swing-event: {}", this.getClass().getSimpleName());

	    view.setLocationRelativeTo(lastView);

	    view.revalidate();
	    view.repaint();
	    view.setVisible(true);

	    logger.info("finished swing-event: {}", this.getClass().getSimpleName());
	});

	selectedQuestion = exam.getQuestions().get(0);

	loadPanelQuestion();
    }

    // =================================================================================================================

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
    }

    private void initActions() {

	view.btnStart.addActionListener(event -> {

	    final var discretPercent = BigDecimal.valueOf((Integer) view.textDiscrete.getValue());

	    final var minScorePercent = BigDecimal.valueOf((Integer) view.textMinScore.getValue());

	    final var practiceMode = equalsIgnoreCase("Practice", (String) view.cbMode.getSelectedItem());

	    final var shuffleQuestions = view.chckbxShuffleQuestions.isSelected();
	    final var shuffleOptions = view.chckbxShuffleOptions.isSelected();

	    exam = new Exam.Builder().with($ -> {
		$.name = this.name;
		$.type = practiceMode ? PRACTICE : EXAM;
		$.discretPercent = discretPercent;
		$.minScorePercent = minScorePercent;
		$.shuffleQuestions = shuffleQuestions;
		$.shuffleOptions = shuffleOptions;
		$.questions = availableQuestions;
	    }).build();

	    service.save(exam);

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
	    view.btnFinish.setEnabled(true);
	    view.btnNext.setEnabled(true);
	    view.btnCheckAnswer.setEnabled(true);

	    final var duration = (Integer) view.spinnerTimeDuration.getValue();

	    if (nonNull(timer)) {
		timer.stop();
	    }

	    timer = null;

	    if (Objects.equals(exam.getType(), PRACTICE)) {
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

	    service.save(exam);
	});

	view.btnPauseProceed.addActionListener(event -> {

	    if (exam.getStatus().equals(RUNNING)) {

		exam.pause();
		view.btnPauseProceed.setText("Proceed");

		view.btnStart.setEnabled(false);
		view.btnStatistics.setEnabled(false);
		view.btnPrevious.setEnabled(false);

		view.lblClock.setText(StringUtils.EMPTY);

		view.btnPrevious.setEnabled(false);
		view.btnPauseProceed.setEnabled(true);
		view.chckbxShuffleQuestions.setEnabled(false);
		view.chckbxShuffleOptions.setEnabled(false);

		view.btnFinish.setEnabled(false);
		view.btnNext.setEnabled(false);
		view.btnCheckAnswer.setEnabled(false);

		if (nonNull(timer)) {
		    timer.stop();
		}

	    } else if (exam.getStatus().equals(PAUSED)) {

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

	    service.save(exam);
	});

	view.btnFinish.addActionListener(event -> {

	    if (showConfirmDialog(view, FINISH_THIS_EXAM_MSG, "Finish Exam", YES_NO_OPTION, QUESTION_MESSAGE) == NO_OPTION) {
		return;
	    }

	    exam.finish();

	    service.save(exam);

	    if (nonNull(timer)) {
		timer.stop();
	    }

	    view.lblClock.setText(StringUtils.EMPTY);

	    view.btnCheckAnswer.setEnabled(false);
	    view.btnStart.setEnabled(false);
	    view.btnFinish.setEnabled(false);
	    view.btnPrevious.setEnabled(false);
	    view.btnNext.setEnabled(false);
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

	    service.save(exam);
	});

	view.btnPrevious.addActionListener(event -> {

	    updateQuestionLabel();
	    final var previousQuestionOptional = previousQuestion(exam.getQuestions(), selectedQuestion);
	    if (previousQuestionOptional.isPresent()) {
		selectedQuestion = previousQuestionOptional.get();
	    }
	    loadPanelQuestion();

	    service.save(exam);
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
	    okButton.setMnemonic(KeyEvent.VK_O);

	    final var panel = new JPanel(new FlowLayout());
	    panel.setBorder(BorderFactory.createEtchedBorder());
	    panel.add(okButton);

	    dialogContainer.add(panel, SOUTH);
	    answerDialog.setVisible(true);
	});

	view.addWindowListener(new WindowAdapter() {

	    @Override
	    public void windowClosing(final WindowEvent windowEvent) {

		if (showConfirmDialog(view, //
				"Are you sure you want to leave this window?", "Close Window", //
				YES_NO_OPTION, //
				QUESTION_MESSAGE) == YES_OPTION) {

		    service.save(exam);

		    view.setVisible(false);
		    mainController.show(view);
		}
	    }
	});
    }

    private void loadPanelQuestion() {

	view.questionInternPanel.removeAll();

	final var optionalConcept = selectedQuestion.getQuestion().getConcept();
	final var conceptName = optionalConcept.isPresent() //
			? " (".concat(optionalConcept.get().getName()).concat(")") //
			: "";

	final var questionText = LF.concat(selectedQuestion.getValue()).concat(LF);

	final var currentOrder = leftPad(selectedQuestion.getOrder().toString(), 2, '0');
	final var originalOrder = selectedQuestion.isSameOrderQuestion() ? EMPTY : " (".concat(leftPad(selectedQuestion.getQuestion().getOrder().toString(), 2, '0')).concat(")");

	final var panelQuestionPanel = new JPanel();
	panelQuestionPanel.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseClicked(final MouseEvent event) {
		markQuestion(panelQuestionPanel);
	    }
	});

	panelQuestionPanel.setLayout(new BoxLayout(panelQuestionPanel, Y_AXIS));
	final var titleBorder = new TitledBorder("Question ".concat(currentOrder).concat(originalOrder).concat(conceptName));
	if (selectedQuestion.isMarked()) {
	    titleBorder.setTitleColor(BLUE);
	} else if (selectedQuestion.isAnswered()) {
	    titleBorder.setTitleColor(GRAY);
	} else {
	    titleBorder.setTitleColor(BLACK);
	}

	final var btnFake = new JButton(EMPTY);
	btnFake.addActionListener(event -> markQuestion(view.questionInternPanel));
	btnFake.setOpaque(false);
	btnFake.setContentAreaFilled(false);
	btnFake.setBorderPainted(false);
	btnFake.setMnemonic(KeyEvent.VK_T);
	panelQuestionPanel.add(btnFake);

	view.questionInternPanel.setBorder(titleBorder);
	panelQuestionPanel.add(createScrollTextToShow(questionText));
	panelQuestionPanel.revalidate();
	panelQuestionPanel.repaint();

	final var groupOptionsQuestionPanel = new JPanel(new BorderLayout());

	if (selectedQuestion.isAnswered() && DISCRET_LIST.contains(selectedQuestion.getType())) {
	    groupOptionsQuestionPanel.setBorder(new TitledBorder("Finished"));
	    groupOptionsQuestionPanel.add(new JLabel("You alrealdy answered it"), NORTH);
	} else if (DISCRET_LIST.contains(selectedQuestion.getType())) {
	    groupOptionsQuestionPanel.add(createDiscreteOptions(selectedQuestion), CENTER);
	} else {
	    groupOptionsQuestionPanel.add(createIndiscreteOptions(selectedQuestion), CENTER);
	}

	view.btnPrevious.setEnabled(hasPreviousQuestion(exam.getQuestions(), selectedQuestion));
	view.btnNext.setEnabled(hasNextQuestion(exam.getQuestions(), selectedQuestion));

	view.questionInternPanel.add(panelQuestionPanel);
	view.questionInternPanel.add(groupOptionsQuestionPanel);

	view.questionInternPanel.revalidate();
	view.questionInternPanel.repaint();
    }

    private void loadNumbersPanel() {
	view.pQuestions.removeAll();

	final var questionLabelListener = new QuestionLabelListener();

	for (final var question : exam.getQuestions()) {

	    final var label = new JLabel(leftPad(question.getOrder().toString(), 2, '0'));
	    label.setForeground(BLACK);
	    label.setName(question.getOrder().toString());
	    view.pQuestions.add(label);
	    label.addMouseListener(questionLabelListener);
	}

	view.pQuestions.revalidate();
	view.pQuestions.repaint();
    }

    private void updateQuestionLabel() {
	final var component = Stream.of(view.pQuestions.getComponents()) //
			.filter(comp -> Objects.equals(comp.getName(), selectedQuestion.getOrder().toString())) //
			.findFirst();

	if (component.isPresent() && component.get() instanceof final JLabel label) {

	    if (selectedQuestion.isMarked()) {
		label.setForeground(BLUE);
	    } else if (selectedQuestion.isAnswered()) {
		label.setForeground(GRAY);
	    } else {
		label.setForeground(BLACK);
	    }

	    label.repaint();
	}
    }

    private void markQuestion(final JPanel tempPanel) {

	selectedQuestion.mark(!selectedQuestion.isMarked());
	updateQuestionLabel();

	final var titleBorder = (TitledBorder) tempPanel.getBorder();

	if (selectedQuestion.isMarked()) {
	    titleBorder.setTitleColor(BLUE);
	} else if (selectedQuestion.isAnswered()) {
	    titleBorder.setTitleColor(GRAY);
	} else {
	    titleBorder.setTitleColor(BLACK);
	}

	tempPanel.repaint();
    }

    private final class QuestionLabelListener extends MouseAdapter {

	@Override
	public void mouseClicked(final MouseEvent event) {
	    final var label = (JLabel) event.getSource();
	    final var text = label.getText();

	    if (isLeftMouseButton(event) && event.getClickCount() == 1 && !event.isConsumed() && nonNull(exam) && exam.getStatus() == RUNNING) {
		selectQuestion(Integer.valueOf(text));
		loadPanelQuestion();
	    }

	    if (selectedQuestion.isMarked()) {
		label.setForeground(BLUE);
	    } else if (selectedQuestion.isAnswered()) {
		label.setForeground(GRAY);
	    } else {
		label.setForeground(BLACK);
	    }

	    label.repaint();
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

}
