package org.examemulator.gui.statitics;

import static java.awt.BorderLayout.CENTER;
import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.util.stream.Collectors.joining;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.examemulator.infra.util.gui.ControllerUtil.hasNextQuestion;
import static org.examemulator.infra.util.gui.ControllerUtil.hasPreviousQuestion;
import static org.examemulator.infra.util.gui.ControllerUtil.nextQuestion;
import static org.examemulator.infra.util.gui.ControllerUtil.previousQuestion;
import static org.examemulator.infra.util.gui.GuiUtil.TAG_BR;
import static org.examemulator.infra.util.gui.GuiUtil.TAG_BR_BR;
import static org.examemulator.infra.util.gui.GuiUtil.TAG_CLOSE_B;
import static org.examemulator.infra.util.gui.GuiUtil.TAG_OPEN_B;
import static org.examemulator.infra.util.gui.GuiUtil.convertTextToHtml;
import static org.examemulator.infra.util.gui.GuiUtil.createScrollHtmlTextToShow;
import static org.examemulator.infra.util.gui.GuiUtil.extractedOptions;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.examemulator.application.ExamService;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamQuestion;
import org.examemulator.gui.exam.ExamController;
import org.examemulator.gui.main.MainController;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StatiticsController {

    private final StatiticsView view;

    private final List<ExamQuestion> questions = new ArrayList<>();

    private final ExamController examController;

    private final MainController mainController;
    
    private final ExamService examService;

    private final MouseAdapter questionLabelListener = new MouseAdapter() {

	@Override
	public void mouseClicked(final MouseEvent event) {
	    final var labelEvent = (JLabel) event.getSource();
	    final var text = labelEvent.getText();

	    if (Objects.nonNull(exam)) {

		selectedQuestion = exam.getQuestions() //
				.stream() //
				.filter(question -> Objects.equals(question.getOrder(), toInt(text))) //
				.findFirst() //
				.orElse(null);

		if (Objects.isNull(selectedQuestion)) {
		    return;
		}

		view.btnPrevious.setEnabled(hasPreviousQuestion(questions, selectedQuestion));
		view.btnNext.setEnabled(hasNextQuestion(questions, selectedQuestion));

		loadPanelQuestion();
	    }
	}
    };

    private final ItemListener itemListener = event -> {
	loadNumbersPanel();
	loadPanelQuestion();
    };

    private Exam exam;

    private ExamQuestion selectedQuestion;

    @Inject
    StatiticsController( //
		    final StatiticsView.StatiticsGui gui, //
		    final ExamController examController, //
		    final MainController mainController, //
		    final ExamService examService) {

	this.view = gui.getView();
	this.examController = examController;
	this.mainController = mainController;
	this.examService = examService;
    }

    @PostConstruct
    void init() {

	view.btnNext.addActionListener(event -> {
	    final var nextQuestionOptional = nextQuestion(questions, selectedQuestion);
	    if (nextQuestionOptional.isPresent()) {
		selectedQuestion = nextQuestionOptional.get();
	    }
	    
	    loadPanelQuestion();
	});

	view.btnPrevious.addActionListener(event -> {
	    final var previousQuestionOptional = previousQuestion(questions, selectedQuestion);
	    if (previousQuestionOptional.isPresent()) {
		selectedQuestion = previousQuestionOptional.get();
	    
	    }
	    loadPanelQuestion();
	});

	view.btnNewExam.addActionListener(event -> {
	    
	    if (questions.isEmpty()) {
		showMessageDialog(view, "You need select at least one question!", "Error!", ERROR_MESSAGE);
		return;
	    }
	    
	    view.setVisible(false);
	    
	    final var examName = examService.getExamNameByBy(exam.getName());
	    final var certMinScorePercent = exam.getCertMinScorePercent();
	    
	    examController.show(examName, certMinScorePercent, questions, view);
	});

	view.btnMain.addActionListener(event -> {
	    view.setVisible(false);
	    mainController.show(view);
	});

	view.btnDeleteExam.addActionListener(event -> {
	    
	    if (showConfirmDialog(view, //
				"Are you sure you want to delete this this Exam?", "Delete Exam", //
				YES_NO_OPTION, //
				QUESTION_MESSAGE) == YES_OPTION) {
		
		examService.delete(exam);
		view.btnMain.doClick();
	    }
	    
	});
	
	view.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(final WindowEvent windowEvent) {
		
		if (showConfirmDialog(view, //
				"Are you sure you want to leave this window?", "Close Window", //
				YES_NO_OPTION, //
				QUESTION_MESSAGE) == YES_OPTION) {
		    
		    view.btnMain.doClick();
		}
	    }
	});	

	view.chckbxCorrects.addItemListener(itemListener);
	view.chckbxIncorrects.addItemListener(itemListener);
	view.chckbxMarked.addItemListener(itemListener);
    }

    public void show(final Exam exam, final Component lastView) {
	this.exam = exam;

	view.contentPane.setBorder(createTitledBorder(exam.getName()));
	view.lblStatistic.setText(getStatistic(exam));

	questions.clear();
	questions.addAll(exam.getQuestions());

	if (questions.isEmpty()) {
	    return;
	}

	selectedQuestion = questions.get(0);

	loadPanelQuestion();
	loadNumbersPanel();

	view.chckbxMarked.setSelected(false);
	view.chckbxIncorrects.setSelected(true);
	view.chckbxCorrects.setSelected(true);

	view.setLocationRelativeTo(lastView);
	view.setVisible(true);
    }

    // =======================================================================================================================================

    private void loadPanelQuestion() {

	final var question = TAG_BR.concat(TAG_OPEN_B).concat(convertTextToHtml(selectedQuestion.getValue())).concat(TAG_CLOSE_B);

	final var options = selectedQuestion.getOptions() //
			.stream() //
			.sorted((o1, o2) -> o1.getLetter().compareTo(o2.getLetter())) //
			.map(option -> TAG_OPEN_B.concat(option.getLetter()).concat(") ").concat(TAG_CLOSE_B).concat(TAG_BR).concat(convertTextToHtml(option.getValue()))) //
			.collect(joining(TAG_BR_BR));

	final var answeredOptions = TAG_OPEN_B.concat("Your Answer(s): ").concat(TAG_CLOSE_B).concat(extractedOptions(selectedQuestion.getAnswers()));

	final var correctOptions = TAG_OPEN_B.concat("Correct Answer(s): ").concat(TAG_CLOSE_B).concat(extractedOptions(selectedQuestion.getCorrectOptions()));

	final var explanation = TAG_OPEN_B.concat("Explanation: ").concat(TAG_CLOSE_B).concat(TAG_BR_BR).concat(convertTextToHtml(selectedQuestion.getExplanation()));

	final var txt = question.concat(TAG_BR_BR) //
			.concat(options).concat(TAG_BR_BR) //
			.concat(answeredOptions).concat(TAG_BR_BR) //
			.concat(correctOptions).concat(TAG_BR_BR) //
			.concat(explanation);


	final String font;
	if (!selectedQuestion.getQuestion().isActive()) {
	    font = "<font color='orange'>";
	} else if (selectedQuestion.isCorrect()) {
	    font = "<font color='green'>";
	} else {
	    font = "<font color='red'>";
	}
	
	final var currentOrder = leftPad(selectedQuestion.getOrder().toString(), 2, '0');
	final var originalOrder = selectedQuestion.isSameOrderQuestion() ? EMPTY : " (".concat(leftPad(selectedQuestion.getQuestion().getOrder().toString(), 2, '0')).concat(")");

	final var pQuestionLabel = "<html> ".concat(font).concat("Question ").concat(currentOrder).concat(originalOrder).concat("</font></html>");

	view.btnPrevious.setEnabled(hasPreviousQuestion(questions, selectedQuestion));
	view.btnNext.setEnabled(hasNextQuestion(questions, selectedQuestion));

	view.pQuestion.removeAll();
	view.pQuestion.add(createScrollHtmlTextToShow(txt), CENTER);
	view.pQuestion.setBorder(BorderFactory.createTitledBorder(pQuestionLabel));
	view.pQuestion.revalidate();
	view.pQuestion.repaint();
    }

    private void loadNumbersPanel() {

	final var showCorrect = view.chckbxCorrects.isSelected();

	final var showIncorrect = view.chckbxIncorrects.isSelected();

	final var showMarked = view.chckbxMarked.isSelected();

	final var questionsTemp = new TreeSet<ExamQuestion>();

	if (showCorrect) {
	    final var questionsCorrects = exam.getQuestions() //
			    .stream() //
			    .filter(ExamQuestion::isCorrect) //
			    .toList();

	    questionsTemp.addAll(questionsCorrects);
	}

	if (showMarked) {
	    final var questionsMarked = questionsTemp //
			    .stream() //
			    .filter(ExamQuestion::isMarked) //
			    .toList();

	    questionsTemp.clear();
	    questionsTemp.addAll(questionsMarked);
	}

	if (showIncorrect) {
	    final var questionsIncorrects = exam.getQuestions() //
			    .stream() //
			    .filter(question -> !question.isCorrect()) //
			    .toList();

	    questionsTemp.addAll(questionsIncorrects);
	}

	questions.clear();
	questions.addAll(questionsTemp);

	view.pQuestions.removeAll();

	for (final var question : questionsTemp) {

	    final var label = new JLabel(leftPad(question.getOrder().toString(), 2, '0'));

	    label.setForeground(BLACK);
	    
	    if (question.isCorrect()) {
		label.setForeground(GREEN);
	    }

	    if (question.isMarked()) {
		label.setForeground(BLUE);
	    }

	    if (!question.isCorrect()) {
		label.setForeground(RED);
	    }
	    
	    if (!question.getQuestion().isActive()) {
		label.setForeground(ORANGE);
	    }

	    view.pQuestions.add(label);
	    label.addMouseListener(questionLabelListener);
	}

	view.pQuestions.revalidate();
	view.pQuestions.repaint();
    }

    private String getStatistic(final Exam exam) {

	final var qtyTotal = exam.getQtyQuestion();

	final var qtyCorrect = exam.getQtyCorrect();

	final var qtyIncorrect = exam.getQtyIncorrect();

	final var minScoreValue = exam.getMinScore();
	
	final var certMinScoreValue = exam.getCertMinScore();	
			
	final var percCorrect = exam.getPercCorret();

	final var percIncorrect = exam.getPercIncorrect(); 
			
	final String result;
	if(exam.isPassed()) {
	    result = "<font color='green'>PASSED</font>";
	} else if (exam.isAlmost()) {
	    result = "<font color='orange'>ALMOST</font>";
	} else {
	    result = "<font color='red'>FAILED</font>";
	}
	
	final var duration = exam.getDuration();

	final var msg = """
			<html>
			You {0} on this exam! <br />
			You had {1} questions with min score {2} ({3}%). <br />
			The certification min score is {4} ({5}%). <br />
			You answered {6} ({7}%) correct(s) and {8} ({9}%) incorrect(s). <br />
			The test duration was {10} minutes.
			</html>
			""";

	return MessageFormat.format( //
			msg, //
			result, // 0
			qtyTotal, // 1
			minScoreValue, // 2
			exam.getMinScorePercent(), // 3
			certMinScoreValue, // 4
			exam.getCertMinScorePercent(), // 5
			qtyCorrect, // 6
			percCorrect, // 7
			qtyIncorrect, // 8
			percIncorrect, // 9
			duration // 10
	);
    }

}
