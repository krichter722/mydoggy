package org.noos.xing.mydoggy.plaf;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToolsOnBarMouseListener;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowBarDropTarget;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowDropTarget;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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

    // Bar Components
    protected JToolScrollBar toolScrollBar;
    protected JPanel toolWindowBarContainer;
    protected TableLayout toolWindowBarContainerLayout;

    protected JSplitPane splitPane;
    protected MultiSplitDockableContainer multiSplitDockableContainer;
    protected ContentPanel contentPanel;
    protected JPopupMenu popupMenu;

    protected int length;
    protected int availableTools;
    protected int orientation;
    protected boolean horizontal;

    protected PropertyChangeSupport propertyChangeSupport;

    protected boolean tempShowed;

    boolean valueAdjusting = false;


    public MyDoggyToolWindowBar(MyDoggyToolWindowManager manager, JSplitPane splitPane, ToolWindowAnchor anchor) {
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
        return multiSplitDockableContainer.getModel();
    }

    public void setLayout(Object layout) {
        multiSplitDockableContainer.setModel((MultiSplitLayout.Node) layout);
    }

    public Container getContainer() {
        return toolScrollBar;
    }

    public JSplitPane getSplitPane() {
        return splitPane;
    }

    public void ensureVisible(final Component component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                toolScrollBar.ensureVisible(component);
            }
        });
    }

    public int getAvailableTools() {
        return availableTools;
    }

    public boolean isTempShowed() {
        return tempShowed;
    }

    public void setTempShowed(boolean tempShowed) {
        boolean old = this.tempShowed;
        this.tempShowed = tempShowed;

        manager.syncPanel(anchor);

        firePropertyChangeEvent("tempShowed", old, tempShowed);
    }

    public int getRepresentativeAnchorIndex(Component representativeAnchor) {
        TableLayoutConstraints constraints = toolWindowBarContainerLayout.getConstraints(representativeAnchor);
        if (constraints == null)
            return -1;

        if (horizontal)
            return (constraints.col1 / 2) - 1;
        else
            return (constraints.row1 / 2) - 1;
    }

    public void deactiveTool(ToolWindow toolWindow) {
        valueAdjusting = true;
        toolWindow.setActive(false);
        valueAdjusting = false;
    }

    public void hideTool(ToolWindow toolWindow) {
        valueAdjusting = true;
        toolWindow.setVisible(false);
        valueAdjusting = false;
    }

    public int getSize() {
        return (getAvailableTools() > 0) ? getLength() : 0;
    }


    protected void initComponents() {
        splitPane.setName(anchor.toString());
//        splitPane.setFocusCycleRoot(true);

        contentPanel = new ContentPanel("toolWindow.container.");
        contentPanel.setDropTarget(new ToolWindowDropTarget(contentPanel, manager, anchor));

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

        toolScrollBar = new JToolScrollBar(manager.getResourceManager(), orientation, toolWindowBarContainer);

        toolWindowBarContainer.setDropTarget(new ToolWindowBarDropTarget(manager, anchor, toolWindowBarContainer));
        toolWindowBarContainer.addMouseListener(new ToolsOnBarMouseListener(manager, this));
    }

    protected void initListeners() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        AvailableListener availableListener = new AvailableListener();
        propertyChangeSupport.addPropertyChangeListener("available", availableListener);
        propertyChangeSupport.addPropertyChangeListener("representativeAnchorButtonVisible", availableListener);
        propertyChangeSupport.addPropertyChangeListener("showUnavailableTools", new ShowUnavailableToolsListener());

        propertyChangeSupport.addPropertyChangeListener("visible.before", new VisibleBeforeListener());
        propertyChangeSupport.addPropertyChangeListener("visible.DOCKED", new VisibleDockedListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING", new VisibleFloatingListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING_FREE", new VisibleFloatingFreeListener());
        propertyChangeSupport.addPropertyChangeListener("visible.SLIDING", new VisibleSlidingListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING_LIVE", new VisibleFloatingLiveListener());
        propertyChangeSupport.addPropertyChangeListener("visible", new VisibleListener());

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

        DragListener dragListener = new DragListener();
        propertyChangeSupport.addPropertyChangeListener("startDrag", dragListener);
        propertyChangeSupport.addPropertyChangeListener("endDrag", dragListener);
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

    protected void setSplitDividerLocation(int divederLocation) {
        if (divederLocation == -1) {
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
                    splitPane.setDividerLocation(divederLocation);
                    break;
                case RIGHT:
                    splitPane.setDividerLocation(Math.abs(splitPane.getWidth() - divederLocation));
                    break;
                case BOTTOM:
                    splitPane.setDividerLocation(Math.abs(splitPane.getHeight() - divederLocation));
                    break;
            }
    }

    protected Component getSplitPaneContent() {
        return contentPanel.getComponent();
    }

    protected void addRepresentativeAnchor(Component representativeAnchor, int index) {
        availableTools++;
        if (horizontal) {
            toolWindowBarContainerLayout.insertColumn(toolWindowBarContainerLayout.getNumColumn(),
                                                      toolWindowBarContainerLayout.getNumColumn() > 0 ? 5 : 1);
            toolWindowBarContainerLayout.insertColumn(toolWindowBarContainerLayout.getNumColumn(),
                                                      TableLayout.PREFERRED);

            // validate index...
            int finalCol = (index * 2 + 2);
            if (finalCol >= toolWindowBarContainerLayout.getNumColumn())
                index = -1;

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

                toolWindowBarContainerLayout.setColumn(finalCol,
                                                       TableLayout.PREFERRED);

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
        SwingUtil.repaint(toolScrollBar);
    }

    protected void removeRepresentativeAnchor(Component representativeAnchor,
                                              DockableDescriptor descriptor) {
        if (representativeAnchor == null)
            return;

        int toDelete;
        TableLayoutConstraints constraints = toolWindowBarContainerLayout.getConstraints(representativeAnchor);
        if (constraints == null)
            return;

        // Remove
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

        SwingUtil.repaint(toolScrollBar);

        descriptor.resetRepresentativeAnchor();
    }


    public class AvailableListener implements PropertyChangeListener {
        protected Map<DockableDescriptor, Integer> rabsPositions;

        public AvailableListener() {
            rabsPositions = new Hashtable<DockableDescriptor, Integer>();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            DockableDescriptor descriptor = (DockableDescriptor) evt.getSource();

            if (isDockableDescriptorValid(descriptor)) {
                boolean rabsEvent = evt.getPropertyName().equals("representativeAnchorButtonVisible");

                if (!rabsEvent) {
                    if (descriptor.getDockableType() == DockableDescriptor.DockableType.TOOL_WINDOW &&
                        !((ToolWindow) descriptor.getDockable()).isRepresentativeAnchorButtonVisible())
                        return;
                }


                boolean oldAvailable = (Boolean) evt.getOldValue();
                boolean newAvailable = (Boolean) evt.getNewValue();

                boolean repaint = false;

                Component representativeAnchor;
                if (oldAvailable && !newAvailable) {
                    boolean flag = false;
                    if (!rabsEvent) {
                        assert evt instanceof UserPropertyChangeEvent;
                        Object[] params = (Object[]) ((UserPropertyChangeEvent) evt).getUserObject();

                        // true -> false
                        flag = (manager.getToolWindowManagerDescriptor().isShowUnavailableTools() &&
                                descriptor.getAnchorIndex() != -1);

                        if (params[1] == Boolean.TRUE)
                            flag = false;
                    }

                    representativeAnchor = descriptor.getRepresentativeAnchor();

                    if (representativeAnchor != null) {
                        if (!flag) {
                            if (rabsEvent)
                                rabsPositions.put(descriptor, descriptor.getAnchorIndex());

                            removeRepresentativeAnchor(representativeAnchor, descriptor);
                        } else
                            descriptor.updateRepresentativeAnchor();

                        repaint = true;
                    }
                } else if (!oldAvailable && newAvailable) {
                    // false -> true
                    Object[] params = null;
                    boolean flag = false;
                    if (!rabsEvent) {
                        assert evt instanceof UserPropertyChangeEvent;
                        params = (Object[]) ((UserPropertyChangeEvent) evt).getUserObject();

                        flag = (manager.getToolWindowManagerDescriptor().isShowUnavailableTools() &&
                                descriptor.getAnchorIndex() != -1);

                        if (params[1] == Boolean.TRUE)
                            flag = false;
                    }

                    representativeAnchor = descriptor.getRepresentativeAnchor(toolWindowBarContainer);
                    if (!flag) {
                        if (rabsEvent) {
                            int index = -1;
                            if (rabsEvent && rabsPositions.containsKey(descriptor))
                                index = rabsPositions.get(descriptor);

                            addRepresentativeAnchor(representativeAnchor, index);
                        } else
                            addRepresentativeAnchor(representativeAnchor, (Integer) params[0]);
                    } else
                        descriptor.updateRepresentativeAnchor();

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
                        addRepresentativeAnchor(manager.getDescriptor(tool).getRepresentativeAnchor(toolWindowBarContainer),
                                                -1);
                    }
                }
            } else {
                for (ToolWindow tool : manager.getToolsByAnchor(anchor)) {
                    if (!tool.isAvailable() && tool.getType() != ToolWindowType.FLOATING_FREE) {
                        ToolWindowDescriptor descriptor = manager.getDescriptor(tool);
                        removeRepresentativeAnchor(descriptor.getRepresentativeAnchor(toolWindowBarContainer),
                                                   descriptor);
                    }
                }
            }
        }
    }

    public class ActiveBeforeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
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
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();

            if (evt.getOldValue() == ToolWindowType.FLOATING_FREE) {
                addRepresentativeAnchor(toolWindowDescriptor.getRepresentativeAnchor(toolWindowBarContainer), -1);
                ensureVisible(toolWindowDescriptor.getRepresentativeAnchor());

                SwingUtil.repaint(toolWindowBarContainer);
            } else
            if ((evt.getNewValue() == ToolWindowType.FLOATING_FREE || evt.getNewValue() == ToolWindowType.EXTERN) &&
                toolWindowDescriptor.getRepresentativeAnchor() != null) {

                removeRepresentativeAnchor(toolWindowDescriptor.getRepresentativeAnchor(), toolWindowDescriptor);
                SwingUtil.repaint(toolWindowBarContainer);
            }

        }
    }


    public class VisibleBeforeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
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
            ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
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
        protected Map<ToolWindowDescriptor, Integer> poss;

        public VisibleDockedListener() {
            poss = new HashMap<ToolWindowDescriptor, Integer>();

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
            boolean shiftShow = false;
            AggregationPosition aggregationPosition = AggregationPosition.DEFAULT;
            ToolWindow aggregationOnTool = null;

            if (evt instanceof UserPropertyChangeEvent) {
                UserPropertyChangeEvent upce = (UserPropertyChangeEvent) evt;
                Object[] args = ((Object[]) upce.getUserObject());

                shiftShow = (Boolean) args[0];
                aggregationPosition = (AggregationPosition) args[1];
                aggregationOnTool = (ToolWindow) args[2];
            }

            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            if (descriptor.getDockedTypeDescriptor().isHideRepresentativeButtonOnVisible()) {
                if (visible) {
                    poss.put(descriptor, descriptor.getAnchorIndex());
                    removeRepresentativeAnchor(descriptor.getRepresentativeAnchor(), descriptor);
                } else {
                    int index = -1;
                    if (poss.containsKey(descriptor))
                        index = poss.get(descriptor);

                    addRepresentativeAnchor(descriptor.getRepresentativeAnchor(toolWindowBarContainer),
                                            index);
                }
            }

            Component content = (visible) ? descriptor.getComponent() : null;
            if (content != null)
                content = descriptor.getToolWindowPanel();

            if (content == null || descriptor.getDividerLocation() > 0 && splitPane.getDividerSize() != 0) {
                synchronized (splitAnimation) {
                    if (splitAnimation.isAnimating())
                        splitAnimation.stop();
                }

                if (manager.getShowingGroup() == null) {
                    descriptor.setDividerLocation(getSplitDividerLocation());
                } else {
                    int divederLocation = descriptor.getDividerLocation();
                    for (ToolWindow toolWindow : manager.getToolsByAnchor(anchor)) {
                        if (toolWindow.isVisible())
                            manager.getDescriptor(toolWindow).setDividerLocation(divederLocation);
                    }
                }
            }

            if (content == null && descriptor.getToolWindow().isVisible())
                return;

            int divederLocation = Math.max(descriptor.getDividerLocation(),
                                           descriptor.getDockedTypeDescriptor().getMinimumDockLength());

            if (getSplitDividerLocation() != 0) {
                for (ToolWindow toolWindow : manager.getToolsByAnchor(anchor)) {
                    if (descriptor.getToolWindow() != toolWindow && toolWindow.isVisible() &&
                        toolWindow.getType() == ToolWindowType.DOCKED) {
                        divederLocation = getSplitDividerLocation();
                        break;
                    }
                }
            }

//            if (getSplitDividerLocation() != 0)
//                divederLocation = getSplitDividerLocation();
//            System.out.println("divederLocation(" + anchor + ") : " + divederLocation);

            Component splitPaneContent = getSplitPaneContent();
            boolean animate = true;
            if (splitPaneContent != null) {
                if (splitPaneContent instanceof MultiSplitDockableContainer) {
                    MultiSplitDockableContainer multiSplitDockableContainer = (MultiSplitDockableContainer) splitPaneContent;

                    if (content == null) {
                        multiSplitDockableContainer.removeDockable(descriptor.getToolWindow(),
                                                                   !visible && !manager.isShowingGroup());
                        animate = false;

                        if (multiSplitDockableContainer.isEmpty()) {
                            animate = true;
                            content = null;
                        } else if (multiSplitDockableContainer.getContentCount() == 1) {
                            // Remove last dockable on container...
                            animate = false;

                            MultiSplitDockableContainer.DockableEntry dockableEntry = multiSplitDockableContainer.getContents().get(0);
                            content = multiSplitDockableContainer.getContents().get(0).getComponent();
                            multiSplitDockableContainer.removeDockable(dockableEntry.getDockable(),
                                                                       !visible && !manager.isShowingGroup());

                            // Put the component on the new location...
                            int temp = getSplitDividerLocation();
                            setSplitPaneContent(content);
                            setSplitDividerLocation(temp);
                        }
                    } else {
                        if (manager.getShowingGroup() != null) {
                            multiSplitDockableContainer.addDockable(descriptor.getToolWindow(),
                                                                    content,
                                                                    aggregationOnTool,
                                                                    -1, aggregationPosition);
                        } else
                            setSplitPaneContent(content);
                    }
                } else if (manager.getShowingGroup() != null && content != null) {
                    multiSplitDockableContainer.clear();
                    if (shiftShow)
                        multiSplitDockableContainer.addDockable(
                                (Dockable) ((JComponent) splitPaneContent).getClientProperty(ToolWindow.class),
                                splitPaneContent,
                                null,
                                -1, AggregationPosition.DEFAULT);
                    multiSplitDockableContainer.addDockable(descriptor.getToolWindow(),
                                                            content,
                                                            null,
                                                            -1, aggregationPosition);

                    setSplitPaneContent(multiSplitDockableContainer);
                } else if (content != null)
                    setSplitPaneContent(content);
            } else {
                if (manager.getShowingGroup() != null && content != null) {
                    multiSplitDockableContainer.clear();
                    multiSplitDockableContainer.addDockable(descriptor.getToolWindow(),
                                                            content,
                                                            null,
                                                            -1, AggregationPosition.DEFAULT);

                    setSplitPaneContent(multiSplitDockableContainer);
                } else if (content != null)
                    setSplitPaneContent(content);
            }

            if (animate) {
                if (content != null) {
                    splitPane.setDividerSize(getDividerSize());
                    if (manager.getShowingGroup() == null &&
                        descriptor.getTypeDescriptor(ToolWindowType.DOCKED).isAnimating()) {
                        splitAnimation.show(divederLocation);
                    } else {
                        if (divederLocation != 0) {
                            vsdValueAdjusting = true;
                            setSplitDividerLocation(divederLocation);
                            vsdValueAdjusting = false;
                            SwingUtil.repaintNow(splitPane);
                        }
                    }
                } else {
                    splitPane.setDividerSize(0);
                    setSplitPaneContent(null);
                    vsdValueAdjusting = true;
                    setSplitDividerLocation(0);
                    SwingUtil.repaintNow(splitPane);
                    vsdValueAdjusting = false;
//                    splitAnimation.hide(divederLocation);
                }
            } else {
                setSplitDividerLocation(divederLocation);
                SwingUtil.repaint(splitPane);
            }
        }

        public void disabledContentManagerPropertyChange(PropertyChangeEvent evt) {
            MultiSplitDockableContainer managerDockableContainer = (MultiSplitDockableContainer) ((ContentPanel) manager.getMainContent()).getComponent();

            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

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
        }

        protected void setSplitPaneContent(Component content) {
            vsdValueAdjusting = true;
            try {
                if (content != null && splitPane.getDividerLocation() == 0)
                    splitPane.setDividerLocation(1);

                Component splitPaneContent = content;

                if (content != null) {
                    contentPanel.setComponent(content);
                    splitPaneContent = contentPanel;
                } else {
                    contentPanel.resetComponent();
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

    protected static class VisibleFloatingListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            FloatingContainer container = (FloatingContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.FLOATING);

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.propertyChange(evt);
            container.setVisible(visible);
        }
    }

    protected static class VisibleFloatingFreeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            FloatingContainer container = (FloatingContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.FLOATING_FREE);

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.propertyChange(evt);
            container.setVisible(visible);
        }
    }

    public class VisibleSlidingListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            SlidingContainer container = (SlidingContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.SLIDING);

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.setVisible(visible, getContainer());
        }
    }

    public class VisibleFloatingLiveListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
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

                Component content = (visible) ? toolWindowDescriptor.getContentContainer() : null;
                if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                    return;

                FloatingLiveContainer container = (FloatingLiveContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.FLOATING_LIVE);
                container.setVisible(referenceAggregationDescriptor,
                                     content,
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
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            JLabel representativeAnchor = descriptor.getRepresentativeAnchor();
            if (representativeAnchor != null) {
                TableLayoutConstraints constraints = toolWindowBarContainerLayout.getConstraints(representativeAnchor);

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

    public class DragListener implements PropertyChangeListener {

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
                for (Component cmp : toolWindowBarContainer.getComponents()) {
                    Component source = SwingUtil.getComponentWhoseParentIs((Component) evt.getSource(), toolWindowBarContainer);

                    if (cmp == source) {
                        TableLayout layout = (TableLayout) toolWindowBarContainer.getLayout();
                        switch (anchor) {
                            case LEFT:
                            case RIGHT:
                                layout.setRow(layout.getConstraints(cmp).row1, dragComponentLength);
                                break;
                            case TOP:
                            case BOTTOM:
                                layout.setColumn(layout.getConstraints(cmp).col1, dragComponentLength);
                                break;
                        }
                        SwingUtil.repaint(toolWindowBarContainer);
                        manager.syncPanel(anchor);
                    }
                }
            } else
                throw new IllegalArgumentException("Invalid property name. [property : " + evt.getPropertyName() + "]");
        }
    }

    public class MaximizedListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            if (descriptor.getToolWindow().getType() == ToolWindowType.DOCKED) {
                if ((Boolean) evt.getNewValue()) {
                    descriptor.setTempDivederLocation(getSplitDividerLocation());

                    ToolWindow maximizedTool = descriptor.getToolWindow();
                    for (ToolWindow tool : descriptor.getManager().getToolWindows())
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
}


