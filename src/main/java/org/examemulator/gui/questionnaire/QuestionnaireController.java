package org.examemulator.gui.questionnaire;

import static java.awt.BorderLayout.CENTER;
import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.examemulator.application.ExamService;
import org.examemulator.application.QuestionnaireService;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.gui.exam.ExamController;
import org.examemulator.gui.main.MainController;
import org.examemulator.gui.questionnaire.QuestionnaireView.PreExameGui;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuestionnaireController {

	private final QuestionnaireView view;

	private final ExamController examController;

	private final MainController mainController;

	private final ExamService examService;

	private final QuestionnaireService questionnaireService;

	private final Logger logger;

	private final List<Question> toExamQuestions = new ArrayList<>();

	private Questionnaire questionnaire;

	private Question selectedQuestion;

	@Inject
	QuestionnaireController(
			final PreExameGui gui,
			final ExamController examController,
			final MainController mainController,
			final ExamService examService,
			final QuestionnaireService questionnaireService,
			final Logger logger) {
		super();
		this.view = gui.getView();
		this.examController = examController;
		this.mainController = mainController;
		this.examService = examService;
		this.questionnaireService = questionnaireService;
		this.logger = logger;
	}

	@PostConstruct
	void init() {
		initActions();
	}

	public void show(final Component lastView, final Questionnaire questionnaire) {

		this.questionnaire = questionnaire;

		initView();

		SwingUtilities.invokeLater(() -> {

			logger.info("starting swing-event: {}", this.getClass().getSimpleName());

			view.setLocationRelativeTo(lastView);

			view.revalidate();
			view.repaint();
			view.setVisible(true);

			logger.info("finished swing-event: {}", this.getClass().getSimpleName());
		});
	}

	private void initView() {
		view.questionInternPanel.removeAll();
		view.questionInternPanel.revalidate();
		view.questionInternPanel.repaint();

		view.pQuestions.removeAll();
		view.pQuestions.revalidate();
		view.pQuestions.repaint();

		toExamQuestions.clear();
		toExamQuestions.addAll(questionnaire.getQuestions());

		final var questionnaireSet = questionnaire.getSet();
		final var tabText = questionnaireSet.getName()
				.concat(" - ")
				.concat(questionnaire.getName());
		view.contentPane.setBorder(createTitledBorder(tabText));

		view.rdbtnAll.setEnabled(true);
		view.rdbtnNone.setEnabled(true);

		view.btnNewExam.setEnabled(true);
		view.btnNewExam.setEnabled(true);
		view.textDescription.setText(questionnaire.getDescription());

		final var set = questionnaire.getSet();
		view.textSet.setText(set.getName());

		final var certification = questionnaire.getCertification();
		view.textCertification.setText(certification.getName());

		view.textOrder.setText(leftPad(questionnaire.getOrder().toString(), 2, "0"));

		view.btnNext.setEnabled(false);
		view.btnPrevious.setEnabled(false);

		loadNumbersPanel();
		selectFirstQuestion();
		loadPanelQuestion();
	}

	private void initActions() {

		view.btnNext.addActionListener(event -> {

			final var nextQuestionOptional = nextQuestion(questionnaire.getQuestions(), selectedQuestion);
			if (nextQuestionOptional.isPresent()) {
				selectedQuestion = nextQuestionOptional.get();
			}
			loadPanelQuestion();
		});

		view.btnPrevious.addActionListener(event -> {

			final var previousQuestionOptional = previousQuestion(questionnaire.getQuestions(), selectedQuestion);
			if (previousQuestionOptional.isPresent()) {
				selectedQuestion = previousQuestionOptional.get();
			}
			loadPanelQuestion();
		});

		view.btnDelete.addActionListener(event -> {

			if (showConfirmDialog(view, //
					"Are you sure you want to delete this questionnaire?", "Delete Questionnaire", //
					YES_NO_OPTION, //
					QUESTION_MESSAGE) == NO_OPTION) {

				return;
			}

			questionnaireService.delete(questionnaire);

			view.setVisible(false);

			view.btnMain.doClick();

		});

		// view.btnLoad.addActionListener(event -> {
		//
		// final var chooser = new JFileChooser();
		// chooser.setCurrentDirectory(new File("."));
		// chooser.setDialogTitle("select an exam folder");
		// chooser.setFileSelectionMode(DIRECTORIES_ONLY);
		// chooser.setAcceptAllFileFilterUsed(false);
		// chooser.showOpenDialog(view);
		//
		// view.questionInternPanel.removeAll();
		// view.questionInternPanel.revalidate();
		// view.questionInternPanel.repaint();
		//
		// view.pQuestions.removeAll();
		// view.pQuestions.revalidate();
		// view.pQuestions.repaint();
		//
		// currentFolder = Objects.nonNull(chooser.getSelectedFile()) //
		// ? chooser.getSelectedFile().getAbsolutePath() //
		// : StringUtils.EMPTY;
		//
		// if (isNotBlank(currentFolder)) {
		//
		//// questionnaire = loadFromFileService.loadQuestionnaire(currentFolder);
		//
		// }
		// });

		view.btnNewExam.addActionListener(event -> {

			if (toExamQuestions.isEmpty()) {
				showMessageDialog(view, "You need select at least one question!", "Error!", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (toExamQuestions.size() > questionnaire.getQuestions().size()) {
				showMessageDialog(view, "You selected more question than quesitonnaire!", "Error!",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			view.setVisible(false);

			final var questionnaireSet = questionnaire.getSet();
			final var certification = questionnaireSet.getCertification();

			final var examName = toExamQuestions.size() == questionnaire.getQuestions().size()
					? examService.getExamNameBy(questionnaire)
					: examService.getNextExamDynamicNameBy(certification, questionnaireSet, questionnaire);

			examController.show(examName, certification.getMinScorePercent(), toExamQuestions, view);
		});

		view.rdbtnAll.addActionListener(event -> {
			toExamQuestions.addAll(questionnaire.getQuestions());
			loadNumbersPanel();
			loadPanelQuestion();
		});

		view.rdbtnNone.addActionListener(event -> {
			toExamQuestions.clear();
			loadNumbersPanel();
			loadPanelQuestion();
		});

		view.btnMain.addActionListener(event -> {
			view.setVisible(false);
			mainController.show(view);
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

	}

	private void loadPanelQuestion() {
		view.questionInternPanel.removeAll();

		final var question = TAG_BR.concat(TAG_OPEN_B).concat(convertTextToHtml(selectedQuestion.getValue()))
				.concat(TAG_CLOSE_B);

		final var options = selectedQuestion.getOptions() //
				.stream() //
				.map(option -> TAG_OPEN_B.concat(option.getLetter().concat(") ")).concat(TAG_CLOSE_B).concat(TAG_BR)
						.concat(convertTextToHtml(option.getValue()))) //
				.collect(joining(TAG_BR_BR));

		final var correctOptions = TAG_OPEN_B.concat("Correct Answer(s): ").concat(TAG_CLOSE_B)
				.concat(extractedOptions(selectedQuestion.getCorrectOptions()));

		final var explanation = TAG_OPEN_B.concat("Explanation: ").concat(TAG_CLOSE_B).concat(TAG_BR_BR)
				.concat(convertTextToHtml(selectedQuestion.getExplanation()));

		final var txt = question.concat(TAG_BR_BR) //
				.concat(options).concat(TAG_BR_BR) //
				.concat(correctOptions).concat(TAG_BR_BR) //
				.concat(explanation);

		final var optionalConcept = selectedQuestion.getConcept();
		final var conceptName = optionalConcept.isPresent() //
				? " (".concat(optionalConcept.get().getName()).concat(")") //
				: "";

		final var panelQuestionPanel = new JPanel();
		panelQuestionPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent event) {
				markQuestion(panelQuestionPanel);
			}
		});

		final var btnFake = new JButton(EMPTY);
		btnFake.addActionListener(event -> markQuestion(panelQuestionPanel));
		btnFake.setOpaque(false);
		btnFake.setContentAreaFilled(false);
		btnFake.setBorderPainted(false);
		btnFake.setMnemonic(KeyEvent.VK_T);
		panelQuestionPanel.add(btnFake);

		panelQuestionPanel.setLayout(new BorderLayout());
		final var titledBorder = createTitledBorder(
				"Question " + leftPad(selectedQuestion.getOrder().toString(), 2, '0').concat(conceptName));
		if (toExamQuestions.contains(selectedQuestion)) {
			titledBorder.setTitleColor(BLUE);
		} else {
			titledBorder.setTitleColor(BLACK);
		}

		panelQuestionPanel.setBorder(titledBorder);
		panelQuestionPanel.add(createScrollHtmlTextToShow(txt), CENTER);
		panelQuestionPanel.revalidate();
		panelQuestionPanel.repaint();

		view.btnPrevious.setEnabled(hasPreviousQuestion(questionnaire.getQuestions(), selectedQuestion));
		view.btnNext.setEnabled(hasNextQuestion(questionnaire.getQuestions(), selectedQuestion));

		view.questionInternPanel.add(panelQuestionPanel);
		view.questionInternPanel.revalidate();
		view.questionInternPanel.repaint();
	}

	private void markQuestion(final JPanel tempPanel) {

		final var titleBorder = (TitledBorder) tempPanel.getBorder();

		if (toExamQuestions.contains(selectedQuestion)) {
			toExamQuestions.remove(selectedQuestion);
			titleBorder.setTitleColor(BLACK);
		} else {
			toExamQuestions.add(selectedQuestion);
			titleBorder.setTitleColor(BLUE);
		}

		updateQuestionLabel();
		tempPanel.repaint();

		updateRadioButtonsSelection();
	}

	private void loadNumbersPanel() {
		view.pQuestions.removeAll();
		view.pQuestions.revalidate();
		view.pQuestions.repaint();

		final var questionLabelListener = new QuestionLabelListener();

		for (final var question : questionnaire.getQuestions()) {

			final var label = new JLabel(leftPad(question.getOrder().toString(), 2, '0'));

			if (toExamQuestions.contains(question)) {
				label.setForeground(BLUE);
			} else {
				label.setForeground(BLACK);
			}

			label.setName(question.getOrder().toString());
			view.pQuestions.add(label);
			label.addMouseListener(questionLabelListener);
		}
	}

	private void selectFirstQuestion() {

		if (questionnaire.getQuestions().isEmpty()) {
			return;
		}

		selectedQuestion = questionnaire.getQuestions().get(0);
		view.btnPrevious.setEnabled(hasPreviousQuestion(questionnaire.getQuestions(), selectedQuestion));
		view.btnNext.setEnabled(hasNextQuestion(questionnaire.getQuestions(), selectedQuestion));
	}

	private void updateQuestionLabel() {
		final var component = Stream.of(view.pQuestions.getComponents()) //
				.filter(comp -> Objects.equals(comp.getName(), selectedQuestion.getOrder().toString())) //
				.findFirst();

		if (component.isPresent() && component.get() instanceof final JLabel label) {

			if (toExamQuestions.contains(selectedQuestion)) {
				label.setForeground(BLUE);
			} else {
				label.setForeground(BLACK);
			}

			label.repaint();
		}
	}

	private void updateRadioButtonsSelection() {

		if (toExamQuestions.isEmpty()) {
			view.rdbtnNone.setSelected(true);
		} else {

			final var questionnaireQuestionSize = nonNull(questionnaire) ? questionnaire.getQuestions().size() : 0;

			if (toExamQuestions.size() < questionnaireQuestionSize) {
				view.rdbtnAny.setSelected(true);
			} else {
				view.rdbtnAll.setSelected(true);
			}
		}
	}

	private class QuestionLabelListener extends MouseAdapter {

		@Override
		public void mouseClicked(final MouseEvent event) {

			final var label = (JLabel) event.getSource();
			final var text = label.getText();

			if (isLeftMouseButton(event) && event.getClickCount() == 1 && nonNull(questionnaire)) {

				selectQuestion(toInt(text));

				if (toExamQuestions.contains(selectedQuestion)) {
					label.setForeground(BLUE);
				} else {
					label.setForeground(BLACK);
				}

				loadPanelQuestion();

				label.repaint();

			} else if (isRightMouseButton(event) && event.getClickCount() == 1 && nonNull(questionnaire)) {

				selectQuestion(toInt(text));

				if (toExamQuestions.contains(selectedQuestion)) {
					toExamQuestions.remove(selectedQuestion);
					label.setForeground(BLACK);
				} else {
					toExamQuestions.add(selectedQuestion);
					label.setForeground(BLUE);
				}

				updateRadioButtonsSelection();

				loadPanelQuestion();

				label.repaint();
			}
		}

		private void selectQuestion(int order) {

			final var questionOptional = questionnaire.getQuestions() //
					.stream() //
					.filter(question -> Objects.equals(question.getOrder(), order)) //
					.findFirst();

			if (questionOptional.isEmpty()) {
				return;
			}

			selectedQuestion = questionOptional.get();

			view.btnPrevious.setEnabled(hasPreviousQuestion(questionnaire.getQuestions(), selectedQuestion));
			view.btnNext.setEnabled(hasNextQuestion(questionnaire.getQuestions(), selectedQuestion));
		}
	}
}