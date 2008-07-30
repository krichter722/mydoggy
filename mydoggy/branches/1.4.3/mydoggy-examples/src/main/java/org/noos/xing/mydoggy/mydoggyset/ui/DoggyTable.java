package org.noos.xing.mydoggy.mydoggyset.ui;

import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
     * Table
 */
public class DoggyTable extends JXTable {
    static final String[] cols = {"col0", "col1", "col2", "col3", "col4", "col5", "col6"};
    static final int nb_rows = 100;

    public DoggyTable() {
        setSortable(false);
        setColumnControlVisible(true);
        setHorizontalScrollEnabled(true);
        setColumnSelectionAllowed(true);
        setRowSelectionAllowed(true);
        setModel(new DoggyTableModel());
        setDefaultEditor(Double.class, new MySpinnerEditor());
    }


    public static class DoggyTableModel extends DefaultTableModel {
        private double[][] data = null;

        public DoggyTableModel() {
            data = new double[nb_rows][cols.length];
            for (int row = 0; row < nb_rows; row++) {
                for (int col = 0; col < cols.length; col++) {
                    data[row][col] = Math.random();
                }
            }
        }


        public int getRowCount() {
            return (data!= null) ? data.length : 0;
        }

        public int getColumnCount() {
            return cols.length;
        }

        public String getColumnName(int column) {
            return cols[column];
        }

        public Class getColumnClass(int columnIndex) {
            return Double.class;
        }


        public Object getValueAt(int row, int column) {
            return data[row][column];
        }


        public boolean isCellEditable(int row, int column) {
            return true;
        }


        public void setValueAt(Object aValue, int row, int column) {
            data[row][column] = (Double) aValue;
        }

    }

    public static class MySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner spinner = null;

        public MySpinnerEditor() {
            SpinnerNumberModel model = new SpinnerNumberModel(50, 0, 100, 1);
            spinner = new JSpinner(model);
            JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
            textField.setColumns(5);
            textField.setHorizontalAlignment(JTextField.RIGHT);
            textField.addKeyListener(new KeyAdapter() {

                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        fireEditingStopped();
                    }
                }
            });

            spinner.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                }
            });
        }


        public Object getCellEditorValue() {
            Integer integer = (Integer) spinner.getValue();
            return integer.doubleValue();
        }


        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return spinner;
        }
    }

}
