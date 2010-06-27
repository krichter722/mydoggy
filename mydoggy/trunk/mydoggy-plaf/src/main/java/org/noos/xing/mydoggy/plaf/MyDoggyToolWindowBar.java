package org.noos.xing.mydoggy.plaf;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToolsOnBarMouseListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.multisplit.MultiSplitLayout;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowBarDropTarget;
import org.noos.xing.mydoggy.plaf.ui.util.SourceFilterPropertyChangeListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro
 */
public class MyDoggyToolWindowBar extends PropertyChangeEventSource implements ToolWindowBar,
                                                                               SwingConstants,
                                                                               PropertyChangeListener {
    public static final int VERTICAL_LEFT = TextIcon.ROTATE_LEFT;
    public static final int VERTICAL_RIGHT = TextIcon.ROTATE_RIGHT;
    public static final int HORIZONTAL = TextIcon.ROTATE_NONE;
    static int dragComponentLength;

    protected MyDoggyToolWindowManager manager;
    protected ToolWindowAnchor anchor;
    protected int dividerSize;
    protected boolean aggregateMode;
    protected boolean visible = true;

    // Bar Components
    protected ToolWindowScrollBar toolWindowScrollBar;
    protected JPanel toolWindowBarContainer;
    protected TableLayout toolWindowBarContainerLayout;

    protected JSplitPane splitPane;
    protected MultiSplitDockableContainer multiSplitDockableContainer;
    protected DockableDropPanel dockableDropPanel;
    protected JPopupMenu popupMenu;

    protected int length;
    protected int availableTools;
    protected int orientation;
    protected boolean horizontal;

    protected PropertyChangeSupport propertyChangeSupport;

    protected boolean temporarilyVisible;

    boolean valueAdjusting = false;

    // Used for setVisible store/restore the layout of the bar...
    protected boolean toolsVisible = true;
    protected byte[] toolsWorkspace;


    public MyDoggyToolWindowBar(MyDoggyToolWindowManager manager, JSplitPane splitPane, ToolWindowAnchor anchor) {
        super(manager.getFirePublicEvent());

        this.manager = manager;
        this.splitPane = splitPane;
        if (splitPane instanceof DebugSplitPane)
            ((DebugSplitPane) splitPane).setToolWindowBar(this);
        this.anchor = anchor;
        this.availableTools = 0;
        this.dividerSize = 3;
        this.length = SwingUtil.getInt("ToolWindowBarPanelUI." + anchor.toString().toLowerCase() + ".length", 23);

        initComponents();
        initListeners();

        if (anchor == ToolWindowAnchor.LEFT || anchor == ToolWindowAnchor.TOP)
            setSplitDividerLocation(0);
    }


    public ToolWindowManager getToolWindowManager() {
        return manager;
    }

    public ToolWindowAnchor getAnchor() {
        return anchor;
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(int size) {
        if (this.dividerSize == size)
            return;

        if (size < 1)
            throw new IllegalArgumentException("Size cannot be lesser than 1");

        int old = this.dividerSize;
        this.dividerSize = size;

        firePropertyChangeEvent("dividerSize", old, size);
    }

    public void setAggregateMode(boolean enable) {
        if (this.aggregateMode == enable)
            return;

        boolean old = this.aggregateMode;
        this.aggregateMode = enable;

        firePropertyChangeEvent("aggregateMode", old, enable);
    }

    public boolean isAggregateMode() {
        return aggregateMode;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setLength(int length) {
        if (this.length == length)
            return;

        int old = this.length;
        this.length = length;
        firePropertyChangeEvent("length", old, length);
    }

    public int getLength() {
        return length;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        if (this.popupMenu != null && this.popupMenu.equals(popupMenu))
            return;

        JPopupMenu old = this.popupMenu;
        this.popupMenu = popupMenu;

        firePropertyChangeEvent("popupMenu", old, popupMenu);
    }

    public ToolWindow[] getToolWindows() {
        return manager.getToolsByAnchor(anchor);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        
        firePropertyChangeEvent("visible", !visible, visible);
    }

    public boolean areToolsVisible() {
        return toolsVisible;
    }

    public void setToolsVisible(boolean visible) {
        if (visible) {
            if (toolsWorkspace != null)
                manager.getPersistenceDelegate().merge(new ByteArrayInputStream(toolsWorkspace),
                                                       PersistenceDelegate.MergePolicy.RESET);
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            manager.getPersistenceDelegate().save(outputStream, new PersistenceDelegateFilter() {
                public boolean storeToolWindowManagerDescriptor() {
                    return false;
                }

                public boolean storeToolWindow(ToolWindow toolWindow) {
                    return toolWindow.getAnchor() == anchor;
                }

                public boolean storeToolWindowBar(ToolWindowBar toolWindowBar) {
                    return toolWindowBar == MyDoggyToolWindowBar.this;
                }

                public boolean storeContentManager() {
                    return false;
                }
            });
            toolsWorkspace = outputStream.toByteArray();

            for (ToolWindow toolWindow : getToolWindows()) {
                toolWindow.setVisible(false);
            }
        }

        this.toolsVisible = visible;

        firePropertyChangeEvent("toolsVisible", !visible, visible);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }

    public String toString() {
        return "MyDoggyToolWindowBar{" +
               "anchor=" + anchor +
               ", availableTools=" + availableTools +
               ", orientation=" + orientation +
               '}';
    }


    public Object getLayout() {
        return multiSplitDockableContainer.getMultiSplitLayout();
    }

    public void setLayout(Object layout) {
        multiSplitDockableContainer.setMultiSplitLayout((MultiSplitLayout.Node) layout);
    }

    public Container getContainer() {
        return toolWindowScrollBar;
    }

    public JSplitPane getSplitPane() {
        return splitPane;
    }

    public void ensureVisible(final Component component) {
        if (component == null)
            return;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                toolWindowScrollBar.ensureVisible(component);
            }
        });
    }

    public int getAvailableTools() {
        return availableTools;
    }

    public boolean isTemporarilyVisible() {
        return temporarilyVisible;
    }

    public void setTemporarilyVisible(boolean temporarilyVisible) {
        boolean old = this.temporarilyVisible;
        this.temporarilyVisible = temporarilyVisible;

        manager.syncPanel(anchor);

        firePropertyChangeEvent("temporarilyVisible", old, temporarilyVisible);
    }

    public int getRepresentativeAnchorIndex(Component representativeAnchor) {
        TableLayoutConstraints constraints = getRepresentativeAnchorConstraints(representativeAnchor);
        if (constraints == null)
            return -1;

        if (horizontal)
            return (constraints.col1 / 2) - 1;
        else
            return (constraints.row1 / 2) - 1;
    }

    public synchronized void deactiveTool(ToolWindow toolWindow) {
        valueAdjusting = true;
        toolWindow.setActive(false);
        valueAdjusting = false;
    }

    public synchronized void hideTool(ToolWindow toolWindow) {
        valueAdjusting = true;
        toolWindow.setVisible(false);
        valueAdjusting = false;
    }

    public int getSize() {
        return (getAvailableTools() > 0) ? getLength() : 0;
    }

    public boolean isValueAdjusting() {
        return valueAdjusting;
    }

    public byte[] getToolsWorkspace() {
        return toolsWorkspace;
    }

    public void setToolWorkspace(byte[] workspace, boolean toolsVisible) {
        this.toolsWorkspace = workspace;
        this.toolsVisible = toolsVisible;
    }


    public void updateMaximizedToolSize() {
        setSplitDividerLocation(-1);
        SwingUtil.repaintNow(splitPane);
    }


    protected void initComponents() {
        splitPane.setName(anchor.toString());
//        splitPane.setFocusCycleRoot(true);

        dockableDropPanel = new DockedDockableDropPanel();

        toolWindowBarContainer = new ToolWindowBarPanel(this);
        toolWindowBarContainer.setName("toolWindowManager.bar." + anchor.toString());
        toolWindowBarContainer.setFocusable(false);
        toolWindowBarContainer.setFocusCycleRoot(true);

        switch (anchor) {
            case LEFT:
            case RIGHT:
                horizontal = false;
                toolWindowBarContainer.setLayout(
                        toolWindowBarContainerLayout = new ExtendedTableLayout(
                                new double[][]{{2, getLength() - 4, 2}, {0}}
                        )
                );
                orientation = JSplitPane.VERTICAL_SPLIT;
                break;
            default:
                horizontal = true;
                toolWindowBarContainer.setLayout(
                        toolWindowBarContainerLayout = new ExtendedTableLayout(
                                new double[][]{{0}, {2, getLength() - 4, 2}}
                        )
                );
                orientation = JSplitPane.HORIZONTAL_SPLIT;
        }

        multiSplitDockableContainer = new MultiSplitDockableContainer(manager, orientation);

        toolWindowScrollBar = new ToolWindowScrollBar(this, toolWindowBarContainer);

        toolWindowBarContainer.setDropTarget(new ToolWindowBarDropTarget(manager, anchor, toolWindowBarContainer));
        toolWindowBarContainer.addMouseListener(new ToolsOnBarMouseListener(manager, this));
    }

    protected void initListeners() {
        propertyChangeSupport = new PropertyChangeSupport(this);

        AvailableListener availableListener = new AvailableListener();
        propertyChangeSupport.addPropertyChangeListener("available", availableListener);
        propertyChangeSupport.addPropertyChangeListener("visible", new SourceFilterPropertyChangeListener(availableListener, RepresentativeAnchorDescriptor.class));
        propertyChangeSupport.addPropertyChangeListener("showUnavailableTools", new ShowUnavailableToolsListener());

        propertyChangeSupport.addPropertyChangeListener("visible.before", new VisibleBeforeListener());
        propertyChangeSupport.addPropertyChangeListener("visible.DOCKED", new VisibleDockedListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING", new VisibleFloatingListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING_FREE", new VisibleFloatingFreeListener());
        propertyChangeSupport.addPropertyChangeListener("visible.SLIDING", new VisibleSlidingListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING_LIVE", new VisibleFloatingLiveListener());
        propertyChangeSupport.addPropertyChangeListener("visible", new SourceFilterPropertyChangeListener(new VisibleListener(), ToolWindow.class));

        propertyChangeSupport.addPropertyChangeListener("active.before", new ActiveBeforeListener());

        propertyChangeSupport.addPropertyChangeListener("type", new TypeListener());
        propertyChangeSupport.addPropertyChangeListener("index", new IndexListener());
        propertyChangeSupport.addPropertyChangeListener("title", new TitleListener());
        propertyChangeSupport.addPropertyChangeListener("icon", new IconListener());

        propertyChangeSupport.addPropertyChangeListener("dockLength", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ToolWindow toolWindow = (ToolWindow) evt.getSource();
                if (toolWindow.isVisible()) {
                    setSplitDividerLocation((Integer) evt.getNewValue());
                    SwingUtil.repaint(splitPane);
                }
            }
        });

        DragSupportListener dragSupportListener = new DragSupportListener();
        propertyChangeSupport.addPropertyChangeListener("startDrag", dragSupportListener);
        propertyChangeSupport.addPropertyChangeListener("endDrag", dragSupportListener);
        propertyChangeSupport.addPropertyChangeListener("maximized", new MaximizedListener());

        // Why doesn't this use propertyChangeSupport.add....  
        addPropertyChangeListener("dividerSize", new DividerSizeListener());
        addPropertyChangeListener("length", new BarLengthListener());
    }


    protected int getSplitDividerLocation() {
        int dividerLocation = 0;
        switch (anchor) {
            case LEFT:
            case TOP:
                dividerLocation = splitPane.getDividerLocation();
                break;
            case RIGHT:
                dividerLocation = splitPane.getWidth() - splitPane.getDividerLocation();
                break;
            case BOTTOM:
                dividerLocation = splitPane.getHeight() - splitPane.getDividerLocation();
        }
        return dividerLocation;
    }

    protected void setSplitDividerLocation(int dividerLocation) {
        if (dividerLocation == -1) {
            switch (anchor) {
                case LEFT:
                    splitPane.setDividerLocation(splitPane.getWidth());
                    break;
                case TOP:
                    splitPane.setDividerLocation(splitPane.getHeight());
                    break;
                case RIGHT:
                    splitPane.setDividerLocation(0);
                    break;
                case BOTTOM:
                    splitPane.setDividerLocation(0);
                    break;
            }
        } else
            switch (anchor) {
                case LEFT:
                case TOP:
                    splitPane.setDividerLocation(dividerLocation);
                    break;
                case RIGHT:
                    splitPane.setDividerLocation(Math.abs(splitPane.getWidth() - dividerLocation));
                    break;
                case BOTTOM:
                    splitPane.setDividerLocation(Math.abs(splitPane.getHeight() - dividerLocation));
                    break;
            }
    }

    protected Component getSplitPaneContent() {
        return dockableDropPanel.getComponent();
    }

    protected void addRepresentativeAnchor(DockableDescriptor dockableDescriptor, Component representativeAnchor, int index) {
        // create the wrapper
        representativeAnchor = new RepresentativeAnchorWrapper(dockableDescriptor, representativeAnchor);

        if (dockableDescriptor.isAvailableCountable())
            availableTools++;

        if (horizontal) {
            // insert columns
            toolWindowBarContainerLayout.insertColumn(toolWindowBarContainerLayout.getNumColumn(),
                                                      toolWindowBarContainerLayout.getNumColumn() > 0 ? 5 : 1);
            toolWindowBarContainerLayout.insertColumn(toolWindowBarContainerLayout.getNumColumn(),
                                                      TableLayout.PREFERRED);

            // validate index...
            int finalCol = (index * 2 + 2);
            if (finalCol >= toolWindowBarContainerLayout.getNumColumn())
                index = -1;
            else if (index != -1) {
                // Verify for locked index
                Component[] components = toolWindowBarContainer.getComponents();

                for (Component component : components) {
                    TableLayoutConstraints constraints = toolWindowBarContainerLayout.getConstraints(component);

                    if (constraints.col1 >= finalCol) {
                        if (((RepresentativeAnchorWrapper) component).getDockableDescriptor().isAnchorPositionLocked()) {
                            finalCol += 2;
                            index++;
                        }
                    }
                }
            }

            if (index >= 0) {
                Component[] components = toolWindowBarContainer.getComponents();

                Map<Integer, Double> olds = new Hashtable<Integer, Double>();
                for (Component component : components) {

                    TableLayoutConstraints constraints = toolWindowBarContainerLayout.getConstraints(component);
                    if (constraints.col1 >= finalCol) {
                        int newCol1 = constraints.col1 + 2;
                        toolWindowBarContainerLayout.setConstraints(component,
                                                                    new TableLayoutConstraints(
                                                                            newCol1 + ",1,"
                                                                    ));

                        olds.put(newCol1, toolWindowBarContainerLayout.getColumn(newCol1));
                        Double colSize = olds.get(constraints.col1);
                        if (colSize == null)
                            colSize = toolWindowBarContainerLayout.getColumn(constraints.col1);

                        toolWindowBarContainerLayout.setColumn(newCol1, colSize);
                    }
                }

                toolWindowBarContainerLayout.setColumn(finalCol, TableLayout.PREFERRED);

                toolWindowBarContainer.add(representativeAnchor, (index * 2 + 2) + ",1");
            } else
                toolWindowBarContainer.add(representativeAnchor, (toolWindowBarContainerLayout.getNumColumn() - 1) + ",1");
        } else {
            toolWindowBarContainerLayout.insertRow(toolWindowBarContainerLayout.getNumRow(),
                                                   toolWindowBarContainerLayout.getNumRow() > 0 ? 5 : 1);
            toolWindowBarContainerLayout.insertRow(toolWindowBarContainerLayout.getNumRow(),
                                                   TableLayout.PREFERRED);

            // validate index...
            int finalRow = (index * 2 + 2);
            if (finalRow >= toolWindowBarContainerLayout.getNumRow())
                index = -1;
            else if (index != -1) {
                // Verify for locked index
                Component[] components = toolWindowBarContainer.getComponents();

                for (Component component : components) {
                    TableLayoutConstraints constraints = toolWindowBarContainerLayout.getConstraints(component);

                    if (constraints.row1 >= finalRow) {
                        if (((RepresentativeAnchorWrapper) component).getDockableDescriptor().isAnchorPositionLocked()) {
                            finalRow += 2;
                            index++;
                        }
                    }
                }
            }

            if (index >= 0) {
                Component[] components = toolWindowBarContainer.getComponents();

                Map<Integer, Double> olds = new Hashtable<Integer, Double>();
                for (Component component : components) {

                    TableLayoutConstraints constraints = toolWindowBarContainerLayout.getConstraints(component);

                    if (constraints.row1 >= finalRow) {
                        int newRow1 = constraints.row1 + 2;
                        toolWindowBarContainerLayout.setConstraints(component,
                                                                    new TableLayoutConstraints(
                                                                            "1," + newRow1
                                                                    ));

                        olds.put(newRow1, toolWindowBarContainerLayout.getRow(newRow1));
                        Double rowSize = olds.get(constraints.row1);
                        if (rowSize == null)
                            rowSize = toolWindowBarContainerLayout.getRow(constraints.row1);

                        toolWindowBarContainerLayout.setRow(newRow1, rowSize);
                    }
                }

                if (toolWindowBarContainerLayout.getNumRow() <= finalRow) {
                    toolWindowBarContainerLayout.setRow(toolWindowBarContainerLayout.getNumRow() - 1,
                                                        TableLayout.PREFERRED);
                } else
                    toolWindowBarContainerLayout.setRow(finalRow,
                                                        TableLayout.PREFERRED);

                toolWindowBarContainer.add(representativeAnchor, "1," + (index * 2 + 2));
            } else
                toolWindowBarContainer.add(representativeAnchor, "1," + (toolWindowBarContainerLayout.getNumRow() - 1));
        }
        SwingUtil.repaint(toolWindowScrollBar);
    }

    protected void removeRepresentativeAnchor(DockableDescriptor dockableDescriptor, Component representativeAnchor) {
        if (representativeAnchor == null)
            return;

        int toDelete;

        Component[] components = toolWindowBarContainer.getComponents();
        for (Component component : components) {
            if (((RepresentativeAnchorWrapper) component).getRepresentativeAnchor().equals(representativeAnchor)) {
                representativeAnchor = component;
                break;
            }
        }

        TableLayoutConstraints constraints = toolWindowBarContainerLayout.getConstraints(representativeAnchor);
        if (constraints == null)
            return;

        // Remove
        if (dockableDescriptor.isAvailableCountable())
            availableTools--;

        toDelete = horizontal ? constraints.col1 : constraints.row1;

        toolWindowBarContainer.remove(representativeAnchor);
        if (horizontal) {
            toolWindowBarContainerLayout.deleteColumn(toDelete);
            toolWindowBarContainerLayout.deleteColumn(toDelete - 1);
        } else {
            toolWindowBarContainerLayout.deleteRow(toDelete);
            toolWindowBarContainerLayout.deleteRow(toDelete - 1);
        }

        SwingUtil.repaint(toolWindowScrollBar);
        dockableDescriptor.resetRepresentativeAnchor();
        manager.syncPanel(dockableDescriptor.getAnchor());
    }

    protected TableLayoutConstraints getRepresentativeAnchorConstraints(Component representativeAnchor) {
        Component[] components = toolWindowBarContainer.getComponents();
        for (Component component : components) {
            if (((RepresentativeAnchorWrapper) component).getRepresentativeAnchor().equals(representativeAnchor)) {
                representativeAnchor = component;
                break;
            }
        }

        return toolWindowBarContainerLayout.getConstraints(representativeAnchor);
    }


    public class AvailableListener implements PropertyChangeListener {
        protected Map<DockableDescriptor, Integer> rabsPositions;

        public AvailableListener() {
            rabsPositions = new Hashtable<DockableDescriptor, Integer>();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            DockableDescriptor descriptor = manager.getDockableDescriptorBySource(evt.getSource());

            if (isDockableDescriptorValid(descriptor)) {
                boolean rabEvent = evt.getPropertyName().equals("visible");

                if (!rabEvent) {
                    if (descriptor.getDockableType() == DockableDescriptor.DockableType.TOOL_WINDOW &&
                        !((ToolWindow) descriptor.getDockable()).getRepresentativeAnchorDescriptor().isVisible())
                        return;
                }


                boolean oldAvailable = (Boolean) evt.getOldValue();
                boolean newAvailable = (Boolean) evt.getNewValue();

                boolean repaint = false;

                Component representativeAnchor;
                if (oldAvailable && !newAvailable) {
                    // true -> false

                    boolean updateRepresentativeAnchor = false;

                    if (!rabEvent) {
                        assert evt instanceof UserPropertyChangeEvent;
                        Object[] params = (Object[]) ((UserPropertyChangeEvent) evt).getUserObject();

                        updateRepresentativeAnchor = (manager.getToolWindowManagerDescriptor().isShowUnavailableTools() &&
                                                      descriptor.getAnchorIndex() != -1);

                        if (params[1] == Boolean.TRUE)
                            updateRepresentativeAnchor = false;
                    }

                    representativeAnchor = descriptor.getRepresentativeAnchor();

                    if (representativeAnchor != null) {
                        if (updateRepresentativeAnchor &&
                            descriptor.getDockableType() == DockableDescriptor.DockableType.TOOL_WINDOW) {
                            // Update
                            descriptor.updateRepresentativeAnchor();
                        } else {
                            // Remove
                            if (rabEvent)
                                rabsPositions.put(descriptor, descriptor.getAnchorIndex());

                            removeRepresentativeAnchor(descriptor, representativeAnchor);
                        }

                        repaint = true;
                    }
                } else if (!oldAvailable && newAvailable) {
                    // false -> true
                    Object[] params = null;
                    boolean updateRepresentativeAnchor = false;

                    if (!rabEvent) {
                        assert evt instanceof UserPropertyChangeEvent;
                        params = (Object[]) ((UserPropertyChangeEvent) evt).getUserObject();

                        updateRepresentativeAnchor = (manager.getToolWindowManagerDescriptor().isShowUnavailableTools() &&
                                                      descriptor.getAnchorIndex() != -1);

                        if (params[1] == Boolean.TRUE)
                            updateRepresentativeAnchor = false;
                    }

                    representativeAnchor = descriptor.getRepresentativeAnchor(toolWindowBarContainer);
                    if (updateRepresentativeAnchor &&
                        descriptor.getDockableType() != DockableDescriptor.DockableType.CUSTOM) {
                        // Update
                        descriptor.updateRepresentativeAnchor();
                    } else {
                        // Add
                        if (rabEvent) {
                            int index = -1;
                            if (rabEvent && rabsPositions.containsKey(descriptor))
                                index = rabsPositions.get(descriptor);

                            addRepresentativeAnchor(descriptor, representativeAnchor, index);
                        } else
                            addRepresentativeAnchor(descriptor, representativeAnchor, (Integer) params[0]);
                    }

                    repaint = true;
                }

                if (repaint) {
//                    representativeAnchor.setEnabled(newAvailable);
                    SwingUtil.repaint(toolWindowBarContainer);
                }
            }
        }

        protected boolean isDockableDescriptorValid(DockableDescriptor dockableDescriptor) {
            if (dockableDescriptor.getDockableType() == DockableDescriptor.DockableType.TOOL_WINDOW) {
                ToolWindow tool = (ToolWindow) dockableDescriptor.getDockable();

                return tool.getType() != ToolWindowType.FLOATING_FREE &&
                       tool.getType() != ToolWindowType.EXTERN;
            }
            return true;
        }

    }

    public class ShowUnavailableToolsListener implements PropertyChangeListener {

        public ShowUnavailableToolsListener() {
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() == Boolean.TRUE) {
                for (ToolWindow tool : manager.getToolsByAnchor(anchor)) {
                    if (!tool.isAvailable() && tool.getType() != ToolWindowType.FLOATING_FREE) {
                        ToolWindowDescriptor descriptor = manager.getDescriptor(tool);

                        addRepresentativeAnchor(descriptor,
                                                manager.getDescriptor(tool).getRepresentativeAnchor(toolWindowBarContainer),
                                                -1);
                    }
                }
            } else {
                for (ToolWindow tool : manager.getToolsByAnchor(anchor)) {
                    if (!tool.isAvailable() && tool.getType() != ToolWindowType.FLOATING_FREE) {
                        ToolWindowDescriptor descriptor = manager.getDescriptor(tool);

                        removeRepresentativeAnchor(descriptor,
                                                   descriptor.getRepresentativeAnchor(toolWindowBarContainer));
                    }
                }
            }
        }
    }

    public class ActiveBeforeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = (ToolWindow) evt.getSource();
            boolean newValue = (Boolean) evt.getNewValue();

            if (newValue) {
                // Deactive all tools on the same bar
                ToolWindow[] toolWindows = manager.getToolsByAnchor(getAnchor());
                for (ToolWindow toolWindow : toolWindows) {
                    if (toolWindow == sourceTool)
                        continue;

                    deactiveTool(toolWindow);
                }
            }
        }
    }

    public class TypeListener extends AvailableListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = manager.getDescriptor((ToolWindow)  evt.getSource());

            if (evt.getOldValue() == ToolWindowType.FLOATING_FREE) {
                addRepresentativeAnchor(toolWindowDescriptor, toolWindowDescriptor.getRepresentativeAnchor(toolWindowBarContainer), -1);
                ensureVisible(toolWindowDescriptor.getRepresentativeAnchor());

                SwingUtil.repaint(toolWindowBarContainer);
            } else if ((evt.getNewValue() == ToolWindowType.FLOATING_FREE || evt.getNewValue() == ToolWindowType.EXTERN) &&
                       toolWindowDescriptor.getRepresentativeAnchor() != null) {

                removeRepresentativeAnchor(toolWindowDescriptor, toolWindowDescriptor.getRepresentativeAnchor());
                SwingUtil.repaint(toolWindowBarContainer);
            }

        }
    }


    public class VisibleBeforeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = (ToolWindow) evt.getSource();
            if (sourceTool.getType() == ToolWindowType.FLOATING ||
                sourceTool.getType() == ToolWindowType.FLOATING_FREE)
                return;

            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            if (!oldValue && newValue) { // false and true
                if (getAnchor().equals(sourceTool.getAnchor()) && sourceTool.getType() == ToolWindowType.SLIDING)
                    return;

                ToolWindow[] toolWindows = manager.getToolsByAnchor(getAnchor());
                for (ToolWindow toolWindow : toolWindows) {
                    if (toolWindow == sourceTool)
                        continue;


                    if (manager.getShowingGroup() == null) {
                        if (toolWindow.getType() == ToolWindowType.FLOATING ||
                            toolWindow.getType() == ToolWindowType.FLOATING_FREE ||
                            toolWindow.getType() == ToolWindowType.FLOATING_LIVE ||
                            toolWindow.getType() == ToolWindowType.EXTERN)
                            continue;

                        if (toolWindow.getAnchor().equals(sourceTool.getAnchor()))
//                            toolWindow.setVisible(false);
                            hideTool(toolWindow);
                        else if (toolWindow.isAutoHide() || toolWindow.getType() == ToolWindowType.SLIDING)
                            hideTool(toolWindow);
//                            toolWindow.setVisible(false);
                    } else if (toolWindow.getType() == ToolWindowType.SLIDING)
                        hideTool(toolWindow);
//                        toolWindow.setVisible(false);

                    if (toolWindow.isVisible() && toolWindow.isMaximized())
                        toolWindow.setMaximized(false);
                }
            }
        }

    }

    public class VisibleListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = (ToolWindow) evt.getSource();
            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            if (evt instanceof UserPropertyChangeEvent) {
                MyDoggyToolWindowBar.this.propertyChange(new UserPropertyChangeEvent(evt.getSource(), "visible." + sourceTool.getType().toString(),
                                                                                     null, !oldValue && newValue,
                                                                                     ((UserPropertyChangeEvent) evt).getUserObject()
                ));
            } else
                MyDoggyToolWindowBar.this.propertyChange(new PropertyChangeEvent(evt.getSource(), "visible." + sourceTool.getType().toString(),
                                                                                 null, !oldValue && newValue));
        }
    }


    public class VisibleDockedListener implements PropertyChangeListener {
        protected final SplitAnimation splitAnimation = new SplitAnimation();
        protected boolean vsdValueAdjusting = false;
        protected Map<ToolWindowDescriptor, Integer> anchorPositions;

        public VisibleDockedListener() {
            anchorPositions = new HashMap<ToolWindowDescriptor, Integer>();

            splitPane.addPropertyChangeListener("dividerLocation", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    int dividerLocation = getSplitDividerLocation();

                    if (splitAnimation.isAnimating() || vsdValueAdjusting || dividerLocation <= 5)
                        return;

                    for (ToolWindow toolWindow : manager.getToolsByAnchor(anchor)) {
                        if (toolWindow.isVisible())
                            manager.getDescriptor(toolWindow).setDividerLocation(dividerLocation);
                    }
                }
            });
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (!manager.getContentManager().isEnabled() && manager.dockableMainContentMode)
                disabledContentManagerPropertyChange(evt);
            else
                enabledContentManagerPropertyChange(evt);
        }


        public void enabledContentManagerPropertyChange(PropertyChangeEvent evt) {
            // Prepare parameters
            boolean shiftShow = false;
            AggregationPosition aggregationPosition = AggregationPosition.DEFAULT;
            ToolWindow aggregationOnTool = null;

            if (evt instanceof UserPropertyChangeEvent) {
                // This is an aggregation
                UserPropertyChangeEvent upce = (UserPropertyChangeEvent) evt;
                Object[] args = ((Object[]) upce.getUserObject());

                shiftShow = (Boolean) args[0];
                aggregationPosition = (AggregationPosition) args[1];
                aggregationOnTool = (ToolWindow) args[2];
            }

            final ToolWindowDescriptor descriptor = manager.getDescriptor((ToolWindow) evt.getSource());
            boolean visible = (Boolean) evt.getNewValue();

            ToolWindowDescriptor.fullExternalFocusValueAdjusting = true;

            // Check if we should hide the representative anchor button
            if (descriptor.getTypeDescriptor().isHideRepresentativeButtonOnVisible()) {
                if (visible) {
                    // Store position and remove anchor
                    anchorPositions.put(descriptor, descriptor.getAnchorIndex());

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            removeRepresentativeAnchor(descriptor, descriptor.getRepresentativeAnchor());
                        }
                    });
                } else {
                    // Restore the anchor to the old position
                    int index = -1;
                    if (anchorPositions.containsKey(descriptor))
                        index = anchorPositions.get(descriptor);

                    addRepresentativeAnchor(descriptor, descriptor.getRepresentativeAnchor(toolWindowBarContainer),
                                            index);
                }
            }

            // Choose the component...
            Component component = (visible) ? descriptor.getComponent() : null;
            if (component != null)
                component = descriptor.getToolWindowPanel();

            if (component == null || descriptor.getDividerLocation() > 0 && splitPane.getDividerSize() != 0) {
                synchronized (splitAnimation) {
                    if (splitAnimation.isAnimating())
                        splitAnimation.stop();
                }

                if (manager.getShowingGroup() == null) {
                    descriptor.setDividerLocation(getSplitDividerLocation());
                } else {
                    updateToolDividerLocation(descriptor.getDividerLocation());
                }
            }

            if (component == null && descriptor.getToolWindow().isVisible())
                return;

            // Populate the dividerLocation.

            // Ensure the minimun size
            int dividerLocation = Math.max(descriptor.getDividerLocation(),
                                           descriptor.getDockedTypeDescriptor().getMinimumDockLength());

            if (getSplitDividerLocation() != 0) {

                // If a toolwindow is already visible on the anchor, peek the dividerLocation from the split pane...  
                for (ToolWindow toolWindow : manager.getToolsByAnchor(anchor)) {
                    if (descriptor.getToolWindow() != toolWindow &&
                        toolWindow.isVisible() &&
                        toolWindow.getType() == ToolWindowType.DOCKED) {

                        dividerLocation = getSplitDividerLocation();
                        break;
                    }
                }
                // TODO: is this necessary?
//                updateToolDividerLocation(dividerLocation);
            }

//            if (getSplitDividerLocation() != 0)
//                dividerLocation = getSplitDividerLocation();
//            System.out.println("dividerLocation(" + anchor + ") : " + dividerLocation);

            // Add the component...

            boolean animate = true;
            Component splitPaneContent = getSplitPaneContent();

            if (splitPaneContent != null) {
                // There are one or more tools already visible...
                if (splitPaneContent instanceof MultiSplitDockableContainer) {
                    // These tools are aggregated...
                    MultiSplitDockableContainer dockableContainer = (MultiSplitDockableContainer) splitPaneContent;

                    if (component == null) {
                        // Remove the tool..
                        dockableContainer.removeDockable(descriptor.getToolWindow(),
                                                         !visible && !manager.isShowingGroup());
                        animate = false;
                        if (dockableContainer.isEmpty()) {
                            // No more tools, hide the pane
                            animate = true;
                            component = null;
                        } else if (dockableContainer.getDockableCount() == 1) {
                            // Remove last dockable on container...
                            animate = false;

                            Dockable dockable = dockableContainer.getDockable();
                            component = dockableContainer.getDockableComponent();
                            dockableContainer.removeDockable(dockable, false);

                            // Put the component on the new location...
                            int temp = getSplitDividerLocation();

                            setSplitPaneContent(component);
                            setSplitDividerLocation(temp);
                        }
                    } else {
                        if (manager.getShowingGroup() != null) {
                            dockableContainer.addDockable(descriptor.getToolWindow(),
                                                          component,
                                                          aggregationOnTool,
                                                          -1, aggregationPosition);
                        } else
                            setSplitPaneContent(component);
                    }
                } else if (manager.getShowingGroup() != null && component != null) {
                    multiSplitDockableContainer.clear();
                    if (shiftShow)
                        multiSplitDockableContainer.addDockable(
                                (Dockable) ((JComponent) splitPaneContent).getClientProperty(ToolWindow.class),
                                splitPaneContent,
                                null,
                                -1, AggregationPosition.DEFAULT);
                    multiSplitDockableContainer.addDockable(descriptor.getToolWindow(),
                                                            component,
                                                            null,
                                                            -1, aggregationPosition);

                    setSplitPaneContent(multiSplitDockableContainer);
                } else if (component != null)
                    setSplitPaneContent(component);
            } else {
                if (manager.getShowingGroup() != null && component != null) {
                    multiSplitDockableContainer.clear();
                    multiSplitDockableContainer.addDockable(descriptor.getToolWindow(),
                                                            component,
                                                            null,
                                                            -1, AggregationPosition.DEFAULT);

                    setSplitPaneContent(multiSplitDockableContainer);
                } else if (component != null)
                    setSplitPaneContent(component);
            }

            if (animate) {
                if (component != null) {
                    splitPane.setDividerSize(getDividerSize());
                    if (manager.getShowingGroup() == null &&
                        descriptor.getTypeDescriptor(ToolWindowType.DOCKED).isAnimating()) {

                        splitAnimation.show(dividerLocation);
                    } else {
                        if (dividerLocation != 0) {
                            vsdValueAdjusting = true;
                            try {
                                setSplitDividerLocation(dividerLocation);
                            } finally {
                                vsdValueAdjusting = false;
                            }
                            SwingUtil.repaintNow(splitPane);
                        }
                    }
                } else {
                    splitPane.setDividerSize(0);

                    setSplitPaneContent(null);

                    vsdValueAdjusting = true;
                    try {
                        setSplitDividerLocation(0);
                        SwingUtil.repaintNow(splitPane);
                    } finally {
                        vsdValueAdjusting = false;
                    }

//                    splitAnimation.hide(dividerLocation);
                }
            } else {
                setSplitDividerLocation(dividerLocation);
                SwingUtil.repaint(splitPane);
            }

//            if (visible)
                descriptor.disableFullExternalFocusValueAdjustingLater();
        }

        public void disabledContentManagerPropertyChange(PropertyChangeEvent evt) {
            MultiSplitDockableContainer managerDockableContainer = (MultiSplitDockableContainer) ((DockableDropPanel) manager.getMainContent()).getComponent();

            ToolWindowDescriptor descriptor = manager.getDescriptor((ToolWindow) evt.getSource());
            boolean visible = (Boolean) evt.getNewValue();

            ToolWindowDescriptor.fullExternalFocusValueAdjusting = true;

            try {
                if (visible) {
                    AggregationPosition aggregationPosition;
                    ToolWindow aggregationOnTool = null;

                    if (evt instanceof UserPropertyChangeEvent) {
                        // Load parameter
                        UserPropertyChangeEvent upce = (UserPropertyChangeEvent) evt;
                        Object[] args = ((Object[]) upce.getUserObject());

                        aggregationPosition = (AggregationPosition) args[1];
                        aggregationOnTool = (ToolWindow) args[2];
                    } else
                        aggregationPosition = AggregationPosition.valueOf(anchor.toString());

    //                if (aggregationOnTool == null)
    //                    aggregationPosition = AggregationPosition.valueOf(anchor.toString());


                    managerDockableContainer.addDockable(descriptor.getToolWindow(),
                                                         descriptor.getToolWindowPanel(),
                                                         aggregationOnTool,
                                                         -1,
                                                         aggregationPosition
                    );
                } else
                    managerDockableContainer.removeDockable(descriptor.getToolWindow());

                SwingUtil.repaint(managerDockableContainer);
            } finally {
                descriptor.disableFullExternalFocusValueAdjustingLater();
            }
        }


        protected void setSplitPaneContent(Component content) {
            vsdValueAdjusting = true;
            try {
                if (content != null && splitPane.getDividerLocation() == 0)
                    splitPane.setDividerLocation(1);

                Component splitPaneContent = content;

                if (content != null) {
                    dockableDropPanel.setComponent(content);
                    splitPaneContent = dockableDropPanel;
                } else {
                    dockableDropPanel.resetComponent();
                }

                switch (anchor) {
                    case LEFT:
                        splitPane.setLeftComponent(splitPaneContent);
                        break;
                    case RIGHT:
                        splitPane.setRightComponent(splitPaneContent);
                        if (splitPaneContent != null)
                            splitPane.setDividerLocation(splitPane.getWidth());
                        break;
                    case BOTTOM:
                        splitPane.setBottomComponent(splitPaneContent);
                        if (splitPaneContent != null)
                            splitPane.setDividerLocation(splitPane.getHeight());
                        break;
                    case TOP:
                        splitPane.setTopComponent(splitPaneContent);
                        break;
                }

                if (splitPaneContent != null)
                    content.setVisible(true);
            } finally {
                vsdValueAdjusting = false;
            }
        }

        protected void updateToolDividerLocation(int divederLocation) {
            for (ToolWindow toolWindow : manager.getToolsByAnchor(anchor)) {
                if (toolWindow.isVisible())
                    manager.getDescriptor(toolWindow).setDividerLocation(divederLocation);
            }
        }


        public class SplitAnimation extends AbstractAnimation {
            protected int dividerLocation;
            protected int sheetLen;

            public SplitAnimation() {
                super(60f);
            }

            protected float onAnimating(float animationPercent) {
                int animatingHeight;

                Direction direction = getAnimationDirection();
                if (direction == Direction.INCOMING)
                    animatingHeight = (int) (animationPercent * sheetLen);
                else
                    animatingHeight = (int) ((1.0f - animationPercent) * sheetLen);

//                System.out.println("animatingHeight = " + animatingHeight);

                switch (anchor) {
                    case LEFT:
                    case TOP:
                        if (direction == Direction.INCOMING) {
                            if (splitPane.getDividerLocation() <= animatingHeight)
                                splitPane.setDividerLocation(animatingHeight);
                        } else
                            splitPane.setDividerLocation(animatingHeight);
                        break;
                    case RIGHT:
                        splitPane.setDividerLocation(splitPane.getWidth() - animatingHeight);
                        break;
                    case BOTTOM:
                        splitPane.setDividerLocation(splitPane.getHeight() - animatingHeight);
                        break;

                }
                return animationPercent;
            }

            protected void onFinishAnimation() {
                if (splitPane.getDividerSize() == 0) {
                    setSplitPaneContent(null);
                } else {
                    if (getAnimationDirection() == Direction.OUTGOING) {
                        vsdValueAdjusting = true;
                        setSplitDividerLocation(0);
                        vsdValueAdjusting = false;
                    } else {
                        setSplitDividerLocation(sheetLen);
                        SwingUtil.repaintNow(splitPane);
                    }
                }
            }

            protected void onHide(Object... params) {
                this.dividerLocation = (Integer) params[0];
            }

            protected void onShow(Object... params) {
                this.dividerLocation = (Integer) params[0];
            }

            protected void onStartAnimation(Direction direction) {
                sheetLen = dividerLocation;
            }

            protected Direction chooseFinishDirection(Type type) {
                return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
            }
        }

    }

    public class VisibleSlidingListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = manager.getDescriptor((ToolWindow) evt.getSource());
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            SlidingContainer container = (SlidingContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.SLIDING);

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.setVisible(visible, getContainer());
        }
    }

    public class VisibleFloatingListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = manager.getDescriptor((ToolWindow) evt.getSource());
            boolean visible = (Boolean) evt.getNewValue();

            if (evt instanceof UserPropertyChangeEvent) {
                Object[] params = (Object[]) ((UserPropertyChangeEvent) evt).getUserObject();

                // pick referenceAggregationTool from params
                ToolWindow referenceAggregationTool = (ToolWindow) params[3];
                ToolWindowDescriptor referenceAggregationDescriptor = null;
                if (referenceAggregationTool != null)
                    referenceAggregationDescriptor = manager.getDescriptor(referenceAggregationTool);

                // pick aggregationOnTool from params
                ToolWindow aggregationOnTool = (ToolWindow) params[2];
                ToolWindowDescriptor aggregationOnDescriptor = null;
                if (aggregationOnTool != null)
                    aggregationOnDescriptor = manager.getDescriptor(aggregationOnTool);

                if (referenceAggregationDescriptor == null)
                    referenceAggregationDescriptor = aggregationOnDescriptor;

                Component content = (visible) ? toolWindowDescriptor.getToolWindowPanel() : null;
                if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                    return;

                FloatingContainer container = (FloatingContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.FLOATING);
                container.setVisible(referenceAggregationDescriptor,
                                     content,
                                     aggregationOnDescriptor,
                                     (AggregationPosition) params[1]);
            } else {
                Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
                FloatingContainer container = (FloatingContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.FLOATING);

                if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                    return;

                container.setVisible(visible);
            }
        }

    }

    public class VisibleFloatingFreeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = manager.getDescriptor((ToolWindow) evt.getSource());
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            FloatingContainer container = (FloatingContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.FLOATING_FREE);

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.setVisible(visible);
        }
    }

    public class VisibleFloatingLiveListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = manager.getDescriptor((ToolWindow) evt.getSource());

            boolean visible = (Boolean) evt.getNewValue();

            if (evt instanceof UserPropertyChangeEvent) {
                Object[] params = (Object[]) ((UserPropertyChangeEvent) evt).getUserObject();

                // pick referenceAggregationTool from params
                ToolWindow referenceAggregationTool = (ToolWindow) params[3];
                ToolWindowDescriptor referenceAggregationDescriptor = null;
                if (referenceAggregationTool != null)
                    referenceAggregationDescriptor = manager.getDescriptor(referenceAggregationTool);

                // pick aggregationOnTool from params
                ToolWindow aggregationOnTool = (ToolWindow) params[2];
                ToolWindowDescriptor aggregationOnDescriptor = null;
                if (aggregationOnTool != null)
                    aggregationOnDescriptor = manager.getDescriptor(aggregationOnTool);

                if (referenceAggregationDescriptor == null)
                    referenceAggregationDescriptor = aggregationOnDescriptor;

                Component component = (visible) ? toolWindowDescriptor.getToolWindowPanel() : null;
                if (component == null && toolWindowDescriptor.getToolWindow().isVisible())
                    return;

                FloatingLiveContainer container = (FloatingLiveContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.FLOATING_LIVE);
                container.setVisible(referenceAggregationDescriptor,
                                     component,
                                     aggregationOnDescriptor,
                                     (AggregationPosition) params[1]);
            } else {
                Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
                FloatingLiveContainer container = (FloatingLiveContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.FLOATING_LIVE);

                if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                    return;

                container.setVisible(visible);
            }
        }
    }

    public class IndexListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = manager.getDescriptor((ToolWindow) evt.getSource());

            JLabel representativeAnchor = descriptor.getRepresentativeAnchor();
            if (representativeAnchor != null) {
                TableLayoutConstraints constraints = getRepresentativeAnchorConstraints(representativeAnchor);

                if (horizontal) {
                    int width = representativeAnchor.getPreferredSize().width + 6;

                    toolWindowBarContainerLayout.setColumn(constraints.col1, width);
                } else {
                    int height = Math.max(representativeAnchor.getPreferredSize().height,
                                          representativeAnchor.getSize().height);
                    toolWindowBarContainerLayout.setRow(constraints.row1, height);
                }

                SwingUtil.repaint(toolWindowBarContainer);
            }
        }
    }

    public class IconListener extends IndexListener {
    }

    public class TitleListener extends IndexListener {
    }

    public class BarLengthListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            int oldValue = (Integer) evt.getOldValue() - 4;
            int newValue = (Integer) evt.getNewValue() - 4;

            switch (anchor) {
                case LEFT:
                case RIGHT:
                    for (int i = 0, size = toolWindowBarContainerLayout.getNumColumn(); i < size; i++) {
                        if (toolWindowBarContainerLayout.getColumn(i) == oldValue)
                            toolWindowBarContainerLayout.setColumn(i, newValue);
                    }
                    break;
                case TOP:
                case BOTTOM:
                    for (int i = 0, size = toolWindowBarContainerLayout.getNumRow(); i < size; i++) {
                        if (toolWindowBarContainerLayout.getRow(i) == oldValue)
                            toolWindowBarContainerLayout.setRow(i, newValue);
                    }
                    break;
            }

            SwingUtil.repaint(toolWindowBarContainer);
        }

    }

    public class DragSupportListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if ("startDrag".equals(evt.getPropertyName())) {
                Component cmp = SwingUtil.getComponentWhoseParentIs((Component) evt.getSource(), toolWindowBarContainer);
                TableLayout layout = (TableLayout) toolWindowBarContainer.getLayout();

                switch (anchor) {
                    case LEFT:
                    case RIGHT:
                        dragComponentLength = cmp.getHeight();
                        layout.setRow(layout.getConstraints(cmp).row1, 0);
                        break;
                    case TOP:
                    case BOTTOM:
                        dragComponentLength = cmp.getWidth();
                        layout.setColumn(layout.getConstraints(cmp).col1, 0);
                        break;
                }
                SwingUtil.repaint(toolWindowBarContainer);
            } else if ("endDrag".equals(evt.getPropertyName())) {
                TableLayout layout = (TableLayout) toolWindowBarContainer.getLayout();
                TableLayoutConstraints constraints = getRepresentativeAnchorConstraints((Component) evt.getSource());

                switch (anchor) {
                    case LEFT:
                    case RIGHT:
                        if (constraints != null)
                            layout.setRow(constraints.row1, dragComponentLength);

                        break;
                    case TOP:
                    case BOTTOM:
                        if (constraints != null)
                            layout.setColumn(constraints.col1, dragComponentLength);

                        break;
                }

                if (constraints != null) {
                    SwingUtil.repaint(toolWindowBarContainer);
                    manager.syncPanel(anchor);
                }
            } else
                throw new IllegalArgumentException("Invalid property name. [property : " + evt.getPropertyName() + "]");
        }
    }

    public class MaximizedListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = manager.getDescriptor((ToolWindow) evt.getSource());

            if (descriptor.getToolWindow().getType() == ToolWindowType.DOCKED) {

                if ((Boolean) evt.getNewValue()) {
                    descriptor.setTempDivederLocation(getSplitDividerLocation());

                    ToolWindow maximizedTool = descriptor.getToolWindow();
                    for (ToolWindow tool : manager.getToolWindows())
                        if (tool != maximizedTool &&
                            tool.getType() != ToolWindowType.FLOATING &&
                            tool.getType() != ToolWindowType.FLOATING_FREE &&
                            tool.getType() != ToolWindowType.EXTERN)
                            tool.setVisible(false);

                    setSplitDividerLocation(-1);
                    SwingUtil.repaintNow(splitPane);
                } else {
                    setSplitDividerLocation(descriptor.getTempDivederLocation());
                    SwingUtil.repaintNow(splitPane);
                }

            }
        }

    }

    public class DividerSizeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (splitPane.getDividerSize() > 0)
                splitPane.setDividerSize((Integer) evt.getNewValue());
        }

    }


    public class DockedDockableDropPanel extends DockableDropPanel {

        public DockedDockableDropPanel() {
            super(ToolWindow.class, Content.class);
        }


        public boolean dragStart(Transferable transferable, int action) {
            try {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {

                    if (transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_MANAGER).equals(System.identityHashCode(manager))) {
                        if (action == DnDConstants.ACTION_MOVE &&
                            (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF) ||
                             transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF) ||
                             transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)))

                            return super.dragStart(transferable, action);
                    }
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean drop(Transferable transferable) {
            if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF)) {
                try {
                    ToolWindow toolWindow = manager.getToolWindow(
                            transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_ID_DF)
                    );

                    if (toolWindow != null) {
                        // Move tool to another anchor

                        // Chech if it was a tab
                        if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)) {
                            // Remove from tab
                            ToolWindowTab tab = (ToolWindowTab) manager.lookupDockable(
                                    transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)
                            );
                            tab.getOwner().removeToolWindowTab(tab);
                            toolWindow = (ToolWindow) tab.getDockableDelegator();
                        }

                        ToolWindow onToolWindow = (ToolWindow) getOnDockable();
                        int anchorIndex = (onToolWindow != null) ? onToolWindow.getAnchorIndex() : -1;

                        if (toolWindow == onToolWindow)
                            return false;

                        boolean oldAggregateMode = toolWindow.isAggregateMode();
                        toolWindow.setAggregateMode(true);
                        try {
                            ToolWindowAnchor dragAnchor = getOnAnchor();

                            if (dragAnchor == null && onToolWindow != null && toolWindow != onToolWindow) {
                                if (!SwingUtil.getBoolean("drag.toolwindow.asTab", true)) {
                                    // Choose drag anchor ...
                                    switch (onToolWindow.getAnchor()) {
                                        case LEFT:
                                        case RIGHT:
                                            dragAnchor = ToolWindowAnchor.TOP;
                                            break;
                                        case TOP:
                                        case BOTTOM:
                                            dragAnchor = ToolWindowAnchor.LEFT;
                                            break;
                                    }
                                }
                            }

                            if (dragAnchor != null) {
                                switch (dragAnchor) {
                                    case LEFT:
                                        if (onToolWindow != null) {
                                            toolWindow.setAnchor(anchor, anchorIndex != -1 ? anchorIndex - 1 : -1);
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.LEFT);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.setAnchor(anchor, 0);
                                                toolWindow.aggregate(AggregationPosition.LEFT);
                                            }
                                        }
                                        break;
                                    case RIGHT:
                                        if (onToolWindow != null) {
                                            toolWindow.setAnchor(anchor, anchorIndex != -1 ? anchorIndex + 1 : -1);
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.setAnchor(anchor);
                                                toolWindow.aggregate(AggregationPosition.RIGHT);
                                            }
                                        }
                                        break;
                                    case BOTTOM:
                                        if (onToolWindow != null) {
                                            toolWindow.setAnchor(anchor, anchorIndex != -1 ? anchorIndex + 1 : -1);
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.setAnchor(anchor);
                                                toolWindow.aggregate(AggregationPosition.BOTTOM);
                                            }
                                        }
                                        break;
                                    case TOP:
                                        if (onToolWindow != null) {
                                            toolWindow.setAnchor(anchor, anchorIndex != -1 ? anchorIndex - 1 : -1);
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.setAnchor(anchor, 0);
                                                toolWindow.aggregate(AggregationPosition.TOP);
                                            }
                                        }
                                        break;
                                }
                                toolWindow.setActive(true);
                            } else {
                                if (onToolWindow != null && toolWindow != onToolWindow) {
                                    onToolWindow.addToolWindowTab(toolWindow).setSelected(true);
                                    onToolWindow.setActive(true);
                                } else {
                                    toolWindow.aggregate();
                                    toolWindow.setActive(true);
                                }
                            }
                        } finally {
                            toolWindow.setAggregateMode(oldAggregateMode);
                        }

                        return true;
                    } else
                        return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)) {
                try {
                    Content content = manager.getContentManager().getContent(
                            transferable.getTransferData(MyDoggyTransferable.CONTENT_ID_DF)
                    );

                    if (content != null) {
                        if (content.getDockableDelegator() != null) {
                            manager.getContentManager().removeContent(content);

                            Dockable delegator = content.getDockableDelegator();

                            if (delegator instanceof ToolWindow) {
                                ToolWindow toolWindow = (ToolWindow) delegator;

                                ToolWindow onToolWindow = (ToolWindow) getOnDockable();
                                int anchorIndex = (onToolWindow != null) ? onToolWindow.getAnchorIndex() : -1;
                                ToolWindowAnchor dragAnchor = getOnAnchor();

                                if (toolWindow == onToolWindow)
                                    return false;

                                boolean oldAggregateMode = toolWindow.isAggregateMode();
                                toolWindow.setAggregateMode(true);
                                try {
                                    toolWindow.setAnchor(anchor, anchorIndex);

                                    if (dragAnchor != null) {
                                        switch (dragAnchor) {
                                            case LEFT:
                                                if (onToolWindow != null)
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.LEFT);
                                                else
                                                    toolWindow.aggregate(AggregationPosition.LEFT);
                                                break;
                                            case RIGHT:
                                                if (onToolWindow != null)
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                                else
                                                    toolWindow.aggregate(AggregationPosition.RIGHT);
                                                break;
                                            case BOTTOM:
                                                if (onToolWindow != null)
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                                else
                                                    toolWindow.aggregate(AggregationPosition.BOTTOM);
                                                break;
                                            case TOP:
                                                if (onToolWindow != null)
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                                else
                                                    toolWindow.aggregate(AggregationPosition.TOP);
                                                break;
                                        }
                                        toolWindow.setActive(true);
                                    } else {
                                        if (onToolWindow != null) {
                                            onToolWindow.addToolWindowTab(toolWindow).setSelected(true);
                                            onToolWindow.setActive(true);
                                        } else {
                                            toolWindow.aggregate();
                                            toolWindow.setActive(true);
                                        }
                                    }
                                } finally {
                                    toolWindow.setAggregateMode(oldAggregateMode);
                                }
                                return true;
                            }
                        } else
                            return false;
                    } else
                        return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return false;
        }

        protected boolean checkCondition(ToolWindow toolWindow) {
/*            if (toolWindow.getAnchor() != anchor)
                return true;

            int visibleNum = 0;
            boolean flag = false;
            for (ToolWindow tool : manager.getToolsByAnchor(anchor)) {
                if (tool.isVisible())
                    visibleNum++;
                if (tool == toolWindow)
                    flag = true;
            }

            return (!flag || visibleNum != 1);*/
            return true;

        }
    }

    public class RepresentativeAnchorWrapper extends JPanel {
        protected DockableDescriptor dockableDescriptor;
        protected Component representativeAnchor;


        public RepresentativeAnchorWrapper(DockableDescriptor dockableDescriptor, Component representativeAnchor) {
            this.dockableDescriptor = dockableDescriptor;
            this.representativeAnchor = representativeAnchor;

            setOpaque(false);
            setLayout(new ExtendedTableLayout(new double[][]{{-1},{-1}}));
            putClientProperty("ra", representativeAnchor);
            add(representativeAnchor, "0,0,FULL,FULL");
        }


        public DockableDescriptor getDockableDescriptor() {
            return dockableDescriptor;
        }

        public Component getRepresentativeAnchor() {
            return representativeAnchor;
        }
    }
}


