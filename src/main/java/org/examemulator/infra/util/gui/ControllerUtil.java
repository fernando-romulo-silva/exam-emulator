package org.examemulator.infra.util.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.reflect.MethodUtils.getMatchingAccessibleMethod;
import static org.examemulator.infra.util.gui.GuiUtil.TAG_BR;
import static org.examemulator.infra.util.gui.GuiUtil.TAG_BR_BR;
import static org.examemulator.infra.util.gui.GuiUtil.TAG_CLOSE_B;
import static org.examemulator.infra.util.gui.GuiUtil.TAG_OPEN_B;
import static org.examemulator.infra.util.gui.GuiUtil.convertTextToHtml;
import static org.examemulator.infra.util.gui.GuiUtil.createScrollHtmlTextToShow;
import static org.examemulator.infra.util.gui.GuiUtil.extractedOptions;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.gui.components.TableColumnAdjuster;
import org.examemulator.infra.util.TableModelField;

public final class ControllerUtil {

    private ControllerUtil() {
	throw new IllegalStateException("You can't instanciate this class");
    }

    public static <T> Optional<T> selectFirstQuestion(final List<T> questions) {

	if (questions.isEmpty()) {
	    return Optional.empty();
	}

	return Optional.of(questions.get(0));
    }

    public static <T> Optional<T> nextQuestion(final List<T> questions, final T selectedQuestion) {

	if (questions.isEmpty()) {
	    return Optional.empty();
	}

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex + 1);

	if (iterator.hasNext()) {
	    return Optional.of(iterator.next());
	}

	return Optional.empty();
    }

    public static <T> boolean hasNextQuestion(final List<T> questions, final T selectedQuestion) {
	if (questions.isEmpty()) {
	    return false;
	}

	final var currentIndex = questions.indexOf(selectedQuestion);
	if (currentIndex < 0) {
	    return false;
	}

	final var iterator = questions.listIterator(currentIndex + 1);
	return iterator.hasNext();
    }

    public static <T> boolean hasPreviousQuestion(final List<T> questions, final T selectedQuestion) {
	if (questions.isEmpty()) {
	    return false;
	}

	final var currentIndex = questions.indexOf(selectedQuestion);

	if (currentIndex < 0) {
	    return false;
	}

	final var iterator = questions.listIterator(currentIndex);
	return iterator.hasPrevious();
    }

    public static <T> Optional<T> previousQuestion(final List<T> questions, final T selectedQuestion) {

	if (questions.isEmpty()) {
	    return Optional.empty();
	}

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex);

	if (iterator.hasPrevious()) {
	    return Optional.of(iterator.previous());
	}

	return Optional.empty();
    }

    public static <T> void alignTableModel(final JTable table, final Class<T> beanClass, final List<T> list, final List<TableModelField> fields) {
	table.setModel(createTableModel(beanClass, list, fields));
	
	final var tableColumnModel = table.getColumnModel();
	
	for(var i = 0; i < fields.size(); i++) {
	    
	    final var field = fields.get(i);
	    final var column = tableColumnModel.getColumn(i);
	    
	    if (Objects.nonNull(field.cellRender())) {
		column.setCellRenderer(field.cellRender());
	    }
	    
	    if (field.principal()) {
		column.setPreferredWidth(Integer.MAX_VALUE);
	    } else {
		column.setPreferredWidth(table.getColumnName(i).length() + 2);
	    }
	}
	
	final var tca = new TableColumnAdjuster(table);
	tca.adjustColumns();
	table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
    
    private static <T> AbstractTableModel createTableModel(final Class<T> beanClass, final List<T> list, final List<TableModelField> fields) {

	final var columns = new ArrayList<String>();
	final var getters = new ArrayList<Method>();

	final var classFields = FieldUtils.getAllFields(beanClass);

	for (final var field : fields) {
	    final var name = field.name();

	    final var optionalClassField = Stream.of(classFields) //
			    .filter(desc -> Objects.equals(desc.getName(), name)) //
			    .findFirst();

	    if (optionalClassField.isEmpty()) {
		continue;
	    }

	    columns.add(field.label());

	    if (beanClass.isRecord()) {
		getters.add(getMatchingAccessibleMethod(beanClass, name));
	    } else {
		
		String methodName;
		if (Boolean.class.equals(optionalClassField.get().getType()) || boolean.class.equals(optionalClassField.get().getType())) {
		    methodName = "is" + StringUtils.capitalize(name);
		}else {
		    methodName = "get" + StringUtils.capitalize(name);
		}
		
		getters.add(getMatchingAccessibleMethod(beanClass, methodName));
	    }
	}

	return new AbstractTableModel() {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public String getColumnName(int column) {
		return columns.get(column);
	    }

	    @Override
	    public int getRowCount() {
		return list.size();
	    }

	    @Override
	    public int getColumnCount() {
		return columns.size();
	    }
	    
	    @Override
	    public Class<?> getColumnClass(int columnIndex) {
		
		if (getRowCount() == 0) {
		    return Object.class;
		}
		
	        final var value = getValueAt(0, columnIndex);
	        
	        if (ObjectUtils.isEmpty(value)) {
		    return Objects.class;
		}
	        
		return value.getClass();
	    }	    

	    @Override
	    public Object getValueAt(int rowIndex, int columnIndex) {
		try {
		    final var method = getters.get(columnIndex);
		    final var object = list.get(rowIndex);
		    final var value = method.invoke(object);
		    
		    if (value instanceof String valueString) {
			return StringUtils.capitalize(valueString);
		    }
		    
		    return value;
		} catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
		    throw new IllegalArgumentException(ex);
		}
	    }
	};
    }
    
    public static void createQuestionDialog(final Frame owner, final Question question) {

	final var optionalConcept = question.getConcept();
	final var conceptName = optionalConcept.isPresent() //
		    ? " (".concat(optionalConcept.get().getName()).concat(")") //
		    : "";
	
	final var questionValue = TAG_BR.concat(TAG_OPEN_B).concat(convertTextToHtml(question.getValue())).concat(TAG_CLOSE_B);

	final var options = question.getOptions() //
		    .stream() //
		    .map(option -> TAG_OPEN_B.concat(option.getLetter()).concat(") ").concat(TAG_CLOSE_B).concat(TAG_BR).concat(convertTextToHtml(option.getValue()))) //
		    .collect(joining(TAG_BR_BR));

	final var correctOptions = TAG_OPEN_B.concat("Correct Answer(s): ").concat(TAG_CLOSE_B).concat(extractedOptions(question.getCorrectOptions()));

	final var explanation = TAG_OPEN_B.concat("Explanation: ").concat(TAG_CLOSE_B).concat(TAG_BR_BR).concat(convertTextToHtml(question.getExplanation()));

	final var txt = questionValue.concat(TAG_BR_BR) //
		    .concat(options).concat(TAG_BR_BR) //
		    .concat(correctOptions).concat(TAG_BR_BR) //
		    .concat(explanation);

	final var dialogQuestion = new JDialog(owner, question.getName().concat(conceptName), true);
	dialogQuestion.setLocationRelativeTo(owner);
	
	final var panelQuestionPanel = dialogQuestion.getContentPane();
	panelQuestionPanel.setLayout(new BorderLayout());
	panelQuestionPanel.add(createScrollHtmlTextToShow(txt), CENTER);
	
	final var panel = new JPanel(new FlowLayout());
	panel.setBorder(BorderFactory.createEtchedBorder());
	panelQuestionPanel.add(panel, SOUTH);
	
	final var okButton = new JButton("Ok");
	okButton.addActionListener(okEvent -> dialogQuestion.setVisible(false));
	okButton.setMnemonic(KeyEvent.VK_O);
	panel.add(okButton);

	dialogQuestion.pack();
	dialogQuestion.setVisible(true);
    }
}
