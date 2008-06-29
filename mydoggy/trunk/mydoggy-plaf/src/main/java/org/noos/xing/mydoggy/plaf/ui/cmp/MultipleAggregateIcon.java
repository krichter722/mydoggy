package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MultipleAggregateIcon implements Icon, SwingConstants {
    protected int orientation;

    protected Icon[] icons;
    protected boolean[] visible;
    protected Rectangle[] lastPaintedRecs;


    public MultipleAggregateIcon(int numIcon, int orientation) {
        this.icons = new Icon[numIcon];
        this.visible = new boolean[numIcon];
        this.lastPaintedRecs = new Rectangle[numIcon];
        this.orientation =  orientation;

        for (int i = 0; i < visible.length; i++)
            visible[i] = true;
    }


    public void paintIcon(Component c, Graphics g, int x, int y) {
        switch (orientation) {
            case HORIZONTAL:
                // Find max height
                int maxHeight = 0;
                for (int i = 0; i < visible.length; i++) {
                    if (isVisibleAt(i)) {
                        Icon icon = getIconAt(i);
                        int height = icon.getIconHeight();
                        if (height > maxHeight)
                            maxHeight = height;
                    }
                }

                int xCursor = x;
                for (int i = 0; i < visible.length; i++) {
                    if (isVisibleAt(i)) {
                        Icon icon = getIconAt(i);

                        int yCursor = y + ((maxHeight - icon.getIconHeight()) / 2);
                        icon.paintIcon(c, g, xCursor, yCursor);

                        lastPaintedRecs[i] = new Rectangle(xCursor, yCursor,
                                                           icon.getIconWidth(), icon.getIconHeight());

                        xCursor += icon.getIconWidth();
                    } else
                        lastPaintedRecs[i] = null;
                }
                break;
            case VERTICAL:
                // Find max height
                int maxWidth = 0;
                for (int i = 0; i < visible.length; i++) {
                    if (isVisibleAt(i)) {
                        Icon icon = getIconAt(i);
                        int width = icon.getIconWidth();
                        if (width > maxWidth)
                            maxWidth = width;
                    }
                }

                int yCursor = y;
                for (int i = 0; i < visible.length; i++) {
                    if (isVisibleAt(i)) {
                        Icon icon = getIconAt(i);

                        xCursor = x + ((maxWidth - icon.getIconWidth()) / 2);
                        icon.paintIcon(c, g, xCursor, yCursor);

                        lastPaintedRecs[i] = new Rectangle(xCursor, yCursor,
                                                           icon.getIconWidth(), icon.getIconHeight());

                        yCursor += icon.getIconWidth();
                    } else
                        lastPaintedRecs[i] = null;
                }
                break;
        }
    }

    public int getIconWidth() {
        if (orientation == HORIZONTAL) {
            // Sum all
            int width = 0;
            for (int i = 0; i < visible.length; i++) {
                if (visible[i]) {
                    width += SwingUtil.getIconWidth(icons[i]);
                }
            }
            return width;
        } else {
            // Find max
            int width = 0;
            for (int i = 0; i < visible.length; i++) {
                if (visible[i]) {
                    int newWidth = SwingUtil.getIconWidth(icons[i]);
                    if (newWidth > width)
                        width = newWidth;
                }
            }
            return width;
        }
    }

    public int getIconHeight() {
        if (orientation == VERTICAL) {
            // Sum all
            int height = 0;
            for (int i = 0; i < visible.length; i++) {
                if (visible[i]) {
                    height += SwingUtil.getIconHeight(icons[i]);
                }
            }
            return height;
        } else {
            // Find max
            int height = 0;
            for (int i = 0; i < visible.length; i++) {
                if (visible[i]) {
                    int newHeight = SwingUtil.getIconHeight(icons[i]);
                    if (newHeight > height)
                        height = newHeight;
                }
            }
            return height;
        }
    }


    public int getSize() {
        return icons.length;
    }

    public Icon getIconAt(int index) {
        return icons[index];
    }

    public void setIconAt(int index, Icon icon) {
        icons[index] = icon;
    }

    public boolean isVisibleAt(int index) {
        return visible[index] && icons[index] != null;
    }

    public void setVisibleAt(int index, boolean visible) {
        this.visible[index] = visible;
    }

    public Rectangle getLastPaintedRecAt(int index) {
        return lastPaintedRecs[index];
    }

}