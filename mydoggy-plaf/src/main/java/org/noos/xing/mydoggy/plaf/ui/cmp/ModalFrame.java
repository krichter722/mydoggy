package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ModalFrame extends JFrame implements ModalWindow,
                                                  PropertyChangeListener,
                                                  ActionListener {
    protected MyDoggyToolWindowManager toolWindowManager;

    // Modal support
    protected Window modalToWindow;
    protected boolean notifiedModalToWindow;
    protected Component returnFocus;

    // Multi split support
    protected DockableDropPanel dockableDropPanel;
    protected MultiSplitDockableContainer multiSplitDockableContainer;

    // Transparency support
    protected Timer transparencyTimer;
    protected TransparencyManager<Window> transparencyManager;
    protected TransparencyAnimation transparencyAnimation;


    public ModalFrame(MyDoggyToolWindowManager toolWindowManager, ToolWindow toolWindow, Window owner, Component returnFocus, boolean modal) {
        super(toolWindow.getTitle());

        this.toolWindowManager = toolWindowManager;
        this.returnFocus = returnFocus;

        setUndecorated(true);
        setAlwaysOnTop(SwingUtil.getBoolean("dialog.owner.enabled", true));
        setFocusableWindowState(true);

        synchronized (ModalFrame.this) {
            if (modal)
                modalToWindow = owner;

            notifiedModalToWindow = true;
        }

        enableEvents(WindowEvent.WINDOW_EVENT_MASK | ComponentEvent.MOUSE_MOTION_EVENT_MASK);

        initComponents();
    }


    public void propertyChange(final PropertyChangeEvent evt) {
        if ("active".equals(evt.getPropertyName())) {
            ToolWindow toolWindow = (ToolWindow) evt.getSource();
            FloatingTypeDescriptor typeDescriptor = toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class);

            if (transparencyTimer != null) {
                transparencyTimer.stop();
                if (transparencyAnimation.isAnimating()) {
                    synchronized (ModalFrame.this) {
                        if (transparencyManager.isAlphaModeEnabled(ModalFrame.this)) {
                            transparencyAnimation.stop();
                            transparencyManager.setAlphaModeRatio(ModalFrame.this, 0.0f);
                        }
                    }
                }
            }

            if (typeDescriptor.isTransparentMode()) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    if (transparencyAnimation != null) {
                        transparencyTimer = new Timer(typeDescriptor.getTransparentDelay() + 100, this) {
                            @Override
                            protected void fireActionPerformed(ActionEvent e) {
                                e = new ActionEvent(evt.getSource(),
                                                    e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
                                super.fireActionPerformed(e);
                            }
                        };
                        transparencyTimer.start();
                    }
                } else {
                    if (transparencyTimer != null)
                        transparencyTimer.stop();

                    if (transparencyAnimation != null) {
                        synchronized (ModalFrame.this) {
                            if (transparencyManager.isAlphaModeEnabled(ModalFrame.this)) {
                                transparencyAnimation.stop();
                                transparencyManager.setAlphaModeRatio(ModalFrame.this, 0.0f);
                            }
                        }
                    }
                }
            }
        }
/*
        else if (evt.getPropertyName().startsWith("visible")) {
            synchronized (transparencyManager) {
                if (evt.getNewValue() == Boolean.FALSE && transparencyManager.isAlphaModeEnabled(ModalDialog.this)) {
                    if (transparencyTimer != null)
                        transparencyTimer.stop();

                    if (transparencyManager.isAlphaModeEnabled(ModalDialog.this)) {
                        transparencyAnimation.stop();
                        transparencyManager.setAlphaModeRatio(ModalDialog.this, 0.0f);
                    }
                }
            }

            if (evt.getNewValue() == Boolean.TRUE) {
                ToolWindow toolWindow = (ToolWindow) evt.getSource();
                FloatingTypeDescriptor typeDescriptor = toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class);

                if (typeDescriptor.isTransparentMode()) {
                    transparencyTimer = new Timer(1000 + typeDescriptor.getTransparentDelay(), this);
                    transparencyTimer.start();
                }
            }
        }
*/
    }

    public void actionPerformed(ActionEvent e) {
        if (transparencyTimer.isRunning()) {
            transparencyTimer.stop();

            if (transparencyAnimation != null) {
                FloatingTypeDescriptor floatingTypeDescriptor = ((ToolWindow) e.getSource()).getTypeDescriptor(FloatingTypeDescriptor.class);
                transparencyAnimation.setAlpha(floatingTypeDescriptor.getTransparentRatio());
                transparencyAnimation.show();
            }
        }
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
        addDockable(toolWindow, content, null, AggregationPosition.DEFAULT);
    }

    public void addDockable(ToolWindow toolWindow,
                            Component content,
                            ToolWindow aggregationOnDockable,
                            AggregationPosition aggregationPosition) {
        multiSplitDockableContainer.addDockable(toolWindow,
                                                content,
                                                aggregationOnDockable,
                                                -1,
                                                aggregationPosition);
        toolWindow.addPropertyChangeListener(this);
    }

    public void removeDockable(ToolWindow toolWindow) {
        try {
            multiSplitDockableContainer.removeDockable(toolWindow);
        } finally {
            toolWindow.removePropertyChangeListener(this);
        }
    }

    public int getNumDockables() {
        return multiSplitDockableContainer.getContentCount();
    }

    public ToolWindow getFirstToolWindow() {
        return (ToolWindow) multiSplitDockableContainer.getContents().get(0).dockable;
    }


    protected void initComponents() {
        multiSplitDockableContainer = new MultiSplitDockableContainer(toolWindowManager, JSplitPane.VERTICAL_SPLIT);

        dockableDropPanel = new ModalWindowDockableDropPanel(this, toolWindowManager);
        dockableDropPanel.setComponent(multiSplitDockableContainer);

        ((JComponent) getContentPane()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setLayout(new ExtendedTableLayout(new double[][]{{0, TableLayout.FILL, 0}, {0, TableLayout.FILL, 0}}));
        add(dockableDropPanel, "1,1,FULL,FULL");

        this.transparencyManager = SwingUtil.getTransparencyManager();
        if (transparencyManager.isServiceAvailable()) {
            this.transparencyAnimation = new TransparencyAnimation(SwingUtil.getTransparencyManager(), this, 0.0f);
        } else
            this.transparencyAnimation = null;
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
