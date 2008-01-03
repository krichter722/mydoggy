package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.dnd.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentManagerDropTarget extends DropTarget {

    public ContentManagerDropTarget(JComponent component,
                                    ToolWindowManager toolWindowManager,
                                    ResourceManager resourceManager) throws HeadlessException {
        super(component,
                DnDConstants.ACTION_MOVE,
                new ContentManagerDropTargetListener(toolWindowManager, resourceManager, component));
    }

    public static class ContentManagerDropTargetListener implements DropTargetListener {
        protected ToolWindowManager toolWindowManager;
        protected ResourceManager resourceManager;
        protected JComponent component;
        protected Border oldBorder;
        protected Border dragBorder = new LineBorder(Color.BLUE, 3);

        public ContentManagerDropTargetListener(ToolWindowManager toolWindowManager,
                                                ResourceManager resourceManager,
                                                JComponent component) {
            this.toolWindowManager = toolWindowManager;
            this.resourceManager = resourceManager;
            this.component = component;
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            if (!isEnabled())
                return;

            if (dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF) &&
                    dtde.getDropAction() == DnDConstants.ACTION_MOVE) {

                dtde.acceptDrag(dtde.getDropAction());
                if (component.getBorder() != dragBorder)
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
            if (!isEnabled())
                return;

            component.setBorder(oldBorder);
            oldBorder = null;
        }

        public void drop(DropTargetDropEvent dtde) {
            if (!isEnabled())
                return;

            if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) {
                if (dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF)) {
                    try {
                        ToolWindow toolWindow = toolWindowManager.getToolWindow(
                                dtde.getTransferable().getTransferData(MyDoggyTransferable.TOOL_WINDOW_ID_DF)
                        );
                        if (toolWindow != null) {
                            toolWindowManager.getContentManager().addContent(toolWindow).setSelected(true);

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

        protected boolean isEnabled() {
            return resourceManager.getBoolean("ContentManagerDropTarget.enabled", false);
        }
    }

}
