package org.examemulator.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.FlowLayout.LEFT;
import static java.util.Objects.nonNull;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import static org.apache.commons.lang3.RegExUtils.replaceAll;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringsBetween;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.exam.ExamQuestion;
import org.examemulator.gui.components.WrapLayout;

public class GuiUtil {

    private static final int SIXTY_VALUE = 60;

    public static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    public static final int MILLISECOND = 1000;
    
    public static final String TAG_BR = "<br />";
    
    public static final String TAG_BR_BR = "<br /> <br />";
    
    public static final String TAG_OPEN_B = "<b>";
    
    public static final String TAG_CLOSE_B = "</b>";
    
    public static final String APP_NAME = "ExamEmulator";

    @FunctionalInterface
    public static interface Action {
	void execute();
    }

    private static String treatOptionText(final String id, final String text) {

	if (StringUtils.containsAny(text, "\n")) { // large text option
	    return "<html>" + id + ")"+ TAG_BR + replaceAll(text, "[\\n]", TAG_BR) + "</html>";
	}

	// short text option
	return "<html>" + id + ")" + TAG_BR + text + "</html>";
    }
    
    
    public static String convertTextToHtml(final String text) {
	var textTemp = text.trim();
	
	final var formatteds = substringsBetween(textTemp, "```", "´´´");
	if (nonNull(formatteds)) {
	    int i = 1;
	    for (final var formatted : formatteds) {
		textTemp = replace(textTemp, formatted, "$"+i);
		i++;
	    }
	}
	
	textTemp = replace(textTemp, "```", "");
	textTemp = replace(textTemp, "´´´", "");
	textTemp = replace(textTemp, "&", "&amp;");
	textTemp = replace(textTemp, "<", "&lt;");
	textTemp = replace(textTemp, ">", "&gt;");
	textTemp = replace(textTemp, "\n", " <br />");
	
	if (nonNull(formatteds)) {
	    int i = 1;
	    for (final var formatted : formatteds) {
		textTemp = replace(textTemp, "$"+i, "<pre>"+formatted+"</pre>");
		i++;
	    }
	}
	
	return textTemp;
    }
    
    public static JComponent createScrollHtmlTextToShow(final String text) {
	final var textComponent = new JEditorPane();
	textComponent.setEditable(false);
	final var kit = new HTMLEditorKit();
	textComponent.setEditorKit(kit);
	var doc = kit.createDefaultDocument();
	textComponent.setDocument(doc);
	textComponent.setText("<html> <body>" +text+" </body> </html>");
	textComponent.setMinimumSize(new Dimension(100, 100));
	textComponent.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
	textComponent.setFont(DEFAULT_FONT);

	return new JScrollPane(textComponent, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
    }

    public static JComponent createScrollTextToShow(final String text) {
	
	var textTemp = replace(text, "```", "");
	textTemp = replace(textTemp, "´´´", "");
	
	final var textComponent = new JTextArea();
	textComponent.setText(textTemp);
	textComponent.setEditable(false);
	textComponent.setLineWrap(true);
	textComponent.setWrapStyleWord(true);
	textComponent.setFont(DEFAULT_FONT);

	return new JScrollPane(textComponent, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
    }

    public static ActionListener createTimerAction(final Integer duration, final JLabel jlabel, final Action action) {
	
	return new ActionListener() {

	    private long time = (long) duration * MILLISECOND * SIXTY_VALUE;

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

    public static JPanel createDiscreteOptions(final ExamQuestion question) {
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
	    final var letter = option.getLetter();
	    optionsLabels.add(letter + "|" + option.getValue());
	}

	final var substringAfter = substringAfter(optionsLabels.get(0), "|");
	optionPanel.add(createTextToShow(substringAfter));

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

		    optionPanel.add(createTextToShow(substringAfter(current, "|")));
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

    public static JPanel createIndiscreteOptions(final ExamQuestion question) {

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

		final var radio = new JRadioButton(treatOptionText(questionOption.getLetter(), questionOption.getValue()));
		radio.setFont(DEFAULT_FONT);

		if (question.getAnswers().contains(questionOption.getLetter())) {
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

		final var check = new JCheckBox(treatOptionText(questionOption.getLetter(), questionOption.getValue()));
		check.setFont(DEFAULT_FONT);

		if (question.getAnswers().contains(questionOption.getLetter())) {
		    check.setSelected(true);
		}

		check.addItemListener(checkItemListener);

		box.add(new JScrollPane(check));
	    }

	    optionsQuestionPanel.add(box);
	    return optionsQuestionPanel;
	}
    }

    public static String extractedOptions(final List<String> list) {

	final String joined;
	if (list.isEmpty()) {
	    joined = "N/A";
	} else if (list.size() > 1) {
	    int last = list.size() - 1;
	    joined = String.join(" and ", String.join(", ", list.subList(0, last)), list.get(last));
	} else {
	    joined = list.get(0);
	}

	return joined;
    }
    
    private static JTextArea createTextToShow(final String text) {
	
	var textTemp = replace(text, "```", "");
	textTemp = replace(textTemp, "´´´", "");
	
	final var textComponent = new JTextArea(textTemp);
	textComponent.setBorder(new EmptyBorder(0, 0, 0, 0));
	textComponent.setOpaque(false);
	textComponent.setEditable(false);
	textComponent.setFont(DEFAULT_FONT);
	return textComponent;
    }
}