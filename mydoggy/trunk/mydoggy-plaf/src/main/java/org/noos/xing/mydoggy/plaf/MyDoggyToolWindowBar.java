package org.noos.xing.mydoggy.plaf;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowBarDropTarget;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToolsOnBarMouseListener;
import org.noos.xing.mydoggy.plaf.ui.look.RepresentativeAnchorUI;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * @author Angelo De Caro
 */
public class MyDoggyToolWindowBar implements SwingConstants, PropertyChangeListener {
    public static final int VERTICAL_LEFT = TextIcon.ROTATE_LEFT;
    public static final int VERTICAL_RIGHT = TextIcon.ROTATE_RIGHT;
    public static final int HORIZONTAL = TextIcon.ROTATE_NONE;

    private static final double[] COLUMNS = {2, 19, 2};
    private static final double[] ROWS = COLUMNS;

    protected MyDoggyToolWindowManager manager;

    protected ToolWindowAnchor anchor;

    // Bar Components
    protected JToolScrollBar toolScrollBar;
    protected JPanel contentPane;
    protected TableLayout contentPaneLayout;
    protected JSplitPane splitPane;
    protected MultiSplitContainer multiSplitContainer;

    protected int availableTools;
    protected int orientation;
    protected boolean horizontal;

    protected PropertyChangeSupport propertyChangeSupport;

    protected boolean tempShowed;

    boolean valueAdjusting = false;

    MyDoggyToolWindowBar(MyDoggyToolWindowManager manager, JSplitPane splitPane, ToolWindowAnchor anchor) {
        this.manager = manager;
        this.splitPane = splitPane;
        if (splitPane instanceof DebugSplitPane)
            ((DebugSplitPane) splitPane).setToolWindowBar(this);
        this.anchor = anchor;
        this.availableTools = 0;

        initComponents();
        initListeners();

        if (anchor == ToolWindowAnchor.LEFT || anchor == ToolWindowAnchor.TOP)
            setSplitDividerLocation(0);
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

    public JToolScrollBar getToolScrollBar() {
        return toolScrollBar;
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    public ToolWindowAnchor getAnchor() {
        return anchor;
    }

    public JSplitPane getSplitPane() {
        return splitPane;
    }

    public int getAvailableTools() {
        return availableTools;
    }

    public void ensureVisible(Component component) {
        toolScrollBar.ensureVisible(component);
    }

    public boolean isTempShowed() {
        return tempShowed;
    }

    public void setTempShowed(boolean tempShowed) {
        boolean old = this.tempShowed;
        this.tempShowed = tempShowed;
        manager.syncPanel(anchor);

        manager.propertyChange(new PropertyChangeEvent(this, "tempShowed", old, tempShowed));
    }

    public int getRepresentativeAnchorIndex(JLabel representativeAnchor) {
        TableLayoutConstraints constraints = contentPaneLayout.getConstraints(representativeAnchor);
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


    protected void initComponents() {
        splitPane.setName(anchor.toString());
        splitPane.setFocusCycleRoot(true);

        multiSplitContainer = new MultiSplitContainer(manager, orientation);

        contentPane = (JPanel) manager.getResourceManager().createComponent(MyDoggyKeySpace.ANCHOR_CONTENT_PANE, manager);
        contentPane.setName("toolWindowManager.bar." + anchor.toString());
        contentPane.setFocusable(false);
        contentPane.setFocusCycleRoot(true);

        if (anchor == ToolWindowAnchor.LEFT || anchor == ToolWindowAnchor.RIGHT) {
            horizontal = false;
            contentPane.setLayout(new ExtendedTableLayout(new double[][]{COLUMNS, {0}}));
            orientation = JSplitPane.VERTICAL_SPLIT;
        } else if (anchor == ToolWindowAnchor.TOP || anchor == ToolWindowAnchor.BOTTOM) {
            horizontal = true;
            contentPane.setLayout(new ExtendedTableLayout(new double[][]{{0}, ROWS}));
            orientation = JSplitPane.HORIZONTAL_SPLIT;
        }

        toolScrollBar = new JToolScrollBar(manager.getResourceManager(), orientation, contentPane);

        contentPaneLayout = (ExtendedTableLayout) contentPane.getLayout();

        contentPane.setDropTarget(new ToolWindowBarDropTarget(manager, anchor, contentPane));
        contentPane.addMouseListener(new ToolsOnBarMouseListener(manager, anchor));
    }

    protected void initListeners() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        AvailableListener availableListener = new AvailableListener();
        propertyChangeSupport.addPropertyChangeListener("available", availableListener);
        propertyChangeSupport.addPropertyChangeListener("representativeAnchorButtonVisible", availableListener);
        propertyChangeSupport.addPropertyChangeListener("visible.before", new VisibleBeforeListener());
        propertyChangeSupport.addPropertyChangeListener("visible.DOCKED", new VisibleDockedListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING", new VisibleFloatingListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING_FREE", new VisibleFloatingFreeListener());
        propertyChangeSupport.addPropertyChangeListener("visible.SLIDING", new VisibleSlidingListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING_LIVE", new VisibleFloatingLiveListener());
        propertyChangeSupport.addPropertyChangeListener("visible.after", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
                if (sourceTool.getType() != ToolWindowType.DOCKED ||
                    sourceTool.getAnchor() != anchor ||
                    !(getSplitPaneContent() instanceof MultiSplitContainer))
                    return;

                MultiSplitContainer multiSplitContainer = (MultiSplitContainer) getSplitPaneContent();
                if (multiSplitContainer.getContentCount() <= 1)
                    return;

                java.util.List<Component> components = Arrays.asList(contentPane.getComponents());
                Collections.sort(components, new Comparator<Component>() {
                    public int compare(Component o1, Component o2) {
                        TableLayoutConstraints c1 = contentPaneLayout.getConstraints(o1);
                        TableLayoutConstraints c2 = contentPaneLayout.getConstraints(o2);
                        if (horizontal) {
                            if (c1.col1 < c2.col1)
                                return -1;
                            else if (c1.col1 == c2.col1)
                                return 0;
                        } else {
                            if (c1.row1 < c2.row1)
                                return -1;
                            else if (c1.row1 == c2.row1)
                                return 0;
                        }
                        return 1;
                    }
                });

                int i = 0;
                for (Component component : components) {
                    if (component instanceof JLabel) {
                        JLabel representativeAnchor = (JLabel) component;
                        ToolWindowDescriptor descriptor = ((RepresentativeAnchorUI) representativeAnchor.getUI()).getDescriptor();

                        if (descriptor.getToolWindow().isVisible()) {
                            Component content = descriptor.getContentContainer();
                            multiSplitContainer.setComponentAt(
                                    descriptor.getToolWindow().getId(),
                                    content, i++);
                        }
                    }

                }
            }
        });
        propertyChangeSupport.addPropertyChangeListener("visible", new VisibleListener());
        propertyChangeSupport.addPropertyChangeListener("active.before", new ActiveBeforeListener());
        propertyChangeSupport.addPropertyChangeListener("active", new ActiveListener());
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

        manager.getToolWindowManagerDescriptor().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("dividerSize".equals(evt.getPropertyName())) {
                    Object[] values = (Object[]) evt.getNewValue();
                    if (values[0].equals(anchor)) {
                        if (splitPane.getDividerSize() > 0)
                            splitPane.setDividerSize((Integer) values[1]);
                    }
                }
            }
        });
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
                    splitPane.setDividerLocation(splitPane.getWidth() - divederLocation);
                    break;
                case BOTTOM:
                    splitPane.setDividerLocation(splitPane.getHeight() - divederLocation);
                    break;
            }
    }

    protected Component getSplitPaneContent() {
        switch (anchor) {
            case LEFT:
                return splitPane.getLeftComponent();
            case RIGHT:
                return splitPane.getRightComponent();
            case BOTTOM:
                return splitPane.getBottomComponent();
            case TOP:
                return splitPane.getTopComponent();
        }
        throw new IllegalStateException();
    }

    protected void addRepresentativeAnchor(JLabel representativeAnchor, int index) {
        availableTools++;
        if (horizontal) {
            int width = representativeAnchor.getPreferredSize().width + 6;

            contentPaneLayout.insertColumn(contentPaneLayout.getNumColumn(), contentPaneLayout.getNumColumn() > 0 ? 5 : 1);
            contentPaneLayout.insertColumn(contentPaneLayout.getNumColumn(), width);

            if (index >= 0) {
                Component[] components = contentPane.getComponents();
                int finalCol = (index * 2 + 2);

                Map<Integer, Double> olds = new Hashtable<Integer, Double>();
                for (Component component : components) {
                    TableLayoutConstraints constraints = contentPaneLayout.getConstraints(component);
                    if (constraints.col1 >= finalCol) {
                        int newCol1 = constraints.col1 + 2;
                        contentPaneLayout.setConstraints(component,
                                                         new TableLayoutConstraints(
                                                                 newCol1 + ",1,"
                                                         ));

                        olds.put(newCol1, contentPaneLayout.getColumn(newCol1));
                        Double colSize = olds.get(constraints.col1);
                        if (colSize == null)
                            colSize = contentPaneLayout.getColumn(constraints.col1);

                        contentPaneLayout.setColumn(newCol1, colSize);
                    }
                }
                contentPaneLayout.setColumn(finalCol, width);
                contentPane.add(representativeAnchor, (index * 2 + 2) + ",1,");
            } else
                contentPane.add(representativeAnchor, (contentPaneLayout.getNumColumn() - 1) + ",1,");
        } else {
            int height = Math.max(representativeAnchor.getHeight(),
                                  Math.max(representativeAnchor.getPreferredSize().height,
                                           representativeAnchor.getSize().height)) + 12;

            contentPaneLayout.insertRow(contentPaneLayout.getNumRow(), contentPaneLayout.getNumRow() > 0 ? 5 : 1);
            contentPaneLayout.insertRow(contentPaneLayout.getNumRow(), height);

            if (index >= 0) {
                Component[] components = contentPane.getComponents();
                int finalRow = (index * 2 + 2);


                Map<Integer, Double> olds = new Hashtable<Integer, Double>();
                for (Component component : components) {
                    TableLayoutConstraints constraints = contentPaneLayout.getConstraints(component);

                    if (constraints.row1 >= finalRow) {
                        int newRow1 = constraints.row1 + 2;
                        contentPaneLayout.setConstraints(component,
                                                         new TableLayoutConstraints(
                                                                 "1," + newRow1
                                                         ));

                        olds.put(newRow1, contentPaneLayout.getRow(newRow1));
                        Double rowSize = olds.get(constraints.row1);
                        if (rowSize == null)
                            rowSize = contentPaneLayout.getRow(constraints.row1);

                        contentPaneLayout.setRow(newRow1, rowSize);
                    }
                }
                if (contentPaneLayout.getNumRow() <= finalRow) {
                    contentPaneLayout.setRow(contentPaneLayout.getNumRow() - 1, height);
                } else
                    contentPaneLayout.setRow(finalRow, height);

                contentPane.add(representativeAnchor, "1," + (index * 2 + 2));
            } else
                contentPane.add(representativeAnchor, "1," + (contentPaneLayout.getNumRow() - 1));
        }
        SwingUtil.repaint(toolScrollBar);
    }

    protected void removeRepresentativeAnchor(JLabel representativeAnchor, ToolWindowDescriptor descriptor) {
        if (representativeAnchor == null)
            return;
        
        int toDelete;
        TableLayoutConstraints constraints = contentPaneLayout.getConstraints(representativeAnchor);
        if (constraints == null)
            return;

        // Remove
        availableTools--;

        toDelete = horizontal ? constraints.col1 : constraints.row1;

        contentPane.remove(representativeAnchor);
        if (horizontal) {
            contentPaneLayout.deleteColumn(toDelete);
            contentPaneLayout.deleteColumn(toDelete - 1);
        } else {
            contentPaneLayout.deleteRow(toDelete);
            contentPaneLayout.deleteRow(toDelete - 1);
        }

        SwingUtil.repaint(toolScrollBar);

        descriptor.resetRepresentativeAnchor();
    }


    protected class AvailableListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            if (descriptor.getToolWindow().getType() != ToolWindowType.FLOATING_FREE &&
                descriptor.getToolWindow().getType() != ToolWindowType.EXTERN) {

                boolean rabsEvent = evt.getPropertyName().equals("representativeAnchorButtonVisible");

                if (!rabsEvent) {
                    if (!descriptor.getToolWindow().isRepresentativeAnchorButtonVisible())
                        return;                    
                }


                boolean oldAvailable = (Boolean) evt.getOldValue();
                boolean newAvailable = (Boolean) evt.getNewValue();

                boolean repaint = false;

                JLabel representativeAnchor = null;
                if (oldAvailable && !newAvailable) {
                    // true -> false
                    representativeAnchor = descriptor.getRepresentativeAnchor();
                    if (representativeAnchor != null) {
                        removeRepresentativeAnchor(representativeAnchor, descriptor);
                        repaint = true;
                    }
                } else if (!oldAvailable && newAvailable) {
                    // false -> true
                    assert evt instanceof UserPropertyChangeEvent;
                    assert ((UserPropertyChangeEvent) evt).getUserObject() instanceof Integer;

                    representativeAnchor = descriptor.getRepresentativeAnchor(contentPane);
                    if (rabsEvent) {
                        // TODO: we should remember anchor position before removing it when rabsEvent occurs.
                        addRepresentativeAnchor(representativeAnchor, -1);
                    } else
                        addRepresentativeAnchor(representativeAnchor, (Integer) ((UserPropertyChangeEvent) evt).getUserObject());
                    repaint = true;
                }

                if (repaint) {
                    representativeAnchor.setEnabled(newAvailable);
                    SwingUtil.repaint(contentPane);
                }
            }
        }

    }

    protected class ActiveBeforeListener implements PropertyChangeListener {
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

    protected static class ActiveListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            toolWindowDescriptor.getToolWindowContainer().propertyChange(evt);
        }
    }


    protected class TypeListener extends AvailableListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();

            if (evt.getOldValue() == ToolWindowType.FLOATING_FREE) {
                addRepresentativeAnchor(toolWindowDescriptor.getRepresentativeAnchor(contentPane), -1);
                ensureVisible(toolWindowDescriptor.getRepresentativeAnchor());

                SwingUtil.repaint(contentPane);
            } else
            if ((evt.getNewValue() == ToolWindowType.FLOATING_FREE || evt.getNewValue() == ToolWindowType.EXTERN) &&
                toolWindowDescriptor.getRepresentativeAnchor() != null) {

                removeRepresentativeAnchor(toolWindowDescriptor.getRepresentativeAnchor(), toolWindowDescriptor);
                SwingUtil.repaint(contentPane);
            }

        }
    }


    protected class VisibleBeforeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
            if (sourceTool.getType() == ToolWindowType.FLOATING ||
                sourceTool.getType() == ToolWindowType.FLOATING_FREE)
                return;

            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            if (!oldValue && newValue) { // false and true
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
                            toolWindow.setVisible(false);
                        else if (toolWindow.isAutoHide() || toolWindow.getType() == ToolWindowType.SLIDING)
                            toolWindow.setVisible(false);
                    } else if (toolWindow.getType() == ToolWindowType.SLIDING
                        /*TODO Monitor this.. && toolWindow.getAnchor() == sourceTool.getAnchor()
                       && manager.isShiftShow()*/)
                        toolWindow.setVisible(false);

                    if (toolWindow.isVisible() && toolWindow.isMaximized()
                        /*&& TODO: Monitor this with setDefaultAggregate
                        !manager.isShiftShow() && toolWindow.getAnchor() != sourceTool.getAnchor()*/)
                        toolWindow.setMaximized(false);
                }
            }
        }

    }

    protected class VisibleListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            MyDoggyToolWindowBar.this.propertyChange(new PropertyChangeEvent(evt.getSource(), "visible." + sourceTool.getType().toString(),
                                                                             null, !oldValue && newValue));
        }
    }


    protected class VisibleDockedListener implements PropertyChangeListener {
        protected final SplitAnimation splitAnimation = new SplitAnimation();
        protected boolean vsdValueAdjusting = false;
        protected Map<ToolWindowDescriptor, Integer> poss;

        public VisibleDockedListener() {
            poss = new HashMap<ToolWindowDescriptor, Integer>();

            splitPane.addPropertyChangeListener("dividerLocation", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    int dividerLocation = getSplitDividerLocation();

                    if (splitAnimation.isAnimating() || vsdValueAdjusting || dividerLocation == 0)
                        return;

                    for (ToolWindow toolWindow : manager.getToolsByAnchor(anchor)) {
                        if (toolWindow.isVisible())
                            manager.getDescriptor(toolWindow).setDividerLocation(dividerLocation);
                    }
                }
            });
        }

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            if (descriptor.getDockedTypeDescriptor().isHideRepresentativeButtonOnVisible()) {
                if (visible) {
                    poss.put(descriptor, descriptor.getRepresentativeAnchorIndex());
                    removeRepresentativeAnchor(descriptor.getRepresentativeAnchor(), descriptor);
                } else {
                    assert poss.containsKey(descriptor);
                    addRepresentativeAnchor(descriptor.getRepresentativeAnchor(contentPane), poss.get(descriptor));
                }
            }

            if (visible)
                descriptor.checkIdOnTitleBar();

            Component content = (visible) ? descriptor.getComponent() : null;
            if (content != null) {
                DockedContainer container = (DockedContainer) descriptor.getToolWindowContainer();
                content = container.getContentContainer();
            }

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

            int divederLocation = descriptor.getDividerLocation();

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
                if (splitPaneContent instanceof MultiSplitContainer) {
                    MultiSplitContainer multiSplitContainer = (MultiSplitContainer) splitPaneContent;

                    if (manager.getShowingGroup() != null) {
                        multiSplitContainer.addContent(descriptor.getToolWindow().getId(), content);
                    } else {
                        if (content == null) {
                            DockedContainer dockedContainer = (DockedContainer) descriptor.getToolWindowContainer();
                            multiSplitContainer.removeContent(dockedContainer.getContentContainer());
                            animate = false;

                            if (multiSplitContainer.isEmpty()) {
                                animate = true;
                                content = null;
                            } else if (multiSplitContainer.getContentCount() == 1) {
                                animate = false;
                                content = multiSplitContainer.getContents().get(0);
                                int temp = getSplitDividerLocation();
                                setSplitPaneContent(content);
                                setSplitDividerLocation(temp);
                            }
                        } else {
                            setSplitPaneContent(content);
                        }
                    }
                } else if (manager.getShowingGroup() != null && content != null) {
                    multiSplitContainer.clear();
                    if (manager.isShiftShow())
                        multiSplitContainer.addContent(descriptor.getToolWindow().getId(), splitPaneContent);
                    multiSplitContainer.addContent(descriptor.getToolWindow().getId(), content);

                    setSplitPaneContent(multiSplitContainer);
                } else if (content != null)
                    setSplitPaneContent(content);
            } else {
                if (manager.getShowingGroup() != null && content != null) {
                    multiSplitContainer.clear();
                    multiSplitContainer.addContent(descriptor.getToolWindow().getId(), content);

                    setSplitPaneContent(multiSplitContainer);
                } else if (content != null)
                    setSplitPaneContent(content);
            }

            if (animate) {
                if (content != null) {
                    splitPane.setDividerSize(manager.getToolWindowManagerDescriptor().getDividerSize(anchor));
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
                SwingUtil.repaint(splitPane);
            }
        }

        protected void setSplitPaneContent(Component content) {
            vsdValueAdjusting = true;
            try {
                if (content != null && splitPane.getDividerLocation() == 0)
                    splitPane.setDividerLocation(1);

                switch (anchor) {
                    case LEFT:
                        splitPane.setLeftComponent(content);
                        break;
                    case RIGHT:
                        splitPane.setRightComponent(content);
                        if (content != null)
                            splitPane.setDividerLocation(splitPane.getWidth());
                        break;
                    case BOTTOM:
                        splitPane.setBottomComponent(content);
                        if (content != null)
                            splitPane.setDividerLocation(splitPane.getHeight());
                        break;
                    case TOP:
                        splitPane.setTopComponent(content);
                        break;
                }
                if (content != null)
                    content.setVisible(true);
            } finally {
                vsdValueAdjusting = false;
            }
        }

        protected class SplitAnimation extends AbstractAnimation {
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

    protected class VisibleSlidingListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            SlidingContainer container = (SlidingContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.SLIDING);

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.setVisible(visible, getToolScrollBar());
        }
    }

    protected class VisibleFloatingLiveListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            FloatingLiveContainer container = (FloatingLiveContainer) toolWindowDescriptor.getToolWindowContainer(ToolWindowType.FLOATING_LIVE);

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.setVisible(visible);
        }
    }

    protected class IndexListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            JLabel representativeAnchor = descriptor.getRepresentativeAnchor();
            if (representativeAnchor != null) {
                TableLayoutConstraints constraints = contentPaneLayout.getConstraints(representativeAnchor);

                if (horizontal) {
                    int width = representativeAnchor.getPreferredSize().width + 6;

                    contentPaneLayout.setColumn(constraints.col1, width);
                } else {
                    int height = Math.max(representativeAnchor.getPreferredSize().height,
                                          representativeAnchor.getSize().height);
                    contentPaneLayout.setRow(constraints.row1, height);
                }

                SwingUtil.repaint(contentPane);
            }
        }
    }

    protected class IconListener extends IndexListener {
    }

    protected class TitleListener extends IndexListener {
    }


    protected class DragListener implements PropertyChangeListener {
        protected int len;

        public void propertyChange(PropertyChangeEvent evt) {
            if ("startDrag".equals(evt.getPropertyName())) {
                Component cmp = (Component) evt.getSource();
                TableLayout layout = (TableLayout) contentPane.getLayout();
                switch (anchor) {
                    case LEFT:
                    case RIGHT:
                        len = cmp.getHeight();
                        layout.setRow(layout.getConstraints(cmp).row1, 0);
                        break;
                    case TOP:
                    case BOTTOM:
                        len = cmp.getWidth();
                        layout.setColumn(layout.getConstraints(cmp).col1, 0);
                        break;
                }
                SwingUtil.repaint(contentPane);
            } else if ("endDrag".equals(evt.getPropertyName())) {
                for (Component cmp : contentPane.getComponents()) {
                    if (cmp == evt.getSource()) {
                        TableLayout layout = (TableLayout) contentPane.getLayout();
                        switch (anchor) {
                            case LEFT:
                            case RIGHT:
                                layout.setRow(layout.getConstraints(cmp).row1, len);
                                break;
                            case TOP:
                            case BOTTOM:
                                layout.setColumn(layout.getConstraints(cmp).col1, len);
                                break;
                        }
                        SwingUtil.repaint(contentPane);
                        manager.syncPanel(anchor);
                    }
                }
            } else
                throw new IllegalArgumentException("Invalid Property Name : " + evt.getPropertyName());
        }
    }

    protected class MaximizedListener implements PropertyChangeListener {

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
}


