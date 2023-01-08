package org.examemulator.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.examemulator.gui.ControllerUtil.createTextToShow;
import static org.examemulator.gui.ControllerUtil.extractedOptions;
import static org.examemulator.gui.ControllerUtil.getStatistic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.examemulator.domain.Exam;
import org.examemulator.domain.Question;

class StatiticsController {

    private final StatiticsView view;

    private final Exam exam;

    private final List<Question> questions = new ArrayList<>();

    private final MouseAdapter questionLabelListener = new MouseAdapter() {

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
	loadPanelQuestions();
	loadPanelQuestion();
    };

    private Question selectedQuestion;

    StatiticsController(final Window owner, final Exam exam) {

	view = new StatiticsView();
	this.exam = exam;

	questions.addAll(exam.getQuestions());
	selectFirstQuestion();

	final var answerDialog = new JDialog(owner, "Statistic Exam", Dialog.ModalityType.DOCUMENT_MODAL);
	answerDialog.setBounds(132, 132, 300, 200);
	answerDialog.setSize(600, 500);
	answerDialog.setLocationRelativeTo(view);

	final var dialogContainer = answerDialog.getContentPane();
	dialogContainer.setLayout(new BorderLayout());
	dialogContainer.add(view, CENTER);

	final var okButton = new JButton("Ok");
	okButton.addActionListener(okEvent -> answerDialog.setVisible(false));

	final var panel = new JPanel(new FlowLayout());
	panel.add(okButton);

	dialogContainer.add(panel, SOUTH);

	view.lblStatistic.setText(getStatistic(exam));

	view.btnNext.addActionListener(event -> {
	    nextQuestion();
	    loadPanelQuestion();
	});

	view.btnPrevious.addActionListener(event -> {
	    previousQuestion();
	    loadPanelQuestion();
	});

	view.chckbxCorrects.addItemListener(itemListener);
	view.chckbxIncorrects.addItemListener(itemListener);
	view.chckbxMarked.addItemListener(itemListener);

	loadPanelQuestion();
	loadPanelQuestions();

	answerDialog.setVisible(true);
    }

    void loadPanelQuestion() {

	final var correctOptions = extractedOptions("Correct Answer(s): ", selectedQuestion.getCorrectOptions());

	final var answeredOptions = extractedOptions("Your Answer(s): ", selectedQuestion.getAnswers());

	final var options = selectedQuestion.getOptions() //
			.stream() //
			.map(option -> option.getId() + ") \n" + option.getText()) //
			.collect(joining("\n\n"));

	final var txt = "\n" + selectedQuestion.getValue() + "\n\n" //
			+ options + "\n\n" //
			+ answeredOptions + "\n\n" //
			+ correctOptions + "\n\n\n" //
			+ "Explanation:" + selectedQuestion.getExplanation();

	final var font = selectedQuestion.isCorrect() ? "<font color='green'>" : "<font color='red'>";

	final var pQuestionLabel = "<html> " + font + "Question " + leftPad(selectedQuestion.getOrder().toString(), 2, '0') + "</font></html>";

	view.btnPrevious.setEnabled(hasPreviousQuestion());
	view.btnNext.setEnabled(hasNextQuestion());

	view.pQuestion.removeAll();
	view.pQuestion.add(createTextToShow(txt), CENTER);
	view.pQuestion.setBorder(BorderFactory.createTitledBorder(pQuestionLabel));
	view.pQuestion.revalidate();
	view.pQuestion.repaint();
    }

    void loadPanelQuestions() {

	final var showCorrect = view.chckbxCorrects.isSelected();

	final var showIncorrect = view.chckbxIncorrects.isSelected();

	final var showMarked = view.chckbxMarked.isSelected();

	// all
	final var questionsTemp = new TreeSet<Question>();

	if (showCorrect) {
	    final var questionsCorrects = exam.getQuestions() //
			    .stream() //
			    .filter(question -> question.isCorrect()) //
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
			    .filter(question -> question.isMarked()) //
			    .toList();

	    questionsTemp.clear();
	    questionsTemp.addAll(questionsMarked);
	}

	questions.clear();
	questions.addAll(questionsTemp);

	view.pQuestions.removeAll();

	for (final var question : questionsTemp) {

	    final var label = new JLabel(leftPad(question.getOrder().toString(), 2, '0'));
	    label.setForeground(Color.GREEN);

	    if (question.isMarked()) {
		label.setForeground(Color.YELLOW);
	    }

	    if (!question.isCorrect()) {
		label.setForeground(Color.RED);
	    }

	    view.pQuestions.add(label);
	    label.addMouseListener(questionLabelListener);
	}

	view.pQuestions.revalidate();
	view.pQuestions.repaint();
    }

    // --------------------------------------------------------------------------------------------------------------------

    private void selectFirstQuestion() {

	if (questions.isEmpty()) {
	    return;
	}

	selectedQuestion = questions.get(0);
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

    public void nextQuestion() {

	if (questions.isEmpty()) {
	    return;
	}

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex + 1);

	if (iterator.hasNext()) {
	    selectedQuestion = iterator.next();
	}
    }

    public boolean hasNextQuestion() {
	if (questions.isEmpty()) {
	    return false;
	}

	final var currentIndex = questions.indexOf(selectedQuestion);
	if (currentIndex < 0) {
	    return false;
	}

	final var iterator = questions.listIterator(currentIndex + 1);
	return iterator.hasNext();
    }

    public Question previousQuestion() {

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex);

	if (iterator.hasPrevious()) {
	    selectedQuestion = iterator.previous();
	    return selectedQuestion;
	}

	return selectedQuestion;
    }

    public boolean hasPreviousQuestion() {
	if (questions.isEmpty()) {
	    return false;
	}

	final var currentIndex = questions.indexOf(selectedQuestion);

	if (currentIndex < 0) {
	    return false;
	}

	final var iterator = questions.listIterator(currentIndex);
	return iterator.hasPrevious();
    }
}
