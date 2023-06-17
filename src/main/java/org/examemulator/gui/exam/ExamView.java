package org.examemulator.gui.exam;

import static java.awt.BorderLayout.CENTER;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.border.EtchedBorder.LOWERED;
import static org.examemulator.gui.GuiUtil.APP_NAME;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;

import org.examemulator.gui.components.WrapLayout;

import jakarta.enterprise.context.ApplicationScoped;

class ExamView extends JFrame {

    private static final long serialVersionUID = 1L;
    
    @ApplicationScoped
    static class ExamGui {
	
	ExamView getView() {
	    return new ExamView();
	}
	
//	    public void showMainWindow() {
//		exec("show main window", () -> examView.setVisible(true));
//	    }
	//
//	    private void exec(final String name, final Runnable func) {
//		log.info("starting swing-invoke: " + name);
	//
//		SwingUtilities.invokeLater(() -> {
//		    log.info("starting swing-event: " + name);
//		    func.run();
//		    log.info("finished swing-event: " + name);
//		});
	//
//		log.info("finished swing-invoke: " + name);
//	    }
	//
//	    public void onBoot(@Observes final BootEvent event) {
//		log.info("starting cdi-event: boot");
//		showMainWindow();
//		log.info("finished cdi-event: boot");
//	    }
	//    
//	    public static interface BootEvent {}	
    }

    JPanel questionInternPanel, examPanel, pQuestions, contentPane;

    JButton btnPauseProceed, btnStart, btnFinish, btnPrevious, btnNext, btnCheckAnswer, btnStatistics;

    JLabel lblClock, lblDuration;

    JSpinner textDiscrete, textMinScore, spinnerTimeDuration;

    JCheckBox chckbxMark, chckbxShuffleQuestions, chckbxShuffleOptions;

    JComboBox<String> cbMode;

    ExamView() {
	setDefaultCloseOperation(EXIT_ON_CLOSE);
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
	
	btnPauseProceed = new JButton("Pause");
	btnPauseProceed.setEnabled(false);
	examControlPanel1.add(btnPauseProceed);

	btnFinish = new JButton("Finish");
	btnFinish.setEnabled(false);
	examControlPanel1.add(btnFinish);

	btnStatistics = new JButton("Statistics");
	btnStatistics.setEnabled(false);
	examControlPanel1.add(btnStatistics);

	final var examControlPanel2 = new JPanel();
	examControlPanel2.setLayout(new WrapLayout(LEFT, 5, 5));
	examPanel.add(examControlPanel2);
	
	chckbxShuffleQuestions = new JCheckBox("Shuffle Questions");
	chckbxShuffleQuestions.setEnabled(false);
	chckbxShuffleQuestions.setSelected(false);
	examControlPanel2.add(chckbxShuffleQuestions);
	
	chckbxShuffleOptions = new JCheckBox("Shuffle Options");
	chckbxShuffleOptions.setEnabled(false);
	chckbxShuffleOptions.setSelected(true);
	examControlPanel2.add(chckbxShuffleOptions);

	final var lblDiscretePercent = new JLabel("Discrete (%)");
	examControlPanel2.add(lblDiscretePercent);
	lblDiscretePercent.setBorder(new EtchedBorder(LOWERED, null, null));

	textDiscrete = new JSpinner(new SpinnerNumberModel(100, 0, 100, 10));
	examControlPanel2.add(textDiscrete);
	textDiscrete.setEnabled(false);

	final var lblMode = new JLabel("Mode");
	examControlPanel2.add(lblMode);
	lblMode.setBorder(new EtchedBorder(LOWERED, null, null));

	cbMode = new JComboBox<>();
	examControlPanel2.add(cbMode);
	cbMode.setEnabled(false);
	cbMode.addItem("Practice");
	cbMode.addItem("Exam");
	cbMode.addItem("Study");

	cbMode.setSize(200, cbMode.getPreferredSize().height);

	final var examControlPane3 = new JPanel();
	examControlPane3.setLayout(new WrapLayout(LEFT, 5, 5));
	examPanel.add(examControlPane3);

	final var lblMinScore = new JLabel("Min Score (%)");
	examControlPane3.add(lblMinScore);

	textMinScore = new JSpinner(new SpinnerNumberModel(90, 50, 100, 10));
	textMinScore.setEnabled(false);
	examControlPane3.add(textMinScore);
	
	lblDuration = new JLabel("Duration (Min)");
	examControlPane3.add(lblDuration);

	spinnerTimeDuration = new JSpinner(new SpinnerNumberModel(60, 10, 120, 10));
	examControlPane3.add(spinnerTimeDuration);
	spinnerTimeDuration.setEnabled(false);

	lblClock = new JLabel("");
	examControlPane3.add(lblClock);
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
