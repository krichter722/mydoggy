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
            Component exitedCmp = getComponentFor(previousMoveComponent, MouseEvent.MOUSE_EXITED);
            if (exitedCmp == null)
                exitedCmp = previousMoveComponent;

            MouseEvent me = new MouseEvent(exitedCmp, MouseEvent.MOUSE_EXITED, e.getWhen(),
                                           0, 0, 0, 0, false);
            exitedCmp.dispatchEvent(me);
        }

        if (component != null) {
            Cursor cursor2 = component.getCursor();
            if (cursor2 != glassPane.getCursor())
                glassPane.setCursor(cursor2);
            else
                glassPane.setCursor(Cursor.getDefaultCursor());


            if (component != previousMoveComponent) {
                Component enteredCmp = getComponentFor(component, MouseEvent.MOUSE_ENTERED);
                if (enteredCmp == null) 
                    enteredCmp = component;

                MouseEvent me = new MouseEvent(enteredCmp, MouseEvent.MOUSE_ENTERED, e.getWhen(),
                                               0, 0, 0, 0, false);
                enteredCmp.dispatchEvent(me);
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

                    Component targetCmp = getComponentFor(mouseEventTarget, e.getID());
                    if (targetCmp == null)
                        targetCmp = mouseEventTarget;

                    MouseEvent event = new MouseEvent(targetCmp, e.getID(), e.getWhen(), e.getModifiers(),
                                                      mouseEventTargetPoint.x,
                                                      mouseEventTargetPoint.y,
                                                      e.getClickCount(),
                                                      e.isPopupTrigger(),
                                                      e.getButton());
                    targetCmp.dispatchEvent(event);
                }
            }
        }
    }

    public Component getComponentFor(Component component, int mouseType) {
        while (component != null) {
            switch(mouseType) {
                case MouseEvent.MOUSE_CLICKED:
                case MouseEvent.MOUSE_PRESSED:
                case MouseEvent.MOUSE_RELEASED:
                case MouseEvent.MOUSE_ENTERED:
                case MouseEvent.MOUSE_EXITED:
                    if (component.getMouseListeners().length == 0) {
                        component = component.getParent();
                    } else
                        return component;
                    break;

                case MouseEvent.MOUSE_DRAGGED:
                case MouseEvent.MOUSE_MOVED:
                    if (component.getMouseMotionListeners().length == 0) {
                        component = component.getParent();
                    } else
                        return component;
                    break;

                case MouseEvent.MOUSE_WHEEL:
                    if (component.getMouseWheelListeners().length == 0) {
                        component = component.getParent();
                    } else
                        return component;
                    break;
            }
        }
        return component;
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

