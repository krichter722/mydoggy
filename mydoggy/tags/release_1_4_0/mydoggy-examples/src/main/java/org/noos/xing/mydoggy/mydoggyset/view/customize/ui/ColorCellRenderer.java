package org.noos.xing.mydoggy.mydoggyset.view.customize.ui;

import org.noos.xing.mydoggy.mydoggyset.view.customize.CustomizeView;
import org.noos.xing.mydoggy.mydoggyset.view.customize.ui.ColorIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ColorCellRenderer extends JLabel implements TableCellRenderer {

    public ColorCellRenderer() {
        setIcon(new ColorIcon());
        setBorder(new EmptyBorder(0, 3, 0, 0));
        setOpaque(true);
        setBackground(Color.LIGHT_GRAY);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Color color = (Color) value;
        ((ColorIcon) getIcon()).setColor(color);
        setText("[r=" + color.getRed() + ",g=" + color.getGreen() + ",b=" + color.getBlue() + "]");
        return this;
    }


}
