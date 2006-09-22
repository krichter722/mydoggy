package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SlidingMouseInputHandler implements MouseInputListener {
    static final int BORDER_DRAG_THICKNESS = 5;
    static final int CORNER_DRAG_WIDTH = 16;

    static final int[] LEFT_CURSOR_MAPPING = new int[]{
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, Cursor.E_RESIZE_CURSOR,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0
    };
    static final int[] RIGHT_CURSOR_MAPPING = new int[]{
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            Cursor.W_RESIZE_CURSOR, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0
    };
    static final int[] TOP_CURSOR_MAPPING = new int[]{
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, Cursor.S_RESIZE_CURSOR, 0, 0
    };
    static final int[] BOTTOM_CURSOR_MAPPING = new int[]{
            0, 0, Cursor.N_RESIZE_CURSOR, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0
    };

    private Cursor lastCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    private int dragCursor;
    private int dragOffsetX;
    private int dragOffsetY;
    private int dragWidth;
    private int dragHeight;

    private ToolWindowDescriptor descriptor;
    private ToolWindow toolWindow;

    public SlidingMouseInputHandler(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
    }

    public void mousePressed(MouseEvent ev) {
        Point dragWindowOffset = ev.getPoint();
        Component w = (Component) ev.getSource();
        dragOffsetX = dragWindowOffset.x;
        dragOffsetY = dragWindowOffset.y;
        dragWidth = w.getWidth();
        dragHeight = w.getHeight();
        dragCursor = getCursor(w, calculateCorner(w, dragWindowOffset.x, dragWindowOffset.y));
    }

    public void mouseReleased(MouseEvent ev) {
        dragCursor = 0;
        lastCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    }

    public void mouseMoved(MouseEvent ev) {
        Component w = (Component) ev.getSource();
        int cursor = getCursor(w, calculateCorner(w, ev.getX(), ev.getY()));
        if (cursor != 0)
            w.setCursor(Cursor.getPredefinedCursor(cursor));
        else
            w.setCursor(lastCursor);
    }

    public void mouseDragged(MouseEvent ev) {
        Point pt = ev.getPoint();
        Component component = ev.getComponent();

        if (dragCursor != 0) {
            Rectangle r = component.getBounds();
            Rectangle startBounds = new Rectangle(r);
            Dimension min = component.getMinimumSize();

            switch (toolWindow.getAnchor()) {
                case LEFT:
                    if (dragCursor == Cursor.E_RESIZE_CURSOR)
                        adjust(r, min,
                               0, 0,
                               pt.x + (dragWidth - dragOffsetX) - r.width, 0);
                    break;
                case TOP:
                    if (dragCursor == Cursor.S_RESIZE_CURSOR)
                        adjust(r, min,
                               0, 0,
                               0, pt.y + (dragHeight - dragOffsetY) - r.height);
                    break;
                case RIGHT:
                    if (dragCursor == Cursor.W_RESIZE_CURSOR) {
                        adjust(r, min,
                               pt.x - dragOffsetX, 0,
                               -(pt.x - dragOffsetX), 0);
                    }
                    break;
                case BOTTOM:
                    if (dragCursor == Cursor.N_RESIZE_CURSOR)
                        adjust(r, min,
                               0, pt.y - dragOffsetY,
                               0, -(pt.y - dragOffsetY));
                    break;
            }

            if (!r.equals(startBounds)) {
                switch (toolWindow.getAnchor()) {
                    case LEFT:
                        if (r.width < 5)
                            r.width = 5;

                        int maxWidth = descriptor.getToolWindowManagerContainer().getWidth();
                        if (descriptor.getToolBar(ToolWindowAnchor.RIGHT).getAvailableTools() > 0)
                            maxWidth -= 46;
                        else
                            maxWidth -= 23;

                        if (r.width > maxWidth)
                            r.width = maxWidth;
                        break;
                    case RIGHT:
                        if (r.width < 5)
                            r.width = 5;

                        // Max x
                        int maxX = descriptor.getToolWindowManagerContainer().getWidth() - 28;
                        if (r.x > maxX) {
                            r.x = maxX;
                        }

                        // Min x
                        int minX = descriptor.getToolWindowManagerContainer().getX();
                        if (descriptor.getToolBar(ToolWindowAnchor.LEFT).getAvailableTools() > 0) {
                            minX += 22;
                        }
                        if (r.x < minX)
                            r.x = minX;

                        // Max width
                        maxWidth = descriptor.getToolWindowManagerContainer().getWidth();
                        maxWidth -= descriptor.getToolBar(ToolWindowAnchor.LEFT).getAvailableTools() > 0 ? 45 : 23;

                        if (r.width > maxWidth)
                            r.width = maxWidth;
                        break;
                    case TOP:
                        if (r.height < 5)
                            r.height = 5;

                        // Max width
                        int diff = descriptor.getToolBar(ToolWindowAnchor.BOTTOM).getAvailableTools() > 0 ? 45 : 22;
                        int maxHeight = descriptor.getToolWindowManagerContainer().getHeight() - diff;
                        if (r.height > maxHeight)
                            r.height = maxHeight;

                        break;
                    case BOTTOM:
                        if (r.height < 5)
                            r.height = 5;

                        // Min y
                        int minY = descriptor.getToolWindowManagerContainer().getY();
                        minY -= descriptor.getToolBar(ToolWindowAnchor.TOP).getAvailableTools() > 0 ? -24 : -1;
                        if (r.y < minY) {
                            r.y = minY;
                        }

                        // Max width
                        diff = descriptor.getToolBar(ToolWindowAnchor.TOP).getAvailableTools() > 0 ? 47 : 24;
                        maxHeight = descriptor.getToolWindowManagerContainer().getHeight() - diff;
                        if (r.height > maxHeight)
                            r.height = maxHeight;

                        // Max y
                        int maxY = descriptor.getToolWindowManagerContainer().getHeight();
                        if (descriptor.getToolBar(ToolWindowAnchor.TOP).getAvailableTools() > 0) {
                            maxY -= 28;
                        } else
                            maxY -= 28;

                        if (r.y > maxY)
                            r.y = maxY;

                        break;
                }
                component.setBounds(r);
                component.validate();
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

    private int getCursor(Component c, int corner) {
        if (corner == -1)
            return 0;

        switch (toolWindow.getAnchor()) {
            case LEFT:
                if (corner == 10) {
                    if (c.getWidth() <= 5)
                        return Cursor.E_RESIZE_CURSOR;
                }
                return LEFT_CURSOR_MAPPING[corner];
            case RIGHT:
                if (corner == 10) {
                    if (c.getWidth() <= 5)
                        return Cursor.W_RESIZE_CURSOR;
                }
                return RIGHT_CURSOR_MAPPING[corner];
            case TOP:
                if (corner == 2) {
                    if (c.getHeight() <= 5)
                        return Cursor.S_RESIZE_CURSOR;
                }
                return TOP_CURSOR_MAPPING[corner];
            case BOTTOM:
                if (corner == 14) {
                    if (c.getHeight() <= 5)
                        return Cursor.W_RESIZE_CURSOR;
                }
                return BOTTOM_CURSOR_MAPPING[corner];
        }
        return 0;
    }

    private int calculatePosition(int spot, int width) {
        if (spot < BORDER_DRAG_THICKNESS)
            return 0;

        if (spot < CORNER_DRAG_WIDTH)
            return 1;

        if (spot >= width - BORDER_DRAG_THICKNESS) {
            return 4;
        }

        if (spot >= width - CORNER_DRAG_WIDTH) {
            return 3;
        }

        return 2;
    }

}
