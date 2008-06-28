package org.noos.xing.mydoggy.plaf.ui.translucent;

import javax.swing.*;
import java.awt.*;

public class TranslucentRepaintManager extends javax.swing.RepaintManager {

    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        Rectangle dirtyRegion = getDirtyRegion(c);
        if (dirtyRegion.width == 0 && dirtyRegion.height == 0) {
            int lastDeltaX = c.getX();
            int lastDeltaY = c.getY();
            Container parent = c.getParent();
            while (parent instanceof JComponent) {
                if (!parent.isVisible() || !parent.isDisplayable())
                    return;

                if (parent instanceof TranslucentComponent &&
                    (((TranslucentComponent) parent).getAlphaModeEnabled() < 1f || !parent.isOpaque())) {
                    x += lastDeltaX;
                    y += lastDeltaY;
                    lastDeltaX = lastDeltaY = 0;
                    c = (JComponent) parent;
                }

                lastDeltaX += parent.getX();
                lastDeltaY += parent.getY();
                parent = parent.getParent();
            }
        }
        super.addDirtyRegion(c, x, y, w, h);
    }
}