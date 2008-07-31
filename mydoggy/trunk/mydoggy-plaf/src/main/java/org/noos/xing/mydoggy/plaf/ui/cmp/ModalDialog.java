package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowFloatingDropTarget;
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
    protected MyDoggyToolWindowManager toolWindowManager;

    protected MultiSplitDockableContainer multiSplitDockableContainer;
    protected ContentPanel contentPanel;


    public ModalDialog(MyDoggyToolWindowManager toolWindowManager, Window owner, Component returnFocus, boolean modal) {
        super(owner instanceof Frame ? (Frame) owner : null);

        this.toolWindowManager = toolWindowManager;
        this.returnFocus = returnFocus;

        setUndecorated(true);
        setAlwaysOnTop(SwingUtil.getBoolean("dialog.owner.enabled", true));
        setFocusableWindowState(true);

        synchronized (ModalDialog.this) {
            if (modal)
                modalToWindow = owner;

            notifiedModalToWindow = true;
        }

        enableEvents(WindowEvent.WINDOW_EVENT_MASK | ComponentEvent.MOUSE_MOTION_EVENT_MASK);

        initComponents();
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
    }

    public void removeDockable(ToolWindow toolWindow) {
        multiSplitDockableContainer.removeDockable(toolWindow);
    }

    public int getNumDockables() {
        return multiSplitDockableContainer.getContentCount();
    }

    public ToolWindow getFirstToolWindow() {
        return (ToolWindow) multiSplitDockableContainer.getContents().get(0).dockable;
    }


    protected void initComponents() {
        multiSplitDockableContainer = new MultiSplitDockableContainer(toolWindowManager, JSplitPane.VERTICAL_SPLIT);

        contentPanel = new ContentPanel("toolWindow.container.");
        contentPanel.setDropTarget(new ToolWindowFloatingDropTarget(this, contentPanel, toolWindowManager));
        contentPanel.setComponent(multiSplitDockableContainer);

        ((JComponent) getContentPane()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setLayout(new ExtendedTableLayout(new double[][]{{0, TableLayout.FILL, 0}, {0, TableLayout.FILL, 0}}));
        add(contentPanel, "1,1,FULL,FULL");
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


/*
    public class FloatingToolTransparencyListener implements PropertyChangeListener, ActionListener {
        protected final TransparencyManager<Window> transparencyManager;
        protected TransparencyAnimation transparencyAnimation;

        protected Timer timer;


        public FloatingToolTransparencyListener() {
            this.transparencyManager = SwingUtil.getTransparencyManager();

            if (transparencyManager.isServiceAvailable()) {
                this.transparencyAnimation = new TransparencyAnimation(
                        SwingUtil.getTransparencyManager(),
                        ModalDialog.this,
                        0.0f
                );

                descriptor.getManager().addInternalPropertyChangeListener("active", this);
                descriptor.getManager().addInternalPropertyChangeListener("visible.FLOATING", this);
                descriptor.getManager().addInternalPropertyChangeListener("visible.FLOATING_FREE", this);
            } else
                this.transparencyAnimation = null;
        }


        public synchronized void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor */
/*|| !manager.getDockableDelegator().isVisible()*/
/*
                || (descriptor.getToolWindow().getType() != ToolWindowType.FLOATING &&
                    descriptor.getToolWindow().getType() != ToolWindowType.FLOATING_FREE))
                return;

            if ("active".equals(evt.getPropertyName())) {
                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
                if (descriptor.getFloatingContainer().isAnimating()) {
                    if (timer != null) {
                        timer.stop();
                        synchronized (transparencyManager) {
                            if (transparencyManager.isAlphaModeEnabled(ModalDialog.this)) {
                                transparencyAnimation.stop();
                                transparencyManager.setAlphaModeRatio(ModalDialog.this, 0.0f);
                            }
                        }
                    }
                    return;
                }

                if (typeDescriptor.isTransparentMode()) {
                    if (evt.getNewValue() == Boolean.FALSE) {
                        timer = new Timer(typeDescriptor.getTransparentDelay(), this);
                        timer.start();
                    } else {
                        if (timer != null)
                            timer.stop();

                        synchronized (transparencyManager) {
                            if (transparencyManager.isAlphaModeEnabled(ModalDialog.this)) {
                                transparencyAnimation.stop();
                                transparencyManager.setAlphaModeRatio(ModalDialog.this, 0.0f);
                            }
                        }
                    }
                }
            } else if (evt.getPropertyName().startsWith("visible.")) {
                synchronized (transparencyManager) {
                    if (evt.getNewValue() == Boolean.FALSE && transparencyManager.isAlphaModeEnabled(ModalDialog.this))) {
                        if (timer != null)
                            timer.stop();

                        if (transparencyManager.isAlphaModeEnabled(ModalDialog.this)) {
                            transparencyAnimation.stop();
                            transparencyManager.setAlphaModeRatio(ModalDialog.this, 0.0f);
                        }
                    }
                }

                if (evt.getNewValue() == Boolean.TRUE) {
                    FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
                    if (typeDescriptor.isTransparentMode()) {
                        timer = new Timer(1000 + typeDescriptor.getTransparentDelay(), this);
                        timer.start();
                    }
                }
            }
        }

        public synchronized void actionPerformed(ActionEvent e) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
                if (!descriptor.getToolWindow().isVisible()
                    || (descriptor.getToolWindow().getType() != ToolWindowType.FLOATING &&
                        descriptor.getToolWindow().getType() != ToolWindowType.FLOATING_FREE))
                    return;

                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
                synchronized (transparencyManager) {
                    transparencyAnimation.setAlpha(typeDescriptor.getTransparentRatio());
                    transparencyAnimation.show();
    //                transparencyManager.setAlphaModeRatio(window, typeDescriptor.getTransparentRatio());
                }
            }
        }

    }

*/
}
