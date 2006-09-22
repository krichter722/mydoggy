package org.noos.xing.mydoggy.plaf.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class GlassPaneMouseAdapter implements MouseListener, MouseMotionListener, MouseWheelListener {
    private RootPaneContainer rootPaneContainer;
    private Component glassPane;

    private Component last;

    public GlassPaneMouseAdapter(RootPaneContainer rootPaneContainer, Component glassPane) {
        this.rootPaneContainer = rootPaneContainer;
        this.glassPane = glassPane;
    }

    public void mouseClicked(MouseEvent e) {
        redispatch(e);
    }

    public void mouseEntered(MouseEvent e) {
        redispatch(e);
    }

    public void mouseExited(MouseEvent e) {
        redispatch(e);
    }

    public void mousePressed(MouseEvent e) {
        redispatch(e);
    }

    public void mouseReleased(MouseEvent e) {
        redispatch(e);
        last = null;
    }

    public void mouseDragged(MouseEvent e) {
        if (glassPane.isVisible()) {
            Component deepest = SwingUtilities.getDeepestComponentAt(
                    rootPaneContainer.getContentPane(), e.getX(), e.getY());

            if (last == null)
                last = deepest;

            if (deepest != null) {
                glassPane.setCursor(deepest.getCursor());

                KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(
                        last,
                        SwingUtilities.convertMouseEvent(e.getComponent(), e, last)
                );
            }
        }
        redispatch(e);
    }

    public void mouseMoved(MouseEvent e) {
        redispatch(e);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        redispatch(e);
    }

    protected void redispatch(MouseEvent e) {
        if (glassPane.isVisible()) {
            Component deepest = SwingUtilities.getDeepestComponentAt(
                    rootPaneContainer.getContentPane(), e.getX(), e.getY());

            if (deepest != null) {
                glassPane.setCursor(deepest.getCursor());

                KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(
                        deepest,
                        SwingUtilities.convertMouseEvent(e.getComponent(), e, deepest)
                );
            }
        }
    }
}
