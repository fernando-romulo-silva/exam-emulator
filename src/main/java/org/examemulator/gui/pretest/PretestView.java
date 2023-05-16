package org.examemulator.gui.pretest;

import static java.awt.BorderLayout.CENTER;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.border.EtchedBorder.LOWERED;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.examemulator.gui.components.RangeSlider;
import org.examemulator.gui.components.WrapLayout;

class PretestView extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    JPanel questionInternPanel, examPanel, pQuestions;

    JButton btnNewExam, btnNew, btnPrevious, btnNext, btnSave;

    JLabel lblClock, lblRangeLow, lblUpper, lblQuantity;

    JSpinner textQuantity;

    JComboBox<String> cbGroup;

    RangeSlider rangeQuestions;
    
    JTextField textFieldName;
 
    public PretestView() {
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setBounds(100, 100, 871, 723);

	// -------------------------------------------------------------------------------------------
	// Main Panel
	// -------------------------------------------------------------------------------------------
	setTitle("ExamEmulator!");

	final var contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPane.setLayout(new BoxLayout(contentPane, Y_AXIS));
	setContentPane(contentPane);

	// -------------------------------------------------------------------------------------------
	// Exam Panel
	// -------------------------------------------------------------------------------------------

	examPanel = new JPanel();
//	examPanel.setBorder(new EtchedBorder(LOWERED, null, null));
	examPanel.setLayout(new BoxLayout(examPanel, Y_AXIS));
	contentPane.add(examPanel);

	final var examControlPanel1 = new JPanel();
	examPanel.add(examControlPanel1);
	examControlPanel1.setPreferredSize(new Dimension(80, 40));
	examControlPanel1.setBorder(null);
	examControlPanel1.setLayout(new WrapLayout(LEFT, 5, 5));

	btnNew = new JButton("New");
	btnNew.setEnabled(false);
	examControlPanel1.add(btnNew);

	btnSave = new JButton("Save");
	btnSave.setEnabled(false);
	examControlPanel1.add(btnSave);
	
	btnNewExam = new JButton("New Exam");
	examControlPanel1.add(btnNewExam);

	final var examControlPanel2 = new JPanel();
	examControlPanel2.setLayout(new WrapLayout(LEFT, 5, 5));
	examPanel.add(examControlPanel2);

	final var lblName = new JLabel("Name");
	examControlPanel2.add(lblName);
	lblName.setBorder(new EtchedBorder(LOWERED, null, null));
	
	textFieldName = new JTextField();
	examControlPanel2.add(textFieldName);
	textFieldName.setColumns(10);
	
	lblQuantity = new JLabel("Quantity");
	examControlPanel2.add(lblQuantity);

	textQuantity = new JSpinner(new SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(10), null, Integer.valueOf(10)));
	examControlPanel2.add(textQuantity);
	textQuantity.setEnabled(false);

	final var lblGroup = new JLabel("Group");
	examControlPanel2.add(lblGroup);
	lblGroup.setBorder(new EtchedBorder(LOWERED, null, null));

	cbGroup = new JComboBox<>();
	examControlPanel2.add(cbGroup);
	cbGroup.setEnabled(false);
	cbGroup.addItem("Practice");
	cbGroup.addItem("Exam");
	cbGroup.addItem("Study");

	cbGroup.setSize(200, cbGroup.getPreferredSize().height);

	final var examControlPane3 = new JPanel();
	examControlPane3.setLayout(new WrapLayout(LEFT, 5, 5));
	examPanel.add(examControlPane3);

	final var lblRange = new JLabel("Range");
	examControlPane3.add(lblRange);

	lblRangeLow = new JLabel("1");
	examControlPane3.add(lblRangeLow);

	rangeQuestions = new RangeSlider();
	rangeQuestions.setEnabled(false);
	examControlPane3.add(rangeQuestions);
	rangeQuestions.setPreferredSize(new Dimension(340, rangeQuestions.getPreferredSize().height));
	rangeQuestions.setMinimum(0);
	rangeQuestions.setMaximum(10);

	lblUpper = new JLabel("10");
	examControlPane3.add(lblUpper);

	lblClock = new JLabel("");
	examControlPane3.add(lblClock);
	lblClock.setBorder(new EtchedBorder(LOWERED, null, null));

	// -------------------------------------------------------------------------------------------
	// Control Panel
	// -------------------------------------------------------------------------------------------
	
	pQuestions = new JPanel(new WrapLayout(LEFT, 5, 5));
	examPanel.add(pQuestions);

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
