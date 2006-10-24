package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowBarDropTarget extends DropTarget implements DropTargetListener {
    private ToolWindowAnchor anchor;
    private Container container;

    public ToolWindowBarDropTarget(ToolWindowAnchor anchor, Container container) throws HeadlessException {
        super(container, new BarDropTargetListener(anchor));
        this.anchor = anchor;
        this.container = container;
    }

    private int showPosition(DropTargetDragEvent dtde) {
        Point dtdeLocation = dtde.getLocation();
        int index = -1;

        ExtendedTableLayout tableLayout = (ExtendedTableLayout) container.getLayout();
        double sum = 0;
        int position;
        int[] intervals;

        switch (anchor) {
            case TOP:
            case BOTTOM:
                position = dtdeLocation.x;
                intervals = tableLayout.getColsInPixel();
                break;
            case LEFT:
            case RIGHT:
                position = dtdeLocation.y;
                intervals = tableLayout.getRowsInPixel();
                break;
            default:
                throw new IllegalStateException("Invalid anchor");
        }

        for (Component component : container.getComponents()) {
            if (component instanceof HorizontalSeparatorLabel) {
                container.remove(component);
            }
        }

        for (int i = 0; i < intervals.length; i++) {
            double interval = intervals[i];
            if (position >= sum && position <= sum + interval) {
                switch (anchor) {
                    case TOP:
                    case BOTTOM:
                        container.add(new VerticalSeparatorLabel(), i + ",1,c,c");
                        break;
                    case LEFT:
                    case RIGHT:
                        container.add(new HorizontalSeparatorLabel(), "1," + i + ",c,c");
                        break;
                    default:
                        throw new IllegalStateException("Invalid anchor");
                }
                index = i / 2;
                break;
            } else
                sum += interval;
        }

        container.validate();
        container.repaint();

        return index;
    }

    private void hidePosition() {
        for (Component component : container.getComponents()) {
            if (component instanceof SeparatorLabel)
                container.remove(component);
        }

        container.validate();
        container.repaint();
    }


    private static interface SeparatorLabel {}

    private static class VerticalSeparatorLabel extends JLabel implements SeparatorLabel {
        public VerticalSeparatorLabel() {
            setIcon(
                    new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                            Thread.currentThread().getContextClassLoader()
                                    .getResource("org/noos/xing/mydoggy/plaf/ui/icons/separatorVertical.png")
                    ))
            );
        }
    }

    private static class HorizontalSeparatorLabel extends JLabel implements SeparatorLabel {
        public HorizontalSeparatorLabel() {
            setIcon(
                    new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                            Thread.currentThread().getContextClassLoader()
                                    .getResource("org/noos/xing/mydoggy/plaf/ui/icons/separatorHorizontal.png")
                    ))
            );
        }
    }

    private static class BarDropTargetListener extends DropTargetAdapter {
        private ToolWindowAnchor anchor;
        private int index;

        public BarDropTargetListener(ToolWindowAnchor anchor) {
            this.anchor = anchor;
        }

        public void drop(DropTargetDropEvent dtde) {
            if (dtde.getTransferable().isDataFlavorSupported(ToolWindowTrasferable.TOOL_WINDOW_DATA_FAVLOR)) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                try {
                    ((ToolWindowBarDropTarget) dtde.getDropTargetContext().getDropTarget()).hidePosition();

                    anchor.setIndex(index);
                    ((ToolWindow) dtde.getTransferable().getTransferData(ToolWindowTrasferable.TOOL_WINDOW_DATA_FAVLOR))
                            .setAnchor(anchor);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                    dtde.rejectDrop();
                } catch (IOException e) {
                    e.printStackTrace();
                    dtde.rejectDrop();
                } finally {
                    anchor.setIndex(-1);
                }
            } else
                dtde.rejectDrop();
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            if (checkEvent(dtde)) {
                index = ((ToolWindowBarDropTarget) dtde.getDropTargetContext().getDropTarget()).showPosition(dtde);
            }
        }

        public void dragOver(DropTargetDragEvent dtde) {
            if (checkEvent(dtde)) {
                index = ((ToolWindowBarDropTarget) dtde.getDropTargetContext().getDropTarget()).showPosition(dtde);
            }
        }

        public void dragExit(DropTargetEvent dte) {
            ((ToolWindowBarDropTarget) dte.getDropTargetContext().getDropTarget()).hidePosition();
            index = -1;
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            if (checkEvent(dtde)) {
//                System.out.println("dropActionChanged");
            }
        }


        public boolean checkEvent(DropTargetDragEvent dtde) {
            if (!dtde.getTransferable().isDataFlavorSupported(ToolWindowTrasferable.TOOL_WINDOW_DATA_FAVLOR)) {
                dtde.rejectDrag();
                return false;
            }
            return true;
        }

    }

}
