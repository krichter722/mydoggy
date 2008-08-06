package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ModalDialog extends JDialog implements ModalWindow,
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


    public void propertyChange(final PropertyChangeEvent evt) {
        if ("active".equals(evt.getPropertyName())) {
            ToolWindow toolWindow = (ToolWindow) evt.getSource();
            FloatingTypeDescriptor typeDescriptor = toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class);

            if (transparencyTimer != null) {
                transparencyTimer.stop();
                if (transparencyAnimation.isAnimating()) {
                    synchronized (transparencyManager) {
                        if (transparencyManager.isAlphaModeEnabled(ModalDialog.this)) {
                            transparencyAnimation.stop();
                            transparencyManager.setAlphaModeRatio(ModalDialog.this, 0.0f);
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
                        synchronized (transparencyManager) {
                            if (transparencyManager.isAlphaModeEnabled(ModalDialog.this)) {
                                transparencyAnimation.stop();
                                transparencyManager.setAlphaModeRatio(ModalDialog.this, 0.0f);
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

        dockableDropPanel = new ModalDialogDockableDropPanel();
        dockableDropPanel.setComponent(multiSplitDockableContainer);

        ((JComponent) getContentPane()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setLayout(new ExtendedTableLayout(new double[][]{{0, TableLayout.FILL, 0}, {0, TableLayout.FILL, 0}}));
        add(dockableDropPanel, "1,1,FULL,FULL");

        this.transparencyManager = SwingUtil.getTransparencyManager();
        if (transparencyManager.isServiceAvailable()) {
            this.transparencyAnimation = new TransparencyAnimation(
                    SwingUtil.getTransparencyManager(),
                    ModalDialog.this,
                    0.0f
            );
        } else
            this.transparencyAnimation = null;
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


    public class ModalDialogDockableDropPanel extends DockableDropPanel {

        public ModalDialogDockableDropPanel() {
            super("toolWindow.container.", ToolWindow.class);   // TODO: toolWindow.container. can we change it?
        }


        public boolean dragStart(Transferable transferable, int action) {
             try {
                 if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                     if (System.identityHashCode(toolWindowManager) == (Integer) transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                         if (action == DnDConstants.ACTION_MOVE &&
                             (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF) ||
                              transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF) ||
                              transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)))

                             return super.dragStart(transferable, action);
                     }
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
            
             return false;
         }

        public boolean drop(Transferable transferable) {
            if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF)) {
                try {
                    ToolWindow toolWindow = toolWindowManager.getToolWindow(
                            transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_ID_DF)
                    );

                    if (toolWindow != null) {
                        // Move tool to another anchor

                        // Chech if it was a tab
                        if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)) {
                            // Remove from tab
                            ToolWindowTab tab = (ToolWindowTab) toolWindowManager.getDockable(
                                    transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)
                            );
                            tab.getOwner().removeToolWindowTab(tab);
                            toolWindow = (ToolWindow) tab.getDockableDelegator();
                        }

                        ToolWindow onToolWindow = (ToolWindow) getOnDockable();

                        if (toolWindow == onToolWindow)
                            return false;

                        boolean oldAggregateMode = toolWindow.isAggregateMode();
                        toolWindow.setAggregateMode(true);
                        ToolWindowAnchor dragAnchor = getOnAnchor();
                        try {
                            if (dragAnchor == null && onToolWindow != null && toolWindow != onToolWindow) {
                                if (!SwingUtil.getBoolean("drag.toolwindow.asTab", true)) {
                                    // Choose drag anchor ...
                                    switch (onToolWindow.getAnchor()) {
                                        case LEFT:
                                        case RIGHT:
                                            dragAnchor = ToolWindowAnchor.TOP;
                                            break;
                                        case TOP:
                                        case BOTTOM:
                                            dragAnchor = ToolWindowAnchor.LEFT;
                                            break;
                                    }
                                }
                            }

                            if (dragAnchor != null) {
                                switch (dragAnchor) {
                                    case LEFT:
                                        if (onToolWindow != null) {
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.LEFT);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.aggregateByReference(AggregationPosition.LEFT,
                                                                                getFirstToolWindow());
                                            }
                                        }
                                        break;
                                    case RIGHT:
                                        if (onToolWindow != null) {
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.aggregateByReference(AggregationPosition.RIGHT,
                                                                                getFirstToolWindow());
                                            }
                                        }
                                        break;
                                    case BOTTOM:
                                        if (onToolWindow != null) {
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.aggregateByReference(AggregationPosition.BOTTOM,
                                                                                getFirstToolWindow());

                                            }
                                        }
                                        break;
                                    case TOP:
                                        if (onToolWindow != null) {
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.aggregateByReference(AggregationPosition.TOP,
                                                                                getFirstToolWindow());

                                            }
                                        }
                                        break;
                                }
                                toolWindow.setActive(true);
                            } else {
                                if (onToolWindow != null && toolWindow != onToolWindow) {
                                    onToolWindow.addToolWindowTab(toolWindow).setSelected(true);
                                    onToolWindow.setActive(true);
                                } else {
                                    toolWindow.aggregateByReference(AggregationPosition.DEFAULT,
                                                                    getFirstToolWindow());
                                    toolWindow.setActive(true);
                                }
                            }
                        } finally {
                            toolWindow.setAggregateMode(oldAggregateMode);
                        }

                        return true;
                    } else
                        return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return false;
        }


        protected boolean checkCondition(ToolWindow toolWindow) {
            if (toolWindow.getAnchor() != ToolWindowAnchor.BOTTOM)
                 return true;

             int visibleNum = 0;
             boolean flag = false;
             for (ToolWindow tool : toolWindowManager.getToolsByAnchor(ToolWindowAnchor.BOTTOM)) {
                 if (tool.isVisible())
                     visibleNum++;
                 if (tool == toolWindow)
                     flag = true;
             }

             return (!flag || visibleNum != 1);
        }
    }

}
