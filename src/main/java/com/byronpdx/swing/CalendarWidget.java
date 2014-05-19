/*
 * Copyright (c) 2009-2013 TriMet
 *
 * Last modified on Feb 12, 2013 by palmerb
 */
package com.byronpdx.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Calendar widget Configured so key F1 brings up a popup for entering the date.
 *
 * @author byron
 *
 */
public class CalendarWidget extends JPanel {
    private static final long serialVersionUID = -3552440588908953823L;
    private final static boolean DEBUG = false;
    private final JTextField textField;
    private LocalDate date;
    // formatters
    private final List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
    private final String displayFormat;
    private DateTimeFormatter displayFormatter;
    private final String[] fmtStrings;

    public CalendarWidget(String[] fmtStrings, String displayFormat) {
        this.fmtStrings = fmtStrings;
        this.displayFormat = displayFormat;
        textField = new JTextField();
        setupFormatters();
        initUi();
    }

    void initUi() {
        setBackground(Color.WHITE);
        setBorder(new LineBorder(new Color(0, 0, 0)));
        setPreferredSize(new Dimension(140, 26));
        setMinimumSize(new Dimension(125, 24));
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 114, 20, 0 };
        gridBagLayout.rowHeights = new int[] { 22, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);
        // textField = this;
        textField.setPreferredSize(new Dimension(85, 22));
        textField.setMinimumSize(new Dimension(85, 15));
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setAlignmentY(Component.TOP_ALIGNMENT);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        final GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.fill = GridBagConstraints.BOTH;
        gbc_textField.insets = new Insets(0, 0, 0, 0);
        gbc_textField.gridx = 0;
        gbc_textField.gridy = 0;
        this.add(textField, gbc_textField);
        // add(textField);
        textField.setColumns(10);

        textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                makeChange();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                makeChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                makeChange();
            }

        });

        final JButton button = new JButton("");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                popupCalendar();
            }
        });
        button.setMinimumSize(new Dimension(12, 12));
        button.setPreferredSize(new Dimension(18, 18));
        button.setIconTextGap(0);
        button.setMaximumSize(new Dimension(24, 24));
        button.setMargin(new Insets(1, 0, 1, 0));
        button.setIcon(new ImageIcon(CalendarWidget.class.getResource("/com/byronpdx/swing/calendar_icon.png")));
        final GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.fill = GridBagConstraints.BOTH;
        gbc_button.gridx = 1;
        gbc_button.gridy = 0;
        add(button, gbc_button);
        this.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent arg0) {
                textField.requestFocusInWindow();
            }

            @Override
            public void focusLost(FocusEvent arg0) {
            }

        });
    }

    /**
     * Make change tests to see if anything has changed and updates the date.
     */
    private void makeChange() {
        checkDate();
    }

    private void popupCalendar() {
        final CalendarPopup popup = new CalendarPopup();
        popup.setDate(date);
        popup.setLocation(this.getLocationOnScreen());
        popup.setVisible(true);
        if (popup.isDateValid()) {
            final LocalDate dt = this.date;
            final LocalDate date = popup.getDate();
            setDate(date);
            firePropertyChange("date", dt, date);
            log("popup" + date);
        }
    }

    private void log(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }

    /**
     * Creates a CalendarWidget with the format strings specified.
     *
     * @param fmtStrings
     */
    public CalendarWidget() {
        this(new String[] { "MM/dd", "MM/dd/yy", "MM/dd/yyyy", "yyyy-MM-dd" }, "yyyy-MM-dd");
    }

    /**
     * Setup the formatters. The format strings should be assigned from the
     * shortest format to the longest so that if it matches the short form the
     * parsing can be stopped.
     *
     * @param fmtStrings
     */
    private void setupFormatters() {
        final LocalDate dt = LocalDate.now();
        formatters.clear();
        for (final String fmt : fmtStrings) {
            final DateTimeFormatter dtf = buildFormatter(dt, fmt);
            formatters.add(dtf);
        }
        displayFormatter = buildFormatter(dt, displayFormat);
    }

    private DateTimeFormatter buildFormatter(final LocalDate dt, final String fmt) {
        return new DateTimeFormatterBuilder().appendPattern(fmt).parseDefaulting(ChronoField.YEAR, dt.getYear()).toFormatter();
    }

    private boolean checkDate() {
        final String txt = textField.getText();
        LocalDate dt = null;
        if (txt.isEmpty() && date != null) {
            firePropertyChange("date", date, date = null);
            log("checkDate-Date set to null");
            return true;
        }
        for (final DateTimeFormatter dtf : formatters) {
            try {
                final LocalDate dto = date;
                dt = LocalDate.from(dtf.parse(txt));
                if (!dt.equals(date)) {
                    date = dt;
                    firePropertyChange("date", dto, dt);
                    log("checkDate-Date set to " + dt);
                }
                return true;
            } catch (final DateTimeParseException e) {
                // ignore
            }
        }
        return false;
    }

    /**
     * Get the date
     *
     * @return the date
     */
    public LocalDate getDate() {
        checkDate();
        return date;
    }

    /**
     * Sets the date.
     *
     * @param localDate
     *            the new date
     */
    public void setDate(LocalDate localDate) {
        this.date = localDate;
        if (localDate != null) {
            textField.setText(displayFormatter.format(localDate));
        } else {
            textField.setText("");
        }
        textField.grabFocus();
        textField.setFocusable(true);
        textField.grabFocus();
    }

    public void selectAll() {
        log("SelectAll");
        textField.selectAll();
        log("selectAll " + textField.hasFocus());
    }

    public JTextField getTextField() {
        return textField;
    }

}
