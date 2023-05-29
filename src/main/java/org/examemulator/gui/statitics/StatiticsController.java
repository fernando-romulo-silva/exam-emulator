package org.examemulator.gui.statitics;

import static java.awt.BorderLayout.CENTER;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.math.RoundingMode.HALF_UP;
import static java.util.stream.Collectors.joining;
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

import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.Question;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StatiticsController {

    private final StatiticsView view;

    private final List<Question> questions = new ArrayList<>();

    private final MouseAdapter questionLabelListener = new MouseAdapter() {
	
	private void selectQuestion(int order) {

	    final var questionOptional = exam.getQuestions() //
			    .stream() //
			    .filter(question -> Objects.equals(question.getOrder(), order)) //
			    .findFirst();

	    if (questionOptional.isEmpty()) {
		return;
	    }

	    selectedQuestion = questionOptional.get();

	    view.btnPrevious.setEnabled(hasPreviousQuestion(questions, selectedQuestion));
	    view.btnNext.setEnabled(hasNextQuestion(questions, selectedQuestion));
	}

	@Override
	public void mouseClicked(final MouseEvent event) {
	    final var labelEvent = (JLabel) event.getSource();
	    final var text = labelEvent.getText();

	    if (Objects.nonNull(exam)) {
		selectQuestion(Integer.valueOf(text));
		loadPanelQuestion();
	    }
	}
    };

    private final ItemListener itemListener = event -> {
	loadNumbersPanel();
	loadPanelQuestion();
    };

    private Exam exam;

    private Question selectedQuestion;

    @Inject
    StatiticsController(final StatiticsGui gui) {
	this.view = gui.getView();
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

	view.chckbxCorrects.addItemListener(itemListener);
	view.chckbxIncorrects.addItemListener(itemListener);
	view.chckbxMarked.addItemListener(itemListener);
    }
    
    public void show(final Exam exam, final Component owner) {
	this.exam = exam;
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

	view.setLocationRelativeTo(owner);
	view.setVisible(true);
    }

    // =======================================================================================================================================

    private void loadPanelQuestion() {

	final var question = TAG_BR.concat(TAG_OPEN_B).concat(convertTextToHtml(selectedQuestion.getValue())).concat(TAG_CLOSE_B);

	final var options = selectedQuestion.getOptions() //
			.stream() //
			.map(option -> option.getLetter().concat(") ").concat(TAG_BR).concat(convertTextToHtml(option.getText()))) //
			.collect(joining(TAG_BR_BR));

	final var answeredOptions = TAG_OPEN_B.concat("Your Answer(s): ").concat(TAG_CLOSE_B).concat(extractedOptions(selectedQuestion.getAnswers()));

	final var correctOptions = TAG_OPEN_B.concat("Correct Answer(s): ").concat(TAG_CLOSE_B).concat(extractedOptions(selectedQuestion.getCorrectOptions()));

	final var explanation = TAG_OPEN_B.concat("Explanation: ").concat(TAG_CLOSE_B).concat(TAG_BR_BR).concat(convertTextToHtml(selectedQuestion.getExplanation()));

	final var txt = question.concat(TAG_BR_BR) //
			.concat(options).concat(TAG_BR_BR) //
			.concat(answeredOptions).concat(TAG_BR_BR) //
			.concat(correctOptions).concat(TAG_BR_BR) //
			.concat(explanation);

	final var font = selectedQuestion.isCorrect() ? "<font color='green'>" : "<font color='red'>";

	final var pQuestionLabel = "<html> ".concat(font).concat("Question ").concat(leftPad(selectedQuestion.getOrder().toString(), 2, '0')).concat("</font></html>");

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

	final var questionsTemp = new TreeSet<Question>();

	if (showCorrect) {
	    final var questionsCorrects = exam.getQuestions() //
			    .stream() //
			    .filter(Question::isCorrect) //
			    .toList();

	    questionsTemp.addAll(questionsCorrects);

	}

	if (showIncorrect) {
	    final var questionsIncorrects = exam.getQuestions() //
			    .stream() //
			    .filter(question -> !question.isCorrect()) //
			    .toList();

	    questionsTemp.addAll(questionsIncorrects);

	}

	if (showMarked) {
	    final var questionsMarked = questionsTemp //
			    .stream() //
			    .filter(Question::isMarked) //
			    .toList();

	    questionsTemp.clear();
	    questionsTemp.addAll(questionsMarked);
	}

	questions.clear();
	questions.addAll(questionsTemp);

	view.pQuestions.removeAll();

	for (final var question : questionsTemp) {

	    final var label = new JLabel(leftPad(question.getOrder().toString(), 2, '0'));
	    label.setForeground(GREEN);

	    if (question.isMarked()) {
		label.setForeground(ORANGE);
	    }

	    if (!question.isCorrect()) {
		label.setForeground(RED);
	    }

	    view.pQuestions.add(label);
	    label.addMouseListener(questionLabelListener);
	}

	view.pQuestions.revalidate();
	view.pQuestions.repaint();
    }
    
    private static String getStatistic(final Exam exam) {

	final var qtyTotal = exam.getQuestions().stream() //
			.count();

	final var qtyCorrect = exam.getQuestions().stream() //
			.filter(q -> q.isCorrect()) //
			.count();

	final var qtyIncorrect = qtyTotal - qtyCorrect;

	final var matchContext = new MathContext(2, HALF_UP); // 2 precision

	final var minScoreValue = new BigDecimal(qtyTotal) //
			.multiply(exam.getMinScorePercent()) //
			.divide(BigDecimal.valueOf(100l), matchContext);

	final var percCorrect = new BigDecimal(qtyCorrect) //
			.divide(BigDecimal.valueOf(qtyTotal), matchContext) //
			.multiply(BigDecimal.valueOf(100l));

	final var percIncorrect = new BigDecimal(qtyIncorrect) //
			.divide(BigDecimal.valueOf(qtyTotal), matchContext) //
			.multiply(BigDecimal.valueOf(100l));

	final var result = BigDecimal.valueOf(qtyCorrect).compareTo(minScoreValue) >= 0 ? //
			"<font color='green'>PASSED</font>" : //
			"<font color='red'>FAILED</font>";

	final var duration = exam.getDuration();

	final var msg = """
			<html>
			You {0} on this exam! <br />
			You had {1} questions with min score {2} ({3}%). <br />
			You answered {4} ({5}%) correct(s) and {6} ({7}%) incorrect(s). <br />
			The test duration was {8} minutes.
			</html>
			""";

	return MessageFormat.format( //
			msg, //
			result, // 0
			qtyTotal, // 1
			minScoreValue, // 2
			exam.getMinScorePercent(), // 3
			qtyCorrect, // 4
			percCorrect, // 5
			qtyIncorrect, // 6
			percIncorrect, // 7
			duration // 8
	);
    }
    
}
