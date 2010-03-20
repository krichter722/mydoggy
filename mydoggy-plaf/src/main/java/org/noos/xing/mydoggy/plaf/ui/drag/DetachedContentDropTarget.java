package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.FloatingWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DetachedContentDropTarget extends DropTarget {

    public DetachedContentDropTarget(FloatingWindow floatingWindow,
                                     JComponent component,
                                     MyDoggyToolWindowManager toolWindowManager) throws HeadlessException {
        super(floatingWindow.getWindow(),
              DnDConstants.ACTION_MOVE,
              new ToolWindowFloatingDropTargetListener(floatingWindow, component, toolWindowManager));
    }

    public static class ToolWindowFloatingDropTargetListener implements DropTargetListener, PropertyChangeListener {
        protected MyDoggyToolWindowManager toolWindowManager;
        protected FloatingWindow floatingWindow;
        protected ToolWindowAnchor anchor;
        protected int anchorIndex;
        protected JComponent component;

        protected ToolWindow onToolWindow;
        protected ToolWindowAnchor dragAnchor;

        protected Border oldBorder;
        protected Border dragBorder = new LineBorder(Color.BLUE, 3);

        public ToolWindowFloatingDropTargetListener(FloatingWindow floatingWindow, JComponent component, MyDoggyToolWindowManager toolWindowManager) {
            this.floatingWindow = floatingWindow;
            this.component = component;
            this.toolWindowManager = toolWindowManager;
            this.anchor = ToolWindowAnchor.BOTTOM;

            this.component.addPropertyChangeListener("dragAnchor", this);
            this.component.addPropertyChangeListener("dragToolWindow", this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if ("dragAnchor".equals(propertyName)) {
                this.dragAnchor = (ToolWindowAnchor) evt.getNewValue();
            } else {
                this.onToolWindow = null;
            }
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            if (checkEvent(dtde)) {
                onToolWindow = null;

                dtde.acceptDrag(dtde.getDropAction());

                if (component.getBorder() != dragBorder)
                    oldBorder = component.getBorder();
//                component.setBorder(dragBorder);

                putProperty("dragStart");
            } else
                dtde.rejectDrag();
        }

        public void dragOver(DropTargetDragEvent dtde) {
            if (!checkEvent(dtde))
                return;

            Point location = dtde.getLocation();
            component.putClientProperty("dragOver", location);

            Component deepestCmp = SwingUtilities.getDeepestComponentAt(component, location.x, location.y);
            if (deepestCmp != null) {
                JComponent toolWindowContainer = (JComponent) SwingUtil.getParent(deepestCmp, "toolWindow.container.");
                if (toolWindowContainer != null) {
                    onToolWindow = (ToolWindow) toolWindowContainer.getClientProperty(ToolWindow.class);
                    anchorIndex = onToolWindow.getAnchorIndex();
                }
            } else {
                onToolWindow = null;
                anchorIndex = -1;
            }
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            if (checkEvent(dtde))
                dragEnter(dtde);
        }

        public void dragExit(DropTargetEvent dte) {
//            component.setBorder(oldBorder);
            onToolWindow = null;
            oldBorder = null;
            putProperty("dragExit");
        }

        public void drop(DropTargetDropEvent dtde) {
            try {
                if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) {
                    if (dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF)) {
                        try {
                            Transferable transferable = dtde.getTransferable();
                            ToolWindow toolWindow = toolWindowManager.getToolWindow(
                                    dtde.getTransferable().getTransferData(MyDoggyTransferable.TOOL_WINDOW_ID_DF)
                            );

                            if (toolWindow != null) {
                                // Move tool to another anchor

                                // Chech if it was a tab
                                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)) {
                                    // Remove from tab
                                    ToolWindowTab tab = (ToolWindowTab) toolWindowManager.lookupDockable(
                                            transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)
                                    );
                                    tab.getOwner().removeToolWindowTab(tab);
                                    toolWindow = (ToolWindow) tab.getDockableDelegator();
                                }

                                if (toolWindow == onToolWindow)
                                    return;

                                boolean oldAggregateMode = toolWindow.isAggregateMode();
                                toolWindow.setAggregateMode(true);
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
                                                        toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.LEFT);
                                                    }
                                                }
                                                break;
                                            case RIGHT:
                                                if (onToolWindow != null) {
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                                } else {
                                                    if (checkCondition(toolWindow)) {
                                                        toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.RIGHT);
                                                    }
                                                }
                                                break;
                                            case BOTTOM:
                                                if (onToolWindow != null) {
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                                } else {
                                                    if (checkCondition(toolWindow)) {
                                                        toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.BOTTOM);

                                                    }
                                                }
                                                break;
                                            case TOP:
                                                if (onToolWindow != null) {
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                                } else {
                                                    if (checkCondition(toolWindow)) {
                                                        toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.TOP);
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
                                            toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.DEFAULT);
                                            toolWindow.setActive(true);
                                        }
                                    }
                                } finally {
                                    toolWindow.setAggregateMode(oldAggregateMode);
                                }

                                dtde.dropComplete(true);
                            } else
                                dtde.dropComplete(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            dtde.dropComplete(false);
                        }
                    } else
                        dtde.rejectDrop();
                } else
                    dtde.rejectDrop();
            } finally {
                putProperty("dragEnd");

                // Restore component
                dragExit(dtde);
            }
        }


        protected void putProperty(String name) {
            Boolean value = (Boolean) component.getClientProperty(name);
            if (value != null)
                component.putClientProperty(name, !value);
            else
                component.putClientProperty(name, false);
        }

        protected boolean checkEvent(DropTargetDragEvent dtde) {
            Transferable transferable = dtde.getTransferable();
            try {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                    if (transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_MANAGER).equals(System.identityHashCode(toolWindowManager))) {
                        if (dtde.getDropAction() == DnDConstants.ACTION_MOVE &&
                            (dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF) ||
                             dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF) ||
                             dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF))
                                )
                            return true;
                    }
                }
            } catch (Exception ignoreIt) {
            }
            return false;
        }

        protected boolean checkCondition(ToolWindow toolWindow) {
            if (toolWindow.getAnchor() != anchor)
                return true;

            int visibleNum = 0;
            boolean flag = false;
            for (ToolWindow tool : toolWindowManager.getToolsByAnchor(anchor)) {
                if (tool.isVisible())
                    visibleNum++;
                if (tool == toolWindow)
                    flag = true;
            }

            return (!flag || visibleNum != 1);

        }
    }

}