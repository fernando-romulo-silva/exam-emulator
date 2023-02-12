package org.examemulator.gui.exam;

import static java.awt.BorderLayout.CENTER;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.border.EtchedBorder.LOWERED;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.examemulator.gui.components.RangeSlider;
import org.examemulator.gui.components.WrapLayout;

import jakarta.enterprise.context.RequestScoped;

public class ExamView extends JFrame {

    private static final long serialVersionUID = 1L;

    JPanel questionInternPanel, examPanel, pQuestions;

    JButton btnStart, btnFinish, btnPrevious, btnNext, btnCheckAnswer, btnStatistics;

    JLabel lblClock, lblRangeLow, lblUpper, lblDuration;

    JSpinner textDiscrete, textMinScore, spinnerTimeDuration;

    JCheckBox chckbxMark;

    JMenuItem mntmNew;

    JComboBox<String> cbMode;

    RangeSlider rangeQuestions;

    public ExamView() {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 774, 720);

	// -------------------------------------------------------------------------------------------
	// Main Panel
	// -------------------------------------------------------------------------------------------
	setTitle("ExamEmulator!");
	
	final var menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	final var mnNewMenu = new JMenu("Exam");
	menuBar.add(mnNewMenu);

	mntmNew = new JMenuItem("New");
	mnNewMenu.add(mntmNew);

	final var mntmStop = new JMenuItem("Stop");
	mnNewMenu.add(mntmStop);

	final var contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPane.setLayout(new BoxLayout(contentPane, Y_AXIS));
	setContentPane(contentPane);

	// -------------------------------------------------------------------------------------------
	// Exam Panel
	// -------------------------------------------------------------------------------------------

	examPanel = new JPanel();
	examPanel.setBorder(new EtchedBorder(LOWERED, null, null));
	examPanel.setLayout(new BoxLayout(examPanel, Y_AXIS));
	contentPane.add(examPanel);

	final var examControlPanel1 = new JPanel();
	examPanel.add(examControlPanel1);
	examControlPanel1.setPreferredSize(new Dimension(80, 40));
	examControlPanel1.setBorder(null);
	examControlPanel1.setLayout(new WrapLayout(LEFT, 5, 5));

	btnStart = new JButton("Start");
	btnStart.setEnabled(false);
	examControlPanel1.add(btnStart);

	btnFinish = new JButton("Finish");
	btnFinish.setEnabled(false);
	examControlPanel1.add(btnFinish);

	btnStatistics = new JButton("Statistics");
	btnStatistics.setEnabled(false);
	examControlPanel1.add(btnStatistics);

	final var lblMode = new JLabel("Mode");
	lblMode.setBorder(new EtchedBorder(LOWERED, null, null));
	examControlPanel1.add(lblMode);

	cbMode = new JComboBox<String>();
	cbMode.setEnabled(false);
	cbMode.addItem("Practice");
	cbMode.addItem("Exam");

	cbMode.setSize(200, cbMode.getPreferredSize().height);
	examControlPanel1.add(cbMode);

	final var lblDiscretePercent = new JLabel("Discrete (%)");
	lblDiscretePercent.setBorder(new EtchedBorder(LOWERED, null, null));
	examControlPanel1.add(lblDiscretePercent);

	textDiscrete = new JSpinner(new SpinnerNumberModel(0, 0, 100, 10));
	textDiscrete.setEnabled(false);
	examControlPanel1.add(textDiscrete);

	lblDuration = new JLabel("Duration (Min)");
	examControlPanel1.add(lblDuration);

	spinnerTimeDuration = new JSpinner(new SpinnerNumberModel(60, 10, 120, 10));
	spinnerTimeDuration.setEnabled(false);

	examControlPanel1.add(spinnerTimeDuration);

	final var examControlPane2 = new JPanel();
	examControlPane2.setLayout(new WrapLayout(LEFT, 5, 5));
	examPanel.add(examControlPane2);

	final var lblMinScore = new JLabel("Min Score (%)");
	examControlPane2.add(lblMinScore);

	textMinScore = new JSpinner(new SpinnerNumberModel(70, 50, 100, 10));
	textMinScore.setEnabled(false);
	examControlPane2.add(textMinScore);

	final var lblRange = new JLabel("Range");
	examControlPane2.add(lblRange);

	lblRangeLow = new JLabel("1");
	examControlPane2.add(lblRangeLow);

	rangeQuestions = new RangeSlider();
	rangeQuestions.setEnabled(false);
	examControlPane2.add(rangeQuestions);
	rangeQuestions.setPreferredSize(new Dimension(340, rangeQuestions.getPreferredSize().height));
	rangeQuestions.setMinimum(0);
	rangeQuestions.setMaximum(10);

	lblUpper = new JLabel("10");
	examControlPane2.add(lblUpper);

	lblClock = new JLabel("");
	examControlPane2.add(lblClock);
	lblClock.setBorder(new EtchedBorder(LOWERED, null, null));

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
	quesitonControlPanel.add(btnPrevious);

	btnNext = new JButton("Next");
	btnNext.setEnabled(false);
	quesitonControlPanel.add(btnNext);

	chckbxMark = new JCheckBox("Mark");
	chckbxMark.setEnabled(false);
	quesitonControlPanel.add(chckbxMark);

	btnCheckAnswer = new JButton("Check Answer");
	btnCheckAnswer.setEnabled(false);
	btnCheckAnswer.setVisible(false);
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
