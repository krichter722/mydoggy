package org.noos.xing.mydoggy.plaf;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowBarDropTarget;
import org.noos.xing.mydoggy.plaf.ui.icons.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro
 */
public class MyDoggyToolWindowBar implements SwingConstants, PropertyChangeListener {
    public static final int VERTICAL_LEFT = TextIcon.ROTATE_LEFT;
    public static final int VERTICAL_RIGHT = TextIcon.ROTATE_RIGHT;
    public static final int HORIZONTAL = TextIcon.ROTATE_NONE;

    private static final double[] COLUMNS = {2, 19, 2};
    private static final double[] ROWS = COLUMNS;

    private MyDoggyToolWindowManager manager;

    private ToolWindowAnchor anchor;

    // Bar Components
    private JToolScrollBar toolScrollBar;
    private JPanel contentPane;
    private TableLayout contentPaneLayout;

    private JSplitPane splitPane;
    private int availableTools;
    private int orientation;
    private boolean horizontal;

    private PropertyChangeSupport propertyChangeSupport;

    private boolean tempShowed;

    boolean valueAdjusting = false;

    MyDoggyToolWindowBar(MyDoggyToolWindowManager manager, JSplitPane splitPane, ToolWindowAnchor anchor) {
        this.manager = manager;
        this.splitPane = splitPane;
        ((MyDoggyToolWindowManager.ExtendedJSP) splitPane).setToolWindowBar(this);
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

    public boolean isPointOnBar(Point point) {
        return false;
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


    protected void initComponents() {
        splitPane.setName(anchor.toString());
        splitPane.setFocusCycleRoot(true);

        contentPane = new JPanel();
        if (anchor == ToolWindowAnchor.LEFT || anchor == ToolWindowAnchor.RIGHT) {
            horizontal = false;
            contentPane.setLayout(new ExtendedTableLayout(new double[][]{COLUMNS, {0}}));
            orientation = JSplitPane.VERTICAL_SPLIT;
        } else if (anchor == ToolWindowAnchor.TOP || anchor == ToolWindowAnchor.BOTTOM) {
            horizontal = true;
            contentPane.setLayout(new ExtendedTableLayout(new double[][]{{0}, ROWS}));
            orientation = JSplitPane.HORIZONTAL_SPLIT;
        }

        toolScrollBar = new JToolScrollBar(orientation, contentPane);

        contentPaneLayout = (ExtendedTableLayout) contentPane.getLayout();

        contentPane.setDropTarget(new ToolWindowBarDropTarget(anchor, contentPane));
        contentPane.addMouseListener(new ToolsOnBarMouseListener(manager, anchor));
    }

    protected void initListeners() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        propertyChangeSupport.addPropertyChangeListener("available", new AvailableListener());
        propertyChangeSupport.addPropertyChangeListener("visible.before", new VisibleBeforeListener());
        propertyChangeSupport.addPropertyChangeListener("visible.DOCKED", new VisibleDockedListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING", new VisibleFloatingListener());
        propertyChangeSupport.addPropertyChangeListener("visible.FLOATING_FREE", new VisibleFloatingWindowListener());
        propertyChangeSupport.addPropertyChangeListener("visible.SLIDING", new VisibleSlidingListener());
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


    class AvailableListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            if (descriptor.getToolWindow().getType() != ToolWindowType.FLOATING_FREE) {
                boolean oldAvailable = (Boolean) evt.getOldValue();
                boolean newAvailable = (Boolean) evt.getNewValue();

                boolean repaint = false;
                JLabel anchorLabel = descriptor.getAnchorLabel(contentPane);

                if (oldAvailable && !newAvailable) {
                    // true -> false
                    removeAnchorLabel(anchorLabel, descriptor);
                    repaint = true;
                } else if (!oldAvailable && newAvailable) {
                    // false -> true
                    addAnchorLabel(anchorLabel);
                    repaint = true;
                }

                if (repaint) {
                    anchorLabel.setEnabled(newAvailable);
                    SwingUtil.repaint(contentPane);
                }
            }
        }

        protected void addAnchorLabel(JLabel anchorLabel) {
            try {
                availableTools++;
                if (horizontal) {
                    int width = anchorLabel.getPreferredSize().width + 6;

                    contentPaneLayout.insertColumn(contentPaneLayout.getNumColumn(), contentPaneLayout.getNumColumn() > 0 ? 5 : 1);
                    contentPaneLayout.insertColumn(contentPaneLayout.getNumColumn(), width);

                    if (anchor.getIndex() >= 0) {
                        Component[] components = contentPane.getComponents();
                        int finalCol = (anchor.getIndex() * 2 + 2);

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
                        contentPane.add(anchorLabel, (anchor.getIndex() * 2 + 2) + ",1,");
                    } else
                        contentPane.add(anchorLabel, (contentPaneLayout.getNumColumn() - 1) + ",1,");
                } else {
                    int height = Math.max(anchorLabel.getHeight(),
                                          Math.max(anchorLabel.getPreferredSize().height,
                                                   anchorLabel.getSize().height)) + 12;

                    contentPaneLayout.insertRow(contentPaneLayout.getNumRow(), contentPaneLayout.getNumRow() > 0 ? 5 : 1);
                    contentPaneLayout.insertRow(contentPaneLayout.getNumRow(), height);

                    if (anchor.getIndex() >= 0) {
                        Component[] components = contentPane.getComponents();
                        int finalRow = (anchor.getIndex() * 2 + 2);


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
                        contentPaneLayout.setRow(finalRow, height);

                        contentPane.add(anchorLabel, "1," + (anchor.getIndex() * 2 + 2));
                    } else
                        contentPane.add(anchorLabel, "1," + (contentPaneLayout.getNumRow() - 1));
                }
                SwingUtil.repaint(toolScrollBar);
            } finally {
                anchor.setIndex(-1);
            }
        }

        protected void removeAnchorLabel(JLabel anchorLabel, ToolWindowDescriptor descriptor) {
            // Remove
            availableTools--;

            int toDelete;
            if (horizontal) {
                toDelete = contentPaneLayout.getConstraints(anchorLabel).col1;
            } else {
                toDelete = contentPaneLayout.getConstraints(anchorLabel).row1;
            }
            contentPane.remove(anchorLabel);
            if (horizontal) {
                contentPaneLayout.deleteColumn(toDelete);
                contentPaneLayout.deleteColumn(toDelete - 1);
            } else {
                contentPaneLayout.deleteRow(toDelete);
                contentPaneLayout.deleteRow(toDelete - 1);
            }

            SwingUtil.repaint(toolScrollBar);

            descriptor.resetAnchorLabel();
        }

    }

    class ActiveBeforeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
            boolean newValue = (Boolean) evt.getNewValue();

            if (newValue) {
                // Deactive all tools on the same bar
                ToolWindow[] toolWindows = manager.getToolsByAnchor(getAnchor());
                for (ToolWindow toolWindow : toolWindows) {
                    if (toolWindow == sourceTool)
                        continue;

                    valueAdjusting = true;
                    toolWindow.setActive(false);
                    valueAdjusting = false;
                }
            }
        }
    }

    static class ActiveListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            toolWindowDescriptor.getToolWindowContainer().propertyChange(evt);
        }
    }


    class TypeListener extends AvailableListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();

            if (evt.getOldValue() == ToolWindowType.FLOATING_FREE) {
                addAnchorLabel(toolWindowDescriptor.getAnchorLabel(contentPane));
                ensureVisible(toolWindowDescriptor.getAnchorLabel());

                SwingUtil.repaint(contentPane);
            } else if (evt.getNewValue() == ToolWindowType.FLOATING_FREE &&
                       toolWindowDescriptor.getAnchorLabel() != null) {

                removeAnchorLabel(toolWindowDescriptor.getAnchorLabel(), toolWindowDescriptor);
                SwingUtil.repaint(contentPane);
            }

        }
    }


    class VisibleBeforeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
            if (sourceTool.getType() == ToolWindowType.FLOATING || sourceTool.getType() == ToolWindowType.FLOATING_FREE)
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
                            toolWindow.getType() == ToolWindowType.FLOATING_FREE)
                            continue;

                        if (toolWindow.getAnchor().equals(sourceTool.getAnchor()))
                            toolWindow.setVisible(false);
                        else if (toolWindow.isAutoHide() || toolWindow.getType() == ToolWindowType.SLIDING)
                            toolWindow.setVisible(false);
                    } else if (toolWindow.getType() == ToolWindowType.SLIDING
                               && toolWindow.getAnchor() == sourceTool.getAnchor()
                               && manager.isShiftShow())
                        toolWindow.setVisible(false);

                    if (toolWindow.isVisible() && toolWindow.isMaximized() &&
                        !manager.isShiftShow() && toolWindow.getAnchor() != sourceTool.getAnchor())
                        toolWindow.setMaximized(false);
                }
            }
        }

    }

    class VisibleListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            MyDoggyToolWindowBar.this.propertyChange(new PropertyChangeEvent(evt.getSource(), "visible." + sourceTool.getType().toString(),
                                                                             null, !oldValue && newValue));
        }
    }


    class VisibleDockedListener implements PropertyChangeListener {
        private final SplitAnimation splitAnimation = new SplitAnimation();
        private boolean vsdValueAdjusting = false;

        public VisibleDockedListener() {
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

            for (ToolWindow toolWindow : manager.getToolsByAnchor(anchor)) {
                if (descriptor.getToolWindow() != toolWindow && toolWindow.isVisible()) {
                    divederLocation = getSplitDividerLocation();
                    break;
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
                        multiSplitContainer.addContent(content);
                    } else {
                        if (content == null) {
                            DockedContainer dockedContainer = (DockedContainer) descriptor.getToolWindowContainer();
                            multiSplitContainer.removeContent(dockedContainer.getContentContainer());
                            animate = false;

                            if (multiSplitContainer.isEmpty()) {
                                animate = true;
                                content = null;
                            }
                        } else {
                            setSplitPaneContent(content);
                        }
                    }
                } else if (manager.getShowingGroup() != null && content != null) {
                    MultiSplitContainer container = new MultiSplitContainer(orientation);
                    if (manager.isShiftShow())
                        container.addContent(splitPaneContent);
                    container.addContent(content);

                    setSplitPaneContent(container);
                } else if (content != null)
                    setSplitPaneContent(content);
            } else {
                if (manager.getShowingGroup() != null && content != null) {
                    MultiSplitContainer container = new MultiSplitContainer(orientation);
                    container.addContent(content);

                    setSplitPaneContent(container);
                } else if (content != null)
                    setSplitPaneContent(content);
            }

            if (animate) {
                if (content != null) {
                    splitPane.setDividerSize(5);
                    if (manager.getShowingGroup() == null &&
                        ((DockedTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.DOCKED)).isAnimating()) {
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

        private class SplitAnimation extends AbstractAnimation {
            private int dividerLocation;
            private int sheetLen;

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

    static class VisibleFloatingListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            FloatingContainer container = (FloatingContainer) toolWindowDescriptor.getToolWindowContainer();

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.propertyChange(evt);
            container.setVisible(visible);
        }
    }

    static class VisibleFloatingWindowListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            FloatingContainer container = (FloatingContainer) toolWindowDescriptor.getToolWindowContainer();

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.propertyChange(evt);
            container.setVisible(visible);
        }
    }

    class VisibleSlidingListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? toolWindowDescriptor.getComponent() : null;
            SlidingContainer container = (SlidingContainer) toolWindowDescriptor.getToolWindowContainer();

            if (content == null && toolWindowDescriptor.getToolWindow().isVisible())
                return;

            container.setVisible(visible, getToolScrollBar());
        }
    }


    class IndexListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            JLabel anchorLabel = descriptor.getAnchorLabel();
            if (anchorLabel != null) {
                TableLayoutConstraints constraints = contentPaneLayout.getConstraints(anchorLabel);

                if (horizontal) {
                    int width = anchorLabel.getPreferredSize().width + 6;

                    contentPaneLayout.setColumn(constraints.col1, width);
                } else {
                    int height = Math.max(anchorLabel.getPreferredSize().height,
                                          anchorLabel.getSize().height);
                    contentPaneLayout.setRow(constraints.row1, height);
                }

                SwingUtil.repaint(contentPane);
            }
        }
    }

    class IconListener extends IndexListener {
    }

    class TitleListener extends IndexListener {
    }


    class DragListener implements PropertyChangeListener {
        private int len;

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

    class MaximizedListener implements PropertyChangeListener {

        public MaximizedListener() {
        }

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            if (descriptor.getToolWindow().getType() == ToolWindowType.DOCKED) {
                if ((Boolean) evt.getNewValue()) {
                    descriptor.setTempDivederLocation(getSplitDividerLocation());

                    ToolWindowAnchor opposite = null;
                    switch (descriptor.getToolWindow().getAnchor()) {
                        case LEFT:
                            opposite = ToolWindowAnchor.RIGHT;
                            break;
                        case RIGHT:
                            opposite = ToolWindowAnchor.LEFT;
                            break;
                        case TOP:
                            opposite = ToolWindowAnchor.BOTTOM;
                            break;
                        case BOTTOM:
                            opposite = ToolWindowAnchor.TOP;
                            break;
                    }
                    
                    for (ToolWindow tool : descriptor.getManager().getToolsByAnchor(opposite)) 
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


