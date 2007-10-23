package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.drag.ToolWindowTrasferable;

import java.awt.*;
import java.awt.dnd.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ContentManagerDropTarget extends DropTarget {

    public ContentManagerDropTarget(Component component, ContentManager contentManager) throws HeadlessException {
        super(component, DnDConstants.ACTION_MOVE, new ContentManagerDropTargetListener(contentManager));
    }

    public static class ContentManagerDropTargetListener implements DropTargetListener {
        protected ContentManager contentManager;

        public ContentManagerDropTargetListener(ContentManager contentManager) {
            this.contentManager = contentManager;
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            if  (dtde.getTransferable().isDataFlavorSupported(ToolWindowTrasferable.TOOL_WINDOW_DATA_FAVLOR) &&
                 dtde.getDropAction() == DnDConstants.ACTION_MOVE)
                dtde.acceptDrag(dtde.getDropAction());
            else
                dtde.rejectDrag();
        }

        public void dragOver(DropTargetDragEvent dtde) {
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            dragEnter(dtde);
        }

        public void dragExit(DropTargetEvent dte) {
        }

        public void drop(DropTargetDropEvent dtde) {
            if  (dtde.getTransferable().isDataFlavorSupported(ToolWindowTrasferable.TOOL_WINDOW_DATA_FAVLOR) &&
                 dtde.getDropAction() == DnDConstants.ACTION_MOVE)  {
                try {
                    ToolWindow toolWindow = (ToolWindow) dtde.getTransferable().getTransferData(ToolWindowTrasferable.TOOL_WINDOW_DATA_FAVLOR);

                    contentManager.addContent(toolWindow);

                    dtde.dropComplete(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.dropComplete(false);
                }
            } else
                dtde.dropComplete(false);
        }
    }

}
