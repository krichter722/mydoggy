package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.multisplit.MultiSplitPanel;
import org.noos.xing.mydoggy.plaf.ui.drag.DragListenerAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.image.BufferedImage;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MultiSplitTabbedContentContainer<D extends Dockable> extends MultiSplitDockableContainer<D> {
    protected DockableDropPanel dockableDropPanel;


    public MultiSplitTabbedContentContainer(MyDoggyToolWindowManager toolWindowManager) {
        super(toolWindowManager, JSplitPane.VERTICAL_SPLIT);
        setStoreLayout(false);

        this.dockableDropPanel = createDockableDropPanel();
        add(dockableDropPanel, "0,0,FULL,FULL");
    }


    public Component[] getTabbedComponents() {
        Component component = dockableDropPanel.getComponent();
        if (component instanceof MultiSplitPanel) {
            return ((MultiSplitPanel) component).getComponents();
        } else
            return new Component[]{component};
    }


    protected Component getWrapperForComponent(Dockable dockable, Component component, Action action) {
        if (isWrapRequest(dockable, action)) {
            return forceWrapperForComponent(dockable, component);
        } else {
            Component wrapper = new DockablePanel(dockable, component);
            wrapper.setName("@@mydoggy.dockable.panel");

            return wrapper;
        }
    }

    protected Component forceWrapperForComponent(Dockable dockable, Component component) {
        TabbedContentPane wrapper = new TabbedContentPane();

        wrapper.setToolWindowManager(toolWindowManager);
        wrapper.setName("@@mydoggy.dockable.tabbedpane");
        wrapper.addTab((Content) dockable, new DockablePanel(dockable, component));
        wrapper.setDragListener(new TabbedDragListener(wrapper));

        return wrapper;
    }

    protected Component getComponentFromWrapper(Component wrapper) {
        if (wrapper instanceof TabbedContentPane) {
            TabbedContentPane tabbedPane = (TabbedContentPane) wrapper;
            return getComponentFromWrapper(tabbedPane.getComponentAt(0));
        } else if (wrapper instanceof DockablePanel) {
            return ((DockablePanel) wrapper).getComponent();
        } else
            return wrapper;
    }

    protected Component getComponentFromWrapper(Component wrapper, Dockable dockable) {
        if (wrapper instanceof TabbedContentPane) {
            TabbedContentPane tabbedPane = (TabbedContentPane) wrapper;
            return getComponentFromWrapper(tabbedPane.getComponentAt(tabbedPane.indexOfContent((Content) dockable)), dockable);
            // {[mydoggy - Help] ClassCastException} return ((DockablePanel) tabbedPane.getComponentAt(tabbedPane.indexOfContent((Content) dockable))).getComponent();
        } else if (wrapper instanceof DockablePanel) {
            return ((DockablePanel) wrapper).getComponent();
        } else
            return wrapper;
    }

    protected void addToWrapper(Component wrapper, Dockable dockable,
                                int aggregationIndexLocation, Component content) {
        if (wrapper instanceof TabbedContentPane) {
            TabbedContentPane tabbedContentPane = (TabbedContentPane) wrapper;
            tabbedContentPane.addTab((Content) dockable,
                    new DockablePanel(dockable, content),
                    aggregationIndexLocation);
            tabbedContentPane.setSelectedIndex((aggregationIndexLocation < 0 || aggregationIndexLocation >= tabbedContentPane.getTabCount()) ? tabbedContentPane.getTabCount() - 1 : aggregationIndexLocation);
        } else if (wrapper instanceof DockablePanel) {
            DockablePanel wrapperContainer = (DockablePanel) wrapper;

            // Create a new tabbedContentPane with the old dockable
            TabbedContentPane tabbedContentPane = (TabbedContentPane) forceWrapperForComponent(wrapperContainer.getDockable(),
                    wrapperContainer.getComponent());

            // add the new dockable
            tabbedContentPane.addTab((Content) dockable,
                    new DockablePanel(dockable, content),
                    aggregationIndexLocation);
            tabbedContentPane.setSelectedIndex((aggregationIndexLocation < 0 || aggregationIndexLocation >= tabbedContentPane.getTabCount()) ? tabbedContentPane.getTabCount() - 1 : aggregationIndexLocation);

            // update multiSplitPane
            String leafName = getLeafName(wrapperContainer.getDockable());
            multiSplitPane.remove(multiSplitPane.getMultiSplitLayout().getChildMap().get(leafName));
            multiSplitPane.add(leafName, tabbedContentPane);
        } else
            throw new IllegalArgumentException("Invalid Wrapper.");
    }

    protected int removeFromWrapper(Component wrapperSource, Dockable dockable) {
        TabbedContentPane tabbedPane = (TabbedContentPane) wrapperSource;
        int index = tabbedPane.indexOfContent((Content) dockable);
        if (index != -1) {
            tabbedPane.removeTabAt(index);

            if (tabbedPane.getTabCount() == 1) {
                Dockable lastDockable = tabbedPane.getContentAt(0);
                if (!isWrapRequest(tabbedPane.getContentAt(0), Action.REMOVE_DOCK)) {
                    // remove the wrap represented by tabbedPane

                    String leafName = getLeafName(lastDockable);
                    multiSplitPane.remove(multiSplitPane.getMultiSplitLayout().getChildMap().get(leafName));
                    multiSplitPane.add(leafName,
                            getWrapperForComponent(lastDockable,
                                    ((DockablePanel) tabbedPane.getComponentAt(0)).getComponent(),
                                    Action.REMOVE_DOCK));
                }
            }
            return index;
        } else
            throw new IllegalArgumentException("Cannot find that dockable on the passed tabbedpane");
    }

    protected Component getRootComponent() {
        return dockableDropPanel.getComponent();
    }

    protected void setRootComponent(Component component) {
        dockableDropPanel.setComponent(component);
    }

    protected void resetRootComponent() {
        dockableDropPanel.resetComponent();
    }

    protected boolean isWrapper(Component component) {
        return component instanceof TabbedContentPane;
    }

    protected DockableDropPanel createDockableDropPanel() {
        return new MultiSplitTabbedDockableDropPanel();
    }

    protected boolean isDockableContainerDragEnabled() {
        return true;
    }


    public class TabbedDragListener extends DragListenerAdapter {
        protected TabbedContentPane tabbedContentPane;
        protected int dragTabIndex;


        public TabbedDragListener(TabbedContentPane tabbedContentPane) {
            super(toolWindowManager);
            this.tabbedContentPane = tabbedContentPane;
        }


        public void dragGestureRecognized(DragGestureEvent dge) {
            super.dragGestureRecognized(dge);

            // Acquire locks
            if (!acquireLocks())
                return;

            // Start Drag
            Point origin = dge.getDragOrigin();
            dragTabIndex = tabbedContentPane.indexAtLocation(origin.x, origin.y);
            if (dragTabIndex != -1) {
                Dockable dockable = tabbedContentPane.getContentAt(dragTabIndex);

                if (dockable != null) {
                    dge.startDrag(DragSource.DefaultMoveDrop,
                            new MyDoggyTransferable(manager, MyDoggyTransferable.CONTENT_ID_DF, dockable.getId()),
                            this);

                    // Setup ghostImage
                    if (SwingUtil.getBoolean("drag.icon.useDefault", false)) {
                        setGhostImage(dge.getDragOrigin(),
                                SwingUtil.getImage(MyDoggyKeySpace.DRAG));
                    } else {
                        Component c = dge.getComponent();

                        // Build ghost image
                        Rectangle rect = tabbedContentPane.getBoundsAt(dragTabIndex);

                        // Ensure the image is large enough for partially visible tabs
                        // Ensure exception is somehow handled
                        try {
                            BufferedImage image = new BufferedImage(c.getWidth() + rect.width,
                                                                    c.getHeight() + rect.height,
                                                                    BufferedImage.TYPE_INT_ARGB);
                            Graphics g = image.getGraphics();
                            c.paint(g);
                            image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);

                            setGhostImage(dge.getDragOrigin(), image);
                        } catch (Throwable t) {
                            // TODO: use a default imager
                            t.printStackTrace();
                        }
                    }
                } else
                    releaseLocks();
            } else
                releaseLocks();
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            if (!checkStatus())
                return;

            updateGhostImage(dsde.getLocation());

            updateDropTarget(dsde);
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!checkStatus())
                return;

            releaseLocks();

            // Clean ghost image
            cleanupGhostImage();

            // Finalize drag action...
            try {
                if (lastDropPanel != null) {
                    lastDropPanel.drop(dsde.getDragSourceContext().getTransferable());
                } else if (lastBarAnchor == null && SwingUtil.getBoolean(MyDoggyKeySpace.DND_CONTENT_OUTSIDE_FRAME, true)) {
                    // Detach content

                    Content content = tabbedContentPane.getContentAt(dragTabIndex);
                    if (content == null)
                        return;
                    ContentUI contentUI = content.getContentUI();

                    Rectangle bounds = contentUI.getDetachedBounds();
                    if (bounds != null) {
                        bounds.setLocation(dsde.getLocation());
                    } else {
                        bounds = new Rectangle();
                        bounds.setLocation(dsde.getLocation());
                        bounds.setSize(toolWindowManager.getBoundsToScreen(content.getComponent().getBounds(),
                                content.getComponent().getParent()).getSize());
                    }

                    contentUI.setDetachedBounds(bounds);
                    content.setDetached(true);
                }
            } finally {
                // End dockable drop gesture..
                dockableDropDragEnd();
            }
        }


        @Override
        protected boolean isDragEnabled() {
            return super.isDragEnabled() && isDockableContainerDragEnabled();
        }
    }

    public class MultiSplitTabbedDockableDropPanel extends DockableDropPanel {

        public MultiSplitTabbedDockableDropPanel() {
            super(10, Content.class);
        }


        public boolean dragStart(Transferable transferable, int action) {
            try {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                    if (transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_MANAGER).equals(System.identityHashCode(toolWindowManager))) {
                        if (transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF))
                            return super.dragStart(transferable, action);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

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
                        int onIndex = getOnIndex();

//                        System.out.println("onDockable = " + onDockable + "; onIndex = " + onIndex);

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

                        if (rejectDrop) {
                            return false;
                        } else {
                            ToolWindowAnchor onAnchor = getOnAnchor();
                            if (content.isDetached()) {
                                // Reattach...
                                content.reattach(
                                        new MultiSplitConstraint(onDockable,
                                                onIndex,
                                                (onAnchor == null) ? null : AggregationPosition.valueOf(onAnchor.toString()))
                                );
                            } else {
                                // Move the content using the new position...
                                setConstraints(content,
                                        content.getComponent(),
                                        onDockable,
                                        onIndex,
                                        (onAnchor == null) ? null : AggregationPosition.valueOf(onAnchor.toString()));
                            }

                            return true;
                        }
                    } else
                        return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return false;
        }

    }

}
