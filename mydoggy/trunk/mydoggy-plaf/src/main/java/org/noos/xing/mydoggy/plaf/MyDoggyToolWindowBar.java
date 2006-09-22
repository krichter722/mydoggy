package org.noos.xing.mydoggy.plaf;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowBarDropTarget;
import org.noos.xing.mydoggy.plaf.ui.icons.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

    private ToolWindowManager manager;

    private ToolWindowAnchor anchor;

    // Bar Components
    private JToolScrollBar toolScrollBar;
    private JPanel contentPane;
    private TableLayout contentPaneLayout;

    private JSplitPane splitPane;
    private int availableTools;
    private int orientation;
    private boolean horizontal;

    private Map<String, PropertyChangeListener> listeners;

    public MyDoggyToolWindowBar(ToolWindowManager manager, JSplitPane splitPane, ToolWindowAnchor anchor) {
        this.manager = manager;
        this.splitPane = splitPane;
        splitPane.setDividerLocation(0);
        this.anchor = anchor;
        this.availableTools = 0;

        initComponents();
        initListeners();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        listeners.get(evt.getPropertyName()).propertyChange(evt);
    }

    public String toString() {
        return getClass().getName() + "[anchor : " + anchor + "]";
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

    protected void initComponents() {
        splitPane.setName(anchor.toString());
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
        this.listeners = new Hashtable<String, PropertyChangeListener>();
        listeners.put("available", new AvailableListener());
        listeners.put("visible.before", new VisibleBeforeListener());
        listeners.put("visible.DOCKED", new VisibleDockedListener());
        listeners.put("visible.FLOATING", new VisibleFloatingListener());
        listeners.put("visible.FLOATING_WINDOW", new VisibleFloatingWindowListener());
        listeners.put("visible.SLIDING", new VisibleSlidingListener());
        listeners.put("visible", new VisibleListener());
        listeners.put("active.before", new ActiveBeforeListener());
        listeners.put("active", new ActiveListener());
        listeners.put("type", new TypeListener());

        listeners.put("index", new IndexListener());
        listeners.put("title", new TitleListener());
        listeners.put("icon", new IconListener());
    }


    class AvailableListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            if (descriptor.getToolWindow().getType() != ToolWindowType.FLOATING_WINDOW) {
                boolean oldAvailable = (Boolean) evt.getOldValue();
                boolean newAvailable = (Boolean) evt.getNewValue();

                boolean repaint = false;
                JLabel barLabel = descriptor.getBarLabel(contentPane);

                if (oldAvailable && !newAvailable) {
                    // true -> false
                    removeLabel(barLabel, descriptor);
                    repaint = true;
                } else if (!oldAvailable && newAvailable) {
                    // false -> true
                    addLabel(barLabel);
                    repaint = true;
                }

                if (repaint) {
                    barLabel.setEnabled(newAvailable);
                    SwingUtil.repaint(contentPane);
                }
            }
        }

        protected void addLabel(JLabel barLabel) {
            try {
                availableTools++;
                if (horizontal) {
                    int width = barLabel.getPreferredSize().width + 6;

                    contentPaneLayout.insertColumn(contentPaneLayout.getNumColumn(), contentPaneLayout.getNumColumn() > 0 ? 5 : 1);
                    contentPaneLayout.insertColumn(contentPaneLayout.getNumColumn(), width);

                    if (anchor.getIndex() >= 0) {
                        Component[] components = contentPane.getComponents();
                        int finalCol = (anchor.getIndex() * 2 + 2);
                        for (Component component : components) {
                            TableLayoutConstraints constraints = contentPaneLayout.getConstraints(component);
                            if (constraints.col1 >= finalCol) {
                                contentPaneLayout.setConstraints(component,
                                                           new TableLayoutConstraints(
                                                                   (constraints.col1 + 2) + ",1,"
                                                           ));
                            }
                        }
                        contentPane.add(barLabel, (anchor.getIndex() * 2 + 2) + ",1,");
                    } else
                        contentPane.add(barLabel, (contentPaneLayout.getNumColumn() - 1) + ",1,");
                } else {
                    int height = Math.max(barLabel.getWidth(), Math.max(barLabel.getPreferredSize().height, barLabel.getSize().height)) + 12;

                    contentPaneLayout.insertRow(contentPaneLayout.getNumRow(), contentPaneLayout.getNumRow() > 0 ? 5 : 1);
                    contentPaneLayout.insertRow(contentPaneLayout.getNumRow(), height);

                    if (anchor.getIndex() >= 0) {
                        Component[] components = contentPane.getComponents();
                        int finalRow = (anchor.getIndex() * 2 + 2);
                        for (Component component : components) {
                            TableLayoutConstraints constraints = contentPaneLayout.getConstraints(component);
                            if (constraints.row1 >= finalRow) {
                                contentPaneLayout.setConstraints(component,
                                                           new TableLayoutConstraints(
                                                                   "1," + (constraints.row1 + 2)
                                                           ));
                            }
                        }
                        contentPane.add(barLabel, "1," + (anchor.getIndex() * 2 + 2));
                    } else
                        contentPane.add(barLabel, "1," + (contentPaneLayout.getNumRow() - 1));
                }
            } finally {
                anchor.setIndex(-1);
            }
        }

        protected void removeLabel(JLabel barLabel, ToolWindowDescriptor descriptor) {
            // Remove
            availableTools--;

            int toDelete;
            if (horizontal) {
                toDelete = contentPaneLayout.getConstraints(barLabel).col1;
            } else {
                toDelete = contentPaneLayout.getConstraints(barLabel).row1;
            }
            contentPane.remove(barLabel);
            if (horizontal) {
                contentPaneLayout.deleteColumn(toDelete);
                contentPaneLayout.deleteColumn(toDelete - 1);
            } else {
                contentPaneLayout.deleteRow(toDelete);
                contentPaneLayout.deleteRow(toDelete - 1);
            }
            descriptor.resetBarLabel();
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
                    toolWindow.setActive(false);
                }
            }
        }
    }

    class ActiveListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            toolWindowDescriptor.getToolWindowContainer().propertyChange(evt);
        }
    }


    class TypeListener extends AvailableListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();

            if (evt.getOldValue() == ToolWindowType.FLOATING_WINDOW) {
                addLabel(toolWindowDescriptor.getBarLabel(contentPane));
                SwingUtil.repaint(contentPane);
            } else if (evt.getNewValue() == ToolWindowType.FLOATING_WINDOW &&
                       toolWindowDescriptor.getBarLabel() != null) {

                removeLabel(toolWindowDescriptor.getBarLabel(), toolWindowDescriptor);
                SwingUtil.repaint(contentPane);
            }

        }
    }


    class VisibleBeforeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindow sourceTool = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            if (!oldValue && newValue) { // false and true
                ToolWindow[] toolWindows = manager.getToolsByAnchor(getAnchor());
                for (ToolWindow toolWindow : toolWindows) {
                    if (toolWindow == sourceTool)
                        continue;

                    if (MyDoggyToolWindowManager.currentGroup == null) {
                        if (toolWindow.getType() == ToolWindowType.FLOATING ||
                            toolWindow.getType() == ToolWindowType.FLOATING_WINDOW)
                            continue;

                        if (toolWindow.getAnchor().equals(sourceTool.getAnchor()))
                            toolWindow.setVisible(false);
                        else if (toolWindow.isAutoHide() || toolWindow.getType() == ToolWindowType.SLIDING)
                            toolWindow.setVisible(false);
                    }
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
        private Animation animation = new Animation();

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            boolean visible = (Boolean) evt.getNewValue();

            Component content = (visible) ? descriptor.getComponent() : null;
            if (content != null) {
                DockedContainer container = (DockedContainer) descriptor.getToolWindowContainer();
                content = container.getContentContainer();
            }

            if (content == null || descriptor.getDivederLocation() > 0 && splitPane.getDividerSize() != 0) {
                if (MyDoggyToolWindowManager.currentGroup == null) {
                    switch (anchor) {
                        case LEFT:
                        case TOP:
                            descriptor.setDivederLocation(splitPane.getDividerLocation());
                            break;
                        case RIGHT:
                            descriptor.setDivederLocation(splitPane.getWidth() - splitPane.getDividerLocation());
                            break;
                        case BOTTOM:
                            descriptor.setDivederLocation(splitPane.getHeight() - splitPane.getDividerLocation());
                    }
                }
            }

            if (content == null && descriptor.getToolWindow().isVisible())
                return;

            int divederLocation = descriptor.getDivederLocation();

            Object component = getSplitPaneContent();
            boolean animate = true;
            if (component != null) {
                if (component instanceof MultiSplitContainer) {
                    MultiSplitContainer multiSplitContainer = (MultiSplitContainer) component;
                    if (MyDoggyToolWindowManager.currentGroup != null) {
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
                        }
                    }
                } else if (MyDoggyToolWindowManager.currentGroup != null && content != null) {
                    MultiSplitContainer container = new MultiSplitContainer(orientation);
                    container.addContent(content);

                    setSplitPaneContent(container);
                } else if (content != null)
                    setSplitPaneContent(content);
            } else {
                if (MyDoggyToolWindowManager.currentGroup != null && content != null) {
                    MultiSplitContainer container = new MultiSplitContainer(orientation);
                    container.addContent(content);

                    setSplitPaneContent(container);
                } else if (content != null)
                    setSplitPaneContent(content);
            }

            if (animate) {
                if (content != null) {
                    splitPane.setDividerSize(5);
                    animation.show(divederLocation);
                } else {
                    splitPane.setDividerSize(0);
                    animation.hide(divederLocation);
                }
            } else {
                SwingUtil.repaint(splitPane);
            }

        }

        protected void setSplitPaneContent(Component content) {
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


        private class Animation implements ActionListener {
            private static final int INCOMING = 1;
            private static final int OUTGOING = -1;
            private static final float ANIMATION_DURATION = 100f;
            private static final int ANIMATION_SLEEP = 1;

            private boolean animating;
            private int animationDirection;
            private Timer animationTimer;
            private long animationStart;

            private int dividerLocation;
            private int sheetHeight;

            public void actionPerformed(ActionEvent e) {
                if (animating) {
                    // calculate height to show
                    float animationPercent = (System.currentTimeMillis() - animationStart) / ANIMATION_DURATION;
                    animationPercent = Math.min(1.0f, animationPercent);
                    int animatingHeight;

                    if (animationDirection == INCOMING)
                        animatingHeight = (int) (animationPercent * sheetHeight);
                    else
                        animatingHeight = (int) ((1.0f - animationPercent) * sheetHeight);

                    switch (anchor) {
                        case LEFT:
                        case TOP:
                            if (animationDirection == INCOMING) {
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


                    if (animationPercent >= 1.0f) {
                        stopAnimation();
                        finishAnimation();
                    }
                }
            }

            public synchronized void show(int divederLocation) {
                if (animating) {
                    stopAnimation();
                    finishAnimation();
                }
                this.dividerLocation = divederLocation;
                startAnimation(INCOMING);
            }

            public synchronized void hide(int divederLocation) {
                if (animating) {
                    stopAnimation();
                    finishAnimation();
                }
                this.dividerLocation = divederLocation;
                startAnimation(OUTGOING);
            }


            private void startAnimation(int incoming) {
                if (!animating) {
                    sheetHeight = dividerLocation;
                    animationDirection = incoming;

                    // start animation timer
                    animationStart = System.currentTimeMillis();
                    if (animationTimer == null)
                        animationTimer = new Timer(ANIMATION_SLEEP, this);
                    animating = true;
                    animationTimer.start();
                }
            }

            private void stopAnimation() {
                animationTimer.stop();
                animating = false;
            }

            private void finishAnimation() {
                if (splitPane.getDividerSize() == 0) {
                    setSplitPaneContent(null);
                }
//                else
//                    splitPane.setDividerLocation(dividerLocation);
            }

        }

    }

    class VisibleFloatingListener implements PropertyChangeListener {

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

    class VisibleFloatingWindowListener implements PropertyChangeListener {

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

            container.setVisible(visible, getContentPane());
        }
    }


    class IndexListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            JLabel barLabel = descriptor.getBarLabel();
            if (barLabel != null) {
                TableLayoutConstraints constraints = contentPaneLayout.getConstraints(barLabel);

                if (horizontal) {
                    int width = barLabel.getPreferredSize().width + 6;
                    contentPaneLayout.setColumn(constraints.col1, width);
                } else {
                    int height = Math.max(barLabel.getWidth(), Math.max(barLabel.getPreferredSize().height, barLabel.getSize().height)) + 12;
                    contentPaneLayout.setRow(constraints.row1, height);
                }

                SwingUtil.repaint(contentPane);
            }
        }
    }

    class IconListener extends IndexListener { }

    class TitleListener extends IndexListener { }

}
