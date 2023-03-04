package org.examemulator.gui.statitics;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEFT;
import static javax.swing.BoxLayout.Y_AXIS;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.examemulator.gui.components.WrapLayout;

class StatiticsView extends JDialog {

    private static final long serialVersionUID = 1L;

    JPanel pQuestion, pQuestions, pMain;

    JCheckBox chckbxCorrects, chckbxIncorrects, chckbxMarked;

    JLabel lblStatistic;

    JButton btnNext, btnPrevious;

    StatiticsView() {
	super();
	
	setSize(600, 500);
	setTitle("Statistic Exam");
	setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
	setModal(true);

	final var dialogContainer = getContentPane();
	dialogContainer.setLayout(new BorderLayout());

	pMain = new JPanel();
	pMain.setLayout(new BoxLayout(pMain, Y_AXIS));

	add(pMain, CENTER);

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

	final var okButton = new JButton("Ok");
	okButton.addActionListener(okEvent -> setVisible(false));

	final var panelButton = new JPanel(new FlowLayout());
	panelButton.add(okButton);

	add(panelButton, SOUTH);
    }
}
