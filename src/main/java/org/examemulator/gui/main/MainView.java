package org.examemulator.gui.main;

import static java.awt.FlowLayout.LEFT;
import static javax.swing.SwingConstants.TOP;
import static org.examemulator.gui.GuiUtil.APP_NAME;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;

import jakarta.enterprise.context.ApplicationScoped;

class MainView extends JFrame {

    private static final long serialVersionUID = 1L;
    
    public static final int HEADER_HEIGHT = 32;

    private static final class ExtendedJTable extends JXTable {

	private static final long serialVersionUID = 1L;

	private ExtendedJTable(TableModel dm) {
	    super(dm);
	}

	@Override
	public boolean editCellAt(int row, int column, java.util.EventObject e) {
	    return false;
	}
    }

    @ApplicationScoped
    static class MainGui {

	MainView getView() {
	    return new MainView();
	}
    }

    JPanel pMain, contentPane, pFirstData;

    JLabel lblCertificationStatistics;
    
    JXTable examTable, questionsTable;
    
    JTabbedPane tabbedPane;
    
    JScrollPane spExams, spQuestions, spData;
    
    JPopupMenu popupMenuCertification;
    
    JMenuItem menuItemLoadCertification, menuItemStatiticsCertification;
    
    JTree trData;
    
    MainView() {
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	setBounds(100, 100, 871, 723);

	setTitle(APP_NAME);

	contentPane = new JPanel();
	contentPane.setLayout(new BorderLayout());
	setContentPane(contentPane);

	pMain = new JPanel();
	pMain.setLayout(new BorderLayout());
	pMain.setPreferredSize(new Dimension(80, 200));
	contentPane.add(pMain, BorderLayout.NORTH);

	final var pCertificationStatistics = new JPanel(new FlowLayout(LEFT, 5, 5));
	pMain.add(pCertificationStatistics, BorderLayout.NORTH);

	lblCertificationStatistics = new JLabel("Certifications");
	pCertificationStatistics.add(lblCertificationStatistics);

	menuItemLoadCertification = new JMenuItem("Load from file");
	menuItemStatiticsCertification = new JMenuItem("Show the statitics");
	
	popupMenuCertification = new JPopupMenu();
	popupMenuCertification.add(menuItemLoadCertification);
	popupMenuCertification.add(menuItemStatiticsCertification);
	
	trData = new JTree();
	trData.setEditable(false);
	trData.setRootVisible(false);
	
	pFirstData = new JPanel();
	
	spData = new JScrollPane(trData);
	pMain.add(spData, BorderLayout.CENTER);
	
	tabbedPane = new JTabbedPane(TOP);
	contentPane.add(tabbedPane, BorderLayout.CENTER);
	
        Object[] columnNamesQuestion = { "id", "value", "Correct Answers" };
        Object[][] rowDataQuestion = { 
                { "1", "What your name?", "04" },
        };
	
	questionsTable = new ExtendedJTable(new DefaultTableModel(rowDataQuestion, columnNamesQuestion));
	questionsTable.setCellSelectionEnabled(false);
	questionsTable.setRowSelectionAllowed(true);
	
	spQuestions = new JScrollPane(questionsTable);
	tabbedPane.addTab("Questions", null, spQuestions, null);	
	
        Object[] columnNamesExam = { "id", "name", "status" };
        Object[][] rowDataExam = { 
                { "1", "Questionnaire 01 - Attempt 1", "passed" },
        };
	
	examTable = new ExtendedJTable(new DefaultTableModel(rowDataExam, columnNamesExam));
	examTable.setCellSelectionEnabled(false);
	examTable.setRowSelectionAllowed(true);
	
	spExams = new JScrollPane(examTable);
	tabbedPane.addTab("Exams", null, spExams, null);
    }
}
