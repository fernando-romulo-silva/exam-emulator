package org.examemulator.infra.util.gui;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.table.DefaultTableCellRenderer;

import org.examemulator.gui.components.CustomCellRenderer;

public final class TableCellRendererUtil {

    private TableCellRendererUtil() {
	throw new IllegalStateException("You can't instanciate this class");
    }

    public static final DefaultTableCellRenderer PERCENT_TABLE_CELL_RENDERER = new CustomCellRenderer() {

	private static final long serialVersionUID = 1L;

	private static final NumberFormat PERCENT_FORMATTER = new DecimalFormat("##0.00");

	{
	    setHorizontalAlignment(RIGHT);
	}

	@Override
	public void setValue(final Object value) {

	    if (value instanceof final BigDecimal numberValue) {
		final var formattedValue = PERCENT_FORMATTER.format(numberValue);
		super.setValue(leftPad(formattedValue, 6, '0'));
		return;
	    }

	    super.setValue(value);
	}
    };

    public static final DefaultTableCellRenderer ENUM_TABLE_CELL_RENDERER = new CustomCellRenderer() {

	private static final long serialVersionUID = 1L;

	{
	    setHorizontalAlignment(CENTER);
	}

	@Override
	public void setValue(final Object value) {

	    if (value instanceof final Enum<?> enumValue) {
		super.setValue(capitalize(lowerCase(enumValue.toString())));
		return;
	    }

	    super.setValue(value);
	}
    };

    public static final DefaultTableCellRenderer NUMBER_TABLE_CELL_RENDERER = new CustomCellRenderer() {

	private static final long serialVersionUID = 1L;

	private static final NumberFormat NUMBER_FORMATTER = NumberFormat.getIntegerInstance();

	{
	    setHorizontalAlignment(CENTER);
	}

	@Override
	public void setValue(final Object value) {

	    if (value instanceof final Number numberValue) {
		super.setValue(NUMBER_FORMATTER.format(numberValue));
		return;
	    }

	    super.setValue(value);
	}
    };

    public static final DefaultTableCellRenderer DATE_TIME_TABLE_CELL_RENDERER = new CustomCellRenderer() {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	{
	    setHorizontalAlignment(CENTER);
	}

	@Override
	public void setValue(final Object value) {

	    if (value instanceof final LocalDateTime localDateTimeValue) {
		final var finalValue = DATE_TIME_FORMATTER.format(localDateTimeValue);
		super.setValue(finalValue);
		return;
	    }

	    super.setValue(value);
	}
    };
    
    public static final DefaultTableCellRenderer ORDER_TABLE_CELL_RENDERER = new CustomCellRenderer() {

	private static final long serialVersionUID = 1L;

	{
	    setHorizontalAlignment(CENTER);
	}

	@Override
	public void setValue(final Object value) {

	    if (value instanceof final Number numberValue) {
		super.setValue(leftPad(numberValue.toString(), 2, '0'));
		return;
	    }

	    super.setValue(value);
	}
    };
    
}
