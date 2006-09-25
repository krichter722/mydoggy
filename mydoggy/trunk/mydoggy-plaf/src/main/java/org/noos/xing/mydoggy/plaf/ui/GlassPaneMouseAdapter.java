package org.noos.xing.mydoggy.plaf.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class GlassPaneMouseAdapter implements MouseListener, MouseMotionListener, MouseWheelListener {
    private Container glassPane;
    private Container contentPane;

    private Component previousMoveComponent;
    private Component mouseEventTarget;

    public GlassPaneMouseAdapter(RootPaneContainer rootPaneContainer) {
        this.glassPane = rootPaneContainer.getRootPane();
        this.contentPane = rootPaneContainer.getLayeredPane();
    }

    public void mouseDragged(MouseEvent e) {
        redispatchMouseEvent(e, true);
    }

    public void mouseMoved(MouseEvent e) {
        redispatchMouseEvent(e, true);
    }


    public void mouseClicked(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseEntered(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseExited(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mousePressed(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseReleased(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        redispatchMouseEvent(e, false);
    }


    private void redispatchMouseEvent(MouseEvent e, boolean updateCursor) {
        Point glassPanePoint = e.getPoint();
        Component component;
        Container container = contentPane;
        Point containerPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, contentPane);

        component = SwingUtilities.getDeepestComponentAt(container, containerPoint.x, containerPoint.y);

        if (previousMoveComponent != null && component != previousMoveComponent) {
            MouseEvent me = new MouseEvent(previousMoveComponent, MouseEvent.MOUSE_EXITED, e.getWhen(),
                                           0, 0, 0, 0, false);
            previousMoveComponent.dispatchEvent(me);
        }

        if (component != null) {
            Cursor cursor2 = component.getCursor();
            if (cursor2 != glassPane.getCursor())
                glassPane.setCursor(cursor2);

            if (component != previousMoveComponent) {
                MouseEvent me = new MouseEvent(component, MouseEvent.MOUSE_ENTERED, e.getWhen(),
                                               0, 0, 0, 0, false);
                component.dispatchEvent(me);
            }
            previousMoveComponent = component;

            int eventId = e.getID();
            if (eventId != MouseEvent.MOUSE_ENTERED && eventId != MouseEvent.MOUSE_EXITED) {
                // 4508327 : MOUSE_CLICKED should only go to the recipient of
                // the accompanying MOUSE_PRESSED, so don't reset mouseEventTarget on a
                // MOUSE_CLICKED.
                if (!isMouseGrab(e) && eventId != MouseEvent.MOUSE_CLICKED) {
                    mouseEventTarget = component;
                }

                if (mouseEventTarget != null) {
                    Point mouseEventTargetPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, mouseEventTarget);

                    mouseEventTarget.dispatchEvent(new MouseEvent(mouseEventTarget, e.getID(), e.getWhen(), e.getModifiers(),
                                                                  mouseEventTargetPoint.x,
                                                                  mouseEventTargetPoint.y,
                                                                  e.getClickCount(),
                                                                  e.isPopupTrigger()));
                }
            }
        }

    }

    /*
     * This method effectively returns whether or not a mouse button was down
     * just BEFORE the event happened.  A better method name might be
     * wasAMouseButtonDownBeforeThisEvent().
     */
    private boolean isMouseGrab(MouseEvent e) {
        int modifiers = e.getModifiersEx();

        if (e.getID() == MouseEvent.MOUSE_PRESSED
            || e.getID() == MouseEvent.MOUSE_RELEASED) {
            switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    modifiers ^= InputEvent.BUTTON1_DOWN_MASK;
                    break;
                case MouseEvent.BUTTON2:
                    modifiers ^= InputEvent.BUTTON2_DOWN_MASK;
                    break;
                case MouseEvent.BUTTON3:
                    modifiers ^= InputEvent.BUTTON3_DOWN_MASK;
                    break;
            }
        }
        /* modifiers now as just before event */
        return ((modifiers & (InputEvent.BUTTON1_DOWN_MASK
                              | InputEvent.BUTTON2_DOWN_MASK
                              | InputEvent.BUTTON3_DOWN_MASK)) != 0);
    }
}

