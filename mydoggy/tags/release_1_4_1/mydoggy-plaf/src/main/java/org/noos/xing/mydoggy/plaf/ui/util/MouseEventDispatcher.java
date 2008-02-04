package org.noos.xing.mydoggy.plaf.ui.util;

import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.EventListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MouseEventDispatcher implements MouseInputListener {
    protected EventListenerList eventListenerList;


    public MouseEventDispatcher() {
        this.eventListenerList = new EventListenerList();
    }


    public void mouseClicked(MouseEvent e) {
        for (MouseListener listener : eventListenerList.getListeners(MouseListener.class))
            listener.mouseClicked(e);
    }

    public void mousePressed(MouseEvent e) {
        for (MouseListener listener : eventListenerList.getListeners(MouseListener.class))
            listener.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        for (MouseListener listener : eventListenerList.getListeners(MouseListener.class))
            listener.mouseReleased(e);
    }

    public void mouseEntered(MouseEvent e) {
        for (MouseListener listener : eventListenerList.getListeners(MouseListener.class))
            listener.mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        for (MouseListener listener : eventListenerList.getListeners(MouseListener.class))
            listener.mouseExited(e);
    }

    public void mouseDragged(MouseEvent e) {
        for (MouseMotionListener listener : eventListenerList.getListeners(MouseMotionListener.class))
            listener.mouseDragged(e);
    }

    public void mouseMoved(MouseEvent e) {
        for (MouseMotionListener listener : eventListenerList.getListeners(MouseMotionListener.class))
            listener.mouseMoved(e);
    }


    public void addListener(EventListener eventListener) {
        if (eventListener instanceof MouseListener)
            eventListenerList.add(MouseListener.class, (MouseListener) eventListener);

        if (eventListener instanceof MouseMotionListener)
            eventListenerList.add(MouseMotionListener.class, (MouseMotionListener) eventListener);

        if (eventListener instanceof MouseWheelListener)
            eventListenerList.add(MouseWheelListener.class, (MouseWheelListener) eventListener);
    }

    public void removeListener(EventListener eventListener) {
        if (eventListener instanceof MouseListener)
            eventListenerList.remove(MouseListener.class, (MouseListener) eventListener);

        if (eventListener instanceof MouseMotionListener)
            eventListenerList.remove(MouseMotionListener.class, (MouseMotionListener) eventListener);

        if (eventListener instanceof MouseWheelListener)
            eventListenerList.remove(MouseWheelListener.class, (MouseWheelListener) eventListener);
    }


}
