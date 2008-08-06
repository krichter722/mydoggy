package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

/**
 * TODO: implements new methods...like ModalDialog...
 */
public class ModalFrame extends JFrame implements ModalWindow {
    protected Window modalToWindow;
    protected boolean notifiedModalToWindow;
    protected Component returnFocus;


    public ModalFrame(Dockable dockable, Window owner, Component returnFocus, boolean modal) {
        setAlwaysOnTop(owner != null);
        setUndecorated(true);
        setTitle(dockable.getTitle());
        // TODO: set the icon from dockable...

        setFocusableWindowState(true);
        this.returnFocus = returnFocus;
        synchronized (ModalFrame.this) {
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
                synchronized (ModalFrame.this) {
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
        synchronized (ModalFrame.this) {
            modalToWindow = modal ? getOwner() : null;
        }
    }

    public boolean isModal() {
        synchronized (ModalFrame.this) {
            return modalToWindow != null;
        }
    }

    public void addDockable(ToolWindow toolWindow, Component content) {
    }

    public void addDockable(ToolWindow toolWindow, Component content, ToolWindow aggregationOnDockable, AggregationPosition aggregationPosition) {
    }

    public void removeDockable(ToolWindow toolWindow) {
    }

    public int getNumDockables() {
        return 0;
    }

    public ToolWindow getFirstToolWindow() {
        return null;
    }


    protected void restoreOwner() {
        synchronized (ModalFrame.this) {
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