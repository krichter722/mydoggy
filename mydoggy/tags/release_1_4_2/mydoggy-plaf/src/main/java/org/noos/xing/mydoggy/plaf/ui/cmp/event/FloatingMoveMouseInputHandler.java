package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FloatingMoveMouseInputHandler implements MouseInputListener {
    static final int BORDER_DRAG_THICKNESS = 0;
    static final int CORNER_DRAG_WIDTH = 16;
    static final int[] cursorMapping = new int[]
            {Cursor.NW_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, Cursor.N_RESIZE_CURSOR,
             Cursor.NE_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
             Cursor.NW_RESIZE_CURSOR, 0, 0, 0, Cursor.NE_RESIZE_CURSOR,
             Cursor.W_RESIZE_CURSOR, 0, 0, 0, Cursor.E_RESIZE_CURSOR,
             Cursor.SW_RESIZE_CURSOR, 0, 0, 0, Cursor.SE_RESIZE_CURSOR,
             Cursor.SW_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
             Cursor.SE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR
            };

    protected boolean isMovingWindow;
    protected int dragCursor;
    protected int dragOffsetX;
    protected int dragOffsetY;

    protected Component floatingContainer;
    protected Window floatingWindow;

    public FloatingMoveMouseInputHandler(Component floatingContainer) {
        this.floatingContainer = floatingContainer;
        if (floatingContainer instanceof Window)
            floatingWindow = (Window) floatingContainer;
    }

    public void mousePressed(MouseEvent ev) {
        Component w = (Component) ev.getSource();
        if (w != null) {
            if (floatingWindow != null)
                floatingWindow.toFront();

            Point dragWindowOffset = ev.getPoint();
            Point convertedDragWindowOffset = SwingUtilities.convertPoint(w,
                                                                          dragWindowOffset,
                                                                          ev.getComponent());

            if (ev.getComponent() != null && ev.getComponent().contains(convertedDragWindowOffset)) {
                if (dragWindowOffset.y >= BORDER_DRAG_THICKNESS
                    && dragWindowOffset.x >= BORDER_DRAG_THICKNESS
                    && dragWindowOffset.x < w.getWidth() - BORDER_DRAG_THICKNESS) {
                    isMovingWindow = true;
                    dragOffsetX = dragWindowOffset.x;
                    dragOffsetY = dragWindowOffset.y;
                }
            }
        }
    }

    public void mouseReleased(MouseEvent ev) {
        if (isMovingWindow) {
            if (dragCursor != 0 && floatingContainer != null && !floatingContainer.isValid()) {
                // Some Window systems validate as you resize, others won't,
                // thus the check for validity before repainting.
                floatingContainer.validate();
            }
            isMovingWindow = false;
            dragCursor = 0;
        }
    }

    public void mouseMoved(MouseEvent ev) {
    }

    public void mouseDragged(MouseEvent ev) {
        if (isMovingWindow) {

            Point pt = ev.getPoint();
            Point windowPt = (floatingContainer instanceof Window) ? floatingContainer.getLocationOnScreen()
                             : floatingContainer.getLocation();

            windowPt.x += pt.x - dragOffsetX;
            windowPt.y += pt.y - dragOffsetY;

            if (floatingWindow != null) {
                // Check bounds
                Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
                if (windowPt.y < maxBounds.y)
                    windowPt.y = maxBounds.y;                         
            }

            floatingContainer.setLocation(windowPt);
        }
    }

    public void mouseEntered(MouseEvent ev) {
    }

    public void mouseExited(MouseEvent ev) {
    }

    public void mouseClicked(MouseEvent ev) {
    }

}
