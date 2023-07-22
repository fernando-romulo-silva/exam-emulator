package org.examemulator.util.gui;

import static org.apache.commons.lang3.reflect.MethodUtils.getMatchingAccessibleMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.examemulator.gui.components.TableColumnAdjuster;

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

    public static record TableModelField(String name, String label, boolean principal, DefaultTableCellRenderer cellRender) {

	public TableModelField {
	    Objects.requireNonNull(name);
	    
	    if (Objects.isNull(label)) {
		label = createLabel(name);
	    } 
	}
	
	public static TableModelField fieldOf(String name, String label, DefaultTableCellRenderer cellRender) {
	    return new TableModelField(name, label, false, cellRender);
	}

	public static TableModelField fieldOf(String name, DefaultTableCellRenderer cellRender) {
	    return new TableModelField(name, null, false, cellRender);
	}

	public static TableModelField fieldOf(String name, String label) {
	    return new TableModelField(name, label, false, null);
	}

	public static TableModelField fieldOf(String name) {
	    return new TableModelField(name, null, false, null);
	}
	
	public static TableModelField fieldOf(String name, boolean principal) {
	    return new TableModelField(name, null, principal, null);
	}
	
	private String createLabel(final String name) {

	    final var nameTemp = Character.toUpperCase(name.charAt(0)) + name.substring(1);
	    final var splitedName = nameTemp.split("(?=\\p{Upper})");
	    final var displayName = new StringBuilder("");

	    for (var nameItem : splitedName) {
		displayName.append(nameItem).append(" ");
	    }

	    return displayName.toString();
	}
    }

    public static <T> void alignTableModel(final JTable table, final Class<T> beanClass, final List<T> list, final List<TableModelField> fields) {
	table.setModel(createTableModel(beanClass, list, fields));
	
	final var tableColumnModel = table.getColumnModel();

	
	for(var i = 0; i < fields.size(); i++) {
	    
	    final var field = fields.get(i);
	    final var column = tableColumnModel.getColumn(i);
	    
	    if (Objects.nonNull(field.cellRender)) {
		column.setCellRenderer(field.cellRender);
	    }
	    
	    if (field.principal) {
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
		
	        return getValueAt(0, columnIndex).getClass();
	    }	    

	    @Override
	    public Object getValueAt(int rowIndex, int columnIndex) {
		try {
		    final var method = getters.get(columnIndex);
		    final var object = list.get(rowIndex);
		    return method.invoke(object);
		} catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
		    throw new IllegalArgumentException(ex);
		}
	    }
	};
    }
}
