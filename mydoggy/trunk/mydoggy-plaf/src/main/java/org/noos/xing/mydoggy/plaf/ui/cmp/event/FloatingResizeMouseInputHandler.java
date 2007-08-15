package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FloatingResizeMouseInputHandler implements MouseInputListener {
    static final int BORDER_DRAG_THICKNESS = 5;
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

    private Cursor lastCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    private int dragCursor;
    private int dragOffsetX;
    private int dragOffsetY;
    private int dragWidth;
    private int dragHeight;

    private Window window;

    public FloatingResizeMouseInputHandler(Window window) {
        this.window = window;
    }

    public void mousePressed(MouseEvent ev) {
        Point dragWindowOffset = ev.getPoint();
        Component w = (Component) ev.getSource();

        window.toFront();
        dragOffsetX = dragWindowOffset.x;
        dragOffsetY = dragWindowOffset.y;
        dragWidth = w.getWidth();
        dragHeight = w.getHeight();
        dragCursor = getCursor(calculateCorner(w, dragWindowOffset.x, dragWindowOffset.y));
    }

    public void mouseReleased(MouseEvent ev) {
        if (dragCursor != 0 && window != null && !window.isValid()) {
            // Some Window systems validate as you resize, others won't,
            // thus the check for validity before repainting.
            window.validate();
        }
        dragCursor = 0;
        window.setCursor(Cursor.getDefaultCursor());
    }

    public void mouseMoved(MouseEvent ev) {
        Component w = (Component) ev.getSource();
        int cursor = getCursor(calculateCorner(w, ev.getX(), ev.getY()));
        if (cursor != 0) {
            w.setCursor(Cursor.getPredefinedCursor(cursor));
        } else {
            w.setCursor(lastCursor);
        }
    }

    public void mouseDragged(MouseEvent ev) {
        Point pt = ev.getPoint();

        if (dragCursor != 0) {
            Rectangle r = window.getBounds();
            Rectangle startBounds = new Rectangle(r);
            Dimension min = window.getMinimumSize();

            switch (dragCursor) {
                case Cursor.E_RESIZE_CURSOR:
                    adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX) -
                                         r.width, 0);
                    break;
                case Cursor.S_RESIZE_CURSOR:
                    adjust(r, min, 0, 0, 0, pt.y + (dragHeight - dragOffsetY) -
                                            r.height);
                    break;
                case Cursor.N_RESIZE_CURSOR:
                    adjust(r, min, 0, pt.y - dragOffsetY, 0,
                           -(pt.y - dragOffsetY));
                    break;
                case Cursor.W_RESIZE_CURSOR:
                    adjust(r, min, pt.x - dragOffsetX, 0,
                           -(pt.x - dragOffsetX), 0);
                    break;
                case Cursor.NE_RESIZE_CURSOR:
                    adjust(r, min, 0, pt.y - dragOffsetY,
                           pt.x + (dragWidth - dragOffsetX) - r.width,
                           -(pt.y - dragOffsetY));
                    break;
                case Cursor.SE_RESIZE_CURSOR:
                    adjust(r, min, 0, 0,
                           pt.x + (dragWidth - dragOffsetX) - r.width,
                           pt.y + (dragHeight - dragOffsetY) -
                           r.height);
                    break;
                case Cursor.NW_RESIZE_CURSOR:
                    adjust(r, min, pt.x - dragOffsetX,
                           pt.y - dragOffsetY,
                           -(pt.x - dragOffsetX),
                           -(pt.y - dragOffsetY));
                    break;
                case Cursor.SW_RESIZE_CURSOR:
                    adjust(r, min, pt.x - dragOffsetX, 0,
                           -(pt.x - dragOffsetX),
                           pt.y + (dragHeight - dragOffsetY) - r.height);
                    break;
                default:
                    break;
            }
            if (!r.equals(startBounds)) {
                if (r.width < 150)
                    r.width = 150;
                if (r.height < 24)
                    r.height = 24;

                window.setBounds(r);
                // Defer repaint/validate on mouseReleased unless dynamic
                // layout is active.
                if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
                    window.validate();
                }
            }
        }
    }

    public void mouseEntered(MouseEvent ev) {
        Component w = (Component) ev.getSource();
        lastCursor = w.getCursor();
        mouseMoved(ev);
    }

    public void mouseExited(MouseEvent ev) {
        Component w = (Component) ev.getSource();
        w.setCursor(lastCursor);
    }

    public void mouseClicked(MouseEvent ev) {
    }


    private void adjust(Rectangle bounds, Dimension min, int deltaX, int deltaY, int deltaWidth, int deltaHeight) {
        bounds.x += deltaX;
        bounds.y += deltaY;
        bounds.width += deltaWidth;
        bounds.height += deltaHeight;
        if (min != null) {
            if (bounds.width < min.width) {
                int correction = min.width - bounds.width;
                if (deltaX != 0) {
                    bounds.x -= correction;
                }
                bounds.width = min.width;
            }
            if (bounds.height < min.height) {
                int correction = min.height - bounds.height;
                if (deltaY != 0) {
                    bounds.y -= correction;
                }
                bounds.height = min.height;
            }
        }
    }

    private int calculateCorner(Component c, int x, int y) {
        int xPosition = calculatePosition(x, c.getWidth());
        int yPosition = calculatePosition(y, c.getHeight());

        if (xPosition == -1 || yPosition == -1) {
            return -1;
        }
        return yPosition * 5 + xPosition;
    }

    private int getCursor(int corner) {
        if (corner == -1) {
            return 0;
        }
        return cursorMapping[corner];
    }

    private int calculatePosition(int spot, int width) {
        if (spot < BORDER_DRAG_THICKNESS) {
            return 0;
        }
        if (spot < CORNER_DRAG_WIDTH) {
            return 1;
        }
        if (spot >= width - BORDER_DRAG_THICKNESS) {
            return 4;
        }
        if (spot >= width - CORNER_DRAG_WIDTH) {
            return 3;
        }
        return 2;
    }
}
