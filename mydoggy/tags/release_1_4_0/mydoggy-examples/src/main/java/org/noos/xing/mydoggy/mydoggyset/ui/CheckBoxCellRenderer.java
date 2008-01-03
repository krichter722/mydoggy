package org.noos.xing.mydoggy.mydoggyset.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Angelo De Caro
 */
public class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {

    public CheckBoxCellRenderer() {
        setBackground(Color.white);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        boolean selected = Boolean.valueOf(value.toString());
        setHorizontalAlignment(SwingConstants.CENTER);
        setSelected(selected);
        return this;
    }

}
