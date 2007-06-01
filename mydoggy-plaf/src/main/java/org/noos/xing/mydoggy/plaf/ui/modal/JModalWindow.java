package org.noos.xing.mydoggy.plaf.ui.modal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

public class JModalWindow extends JWindow {
    private Window modalToWindow;
    private boolean notifiedModalToWindow;
    private Component returnFocus;

    public JModalWindow(Window owner, Component returnFocus, boolean modal) {
        super(owner);
        setFocusableWindowState(true);
        this.returnFocus = returnFocus;
        if (modal)
            modalToWindow = owner;

        synchronized (JModalWindow.this) {
            notifiedModalToWindow = true;
        }

        enableEvents(WindowEvent.WINDOW_EVENT_MASK | ComponentEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public void setVisible(boolean visible) {
        if (!visible)
            restoreOwner();
        else {
            if (!isVisible()) {
                synchronized (JModalWindow.this) {
                    if ((modalToWindow != null) && notifiedModalToWindow) {
                        modalToWindow.setEnabled(false);
                        notifiedModalToWindow = false;
                    }
                }
            }
        }
        
        super.setVisible(visible);
    }

    public void setModal(boolean modal) {
        modalToWindow = modal ? getOwner() : null;
    }

    public boolean isModal() {
        return modalToWindow != null;
    }

    
    protected void processWindowEvent(WindowEvent windowEvent) {
        switch (windowEvent.getID()) {
            case WindowEvent.WINDOW_CLOSING:
                tryToDispose(windowEvent);
                break;
            case WindowEvent.WINDOW_CLOSED:
                close(windowEvent);
                break;
            default:
                super.processWindowEvent(windowEvent);
                break;
        }
    }


    private void restoreOwner() {
        synchronized (JModalWindow.this) {
            if ((modalToWindow != null) && !notifiedModalToWindow) {
                modalToWindow.setEnabled(true);
                modalToWindow.toFront();
                notifiedModalToWindow = true;
            }

            if (returnFocus != null) {
                Window owner = SwingUtilities.windowForComponent(returnFocus);
                boolean stillBusy;

                stillBusy = !owner.isEnabled();

                if (!stillBusy) {
                    returnFocus.requestFocusInWindow();
                }
            }
        }
    }

    private void tryToDispose(WindowEvent windowEvent) {
        dispose();
        super.processWindowEvent(windowEvent);
    }

    private void close(WindowEvent windowEvent) {
        restoreOwner();
        super.processWindowEvent(windowEvent);
    }

}
