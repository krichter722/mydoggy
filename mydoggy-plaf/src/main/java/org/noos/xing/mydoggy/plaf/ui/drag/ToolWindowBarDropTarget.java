package org.noos.xing.mydoggy.plaf.ui.drag;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
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


    private int showPosition(DropTargetDragEvent dtde, int lastIndex) {
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

    private void hidePosition(boolean repaint) {
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


    private static class ToolWindowBarDropTargetListener extends DropTargetAdapter {
        private MyDoggyToolWindowManager manager;
        private ToolWindowAnchor anchor;
        private int index;

        public ToolWindowBarDropTargetListener(MyDoggyToolWindowManager manager, ToolWindowAnchor anchor) {
            this.manager = manager;
            this.anchor = anchor;
        }

        public void drop(DropTargetDropEvent dtde) {
            Transferable transferable = dtde.getTransferable();
            if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF)) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);

                ((ToolWindowBarDropTarget) dtde.getDropTargetContext().getDropTarget()).hidePosition(true);

                try {
                    String toolId = (String) transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_ID_DF);
                    ToolWindow toolWindow = manager.getToolWindow(toolId);

                    // Chech if it was a tab
                    if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)) {
                        // Remove from tab
                        ToolWindowTab tab = manager.getToolWindowTab(
                                transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)
                        );
                        tab.getOwner().removeToolWindowTab(tab);
                        toolWindow = tab.getToolWindow();
                        toolWindow.setAnchor(anchor, index);
                        toolWindow.setActive(true);
                    } else {
                        toolWindow.setAnchor(anchor, index);
                    }

                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.rejectDrop();
                }
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
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF) ||
                    transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF))
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
                // Impossible
            }
            return false;
        }

    }

    private static interface SeparatorLabel {
    }

    private static class VerticalSeparatorLabel extends JPanel implements SeparatorLabel {
        public VerticalSeparatorLabel() {
        }
    }

    private static class HorizontalSeparatorLabel extends JPanel implements SeparatorLabel {
        public HorizontalSeparatorLabel() {
        }
    }
}
