package org.examemulator.gui.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    /**
     * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {

	Component rendererComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

	// Very important to handle selected items (render them inversely colored)
//	if (isSelected) {
//	    rendererComp.setBackground(getHuedColor(row).darker().darker());
//	    rendererComp.setForeground(getHuedColor(row).brighter().brighter());
//	} else {
//	    rendererComp.setBackground(getHuedColor(row).brighter().brighter());
//	    rendererComp.setForeground(getHuedColor(row).darker().darker());
//	}

	return rendererComp;
    }

    public Color getHuedColor(int hue) {
	return new Color(Color.HSBtoRGB(85 / 360f * hue, 0.7f, 1.0f));
    }
}
