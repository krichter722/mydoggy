package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

public class ModalDialog extends JDialog implements ModalWindow {
    protected Window modalToWindow;
    protected boolean notifiedModalToWindow;
    protected Component returnFocus;


    public ModalDialog(Window owner, Component returnFocus, boolean modal) {
        super(owner instanceof Frame ? (Frame) owner : null);

        setUndecorated(true);
        setAlwaysOnTop(SwingUtil.getBoolean("dialog.owner.enabled", true));
        setFocusableWindowState(true);
        this.returnFocus = returnFocus;
        synchronized (ModalDialog.this) {
            if (modal)
                modalToWindow = owner;

            notifiedModalToWindow = true;
        }

        enableEvents(WindowEvent.WINDOW_EVENT_MASK | ComponentEvent.MOUSE_MOTION_EVENT_MASK);
    }


    public void setVisible(boolean visible) {
        if (!visible) {
            TransparencyManager<Window> transparencyManager = SwingUtil.getTransparencyManager();
            transparencyManager.setAlphaModeRatio(this, 0.0f);

            restoreOwner();
        } else {
            if (!isVisible()) {
                synchronized (ModalDialog.this) {
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

    public Window getWindow() {
        return this;
    }

    public void setModal(boolean modal) {
        synchronized (ModalDialog.this) {
            modalToWindow = modal ? getOwner() : null;
        }
    }

    public boolean isModal() {
        synchronized (ModalDialog.this) {
            return modalToWindow != null;
        }
    }


    protected void restoreOwner() {
        synchronized (ModalDialog.this) {
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
