package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;

import java.awt.*;
import java.awt.datatransfer.Transferable;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentWindowMultiSplitContainer extends MultiSplitTabbedContentContainer {


    public ContentWindowMultiSplitContainer(MyDoggyToolWindowManager toolWindowManager) {
        super(toolWindowManager);
    }


    protected DockableDropPanel createDockableDropPanel() {
        return new ContentWindowDockableDropPanel();
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


    public class ContentWindowDockableDropPanel extends MultiSplitTabbedDockableDropPanel {
        // TODO: setup dratStart

        public boolean drop(Transferable transferable) {
            if (transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)) {
                try {
                    ContentManager contentManager = toolWindowManager.getContentManager();
                    Content content = contentManager.getContent(
                            transferable.getTransferData(MyDoggyTransferable.CONTENT_ID_DF)
                    );

                    if (content != null) {
                        boolean rejectDrop = false;

                        Content onDockable = (Content) getOnDockable();
                        ToolWindowAnchor onAnchor = getOnAnchor();
                        int onIndex = getOnIndex();

                        if (content == onDockable) {
                            if (onIndex == -1) {
                                rejectDrop = true;
                            } else {
                                Component onDockableContainer = getOnDockableContainer();

                                if (onDockableContainer instanceof TabbedContentPane) {
                                    TabbedContentPane tabbedContentPane = (TabbedContentPane) onDockableContainer;
                                    for (int i = 0, size = tabbedContentPane.getTabCount(); i < size; i++) {
                                        DockablePanel dockablePanel = (DockablePanel) tabbedContentPane.getComponentAt(i);
                                        if (dockablePanel.getDockable() == onDockable && i == onIndex) {
                                            rejectDrop = true;
                                            break;
                                        }
                                    }
                                } else if (onDockableContainer instanceof DockablePanel) {
                                    DockablePanel dockablePanel = (DockablePanel) onDockableContainer;
                                    if (dockablePanel.getDockable() == onDockable)
                                        rejectDrop = true;
                                }
                            }
                        }

                        if (!rejectDrop) {
                            if (onDockable != null) {
                                content.detach(onDockable,
                                               onIndex,
                                               (onAnchor == null) ? null : AggregationPosition.valueOf(onAnchor.toString()));
                            } else {
                                content.detach(onDockable,
                                               (onAnchor == null) ? null : AggregationPosition.valueOf(onAnchor.toString()));
                            }
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return false;
        }
    }

}
