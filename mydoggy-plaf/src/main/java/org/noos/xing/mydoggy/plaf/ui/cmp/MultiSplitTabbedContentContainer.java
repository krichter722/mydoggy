package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MultiSplitTabbedContentContainer extends MultiSplitDockableContainer {
    protected ContentPanel contentPanel;

    public MultiSplitTabbedContentContainer(MyDoggyToolWindowManager toolWindowManager) {
        super(toolWindowManager, JSplitPane.VERTICAL_SPLIT);
        setStoreLayout(false);

        this.contentPanel = new ContentPanel("dockable.tabbedpane", 10);
        this.contentPanel.setDropTarget(new ContentDropTarget(contentPanel, toolWindowManager));
        add(contentPanel, "0,0,FULL,FULL");
    }


    public Component[] getTabbedComponents() {
        Component component = contentPanel.getComponent();
        if (component instanceof MultiSplitPane) {
            return ((MultiSplitPane) component).getComponents();
        } else
            return new Component[]{component};
    }

    protected Container getComponentWrapper(Dockable dockable, Component component) {
        JTabbedContentPane tabbedPane = new JTabbedContentPane();
        tabbedPane.setToolWindowManager(toolWindowManager);
        tabbedPane.setName("dockable.tabbedpane");
        tabbedPane.setFocusCycleRoot(true);
        tabbedPane.addTab((Content) dockable,
                new DockablePanel(dockable, component));

        SwingUtil.registerDragGesture(tabbedPane, new TabbedDragGesture(tabbedPane));

        return tabbedPane;
    }

    protected Component getWrappedComponent(Container container) {
        JTabbedPane tabbedPane = (JTabbedPane) container;
        return ((DockablePanel) tabbedPane.getComponentAt(0)).getComponent(0);
    }

    protected void addToComponentWrapper(Component wrapperSource, Dockable dockable,
                                         int aggregationIndexLocation, Component content) {
        JTabbedContentPane tabbedPane = (JTabbedContentPane) wrapperSource;
        tabbedPane.addTab((Content) dockable,
                new DockablePanel(dockable, content),
                aggregationIndexLocation);
        tabbedPane.setSelectedIndex((aggregationIndexLocation < 0 || aggregationIndexLocation >= tabbedPane.getTabCount()) ? tabbedPane.getTabCount() - 1 : aggregationIndexLocation);
    }

    protected void removeComponentWrapper(Component wrapperSource, Dockable dockable) {
        JTabbedContentPane tabbedPane = (JTabbedContentPane) wrapperSource;
        int index = tabbedPane.indexOfContent((Content) dockable);
        if (index != -1)
            tabbedPane.removeTabAt(index);
        else
            throw new IllegalArgumentException("Cannot find that dockable on the passed tabbedpane");
    }

    protected Component getRootComponent() {
        return contentPanel.getComponent();
    }

    protected void setRootComponent(Component component) {
        contentPanel.setComponent(component);
    }

    protected void resetRootComponent() {
        contentPanel.resetComponent();
    }


    protected class DockablePanel extends JPanel {
        protected Dockable dockable;

        public DockablePanel(Dockable dockable, Component component) {
            this.dockable = dockable;

            setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
            add(component, "0,0,FULL,FULL");
        }

        public Dockable getDockable() {
            return dockable;
        }

        public void setComponent(Component component) {
            add(component, "0,0,FULL,FULL");
        }
    }


    protected class TabbedDragGesture extends DragGestureAdapter {
        protected JTabbedContentPane dockableTabbedPane;

        public TabbedDragGesture(JTabbedContentPane dockableTabbedPane) {
            super(toolWindowManager);
            this.dockableTabbedPane = dockableTabbedPane;
        }

        public void dragGestureRecognized(DragGestureEvent dge) {
            // Acquire locks
            if (!acquireLocks())
                return;

            // Start Drag
            Point origin = dge.getDragOrigin();
            int index = dockableTabbedPane.indexAtLocation(origin.x, origin.y);
            if (index != -1) {
                Dockable dockable = dockableTabbedPane.getContentAt(index);

                if (dockable != null) {
                    dge.startDrag(Cursor.getDefaultCursor(),
                            new MyDoggyTransferable(MyDoggyTransferable.CONTENT_ID_DF,
                                    dockable.getId()),
                            this);

                    // Setup ghostImage
                    Component component = dockableTabbedPane.getComponentAt(index);
                    BufferedImage ghostImage = new BufferedImage(component.getWidth(),
                            component.getHeight(), BufferedImage.TYPE_INT_RGB);
                    component.print(ghostImage.getGraphics());
                    ghostImage = GraphicsUtil.scale(ghostImage,
                            component.getWidth() / 4,
                            component.getHeight() / 4);

                    setGhostImage(dge.getDragOrigin(), ghostImage);
                } else
                    releaseLocks();
            } else
                releaseLocks();
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            if (!checkStatus())
                return;
            updateGhostImage(dsde.getLocation());
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!checkStatus())
                return;

            releaseLocks();
            // Finalize drag action...
            cleanupGhostImage();
        }

    }

    protected class ContentDropTarget extends DropTarget {

        public ContentDropTarget(JComponent component, ToolWindowManager toolWindowManager) throws HeadlessException {
            super(component, DnDConstants.ACTION_MOVE, new ContentDropTargetListener(component, toolWindowManager));
        }

    }

    protected class ContentDropTargetListener implements DropTargetListener, PropertyChangeListener {
        protected ToolWindowManager toolWindowManager;
        protected JComponent component;

        protected JTabbedPane dockableTabbedPane;
        protected Dockable onDockable;
        protected int indexAtLocation;
        protected ToolWindowAnchor dragAnchor;

        protected Border oldBorder;
        protected Border dragBorder = new LineBorder(Color.BLUE, 3);

        public ContentDropTargetListener(JComponent component, ToolWindowManager toolWindowManager) {
            this.component = component;
            this.toolWindowManager = toolWindowManager;

            this.component.addPropertyChangeListener("dragAnchor", this);
            this.component.addPropertyChangeListener("dragToolWindow", this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if ("dragAnchor".equals(propertyName)) {
                this.dragAnchor = (ToolWindowAnchor) evt.getNewValue();
            } else {
                this.onDockable = null;
            }
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            if (dtde.getDropAction() == DnDConstants.ACTION_MOVE &&
                    (dtde.getTransferable().isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF))
                    ) {
                onDockable = null;

                dtde.acceptDrag(dtde.getDropAction());

                if (component.getBorder() != dragBorder)
                    oldBorder = component.getBorder();

                putProperty("dragStart");
            } else
                dtde.rejectDrag();
        }

        public void dragOver(DropTargetDragEvent dtde) {
            Point location = dtde.getLocation();
            component.putClientProperty("dragOver", location);

            Component deepestCmp = SwingUtilities.getDeepestComponentAt(component, location.x, location.y);
            if (deepestCmp != null) {
                dockableTabbedPane = (JTabbedPane) SwingUtil.getParent(deepestCmp, "dockable.tabbedpane");
                if (dockableTabbedPane != null) {
                    Point locationOnDeepest = SwingUtilities.convertPoint(component, location, dockableTabbedPane);
                    indexAtLocation = dockableTabbedPane.indexAtLocation(locationOnDeepest.x, locationOnDeepest.y);

                    DockablePanel dockablePanel = SwingUtil.getParent(deepestCmp, DockablePanel.class);
                    if (dockablePanel != null)
                        onDockable = SwingUtil.getParent(deepestCmp, DockablePanel.class).getDockable();
                    else {
                        if (indexAtLocation != -1)
                            onDockable = ((DockablePanel) dockableTabbedPane.getComponentAt(indexAtLocation)).getDockable();
                        else
                            onDockable = ((DockablePanel) dockableTabbedPane.getComponentAt(0)).getDockable();
                    }
                } else {
                    onDockable = null;
                    indexAtLocation = -1;
                }
            } else {
                dockableTabbedPane = null;
                onDockable = null;
                indexAtLocation = -1;
            }
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            dragEnter(dtde);
        }

        public void dragExit(DropTargetEvent dte) {
//            component.setBorder(oldBorder);
            onDockable = null;
            oldBorder = null;
            putProperty("dragExit");
        }

        public void drop(DropTargetDropEvent dtde) {
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
                                        for (int i = 0, size = dockableTabbedPane.getTabCount(); i < size; i++) {
                                            DockablePanel dockablePanel = (DockablePanel) dockableTabbedPane.getComponentAt(i);
                                            if (dockablePanel.getDockable() == onDockable && i == indexAtLocation) {
                                                rejectDrop = true;
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (rejectDrop) {
                                    dtde.dropComplete(false);
                                } else {
                                    ContentUI contentUI = contentManager.getContentManagerUI().getContentUI(content);

                                    removeDockable(content);
                                    addDockable(content,
                                            content.getComponent(),
                                            onDockable,
                                            indexAtLocation,
                                            (dragAnchor == null) ? null : AggregationPosition.valueOf(dragAnchor.toString()));

                                    dtde.dropComplete(true);
                                }
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
            } finally {
                putProperty("dragEnd");

                // Restore component
                dragExit(dtde);
            }
        }

        protected void putProperty(String name) {
            Boolean value = (Boolean) component.getClientProperty(name);
            if (value != null)
                component.putClientProperty(name, !value);
            else
                component.putClientProperty(name, false);
        }

    }

}
