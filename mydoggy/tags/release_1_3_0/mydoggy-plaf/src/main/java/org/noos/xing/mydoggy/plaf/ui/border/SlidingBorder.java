package org.noos.xing.mydoggy.plaf.ui.border;

import org.noos.xing.mydoggy.ToolWindowAnchor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SlidingBorder implements Border {
    private ToolWindowAnchor anchor;
    private Border border;

    public SlidingBorder() {
        border = BorderFactory.createRaisedBevelBorder();
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        switch (anchor) {
            case LEFT:
                border.paintBorder(c, g, x + c.getWidth() - 5, y, 5, height);
                break;
            case RIGHT:
                border.paintBorder(c, g, x, y, 5, height);
                break;
            case TOP:
                border.paintBorder(c, g, x, y + c.getHeight() - 5, width, 5);
                break;
            case BOTTOM:
                border.paintBorder(c, g, x, y, width, 5);
                break;
        }
    }

    public Insets getBorderInsets(Component c) {
        switch (anchor) {
            case LEFT:
                return new Insets(0, 0, 0, 5);
            case RIGHT:
                return new Insets(0, 5, 0, 0);
            case TOP:
                return new Insets(0, 0, 5, 0);
            case BOTTOM:
                return new Insets(5, 0, 0, 0);
        }
        throw new IllegalStateException();
    }

    public void setAnchor(ToolWindowAnchor anchor) {
        this.anchor = anchor;
    }
}
