package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

public class JModalWindow extends JWindow {
    protected ResourceManager resourceManager;
    protected Window modalToWindow;
    protected boolean notifiedModalToWindow;
    protected Component returnFocus;

    public JModalWindow(ResourceManager resourceManager, Window owner, Component returnFocus, boolean modal) {
        super(owner);
        
        this.resourceManager = resourceManager;
        setFocusableWindowState(true);
        this.returnFocus = returnFocus;
        synchronized (JModalWindow.this) {
            if (modal)
                modalToWindow = owner;

            notifiedModalToWindow = true;
        }

        enableEvents(WindowEvent.WINDOW_EVENT_MASK | ComponentEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public void setVisible(boolean visible) {
        if (!visible) {
            TransparencyManager<Window> transparencyManager = resourceManager.getTransparencyManager();
            transparencyManager.setAlphaModeRatio(this, 0.0f);

            restoreOwner();
        } else {
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


    public void setModal(boolean modal) {
        synchronized (JModalWindow.this) {
            modalToWindow = modal ? getOwner() : null;
        }
    }

    public boolean isModal() {
        synchronized (JModalWindow.this) {
            return modalToWindow != null;
        }
    }


    protected void restoreOwner() {
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

    protected void tryToDispose(WindowEvent windowEvent) {
        dispose();
        super.processWindowEvent(windowEvent);
    }

    protected void close(WindowEvent windowEvent) {
        restoreOwner();
        super.processWindowEvent(windowEvent);
    }

}
