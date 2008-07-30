package org.noos.xing.mydoggy.mydoggyset.view.customize.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ColorIcon implements Icon {
    private Color color;

    public int getIconHeight() {
        return 12;
    }

    public int getIconWidth() {
        return 12;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, 12, 12);

        g.setColor(color);
        g.fillRect(x + 2, y + 2, 9, 9);
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
