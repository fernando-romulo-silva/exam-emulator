package org.examemulator.gui.exam;

import static java.awt.BorderLayout.CENTER;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.BoxLayout.Y_AXIS;
import static org.examemulator.infra.util.gui.GuiUtil.APP_NAME;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.examemulator.gui.components.WrapLayout;

import jakarta.enterprise.context.ApplicationScoped;

class ExamView extends JFrame {

	private static final long serialVersionUID = 1L;

	@ApplicationScoped
	static class ExamGui {

		ExamView getView() {
			return new ExamView();
		}
	}

	JPanel questionInternPanel, examPanel, pQuestions, contentPane;

	JButton btnPauseProceed, btnStart, btnFinish, btnPrevious, btnNext, btnCheckAnswer, btnStatistics;

	JLabel lblClock, lblDuration;

	JSpinner textDiscrete, textMinScore, spinnerTimeDuration;

	JCheckBox chckbxShuffleQuestions, chckbxShuffleOptions;

	JComboBox<String> cbMode;
	private JLabel lblShuffleQuestions;
	private JLabel lblShuffleOptions;

	ExamView() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 871, 723);

		// -------------------------------------------------------------------------------------------
		// Main Panel
		// -------------------------------------------------------------------------------------------
		setTitle(APP_NAME.concat(" - Exam"));

		contentPane = new JPanel();
		contentPane.setBorder(createTitledBorder("No Exam"));
		contentPane.setLayout(new BoxLayout(contentPane, Y_AXIS));
		setContentPane(contentPane);

		// -------------------------------------------------------------------------------------------
		// Exam Panel
		// -------------------------------------------------------------------------------------------

		examPanel = new JPanel();
		examPanel.setBorder(null);
		examPanel.setLayout(new BoxLayout(examPanel, Y_AXIS));
		contentPane.add(examPanel);

		final var examControlPanel1 = new JPanel();
		examPanel.add(examControlPanel1);
		examControlPanel1.setPreferredSize(new Dimension(80, 40));
		examControlPanel1.setBorder(null);
		examControlPanel1.setLayout(new WrapLayout(LEFT, 5, 5));

		btnStart = new JButton("Start");
		btnStart.setEnabled(false);
		btnStart.setMnemonic(KeyEvent.VK_S);
		examControlPanel1.add(btnStart);

		btnPauseProceed = new JButton("Pause");
		btnPauseProceed.setEnabled(false);
		btnPauseProceed.setMnemonic(KeyEvent.VK_P);
		examControlPanel1.add(btnPauseProceed);

		btnFinish = new JButton("Finish");
		btnFinish.setEnabled(false);
		btnFinish.setMnemonic(KeyEvent.VK_F);
		examControlPanel1.add(btnFinish);

		btnStatistics = new JButton("Statistics");
		btnStatistics.setEnabled(false);
		btnStatistics.setMnemonic(KeyEvent.VK_T);
		examControlPanel1.add(btnStatistics);

		final var examControlPanel2 = new JPanel();
		examControlPanel2.setLayout(new WrapLayout(LEFT, 5, 5));
		examPanel.add(examControlPanel2);

		lblShuffleQuestions = new JLabel("Shuffle Questions");
		examControlPanel2.add(lblShuffleQuestions);

		chckbxShuffleQuestions = new JCheckBox("");
		chckbxShuffleQuestions.setHorizontalTextPosition(SwingConstants.LEFT);
		chckbxShuffleQuestions.setEnabled(false);
		chckbxShuffleQuestions.setSelected(true);
		examControlPanel2.add(chckbxShuffleQuestions);

		lblShuffleOptions = new JLabel("Shuffle Options");
		examControlPanel2.add(lblShuffleOptions);

		chckbxShuffleOptions = new JCheckBox("");
		chckbxShuffleOptions.setEnabled(false);
		chckbxShuffleOptions.setSelected(true);
		chckbxShuffleOptions.setHorizontalTextPosition(SwingConstants.LEFT);
		examControlPanel2.add(chckbxShuffleOptions);

		final var lblDiscretePercent = new JLabel("Discrete (%)");
		examControlPanel2.add(lblDiscretePercent);

		textDiscrete = new JSpinner(new SpinnerNumberModel(000, 0, 100, 10));
		examControlPanel2.add(textDiscrete);
		textDiscrete.setEnabled(false);

		final var examControlPanel3 = new JPanel();
		examControlPanel3.setLayout(new WrapLayout(LEFT, 5, 5));
		examPanel.add(examControlPanel3);

		final var lblMinScore = new JLabel("Min Score (%)");
		examControlPanel3.add(lblMinScore);

		textMinScore = new JSpinner(new SpinnerNumberModel(90, 50, 100, 10));
		textMinScore.setEnabled(false);
		examControlPanel3.add(textMinScore);

		final var lblMode = new JLabel("Mode");
		examControlPanel3.add(lblMode);

		cbMode = new JComboBox<>();
		examControlPanel3.add(cbMode);
		cbMode.setEnabled(false);
		cbMode.addItem("Practice");
		cbMode.addItem("Exam");

		cbMode.setSize(200, cbMode.getPreferredSize().height);

		lblDuration = new JLabel("Duration (Min)");
		examControlPanel3.add(lblDuration);

		spinnerTimeDuration = new JSpinner(new SpinnerNumberModel(60, 10, 120, 10));
		examControlPanel3.add(spinnerTimeDuration);
		spinnerTimeDuration.setEnabled(false);

		lblClock = new JLabel("");
		examControlPanel3.add(lblClock);

		// -------------------------------------------------------------------------------------------
		// Control Panel
		// -------------------------------------------------------------------------------------------

		final var quesitonControlPanel = new JPanel();
		examPanel.add(quesitonControlPanel);
		quesitonControlPanel.setPreferredSize(new Dimension(80, 40));
		quesitonControlPanel.setLayout(new WrapLayout(LEFT, 5, 5));
		quesitonControlPanel.setBorder(null);

		btnPrevious = new JButton("Previous");
		btnPrevious.setEnabled(false);
		btnPrevious.setMnemonic(KeyEvent.VK_MINUS);
		quesitonControlPanel.add(btnPrevious);

		btnNext = new JButton("Next");
		btnNext.setEnabled(false);
		btnNext.setMnemonic(KeyEvent.VK_EQUALS);
		quesitonControlPanel.add(btnNext);

		btnCheckAnswer = new JButton("Answer");
		btnCheckAnswer.setEnabled(false);
		btnCheckAnswer.setVisible(false);
		btnCheckAnswer.setMnemonic(KeyEvent.VK_W);
		quesitonControlPanel.add(btnCheckAnswer);

		pQuestions = new JPanel(new WrapLayout(LEFT, 5, 5));
		examPanel.add(pQuestions);

		// -------------------------------------------------------------------------------------------
		// Question Panel
		// -------------------------------------------------------------------------------------------

		final var questionPanel = new JPanel();
		questionPanel.setLayout(new BorderLayout());

		questionInternPanel = new JPanel();
		questionInternPanel.setLayout(new BoxLayout(questionInternPanel, Y_AXIS));
		questionPanel.add(questionInternPanel, CENTER);

		contentPane.add(questionPanel);
	}
}
