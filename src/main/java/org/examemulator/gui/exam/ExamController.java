package org.examemulator.gui.exam;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.BoxLayout.Y_AXIS;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.examemulator.domain.QuestionType.DOMC;
import static org.examemulator.domain.QuestionType.DOSC;
import static org.examemulator.gui.ControllerUtil.MILLISECOND;
import static org.examemulator.gui.ControllerUtil.createDiscreteOptions;
import static org.examemulator.gui.ControllerUtil.createIndiscreteOptions;
import static org.examemulator.gui.ControllerUtil.createTextToShow;
import static org.examemulator.gui.ControllerUtil.createTimerAction;
import static org.examemulator.gui.ControllerUtil.extractedOptions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.util.AbstractMap;
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
import org.examemulator.domain.Exam;
import org.examemulator.domain.ExamStatus;
import org.examemulator.domain.Option;
import org.examemulator.domain.Question;
import org.examemulator.domain.QuestionType;
import org.examemulator.gui.components.RangeSlider;
import org.examemulator.gui.statitics.StatiticsController;
import org.examemulator.service.ExamService;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ExamController {

    private final List<QuestionType> discreteList = List.of(DOMC, DOSC);

    private final MouseAdapter questionLabelListener = new MouseAdapter() {

	@Override
	public void mouseClicked(final MouseEvent event) {
	    final var labelEvent = (JLabel) event.getSource();
	    final var text = labelEvent.getText();

	    if (Objects.nonNull(exam) && exam.getStatus() == ExamStatus.RUNNING) {
		selectQuestion(Integer.valueOf(text));
		loadPanelQuestion();
	    }
	}
    };

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

	view.rangeQuestions.addChangeListener(event -> {
	    final var slider = (RangeSlider) event.getSource();
	    view.lblRangeLow.setText(String.valueOf(slider.getValue()));
	    view.lblUpper.setText(String.valueOf(slider.getUpperValue()));
	});

	view.btnStart.addActionListener(event -> {

	    final var discretPercent = BigDecimal.valueOf((Integer) view.textDiscrete.getValue());

	    final var mimScore = BigDecimal.valueOf((Integer) view.textMinScore.getValue());

	    final var range = new AbstractMap.SimpleEntry<Integer, Integer>(view.rangeQuestions.getValue(), view.rangeQuestions.getUpperValue());

	    var practiceMode = false;
	    if (equalsIgnoreCase("Practice", (String) view.cbMode.getSelectedItem())) {
		practiceMode = true;
	    }

	    exam = service.createExam(currentFolder, practiceMode, discretPercent, mimScore, range);

	    view.btnStart.setEnabled(false);
	    view.btnStatistics.setEnabled(false);
	    view.btnPrevious.setEnabled(false);

	    view.textMinScore.setEnabled(false);
	    view.textDiscrete.setEnabled(false);
	    view.cbMode.setEnabled(false);
	    view.rangeQuestions.setEnabled(false);
	    view.spinnerTimeDuration.setEnabled(false);

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

	    loadPanelQuestions();

	    loadPanelQuestion();
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

	    view.btnStatistics.setEnabled(true);

	    view.questionInternPanel.removeAll();
	    view.questionInternPanel.revalidate();
	    view.questionInternPanel.repaint();
	});

	view.btnStatistics.addActionListener(event -> statiticsController.show(exam, view));

	view.btnNext.addActionListener(event -> {
	    nextQuestion();
	    loadPanelQuestion();
	});

	view.btnPrevious.addActionListener(event -> {
	    previousQuestion();
	    loadPanelQuestion();
	});

	view.mntmNew.addActionListener(event -> {

	    final var chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new File("."));
	    chooser.setDialogTitle("select an exam folder");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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

		view.examPanel.setBorder(BorderFactory.createTitledBorder(service.extractedExamName(currentFolder)));

		view.btnStart.setEnabled(true);
		view.textMinScore.setEnabled(true);
		view.textDiscrete.setEnabled(true);
		view.cbMode.setEnabled(true);
		view.spinnerTimeDuration.setEnabled(true);
		view.rangeQuestions.setEnabled(true);

		view.rangeQuestions.setMinimum(1);
		view.rangeQuestions.setMaximum(service.getQtyFiles(currentFolder));

		view.rangeQuestions.setValue(1);
		view.rangeQuestions.setUpperValue(service.getQtyFiles(currentFolder));

		view.btnCheckAnswer.setEnabled(false);
		view.btnFinish.setEnabled(false);
		view.btnNext.setEnabled(false);
		view.btnPrevious.setEnabled(false);
		view.btnStatistics.setEnabled(false);

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

	    final var correctOptions = extractedOptions("Correct Answer(s): ", selectedQuestion.getCorrectOptions());

	    final String vs;
	    if (discreteList.contains(selectedQuestion.getType())) {
		vs = "\n\n" + selectedQuestion.getOptions().stream() //
				.filter(option -> selectedQuestion.getCorrectOptions().contains(option.getId())) //
				.map(Option::getText) //
				.collect(Collectors.joining("\n"));
	    } else {
		vs = "";
	    }

	    final var explanation = "\n" + selectedQuestion.getExplanation();

	    final var text = correctOptions + vs + explanation;

	    dialogContainer.add(createTextToShow(text), CENTER);

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

	view.btnPrevious.setEnabled(hasPreviousQuestion());
	view.btnNext.setEnabled(hasNextQuestion());
    }

    private void nextQuestion() {

	final var questions = exam.getQuestions();

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex + 1);

	updateQuestionLabel();

	if (iterator.hasNext()) {
	    selectedQuestion = iterator.next();
	}
    }

    private boolean hasNextQuestion() {
	final var questions = exam.getQuestions();

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex + 1);
	return iterator.hasNext();
    }

    private Question previousQuestion() {

	final var questions = exam.getQuestions();

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex);

	updateQuestionLabel();

	if (iterator.hasPrevious()) {
	    selectedQuestion = iterator.previous();
	    return selectedQuestion;
	}

	return selectedQuestion;
    }

    private boolean hasPreviousQuestion() {
	final var questions = exam.getQuestions();

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex);
	return iterator.hasPrevious();
    }

    private void updateQuestionLabel() {
	final var component = Stream.of(view.pQuestions.getComponents()) //
			.filter(comp -> Objects.equals(comp.getName(), selectedQuestion.getOrder().toString())) //
			.findFirst();

	if (selectedQuestion.isAnswered() && component.isPresent() && component.get() instanceof JLabel label) {

	    if (selectedQuestion.isMarked()) {
		label.setForeground(Color.ORANGE);
	    } else {
		label.setForeground(Color.BLUE);
	    }
	}
    }

    private void loadPanelQuestion() {
	view.questionInternPanel.removeAll();

	final var panelQuestionPanel = new JPanel();
	panelQuestionPanel.setLayout(new BoxLayout(panelQuestionPanel, Y_AXIS));
	panelQuestionPanel.setBorder(BorderFactory.createTitledBorder("Question " + leftPad(selectedQuestion.getOrder().toString(), 2, '0')));
	panelQuestionPanel.add(createTextToShow("\n" + selectedQuestion.getValue() + "\n"));
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

	view.btnPrevious.setEnabled(hasPreviousQuestion());
	view.btnNext.setEnabled(hasNextQuestion());

	view.questionInternPanel.add(panelQuestionPanel);
	view.questionInternPanel.add(groupOptionsQuestionPanel);

	view.questionInternPanel.revalidate();
	view.questionInternPanel.repaint();
    }

    private void loadPanelQuestions() {
	view.pQuestions.removeAll();
	view.pQuestions.revalidate();
	view.pQuestions.repaint();

	for (final var question : exam.getQuestions()) {

	    final var label = new JLabel(leftPad(question.getOrder().toString(), 2, '0'));
	    label.setName(question.getOrder().toString());
	    view.pQuestions.add(label);
	    label.addMouseListener(questionLabelListener);
	}
    }
}
