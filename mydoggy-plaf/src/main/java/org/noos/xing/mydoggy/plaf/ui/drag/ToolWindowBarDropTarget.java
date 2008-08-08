package org.noos.xing.mydoggy.plaf.ui.drag;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowBarDropTarget extends DropTarget {
    protected MyDoggyToolWindowManager manager;
    protected ToolWindowAnchor anchor;

    protected Container container;
    protected TableLayout layout;
    protected Point lastPosition;


    public ToolWindowBarDropTarget(MyDoggyToolWindowManager manager, ToolWindowAnchor anchor, Container container) throws HeadlessException {
        super(container, new ToolWindowBarDropTargetListener(manager, anchor));
        this.manager = manager;
        this.anchor = anchor;
        this.container = container;
        this.layout = (TableLayout) container.getLayout();
        this.lastPosition = null;
    }


    protected int showPosition(DropTargetDragEvent dtde, int lastIndex) {
        Point newPosition = dtde.getLocation();
        if (lastPosition != null && lastPosition.equals(newPosition))
            return lastIndex;

        int index = -1;

        ExtendedTableLayout tableLayout = (ExtendedTableLayout) container.getLayout();
        double sum = 0;
        int position;
        int[] intervals;
        int direction;

        switch (anchor) {
            case TOP:
            case BOTTOM:
                position = newPosition.x;
                intervals = tableLayout.getColsInPixel();

                if (lastPosition == null)
                    direction = 1;
                else if (newPosition.x > lastPosition.x)
                    direction = 1;
                else
                    direction = -1;
                break;
            case LEFT:
            case RIGHT:
                position = newPosition.y;
                intervals = tableLayout.getRowsInPixel();

                if (lastPosition == null)
                    direction = 1;
                else if (newPosition.y > lastPosition.y)
                    direction = 1;
                else
                    direction = -1;

                break;
            default:
                throw new IllegalStateException("Invalid anchor.");
        }

        boolean fromUp = false;
        for (int i = 0; i < intervals.length; i++) {
            double interval = intervals[i];
            if (interval == 0 && i != 0)
                fromUp = true;

            if (position >= sum && position <= sum + interval) {
                if (i % 2 == 0 && i != 0) {
                    i += direction;

                    switch (anchor) {
                        case TOP:
                        case BOTTOM:
                            if (i >= layout.getNumColumn()) {
                                sum += interval;
                                hidePosition(false);
                                continue;
                            }
                            break;
                        case LEFT:
                        case RIGHT:
                            if (i >= layout.getNumRow()) {
                                sum += interval;
                                hidePosition(false);
                                continue;
                            }
                            break;
                    }
                }

                hidePosition(false);

                // Insert space for dragging image at specific index.
                switch (anchor) {
                    case TOP:
                    case BOTTOM:
                        container.add(new VerticalSeparatorLabel(), i + ",1,c,c");
                        layout.setColumn(i, manager.getGlassPanel().getDraggingImage().getWidth(container) + 6);
                        break;
                    case LEFT:
                    case RIGHT:
                        container.add(new HorizontalSeparatorLabel(), "1," + i + ",c,c");
                        layout.setRow(i, manager.getGlassPanel().getDraggingImage().getHeight(container) + 6);
                        break;
                }

                index = i / 2;
                if (fromUp)
                    index--;
                break;
            } else
                sum += interval;
        }

        SwingUtil.repaint(container);

        lastPosition = newPosition;
        return index;
    }

    protected void hidePosition(boolean repaint) {
        for (Component component : container.getComponents()) {
            if (component instanceof SeparatorLabel) {
                if (component instanceof HorizontalSeparatorLabel) {
                    layout.setRow(layout.getConstraints(component).row1, 3);
                } else
                    layout.setColumn(layout.getConstraints(component).col1, 3);

                container.remove(component);
            }
        }

        if (repaint)
            SwingUtil.repaint(container);
    }


    protected static class ToolWindowBarDropTargetListener extends DropTargetAdapter {
        protected MyDoggyToolWindowManager manager;
        protected ToolWindowAnchor anchor;
        protected int index;

        public ToolWindowBarDropTargetListener(MyDoggyToolWindowManager manager, ToolWindowAnchor anchor) {
            this.manager = manager;
            this.anchor = anchor;
        }

        public void drop(DropTargetDropEvent dtde) {
            Transferable transferable = dtde.getTransferable();

            if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF)) {
                dropToolWindow(transferable, dtde);
            } else if (transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)) {
                dropContent(transferable, dtde);
            } else if (transferable.isDataFlavorSupported(MyDoggyTransferable.CUSTOM_DESCRIPTOR_ID)) {
                dropBar2Bar(transferable, dtde);
            } else
                dtde.rejectDrop();
        }


        public void dragEnter(DropTargetDragEvent dtde) {
            if (checkEvent(dtde))
                index = ((ToolWindowBarDropTarget) dtde.getDropTargetContext().getDropTarget()).showPosition(dtde, index);
        }

        public void dragOver(DropTargetDragEvent dtde) {
            if (checkEvent(dtde))
                index = ((ToolWindowBarDropTarget) dtde.getDropTargetContext().getDropTarget()).showPosition(dtde, index);
        }

        public void dragExit(DropTargetEvent dte) {
            ((ToolWindowBarDropTarget) dte.getDropTargetContext().getDropTarget()).hidePosition(true);
            index = -1;
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            if (checkEvent(dtde)) {
//                System.out.println("dropActionChanged");
            }
        }


        public boolean checkEvent(DropTargetDragEvent dtde) {
            Transferable transferable = dtde.getTransferable();
            try {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                    if (transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_MANAGER).equals(System.identityHashCode(manager))) {
                        if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF) ||
                            transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF) ||
                            transferable.isDataFlavorSupported(MyDoggyTransferable.CUSTOM_DESCRIPTOR_ID))
                            return true;
                    }
                }
            } catch (Exception ignoreIt) {
            }
            return false;
        }

        protected void dropToolWindow(Transferable transferable, DropTargetDropEvent dtde) {

            ((ToolWindowBarDropTarget) dtde.getDropTargetContext().getDropTarget()).hidePosition(true);

            try {
                String toolId = (String) transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_ID_DF);
                ToolWindow toolWindow = manager.getToolWindow(toolId);
                if (toolWindow == null)
                    return;

                dtde.acceptDrop(DnDConstants.ACTION_MOVE);

                // Chech if it was a tab
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)) {
                    // Remove from tab
                    ToolWindowTab tab = (ToolWindowTab) manager.getDockable(
                            transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)
                    );
                    tab.getOwner().removeToolWindowTab(tab);

                    toolWindow = (ToolWindow) tab.getDockableDelegator();
                    toolWindow.setAnchor(anchor, index);
                    toolWindow.setActive(true);
                } else {
                    boolean oldAggregateMode = toolWindow.isAggregateMode();
                    toolWindow.setAggregateMode(true);
                    try {
                        toolWindow.setAnchor(anchor, index);
                    } finally {
                        toolWindow.setAggregateMode(oldAggregateMode);
                    }
                }

                dtde.dropComplete(true);
            } catch (Exception e) {
                e.printStackTrace();
                dtde.rejectDrop();
            }

        }

        protected void dropContent(Transferable transferable, DropTargetDropEvent dtde) {
            ((ToolWindowBarDropTarget) dtde.getDropTargetContext().getDropTarget()).hidePosition(true);

            try {
                String contentId = (String) transferable.getTransferData(MyDoggyTransferable.CONTENT_ID_DF);
                Content content = manager.getContentManager().getContent(contentId);
                if (content == null)
                    return;

                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                // Chech if it was a tab
                if (content.getDockableDelegator() != null) {
                    if (content.getDockableDelegator() instanceof ToolWindow) {
                        ToolWindow toolWindow = (ToolWindow) content.getDockableDelegator();

                        manager.getContentManager().removeContent(content);

                        toolWindow.setAnchor(anchor, index);
                        toolWindow.setActive(true);
                    }
                }

                dtde.dropComplete(true);
            } catch (Exception e) {
                e.printStackTrace();
                dtde.rejectDrop();
            }
        }

        protected void dropBar2Bar(Transferable transferable, DropTargetDropEvent dtde) {
            ((ToolWindowBarDropTarget) dtde.getDropTargetContext().getDropTarget()).hidePosition(true);

            try {
                String dockableDescriptorId = (String) transferable.getTransferData(MyDoggyTransferable.CUSTOM_DESCRIPTOR_ID);
                DockableDescriptor dockableDescriptor = manager.getDockableDescriptor(dockableDescriptorId);
                if (dockableDescriptor == null)
                    return;

                dtde.acceptDrop(DnDConstants.ACTION_MOVE);

                dockableDescriptor.setAnchor(anchor, index);

                dtde.dropComplete(true);
            } catch (Exception e) {
                e.printStackTrace();
                dtde.rejectDrop();
            }
        }
    }


    protected static interface SeparatorLabel {
    }

    protected static class VerticalSeparatorLabel extends JPanel implements SeparatorLabel {
        public VerticalSeparatorLabel() {
        }
    }

    protected static class HorizontalSeparatorLabel extends JPanel implements SeparatorLabel {
        public HorizontalSeparatorLabel() {
        }
    }
}
