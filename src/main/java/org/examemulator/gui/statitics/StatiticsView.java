package org.examemulator.gui.statitics;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.BoxLayout.Y_AXIS;
import static org.examemulator.gui.GuiUtil.APP_NAME;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.examemulator.gui.components.WrapLayout;

import jakarta.enterprise.context.ApplicationScoped;

class StatiticsView extends JFrame {

    private static final long serialVersionUID = 1L;
    
    @ApplicationScoped
    static class StatiticsGui {

	StatiticsView getView() {
	    return new StatiticsView();
	}
    }

    JPanel pQuestion, pQuestions, pMain, contentPane;

    JCheckBox chckbxCorrects, chckbxIncorrects, chckbxMarked;

    JLabel lblStatistic;

    JButton btnNext, btnPrevious, okButton, newExamButton;

    StatiticsView() {
	super();
	
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setBounds(100, 100, 871, 723);
	
	setTitle(APP_NAME.concat(" - Exam Statitics"));
	
	contentPane = new JPanel();
	contentPane.setLayout(new BoxLayout(contentPane, Y_AXIS));
	contentPane.setBorder(createTitledBorder("No Exam"));
	setContentPane(contentPane);

	final var dialogContainer = getContentPane();
	dialogContainer.setLayout(new BorderLayout());

	pMain = new JPanel();
	pMain.setLayout(new BoxLayout(pMain, Y_AXIS));

	getContentPane().add(pMain, CENTER);

	final var pGroup = new JPanel();
	pGroup.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
	pGroup.setLayout(new BoxLayout(pGroup, BoxLayout.Y_AXIS));
	pMain.add(pGroup);

	final var panelStatistic = new JPanel(new FlowLayout(LEFT, 5, 5));
	pGroup.add(panelStatistic);

	lblStatistic = new JLabel("New label");
	panelStatistic.add(lblStatistic);

	final var panelControl = new JPanel(new FlowLayout(LEFT, 5, 5));
	pGroup.add(panelControl);

	btnPrevious = new JButton("Previous");
	btnPrevious.setEnabled(false);
	panelControl.add(btnPrevious);

	btnNext = new JButton("Next");
	panelControl.add(btnNext);

	chckbxCorrects = new JCheckBox("Corrects");
	chckbxCorrects.setSelected(true);
	panelControl.add(chckbxCorrects);

	chckbxIncorrects = new JCheckBox("Incorrects");
	chckbxIncorrects.setSelected(true);
	panelControl.add(chckbxIncorrects);

	chckbxMarked = new JCheckBox("Marked");
	panelControl.add(chckbxMarked);

	pQuestions = new JPanel(new WrapLayout(LEFT, 5, 5));
	pGroup.add(pQuestions);

	pQuestion = new JPanel();
	pQuestion.setLayout(new BorderLayout(0, 0));
	pMain.add(pQuestion);

	okButton = new JButton("Ok");
	okButton.addActionListener(okEvent -> setVisible(false));
	
	newExamButton = new JButton("New Exam");
	
	final var panelButtons = new JPanel(new FlowLayout());
	panelButtons.add(okButton);
	panelButtons.add(newExamButton);
	
	getContentPane().add(panelButtons, SOUTH);
    }
}
