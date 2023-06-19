package org.examemulator.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.examemulator.gui.components.TableColumnAdjuster;

public class ControllerUtil {

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

    public static record TableModelField(String name, String label, Function<String, Object> formatter) {

	public TableModelField {
	    Objects.requireNonNull(name);
	    Objects.requireNonNull(label);
	}

	public static TableModelField fieldOf(String name, Function<String, Object> formatter) {
	    return new TableModelField(name, createLabel(name), formatter);
	}

	public static TableModelField fieldOf(String name, String label) {
	    return new TableModelField(name, label, null);
	}

	public static TableModelField fieldOf(String name) {
	    return new TableModelField(name, createLabel(name), null);
	}

	private static String createLabel(final String name) {

	    final var nameTemp = Character.toUpperCase(name.charAt(0)) + name.substring(1);
	    final var splitedName = nameTemp.split("(?=\\p{Upper})");
	    final var displayName = new StringBuilder("");

	    for (var nameItem : splitedName) {
		displayName.append(nameItem).append(" ");
	    }

	    return displayName.toString();
	}
    }

    public static <T> AbstractTableModel createTableModel(final Class<T> beanClass, final List<T> list, final List<TableModelField> fields) {

	final BeanInfo beanInfo;
	try {
	    beanInfo = Introspector.getBeanInfo(beanClass);
	} catch (final IntrospectionException ex) {
	    throw new IllegalArgumentException(ex);
	}

	final var columns = new ArrayList<String>();
	final var getters = new ArrayList<Method>();

	final var propertyDescriptors = beanInfo.getPropertyDescriptors();

	for (final var field : fields) {
	    final var name = field.name();

	    final var optionalDescriptor = Stream.of(propertyDescriptors).filter(desc -> Objects.equals(desc.getName(), name)).findFirst();

	    if (optionalDescriptor.isEmpty()) {
		continue;
	    }

	    final var descriptor = optionalDescriptor.get();

	    columns.add(field.label());
	    getters.add(descriptor.getReadMethod());
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
	    public Object getValueAt(int rowIndex, int columnIndex) {
		try {
		    return getters.get(columnIndex).invoke(list.get(rowIndex));
		} catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
		    throw new IllegalArgumentException(ex);
		}
	    }
	};
    }

    public static void alignColumns(final JTable table, final List<Integer> columns, final int alignment) {

	final var centerRenderer = new DefaultTableCellRenderer();
	centerRenderer.setHorizontalAlignment(alignment);

	if (!table.getColumnModel().getColumns().hasMoreElements()) {
	    return;
	}
	
	for (final var column : columns) {
	    table.getColumnModel().getColumn(column).setCellRenderer(centerRenderer);
	}
	
	final var tca = new TableColumnAdjuster(table);
	tca.adjustColumns();
    }
}
