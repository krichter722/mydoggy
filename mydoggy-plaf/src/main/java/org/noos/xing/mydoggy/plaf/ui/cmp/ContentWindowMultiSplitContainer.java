package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ContentWindowMultiSplitContainer extends MultiSplitTabbedContentContainer {


    public ContentWindowMultiSplitContainer(MyDoggyToolWindowManager toolWindowManager) {
        super(toolWindowManager);
    }


    protected DropTarget createDropTarget() {
        return new ContentWindowContentDropTarget(dockableDropPanel, toolWindowManager);
    }

    protected boolean isWrapRequest(Dockable dockable, Action action) {
        switch (action) {
            case ADD_DOCK:
                if (getContentCount() == 0) {
                    return false;
                } else if (getContentCount() >= 1) {
                    return true;
                }
                return false;
            case REMOVE_DOCK:
                if (getContentCount() <= 1) {
                    return false;
                } else if (getContentCount() > 1) {
                    return true;
                }
                return false;
        }
        return false;
    }

    protected Component forceWrapperForComponent(Dockable dockable, Component component) {
        final TabbedContentPane tabbedContentPane = (TabbedContentPane) super.forceWrapperForComponent(dockable, component);
        tabbedContentPane.setShowDetach(false);
        tabbedContentPane.setShowMaximize(false);
        tabbedContentPane.setShowMinimize(false);

//        tabbedContentPane.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                if (!valueAdjusting && !contentValueAdjusting) {
//                    Content newSelected = (Content) tabbedContentPane.getSelectedContent();
//                    if (newSelected != null && !newSelected.isMinimized()) {
//                        if (newSelected == lastSelected)
//                            return;
//
//                        if (lastSelected != null)
//                            lastSelected.setSelected(false);
//
//                        newSelected.setSelected(true);
//                    }
//                }
//            }
//        });
        tabbedContentPane.addTabbedContentPaneListener(new TabbedContentPaneListener() {
            public void tabbedContentPaneEventFired(TabbedContentPaneEvent event) {
                Content content = event.getContent();
                switch (event.getActionId()) {
                    case ON_CLOSE:
                        content.setDetached(false);
                        break;
                    case ON_DETACH:
                        break;
                }
            }
        });
        return tabbedContentPane;
    }


    public class ContentWindowContentDropTarget extends ContentDropTarget {

        public ContentWindowContentDropTarget(JComponent component, ToolWindowManager toolWindowManager) throws HeadlessException {
            super(component, new ContentWindowDropTargetListener(component, toolWindowManager));
        }

    }

    public class ContentWindowDropTargetListener extends ContentDropTargetListener {

        public ContentWindowDropTargetListener(JComponent component, ToolWindowManager toolWindowManager) {
            super(component, toolWindowManager);
        }

        public void drop(DropTargetDropEvent dtde) {
            if (oldTabbedContentPane != null) {
                oldTabbedContentPane.setTargetLine(-1);
                oldTabbedContentPane = null;
            }

            try {
                if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) {

                    if (dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)) {
                        try {
                            ContentManager contentManager = toolWindowManager.getContentManager();
                            Content content = contentManager.getContent(
                                    dtde.getTransferable().getTransferData(MyDoggyTransferable.CONTENT_ID_DF)
                            );

                            if (content != null) {
                                boolean rejectDrop = false;
                                if (content == onDockable) {
                                    if (indexAtLocation == -1) {
                                        rejectDrop = true;
                                    } else {
                                        if (dockableWrapper instanceof TabbedContentPane) {
                                            TabbedContentPane tabbedContentPane = (TabbedContentPane) dockableWrapper;
                                            for (int i = 0, size = tabbedContentPane.getTabCount(); i < size; i++) {
                                                DockablePanel dockablePanel = (DockablePanel) tabbedContentPane.getComponentAt(i);
                                                if (dockablePanel.getDockable() == onDockable && i == indexAtLocation) {
                                                    rejectDrop = true;
                                                    break;
                                                }
                                            }
                                        } else if (dockableWrapper instanceof DockablePanel) {
                                            DockablePanel dockablePanel = (DockablePanel) dockableWrapper;
                                            if (dockablePanel.getDockable() == onDockable)
                                                rejectDrop = true;
                                        }
                                    }
                                }

                                if (!rejectDrop) {
                                    if (onDockable != null) {
                                        content.detachOn((Content) onDockable,
                                                         indexAtLocation,
                                                         (dragAnchor == null) ? null : AggregationPosition.valueOf(dragAnchor.toString()));
                                    } else {
                                        content.detachOn((Content) onDockable,
                                                         (dragAnchor == null) ? null : AggregationPosition.valueOf(dragAnchor.toString()));
                                    }
                                    dtde.dropComplete(true);
                                }
                            }
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

    }
}
