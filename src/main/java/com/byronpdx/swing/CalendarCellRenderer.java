package com.byronpdx.swing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.table.DefaultTableCellRenderer;

public class CalendarCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 3085894133452186022L;
    private DateTimeFormatter formatter;
    private String format;

    /**
     * A cell render for DateMidnight that uses the default format of
     * MM/dd/yyyy.
     */
    public CalendarCellRenderer() {
        this("MM/dd/yyyy");
    }

    public CalendarCellRenderer(String format) {
        setFormat(format);
    }

    @Override
    protected void setValue(Object value) {
        setText(value == null ? "" : formatter.format((LocalDate) value));
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
        formatter = DateTimeFormatter.ofPattern(format);
    }

}
