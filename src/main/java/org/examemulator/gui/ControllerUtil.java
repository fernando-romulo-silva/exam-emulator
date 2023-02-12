package org.examemulator.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.FlowLayout.LEFT;
import static java.math.RoundingMode.HALF_UP;
import static javax.swing.BoxLayout.Y_AXIS;
import static org.apache.commons.lang3.RegExUtils.replaceAll;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.Exam;
import org.examemulator.domain.Question;
import org.examemulator.gui.components.WrapLayout;

public class ControllerUtil {

    private static final int SIXTY_VALUE = 60;

    public static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    public static final int MILLISECOND = 1000;

    @FunctionalInterface
    public static interface Action {
	void execute();
    }

    public static JComponent createTextToShow(final String text) {
	final var textArea = new JTextArea();
	textArea.setText(text);
	textArea.setEditable(false);
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
	textArea.setFont(DEFAULT_FONT);

//	jScrollPane.scrollRectToVisible(new Rectangle(0, 0, 1, 1));

//	SwingUtilities.invokeLater(new Runnable() {
//	    @Override
//	    public void run() {
//		jScrollPane.getViewport().setViewPosition(new Point(0, 0));
//	    }
//	});

	return new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    public static JComponent createTextOpaqueToShow(final String text) {
	final var textArea = new JTextArea(text);
	textArea.setBorder(new EmptyBorder(0, 0, 0, 0));
	textArea.setOpaque(false);
	textArea.setEditable(false);
	textArea.setFont(DEFAULT_FONT);
//	textArea.setLineWrap(true);

//	final var scrollPane = new JScrollPane(textArea);
//	scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
//	scrollPane.getViewport().setOpaque(false);
//	scrollPane.setOpaque(false);	

	return new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    public static ActionListener createTimerAction(final Integer duration, final JLabel jlabel, final Action action) {
	return new ActionListener() {

	    private long time = duration * MILLISECOND * SIXTY_VALUE;

	    public void actionPerformed(final ActionEvent e) {

		if (time >= 0) {
		    final long s = ((time / MILLISECOND) % SIXTY_VALUE);
		    final long m = (((time / MILLISECOND) / SIXTY_VALUE) % SIXTY_VALUE);
		    final long h = ((((time / MILLISECOND) / SIXTY_VALUE) / SIXTY_VALUE) % SIXTY_VALUE);

		    final var hours = leftPad(Long.toString(h), 2, "0");
		    final var minutes = leftPad(Long.toString(m), 2, "0");
		    final var seconds = leftPad(Long.toString(s), 2, "0");

		    jlabel.setText("Time: " + hours + ":" + minutes + ":" + seconds);
		    time -= MILLISECOND;

		} else {
		    action.execute();
		}
	    }
	};
    }

    public static void setUIFont(final Font f) {
	
	var keys = UIManager.getDefaults().keys();
	
	while (keys.hasMoreElements()) {
	    
	    Object key = keys.nextElement();
	    
	    Object value = UIManager.get(key);
	    
	    if (value != null && value instanceof Font) {
		UIManager.put(key, f);
	    }
	}
    }

    public static JPanel createDiscreteOptions(final Question question) {
	final var optionsQuestionPanel = new JPanel();
	optionsQuestionPanel.setLayout(new BorderLayout());

	final var yesButton = new JButton("Yes");
	final var noButton = new JButton("No");

	final var buttonPanel = new JPanel(new WrapLayout(LEFT, 5, 5));
	buttonPanel.add(yesButton);
	buttonPanel.add(noButton);

	final var optionsLabels = new ArrayList<String>();

	final var optionPanel = new JPanel(new WrapLayout(LEFT, 5, 5));

	for (final var option : question.getOptions()) {
	    final var id = option.getId();
	    optionsLabels.add(id + "|" + option.getText());
	}

	final var substringAfter = substringAfter(optionsLabels.get(0), "|");
	optionPanel.add(createTextOpaqueToShow(substringAfter));

	final var buttonYesListener = new ActionListener() {

	    String current = optionsLabels.get(0);

	    @Override
	    public void actionPerformed(final ActionEvent event) {

		final var button = (JButton) event.getSource();

		if (equalsIgnoreCase("yes", button.getText())) {
		    question.selectAnswer(substringBefore(current, "|"));
		}

		optionPanel.removeAll();

		final var currentIndex = optionsLabels.indexOf(current);
		final var iterator = optionsLabels.listIterator(currentIndex + 1);

		if (iterator.hasNext()) {
		    current = optionsLabels.get(currentIndex + 1);

		    optionPanel.add(createTextOpaqueToShow(substringAfter(current, "|")));
		} else {
		    final var label = new JLabel("You finalized this question");
		    label.setFont(DEFAULT_FONT);
		    optionPanel.add(label);
		    buttonPanel.setVisible(false);

		    if (question.getAnswers().isEmpty()) {
			question.selectAnswer("N/A");
		    }
		}

		optionPanel.revalidate();
		optionPanel.repaint();
	    }
	};

	yesButton.addActionListener(buttonYesListener);
	noButton.addActionListener(buttonYesListener);

	optionsQuestionPanel.add(buttonPanel, NORTH);
	optionsQuestionPanel.add(optionPanel, CENTER);

	return optionsQuestionPanel;
    }

    public static JPanel createIndiscreteOptions(final Question question) {

	final var optionsQuestionPanel = new JPanel();
	optionsQuestionPanel.setBorder(BorderFactory.createTitledBorder("Options"));
	optionsQuestionPanel.setLayout(new BoxLayout(optionsQuestionPanel, Y_AXIS));

	if (question.getCorrectOptions().size() == 1) { // One selection

	    final var bg = new ButtonGroup();

	    final ItemListener radioItemListener = event -> {
		final var radioEvent = (JRadioButton) event.getSource();
		final var option = substringBefore(remove(radioEvent.getText(), "<html>"), ")");

		if (radioEvent.isSelected()) {
		    question.selectAnswer(option);
		} else {
		    question.deselectAnswer(option);
		}
	    };

	    for (final var questionOption : question.getOptions()) {

		final var radio = new JRadioButton(treatOptionText(questionOption.getId(), questionOption.getText()));
		radio.setFont(DEFAULT_FONT);

		if (question.getAnswers().contains(questionOption.getId())) {
		    radio.setSelected(true);
		}

		radio.addItemListener(radioItemListener);

		bg.add(radio);

		optionsQuestionPanel.add(new JScrollPane(radio));
	    }

	    return optionsQuestionPanel;

	} else { // Multi selection

	    final var box = new Box(Y_AXIS);

	    final ItemListener checkItemListener = event -> {

		final var checkEvent = (JCheckBox) event.getSource();
		final var option = substringBefore(remove(checkEvent.getText(), "<html>"), ")");

		if (checkEvent.isSelected()) {
		    question.selectAnswer(option);
		} else {
		    question.deselectAnswer(option);
		}
	    };

	    for (final var questionOption : question.getOptions()) {

		final var check = new JCheckBox(treatOptionText(questionOption.getId(), questionOption.getText()));
		check.setFont(DEFAULT_FONT);

		if (question.getAnswers().contains(questionOption.getId())) {
		    check.setSelected(true);
		}

		check.addItemListener(checkItemListener);

		box.add(new JScrollPane(check));
	    }

	    optionsQuestionPanel.add(box);
	    return optionsQuestionPanel;
	}
    }

    public static String getStatistic(final Exam exam) {

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

    public static String extractedOptions(final String msg, final List<String> list) {

	final String joined;
	if (list.isEmpty()) {
	    joined = "N/A";
	} else if (list.size() > 1) {
	    int last = list.size() - 1;
	    joined = String.join(" and ", String.join(", ", list.subList(0, last)), list.get(last));
	} else {
	    joined = list.get(0);
	}

	return msg + joined;
    }

    private static String treatOptionText(final String id, final String text) {

	if (StringUtils.containsAny(text, "\n")) { // large text option
	    return "<html>" + id + ")<br>" + replaceAll(text, "[\\n]", "<br>") + "</html>";
	}

	// short text option
	return "<html>" + id + ")<br>" + text + "</html>";
    }
}
