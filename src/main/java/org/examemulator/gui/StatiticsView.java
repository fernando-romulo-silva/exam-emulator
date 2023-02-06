package org.examemulator.gui;

import static java.awt.FlowLayout.LEFT;
import static javax.swing.BoxLayout.Y_AXIS;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.examemulator.gui.components.WrapLayout;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class StatiticsView extends JPanel {

    private static final long serialVersionUID = 1L;

    JPanel pQuestion, pQuestions;

    JCheckBox chckbxCorrects, chckbxIncorrects, chckbxMarked;

    JLabel lblStatistic;
    
    JButton btnNext, btnPrevious;

    public StatiticsView() {
	this.setBounds(100, 100, 774, 720);
	this.setBorder(new EmptyBorder(5, 5, 5, 5));
	this.setLayout(new BoxLayout(this, Y_AXIS));

	final var pGroup = new JPanel();
	pGroup.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
	pGroup.setLayout(new BoxLayout(pGroup, BoxLayout.Y_AXIS));
	add(pGroup);

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
	add(pQuestion);
    }
}
