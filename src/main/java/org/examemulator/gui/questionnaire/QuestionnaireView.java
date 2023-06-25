package org.examemulator.gui.questionnaire;

import static java.awt.BorderLayout.CENTER;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.border.EtchedBorder.LOWERED;
import static org.examemulator.gui.GuiUtil.APP_NAME;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.examemulator.gui.components.WrapLayout;

import jakarta.enterprise.context.ApplicationScoped;

class QuestionnaireView extends JFrame {

    private static final long serialVersionUID = 1L;

    @ApplicationScoped
    static class PreExameGui {

	QuestionnaireView getView() {
	    return new QuestionnaireView();
	}
    }

    JPanel questionInternPanel, examPanel, pQuestions, contentPane;

    JButton btnNewExam, btnDelete, btnLoad, btnPrevious, btnNext;

    JLabel lblOrder, lblCertification;

    JTextField textDescription, textSet, textCertification, textOrder;
    
    ButtonGroup bgSelection;
    
    JRadioButton rdbtnAll, rdbtnNone, rdbtnAny;

    QuestionnaireView() {
	
	super();
	
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setBounds(100, 100, 871, 723);

	// -------------------------------------------------------------------------------------------
	// Main Panel
	// -------------------------------------------------------------------------------------------
	setTitle(APP_NAME.concat(" - Questionnaire"));

	contentPane = new JPanel();
	contentPane.setBorder(createTitledBorder("No Questionnaire"));
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

	btnLoad = new JButton("Load");
	examControlPanel1.add(btnLoad);

	btnDelete = new JButton("Delete");
	btnDelete.setEnabled(false);
	examControlPanel1.add(btnDelete);

	btnNewExam = new JButton("Exam");
	btnNewExam.setEnabled(false);
	examControlPanel1.add(btnNewExam);

	final var examControlPanel2 = new JPanel();
	examControlPanel2.setLayout(new WrapLayout(LEFT, 5, 5));
	examPanel.add(examControlPanel2);

	final var lblDescription = new JLabel("Description");
	examControlPanel2.add(lblDescription);
	lblDescription.setBorder(new EtchedBorder(LOWERED, null, null));

	textDescription = new JTextField();
	textDescription.setEnabled(false);
	examControlPanel2.add(textDescription);
	textDescription.setColumns(30);

	lblOrder = new JLabel("Order");
	examControlPanel2.add(lblOrder);

	textOrder = new JTextField();
	textOrder.setEnabled(false);
	textOrder.setColumns(5);
	examControlPanel2.add(textOrder);

	final var examControlPanel3 = new JPanel(new WrapLayout(LEFT, 5, 5));
	examPanel.add(examControlPanel3);

	final var lblSet = new JLabel("Set");
	examControlPanel3.add(lblSet);
	lblSet.setBorder(new EtchedBorder(LOWERED, null, null));

	textSet = new JTextField();
	textSet.setEnabled(false);
	examControlPanel3.add(textSet);
	textSet.setColumns(16);

	lblCertification = new JLabel("Certification");
	examControlPanel3.add(lblCertification);

	textCertification = new JTextField();
	textCertification.setEnabled(false);
	examControlPanel3.add(textCertification);
	textCertification.setColumns(20);

	final var examControlPanel4 = new JPanel();
	examControlPanel4.setLayout(new WrapLayout(LEFT, 5, 5));
	examPanel.add(examControlPanel4);

	final var lblRange = new JLabel("Selection");
	examControlPanel4.add(lblRange);
	
	rdbtnAll = new JRadioButton("All");
	rdbtnAll.setSelected(true);
	rdbtnAll.setEnabled(false);
	examControlPanel4.add(rdbtnAll);
	
	rdbtnNone = new JRadioButton("None");
	rdbtnNone.setSelected(false);
	rdbtnNone.setEnabled(false);
	examControlPanel4.add(rdbtnNone);
	
	rdbtnAny = new JRadioButton("Any");
	rdbtnAny.setSelected(false);
	rdbtnAny.setEnabled(false);
	examControlPanel4.add(rdbtnAny);
	
	bgSelection = new ButtonGroup();
	bgSelection.add(rdbtnAll);
	bgSelection.add(rdbtnNone);
	bgSelection.add(rdbtnAny);

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
