package com.byronpdx.swing;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CalendarPanel extends JPanel {
    public enum MONTH {
        January, February, March, April, May, June, July, August, September, October, November, December;
    }

    private final CalendarTableModel model;
    private static final long serialVersionUID = 978379016168308851L;
    private final JTable table;
    private LocalDate date;
    private final JSpinner spinYear;
    private final JComboBox cmbMonth;

    public CalendarPanel() {
        setLayout(new BorderLayout(0, 0));

        final JPanel panControl = new JPanel();
        add(panControl, BorderLayout.NORTH);

        final JButton button = new JButton("<-");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // decrement month
                int indx = cmbMonth.getSelectedIndex() - 1;
                if (indx < 0) {
                    indx = 11;
                    spinYear.setValue((Integer) spinYear.getValue() - 1);
                }
                cmbMonth.setSelectedIndex(indx);
            }
        });
        button.setMargin(new Insets(1, 1, 1, 1));
        panControl.add(button);

        cmbMonth = new JComboBox();
        cmbMonth.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final MONTH sel = (MONTH) cmbMonth.getSelectedItem();
                System.out.println(sel + " " + sel.ordinal());
                date = date.withMonth(sel.ordinal() + 1);
                model.setDate(date);
            }
        });
        cmbMonth.setModel(new DefaultComboBoxModel(MONTH.values()));
        panControl.add(cmbMonth);

        spinYear = new JSpinner();
        spinYear.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent arg0) {
                date = date.withYear((Integer) spinYear.getValue());
                model.setDate(date);
            }
        });
        spinYear.setModel(new SpinnerNumberModel(2011, 1900, 2100, 1));
        panControl.add(spinYear);
        final NumberEditor ne_spinYear = new NumberEditor(spinYear, "####");
        spinYear.setEditor(ne_spinYear);

        final JButton button_1 = new JButton("->");
        button_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int indx = cmbMonth.getSelectedIndex() + 1;
                if (indx > 11) {
                    indx = 0;
                    spinYear.setValue((Integer) spinYear.getValue() + 1);
                }
                cmbMonth.setSelectedIndex(indx);
            }
        });
        button_1.setMargin(new Insets(1, 1, 1, 1));
        panControl.add(button_1);

        table = new JTable();
        table.setFillsViewportHeight(true);
        table.setToolTipText("Select a date");
        table.setRowSelectionAllowed(false);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final int col = table.getSelectedColumn();
                final int row = table.getSelectedRow();
                final LocalDate dt = model.getDateAt(row, col);
                firePropertyChange("date", date, date = dt);
                model.setDate(date);
                model.fireTableDataChanged();
            }
        });
        model = new CalendarTableModel(table);

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(table);
        setDate(LocalDate.now());
        this.doLayout();

    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date2) {
        if (date2 != null) {
            this.date = date2;
        } else {
            this.date = LocalDate.now();
        }
        model.setDate(this.date);
        spinYear.setValue(this.date.getYear());
        cmbMonth.setSelectedIndex(this.date.getMonthValue() - 1);
        table.repaint();
    }

}
