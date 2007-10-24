package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ToolWindowTitleDropTarget extends DropTarget {

    public ToolWindowTitleDropTarget(JComponent component, ToolWindow owner, ToolWindowManager toolWindowManager) throws HeadlessException {
        super(component, DnDConstants.ACTION_MOVE, new ContentManagerDropTargetListener(toolWindowManager, owner, component));
    }

    public static class ContentManagerDropTargetListener implements DropTargetListener {
        protected ToolWindowManager toolWindowManager;
        protected ToolWindow owner;
        protected JComponent component;
        protected Border oldBorder;
        protected Border dragBorder = new LineBorder(Color.BLUE, 3);

        public ContentManagerDropTargetListener(ToolWindowManager toolWindowManager, ToolWindow owner, JComponent component) {
            this.toolWindowManager = toolWindowManager;
            this.owner = owner;
            this.component = component;
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            if  (dtde.getDropAction() == DnDConstants.ACTION_MOVE &&
                 (dtde.getTransferable().isDataFlavorSupported(MyDoggyTrasferable.TOOL_WINDOW_ID_DF) ||
                  dtde.getTransferable().isDataFlavorSupported(MyDoggyTrasferable.TOOL_WINDOW_TAB_ID_DF))
                 ) {

                dtde.acceptDrag(dtde.getDropAction());
                oldBorder = component.getBorder();
                component.setBorder(dragBorder);
            } else
                dtde.rejectDrag();
        }

        public void dragOver(DropTargetDragEvent dtde) {
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            dragEnter(dtde);
        }

        public void dragExit(DropTargetEvent dte) {
            component.setBorder(oldBorder);
            oldBorder = null;
        }

        public void drop(DropTargetDropEvent dtde) {
            if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) {
                if  (dtde.getTransferable().isDataFlavorSupported(MyDoggyTrasferable.TOOL_WINDOW_ID_DF))  {
                    try {
                        Transferable transferable = dtde.getTransferable();
                        ToolWindow toolWindow = toolWindowManager.getToolWindow(
                                transferable.getTransferData(MyDoggyTrasferable.TOOL_WINDOW_ID_DF)
                        );
                        if (toolWindow != null && toolWindow != owner) {
                            owner.addToolWindowTab(toolWindow).setSelected(true);

                            dtde.dropComplete(true);
                        } else
                            dtde.dropComplete(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        dtde.dropComplete(false);
                    }
                } else if  (dtde.getTransferable().isDataFlavorSupported(MyDoggyTrasferable.TOOL_WINDOW_TAB_ID_DF))  {
                    try {
                        ToolWindow toolWindow = toolWindowManager.getToolWindow(
                                dtde.getTransferable().getTransferData(MyDoggyTrasferable.TOOL_WINDOW_ID_DF)
                        );
                        if (toolWindow != null) {

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

            // Restore component
            dragExit(dtde);
        }
    }

}