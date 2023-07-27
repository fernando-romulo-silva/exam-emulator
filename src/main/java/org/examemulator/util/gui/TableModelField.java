package org.examemulator.util.gui;

import java.util.Objects;

import javax.swing.table.DefaultTableCellRenderer;

public record TableModelField(String name, String label, boolean principal, DefaultTableCellRenderer cellRender) {

    public TableModelField {
	Objects.requireNonNull(name);

	if (Objects.isNull(label)) {
	    label = createLabel(name);
	}
    }

    public static TableModelField fieldOf(final String name, final String label, final DefaultTableCellRenderer cellRender) {
	return new TableModelField(name, label, false, cellRender);
    }

    public static TableModelField fieldOf(final String name, final DefaultTableCellRenderer cellRender) {
	return new TableModelField(name, null, false, cellRender);
    }

    public static TableModelField fieldOf(final String name, final String label) {
	return new TableModelField(name, label, false, null);
    }

    public static TableModelField fieldOf(final String name) {
	return new TableModelField(name, null, false, null);
    }

    public static TableModelField fieldOf(final String name, final boolean principal) {
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
