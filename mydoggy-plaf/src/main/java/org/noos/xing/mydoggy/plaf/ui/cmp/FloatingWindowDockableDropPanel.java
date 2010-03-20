package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class FloatingWindowDockableDropPanel extends DockableDropPanel {
    protected ToolWindowManager toolWindowManager;
    protected FloatingWindow floatingWindow;


    public FloatingWindowDockableDropPanel(FloatingWindow floatingWindow, ToolWindowManager toolWindowManager) {
        super(ToolWindow.class);
        
        this.floatingWindow = floatingWindow;
        this.toolWindowManager = toolWindowManager;
    }


    public boolean dragStart(Transferable transferable, int action) {
         try {
             if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                 if (transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_MANAGER).equals(System.identityHashCode(toolWindowManager))) {
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
                        ToolWindowTab tab = (ToolWindowTab) toolWindowManager.lookupDockable(
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
                                            toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.LEFT
                                            );
                                        }
                                    }
                                    break;
                                case RIGHT:
                                    if (onToolWindow != null) {
                                        toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                    } else {
                                        if (checkCondition(toolWindow)) {
                                            toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.RIGHT
                                            );
                                        }
                                    }
                                    break;
                                case BOTTOM:
                                    if (onToolWindow != null) {
                                        toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                    } else {
                                        if (checkCondition(toolWindow)) {
                                            toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.BOTTOM
                                            );

                                        }
                                    }
                                    break;
                                case TOP:
                                    if (onToolWindow != null) {
                                        toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                    } else {
                                        if (checkCondition(toolWindow)) {
                                            toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.TOP
                                            );

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
                                toolWindow.aggregateByReference(floatingWindow.getDockable(), AggregationPosition.DEFAULT
                                );
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


    public FloatingWindow getModalWindow() {
        return floatingWindow;
    }

    public void setModalWindow(FloatingWindow floatingWindow) {
        this.floatingWindow = floatingWindow;
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
