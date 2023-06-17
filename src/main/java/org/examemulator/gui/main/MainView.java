package org.examemulator.gui.main;

import static java.awt.FlowLayout.LEFT;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingConstants.TOP;
import static org.examemulator.gui.GuiUtil.APP_NAME;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;

import jakarta.enterprise.context.ApplicationScoped;

class MainView extends JFrame {

    private static final long serialVersionUID = 1L;

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

    JPanel pMain, contentPane;

    JLabel lblCertificationStatistics, lblQuestionnaireSetStatistics;
    
    JTable certificantionTable, questionnaireSetTable, questionnaireTable, examTable;
    
    JTabbedPane tabbedPane;
    
    JScrollPane spQuestionnaire, spExams;

    MainView() {
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setBounds(100, 100, 871, 723);

	setTitle(APP_NAME);

	contentPane = new JPanel();
	contentPane.setLayout(new BoxLayout(contentPane, Y_AXIS));
	setContentPane(contentPane);

	contentPane.setLayout(new BorderLayout());

	pMain = new JPanel();
	pMain.setLayout(new BoxLayout(pMain, Y_AXIS));
	pMain.setPreferredSize(new Dimension(80, 200));
	contentPane.add(pMain, BorderLayout.NORTH);

	final var pCertificationStatistics = new JPanel(new FlowLayout(LEFT, 5, 5));
	pMain.add(pCertificationStatistics);

	lblCertificationStatistics = new JLabel("Certifications");
	pCertificationStatistics.add(lblCertificationStatistics);

        Object[] columnNamesCertification = { "id", "name" };
        Object[][] rowDataCertification = { 
                { "1", "Docker Certified Associate (DCA)"},
                { "2", "Certified Kubernetes Application Developer (CKAD)"}
        };
        
	certificantionTable = new ExtendedJTable(new DefaultTableModel(rowDataCertification, columnNamesCertification));
	certificantionTable.setCellSelectionEnabled(true);
	
	final var pCertificationTable = new JScrollPane(certificantionTable, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	pCertificationTable.setPreferredSize(new Dimension(100, 250));
	pMain.add(pCertificationTable);	
	
	final var pQuestionnaireSetStatistics = new JPanel(new FlowLayout(LEFT, 5, 5));
	pMain.add(pQuestionnaireSetStatistics);

	lblQuestionnaireSetStatistics = new JLabel("<html> <br /> Sets of Questionnarie </html>");
	pQuestionnaireSetStatistics.add(lblQuestionnaireSetStatistics);
	
	
        Object[] columnNamesQuestionnaireSet = { "id", "name" };
        Object[][] rowDataQuestionnaireSet = { 
                { "1", "Set01"},
                { "2", "Set02"}
        };

	questionnaireSetTable = new ExtendedJTable(new DefaultTableModel(rowDataQuestionnaireSet, columnNamesQuestionnaireSet));
	
	final var pQuestionnaireSetTable = new JScrollPane(questionnaireSetTable, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	pQuestionnaireSetTable.setSize(new Dimension(50, 50));
	pMain.add(pQuestionnaireSetTable);
	
	tabbedPane = new JTabbedPane(TOP);
	contentPane.add(tabbedPane);
	
        Object[] columnNamesQuestionnarie = { "id", "name" };
        Object[][] rowDataQuestionnarie = { 
                { "1", "Questionnaire 01"},
                { "2", "Questionnaire 02"}
        };
        
	questionnaireTable = new ExtendedJTable(new DefaultTableModel(rowDataQuestionnarie, columnNamesQuestionnarie));

	spQuestionnaire = new JScrollPane(questionnaireTable);
	tabbedPane.addTab("Questionnaires", null, spQuestionnaire, null);
	
	spExams = new JScrollPane();
	tabbedPane.addTab("Exams", null, spExams, null);
    }
}
