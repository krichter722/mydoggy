package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingResizeMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.translucent.TranslucentPanel;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FloatingLivePanel extends TranslucentPanel implements FloatingLiveWindow,
                                                                   PropertyChangeListener,
                                                                   ActionListener {

    protected MyDoggyToolWindowManager manager;
    protected JLayeredPane layeredPane;

    protected DockableDropPanel dockableDropPanel;
    protected MultiSplitDockableContainer multiSplitDockableContainer;

    protected TransparencyAnimation animation;
    protected Timer timer;


    public FloatingLivePanel(MyDoggyToolWindowManager manager) {
        this.manager = manager;

        initComponents();
        initListeners();
    }


    public void propertyChange(final PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("active".equals(propertyName)) {
            if (Boolean.TRUE.equals(evt.getNewValue())) {
                layeredPane.setLayer(this, JLayeredPane.DEFAULT_LAYER + 4);

                if (timer != null) {
                    timer.stop();
                    if (animation.isAnimating())
                        animation.stop();
                }
                setAlphaModeRatio(1.0f);
            } else {
                layeredPane.setLayer(this, JLayeredPane.DEFAULT_LAYER + 3);

                FloatingLiveTypeDescriptor floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) ((ToolWindow) evt.getSource()).getTypeDescriptor(ToolWindowType.FLOATING_LIVE);
                if (floatingLiveTypeDescriptor.isTransparentMode()) {
                    timer = new Timer(floatingLiveTypeDescriptor.getTransparentDelay() + 100, this) {
                        @Override
                        protected void fireActionPerformed(ActionEvent e) {
                            e = new ActionEvent(evt.getSource(),
                                                e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
                            super.fireActionPerformed(e);
                        }
                    };
                    timer.start();
                }
            }
            SwingUtil.repaint(layeredPane);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (timer.isRunning()) {
            timer.stop();

            FloatingLiveTypeDescriptor floatingLiveTypeDescriptor = ((ToolWindow) e.getSource()).getTypeDescriptor(FloatingLiveTypeDescriptor.class);
            animation.setAlpha(floatingLiveTypeDescriptor.getTransparentRatio());
            animation.show();
        }
    }


    public void resetLayout() {
        TableLayout layout = (TableLayout) getLayout();
        layout.setColumn(0, 0);
        layout.setColumn(2, 0);
        layout.setRow(0, 0);
        layout.setRow(2, 0);
    }

    public void setLayout() {
        TableLayout layout = (TableLayout) getLayout();
        layout.setColumn(0, 2);
        layout.setColumn(2, 2);
        layout.setRow(0, 2);
        layout.setRow(2, 2);
    }

    public void addDockable(ToolWindow toolWindow,
                            Component content) {
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

    public void mount() {
        if (getParent() == layeredPane)
            return;

        layeredPane.remove(this);
        layeredPane.setLayer(this, JLayeredPane.DEFAULT_LAYER + 3);
        layeredPane.add(this);
    }

    public void unmount() {
        if (multiSplitDockableContainer.getDockableCount() == 0) {
            setLayout();
            layeredPane.remove(this);
            setBorder(null);
        }
        SwingUtil.repaint(layeredPane);
    }


    protected void initComponents() {
        this.layeredPane = manager.getLayeredPane();

        multiSplitDockableContainer = new MultiSplitDockableContainer(manager, JSplitPane.VERTICAL_SPLIT);

        dockableDropPanel = new FloatingLiveDockableDropPanel();
        dockableDropPanel.setComponent(multiSplitDockableContainer);

        setLayout(new ExtendedTableLayout(new double[][]{{2, TableLayout.FILL, 2}, {2, TableLayout.FILL, 2}}));
        add(dockableDropPanel, "1,1,FULL,FULL");

        this.animation = new TransparencyAnimation(this, this, 1.0f, 500f);
    }

    protected void initListeners() {
        FloatingResizeMouseInputHandler resizeMouseInputHandler = new FloatingResizeMouseInputHandler(this);

        addMouseMotionListener(resizeMouseInputHandler);
        addMouseListener(resizeMouseInputHandler);
    }


    public class FloatingLiveDockableDropPanel extends DockableDropPanel {

        public FloatingLiveDockableDropPanel() {
            super("toolWindow.container.", ToolWindow.class);
        }


        public boolean dragStart(Transferable transferable, int action) {
            try {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                    if (System.identityHashCode(manager) == (Integer) transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                        if (action == DnDConstants.ACTION_MOVE &&
                            (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF) ||
                             transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF) ||
                             transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)) )

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
                    ToolWindow toolWindow = manager.getToolWindow(
                            transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_ID_DF)
                    );

                    if (toolWindow != null) {
                        // Move tool to another anchor

                        // Chech if it was a tab
                        if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)) {
                            // Remove from tab
                            ToolWindowTab tab = (ToolWindowTab) manager.getDockable(
                                    transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)
                            );
                            tab.getOwner().removeToolWindowTab(tab);
                            toolWindow = (ToolWindow) tab.getDockableDelegator();
                        }

                        final ToolWindow onToolWindow = (ToolWindow) getOnDockable();

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
                                                toolWindow.aggregateByReference((ToolWindow) multiSplitDockableContainer.getFirstDockable(), AggregationPosition.LEFT
                                                );
                                            }
                                        }
                                        break;
                                    case RIGHT:
                                        if (onToolWindow != null) {
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.aggregateByReference((ToolWindow) multiSplitDockableContainer.getFirstDockable(), AggregationPosition.RIGHT
                                                );
                                            }
                                        }
                                        break;
                                    case BOTTOM:
                                        if (onToolWindow != null) {
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.aggregateByReference((ToolWindow) multiSplitDockableContainer.getFirstDockable(), AggregationPosition.BOTTOM
                                                );

                                            }
                                        }
                                        break;
                                    case TOP:
                                        if (onToolWindow != null) {
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.aggregateByReference((ToolWindow) multiSplitDockableContainer.getFirstDockable(), AggregationPosition.TOP
                                                );

                                            }
                                        }
                                        break;
                                }

                                final ToolWindow toolWindow2 = toolWindow;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        toolWindow2.setActive(true);
                                    }
                                });
                            } else {
                                if (onToolWindow != null && toolWindow != onToolWindow) {
                                    onToolWindow.addToolWindowTab(toolWindow).setSelected(true);

                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            onToolWindow.setActive(true);
                                        }
                                    });
                                } else {
                                    toolWindow.aggregateByReference((ToolWindow) multiSplitDockableContainer.getFirstDockable(), AggregationPosition.DEFAULT);

                                    final ToolWindow toolWindow1 = toolWindow;
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            toolWindow1.setActive(true);
                                        }
                                    });
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
            for (ToolWindow tool : manager.getToolsByAnchor(ToolWindowAnchor.BOTTOM)) {
                if (tool.isVisible())
                    visibleNum++;
                if (tool == toolWindow)
                    flag = true;
            }

            return (!flag || visibleNum != 1);

        }
    }

}
