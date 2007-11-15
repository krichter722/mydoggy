package org.noos.xing.mydoggy.mydoggyset.view.customize.ui;

import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.EventObject;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ColorChooseCellEditor extends BasicCellEditor {
    private JColorChooser colorChooser = new JColorChooser();
    private JDialog dialog;

    private JPanel panel;

    public ColorChooseCellEditor() {
        JLabel label = new JLabel();
        label.setIcon(new ColorIcon());
        label.setBorder(new EmptyBorder(0, 3, 0, 0));

        editorComponent = label;

        delegate = new EditorDelegate() {
            private EventObject eventObject;

            public boolean startCellEditing(EventObject anEvent) {
                eventObject = anEvent;
                return true;
            }

            public void setValue(Object value) {
                startCellEditing(null);
                colorChooser.setColor((Color) value);
            }

            public Object getCellEditorValue() {
                return colorChooser.getColor();
            }

            public void cancelCellEditing() {
                if (dialog != null)
                    dialog.setVisible(false);
                super.cancelCellEditing();
            }

            public boolean stopCellEditing() {
                if (dialog != null)
                    dialog.setVisible(false);
                return super.stopCellEditing();
            }

            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                if ("cancel".equalsIgnoreCase(actionCommand)) {
                    cancelCellEditing();
                } else if ("ok".equalsIgnoreCase(actionCommand)) {
                    stopCellEditing();
                } else if ("starter".equalsIgnoreCase(actionCommand)) {
                    dialog = JColorChooser.createDialog(null, "Color Chooser", true, colorChooser, this, this);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            dialog.setVisible(true);
                        }
                    });
                    super.startCellEditing(eventObject);
                }
            }
        };

        panel = new JPanel();
        panel.setLayout(new ExtendedTableLayout(new double[][]{{-1, 2, 15}, {-1}}));
        panel.add(editorComponent, "0,0");
        panel.setOpaque(false);
        JButton starter = new JButton("...");
        starter.setActionCommand("starter");
        starter.addActionListener(delegate);
        panel.add(starter, "2,0");
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        Color color = (Color) value;
        JLabel label = (JLabel) editorComponent;
        ((ColorIcon) label.getIcon()).setColor(color);
        label.setText("[r=" + color.getRed() + ",g=" + color.getGreen() + ",b=" + color.getBlue() + "]");
        return panel;
    }

}
