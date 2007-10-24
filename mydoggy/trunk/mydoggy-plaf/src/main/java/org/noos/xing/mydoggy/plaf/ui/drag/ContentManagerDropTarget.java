package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTrasferable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ContentManagerDropTarget extends DropTarget {

    public ContentManagerDropTarget(JComponent component, ToolWindowManager toolWindowManager) throws HeadlessException {
        super(component, DnDConstants.ACTION_MOVE, new ContentManagerDropTargetListener(toolWindowManager, component));
    }

    public static class ContentManagerDropTargetListener implements DropTargetListener {
        protected ToolWindowManager toolWindowManager;
        protected JComponent component;
        protected Border oldBorder;
        protected Border dragBorder = new LineBorder(Color.BLUE, 3);

        public ContentManagerDropTargetListener(ToolWindowManager toolWindowManager, JComponent component) {
            this.toolWindowManager = toolWindowManager;
            this.component = component;
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            if  (dtde.getTransferable().isDataFlavorSupported(MyDoggyTrasferable.TOOL_WINDOW_ID_DF) &&
                 dtde.getDropAction() == DnDConstants.ACTION_MOVE) {

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
                        ToolWindow toolWindow = toolWindowManager.getToolWindow(
                                dtde.getTransferable().getTransferData(MyDoggyTrasferable.TOOL_WINDOW_ID_DF)
                        );
                        if (toolWindow != null) {
                            toolWindowManager.getContentManager().addContent(toolWindow);

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
