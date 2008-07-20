package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Transferable;
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

        this.contentPanel = new ContentPanel("@@mydoggy.dockable.", 10);
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
        JTabbedContentPane wrapper = new JTabbedContentPane();

        wrapper.setToolWindowManager(toolWindowManager);
        wrapper.setName("@@mydoggy.dockable.tabbedpane");
//        wrapper.setFocusCycleRoot(true);
        wrapper.addTab((Content) dockable, new DockablePanel(dockable, component));
        
        SwingUtil.registerDragGesture(wrapper, new TabbedDragGesture(wrapper));

        return wrapper;
    }

    protected Component getComponentFromWrapper(Component wrapper) {
        if (wrapper instanceof JTabbedContentPane) {
            JTabbedContentPane tabbedPane = (JTabbedContentPane) wrapper;
            return ((DockablePanel) tabbedPane.getComponentAt(0)).getComponent();
        } else if (wrapper instanceof DockablePanel) {
            return ((DockablePanel) wrapper).getComponent();
        } else
            return wrapper;
    }

    protected Component getComponentFromWrapper(Component wrapper, Dockable dockable) {
        if (wrapper instanceof JTabbedContentPane) {
            JTabbedContentPane tabbedPane = (JTabbedContentPane) wrapper;
            return ((DockablePanel) tabbedPane.getComponentAt(tabbedPane.indexOfContent((Content) dockable))).getComponent();
        } else if (wrapper instanceof DockablePanel) {
            return ((DockablePanel) wrapper).getComponent();
        } else
            return wrapper;
    }

    protected void addToWrapper(Component wrapper, Dockable dockable,
                                int aggregationIndexLocation, Component content) {
        if (wrapper instanceof JTabbedContentPane) {
            JTabbedContentPane tabbedContentPane = (JTabbedContentPane) wrapper;
            tabbedContentPane.addTab((Content) dockable,
                                     new DockablePanel(dockable, content),
                                     aggregationIndexLocation);
            tabbedContentPane.setSelectedIndex((aggregationIndexLocation < 0 || aggregationIndexLocation >= tabbedContentPane.getTabCount()) ? tabbedContentPane.getTabCount() - 1 : aggregationIndexLocation);
        } else if (wrapper instanceof DockablePanel) {
            DockablePanel wrapperContainer = (DockablePanel) wrapper;

            // Create a new tabbedContentPane with the old dockable
            JTabbedContentPane tabbedContentPane = (JTabbedContentPane) forceWrapperForComponent(wrapperContainer.getDockable(),
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
        JTabbedContentPane tabbedPane = (JTabbedContentPane) wrapperSource;
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
        return contentPanel.getComponent();
    }

    protected void setRootComponent(Component component) {
        contentPanel.setComponent(component);
    }

    protected void resetRootComponent() {
        contentPanel.resetComponent();
    }

    protected boolean isWrapper(Component component) {
        return component instanceof JTabbedContentPane; 
    }


    public class TabbedDragGesture extends DragGestureAdapter {
        protected JTabbedContentPane tabbedContentPane;
        protected int dragTabIndex;


        public TabbedDragGesture(JTabbedContentPane tabbedContentPane) {
            super(toolWindowManager);
            this.tabbedContentPane = tabbedContentPane;
        }


        public void dragGestureRecognized(DragGestureEvent dge) {
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
                                  new MyDoggyTransferable(manager,
                                                          MyDoggyTransferable.CONTENT_ID_DF,
                                                          dockable.getId()),
                                  this);

                    // Setup ghostImage
                    if (SwingUtil.getBoolean("drag.icon.useDefault", false)) {
                        setGhostImage(dge.getDragOrigin(),
                                      SwingUtil.getImage(MyDoggyKeySpace.DRAG));
                    } else {
                        Component c = dge.getComponent();

                        // Build ghost image
                        Rectangle rect = tabbedContentPane.getBoundsAt(dragTabIndex);
                        BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics g = image.getGraphics();
                        c.paint(g);
                        image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);

                        setGhostImage(dge.getDragOrigin(), image);
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
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!checkStatus())
                return;

            releaseLocks();
            
            // Finalize drag action...
            cleanupGhostImage();

            if (!dsde.getDropSuccess()) {
                 Content content = tabbedContentPane.getContentAt(dragTabIndex);
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
        }

    }

    public class ContentDropTarget extends DropTarget {

        public ContentDropTarget(JComponent component, ToolWindowManager toolWindowManager) throws HeadlessException {
            super(component, DnDConstants.ACTION_MOVE, new ContentDropTargetListener(component, toolWindowManager));
        }

    }

    public class ContentDropTargetListener implements DropTargetListener, PropertyChangeListener {
        protected ToolWindowManager toolWindowManager;
        protected JComponent component;

        protected Component dockableWrapper;
        protected Dockable onDockable;
        protected int indexAtLocation;
        protected ToolWindowAnchor dragAnchor;

        protected Border oldBorder;
        protected Border dragBorder = new LineBorder(Color.BLUE, 3);

        protected JTabbedContentPane oldTabbedContentPane;


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
            if (checkEvent(dtde)) {
                onDockable = null;

                dtde.acceptDrag(dtde.getDropAction());

                if (component.getBorder() != dragBorder)
                    oldBorder = component.getBorder();

                putProperty("dragStart");
            } else
                dtde.rejectDrag();
        }

        public void dragOver(DropTargetDragEvent dtde) {
            if (!checkEvent(dtde))
                return;

            Point location = dtde.getLocation();
            component.putClientProperty("dragOver", location);

            JTabbedContentPane tabbedContentPane = null;

            Component deepestCmp = SwingUtilities.getDeepestComponentAt(component, location.x, location.y);
            if (deepestCmp != null) {
                dockableWrapper = SwingUtil.getParent(deepestCmp, "@@mydoggy.dockable.");
                if (dockableWrapper != null) {
                    if (dockableWrapper instanceof JTabbedContentPane) {
                        tabbedContentPane = (JTabbedContentPane) dockableWrapper;

                        Point locationOnDeepest = SwingUtilities.convertPoint(component, location, dockableWrapper);
                        indexAtLocation = tabbedContentPane.indexAtLocation(locationOnDeepest.x, locationOnDeepest.y);

                        DockablePanel dockablePanel = SwingUtil.getParent(deepestCmp, DockablePanel.class);
                        if (dockablePanel != null)
                            onDockable = SwingUtil.getParent(deepestCmp, DockablePanel.class).getDockable();
                        else {
                            if (indexAtLocation != -1)
                                onDockable = ((DockablePanel) tabbedContentPane.getComponentAt(indexAtLocation)).getDockable();
                            else
                                onDockable = ((DockablePanel) tabbedContentPane.getComponentAt(0)).getDockable();
                        }
                    } else if (dockableWrapper instanceof DockablePanel) {
                        onDockable = ((DockablePanel) dockableWrapper).getDockable();
                        indexAtLocation = -1;
                    } else {
                        onDockable = null;
                        indexAtLocation = -1;
                    }
                } else {
                    onDockable = null;
                    indexAtLocation = -1;
                }
            } else {
                dockableWrapper = null;
                onDockable = null;
                indexAtLocation = -1;
            }

            if (tabbedContentPane != null)
                tabbedContentPane.setTargetLine(indexAtLocation);

            if (tabbedContentPane != oldTabbedContentPane && oldTabbedContentPane != null)
                oldTabbedContentPane.setTargetLine(-1);    

            oldTabbedContentPane = tabbedContentPane;
        }


        public void dropActionChanged(DropTargetDragEvent dtde) {
            if (checkEvent(dtde))
                dragEnter(dtde);
        }

        public void dragExit(DropTargetEvent dte) {
//            component.setBorder(oldBorder);
            onDockable = null;
            oldBorder = null;
            putProperty("dragExit");
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
                                        if (dockableWrapper instanceof JTabbedContentPane) {
                                            JTabbedContentPane tabbedContentPane = (JTabbedContentPane) dockableWrapper;
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

                                if (rejectDrop) {
                                    dtde.dropComplete(false);
                                } else {
                                    setConstraints(content,
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

        protected boolean checkEvent(DropTargetDragEvent dtde) {
            Transferable transferable = dtde.getTransferable();
            try {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                    if (System.identityHashCode(toolWindowManager) == (Integer) transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                        if (transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF))
                            return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}
