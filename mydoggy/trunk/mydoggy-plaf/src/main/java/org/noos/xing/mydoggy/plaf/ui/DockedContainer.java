package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.common.Question;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowActiveButton;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.util.CleanablePropertyChangeSupport;
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.util.cleaner.Cleaner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Angelo De Caro
 */
public class DockedContainer implements ToolWindowContainer, Cleaner {
    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected transient ResourceManager resourceManager;

    protected JPanel container;
    protected JPanel titleBar;
    protected ToolWindowTabPanel titleBarTabs;
    protected TitleBarButtons titleBarButtons;
    protected JPanel componentContainer;

    protected TitleBarMouseAdapter titleBarMouseAdapter;

    protected CleanablePropertyChangeSupport propertyChangeSupport;

    protected Component focusRequester;
    protected PopupUpdater popupUpdater;

    boolean valueAdjusting;


    public DockedContainer(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
        this.resourceManager = descriptor.getResourceManager();

        descriptor.getCleaner().addCleaner(this);

        initComponents();
        initListeners();
    }


    public void cleanup() {
        // Clean components
        container.putClientProperty(ToolWindow.class, null);
        container.removeAll();

        // Finalize
        popupUpdater = null;
        toolWindow = null;
        descriptor = null;
        resourceManager = null;
    }

    public void updateUI() {
        SwingUtilities.updateComponentTreeUI(getContentContainer());
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void showPopupMenu(Component c, int x, int y) {
        titleBarMouseAdapter.showPopupMenu(c, x, y);
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }


    public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(property, listener);
    }

    public ToolWindowDescriptor getToolWindowDescriptor() {
        return descriptor;
    }

    public Container getContentContainer() {
        return container;
    }

    public TitleBarButtons getTitleBarButtons() {
        return titleBarButtons;
    }

    public ToolWindowTabPanel getTitleBarTabs() {
        return titleBarTabs;
    }

    public Component getTitleBar() {
        return titleBar;
    }

    public void setMainComponent(Component component) {
        componentContainer.removeAll();
        descriptor.setComponent(component);
        componentContainer.add(component, "0,0,FULL,FULL");

        SwingUtil.repaint(componentContainer);
    }

    public MouseListener getTitleBarMouseAdapter() {
        return titleBarMouseAdapter;
    }

    public void setPopupUpdater(PopupUpdater popupUpdater) {
        this.popupUpdater = popupUpdater;
    }


    protected void initComponents() {
        propertyChangeSupport = new CleanablePropertyChangeSupport(this);
        descriptor.getCleaner().addCleaner(propertyChangeSupport);

        titleBarMouseAdapter = new TitleBarMouseAdapter();

        // Container
        container = (JPanel) resourceManager.createComponent(MyDoggyKeySpace.TOOL_WINDOW_CONTAINER, null);
        container.setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL}, {resourceManager.getFloat("toolwindow.title.font.size", 12) + 4,
                                                                                        TableLayout.FILL}}, false));
        container.setName("toolWindow.container." + toolWindow.getId());
        container.setFocusTraversalPolicyProvider(true);
        container.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
        container.setFocusCycleRoot(true);
        container.setFocusable(false);
        container.putClientProperty(ToolWindow.class, toolWindow);

        String id = toolWindow.getId();

        // Title Bar
        ExtendedTableLayout titleBarLayout = new ExtendedTableLayout(new double[][]{{3, TableLayout.FILL, 2, -2, 3},
                                                                                    {0, resourceManager.getFloat("toolwindow.title.font.size", 12) + 4, 0}}, false);
        titleBar = (JPanel) resourceManager.createComponent(
                MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR, descriptor.getManager(),
                descriptor,
                this
        );
        titleBar.setLayout(titleBarLayout);
        titleBar.setName("toolWindow.titleBar." + toolWindow.getId());
        titleBar.setEnabled(false);
        titleBar.setBorder(null);
        titleBar.addMouseListener(titleBarMouseAdapter);

        if (descriptor.isIdVisibleOnTitleBar())
            titleBarLayout.setColumn(0, titleBar.getFontMetrics(
                    titleBar.getFont()
            ).stringWidth(resourceManager.getUserString(id)) + 12);

        // Tabs
        titleBarTabs = new ToolWindowTabPanel(this, descriptor);
        toolWindow.getToolWindowTabs()[0].setSelected(true);

        // Buttons
        titleBarButtons = resourceManager.createInstance(TitleBarButtons.class, descriptor, this);

        // Set TitleBar content
        titleBar.add(titleBarTabs, "1,1");
        titleBar.add(titleBarButtons.getButtonsContainer(), "3,1,right,c");

        // Set Component container
        componentContainer = new JPanel();
        componentContainer.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        componentContainer.setOpaque(false);
        componentContainer.add(descriptor.getComponent(), "0,0,FULL,FULL");

        // Set Container content
        container.add(titleBar, "0,0");
        container.add(componentContainer, "0,1");

        focusRequester = SwingUtil.findFocusable(descriptor.getComponent());
        if (focusRequester == null) {
            titleBarButtons.getFocusable().setFocusable(true);
            focusRequester = titleBarButtons.getFocusable();
        }
        titleBarButtons.toolWindowTypeChanged(ToolWindowType.DOCKED);
    }

    protected void initListeners() {
        addPropertyChangeListener("active", new ActivePropertyChangeListener());
        addPropertyChangeListener("type", new TypePropertyChangeListener());
        addPropertyChangeListener("maximized.before", new MaximizedBeforePropertyChangeListener());

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
                "focusOwner",
                new FocusOwnerPropertyChangeListener(
                        resourceManager.createInstance(ParentOfQuestion.class, container, toolWindow))
        );

        addPropertyChangeListener("parentComponent.closed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                toolWindow.setFlashing(false);
            }
        });

        toolWindow.addToolWindowListener(new DockedToolWindowListener());
    }

    protected void assignFocus() {
        focusRequester = SwingUtil.findFocusable(descriptor.getComponent());
        if (focusRequester == null) {
            titleBarButtons.getFocusable().setFocusable(true);
            focusRequester = titleBarButtons.getFocusable();
        } else {
            titleBarButtons.getFocusable().setFocusable(false);
        }
        SwingUtil.requestFocus(focusRequester);
    }

    protected void enableIdOnTitleBar() {
        TableLayout layout = (TableLayout) titleBar.getLayout();
        layout.setColumn(0,
                         titleBar
                                 .getFontMetrics(titleBar.getFont())
                                 .stringWidth(
                                         resourceManager.getUserString(toolWindow.getId())
                                 )
                         + 12);

        SwingUtil.repaint(titleBar);
    }

    protected void disableIdOnTitleBar() {
        TableLayout layout = (TableLayout) titleBar.getLayout();
        layout.setColumn(0, 3);

        SwingUtil.repaint(titleBar);
    }


    public interface PopupUpdater {

        void update(Component source, JPopupMenu popupMenu);

    }


    protected class TitleBarMouseAdapter extends MouseAdapter implements Cleaner, ActionListener, PropertyChangeListener {
        protected JPopupMenu popupMenu;

        protected JMenuItem visible;
        protected JMenuItem aggregate;
        protected JMenu aggregateMenu;
        protected JCheckBoxMenuItem floatingMode;
        protected JCheckBoxMenuItem floatingLiveMode;
        protected JCheckBoxMenuItem dockedMode;
        protected JCheckBoxMenuItem pinnedMode;

        protected JMenu old;

        protected JMenuItem maximize;
        protected JMenu moveTo;
        protected JMenuItem right;
        protected JMenuItem left;
        protected JMenuItem top;
        protected JMenuItem bottom;

        protected ToolWindowType oldType;

        public TitleBarMouseAdapter() {
            descriptor.getCleaner().addBefore(DockedContainer.this, this);

            initPopupMenu();

            descriptor.getToolWindow().addPlafPropertyChangeListener(this);
            addPropertyChangeListener("type", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getSource() != descriptor ||
                        (evt.getNewValue() != ToolWindowType.FLOATING &&
                         evt.getNewValue() != ToolWindowType.FLOATING_FREE))
                        return;

                    oldType = (ToolWindowType) evt.getOldValue();
                }
            });
        }


        public void cleanup() {
            descriptor.getToolWindow().removePlafPropertyChangeListener(this);
        }

        public void mouseClicked(MouseEvent e) {
            if (!toolWindow.isAvailable())
                return;

            if (SwingUtilities.isLeftMouseButton(e)) {
                toolWindow.setActive(true);

                if (e.getClickCount() == 2)
                    toolWindow.setMaximized(!toolWindow.isMaximized());
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (((DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED)).isPopupMenuEnabled())
                    showPopupMenu(e.getComponent(), e.getX(), e.getY());
            }
        }

        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if ("visible".equals(actionCommand)) {
                if (toolWindow.isActive()) {

                    toolWindow.setActive(false);
                    descriptor.hideToolWindow();
                } else if (toolWindow.isVisible()) {
                    descriptor.hideToolWindow();
                } else
                    toolWindow.setActive(true);
            } else if (actionCommand.startsWith("aggregate")) {
                if (toolWindow.isActive()) {
                    toolWindow.setActive(false);
                    toolWindow.setVisible(false);
                } else if (toolWindow.isVisible())
                    toolWindow.setVisible(false);
                else {
                    if (actionCommand.endsWith("left"))
                        toolWindow.aggregate(AggregationPosition.LEFT);
                    else if (actionCommand.endsWith("right"))
                        toolWindow.aggregate(AggregationPosition.RIGHT);
                    else if (actionCommand.endsWith("top"))
                        toolWindow.aggregate(AggregationPosition.TOP);
                    else if (actionCommand.endsWith("bottom"))
                        toolWindow.aggregate(AggregationPosition.BOTTOM);
                    else
                        toolWindow.aggregate();

                    toolWindow.setActive(true);
                }
            } else if ("move.right".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.RIGHT);
            } else if ("move.left".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.LEFT);
            } else if ("move.top".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.TOP);
            } else if ("move.bottom".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.BOTTOM);
            } else if ("floating".equals(actionCommand)) {
                if (floatingMode.isSelected()) {
                    toolWindow.setType((descriptor.isFloatingWindow()) ? ToolWindowType.FLOATING_FREE : ToolWindowType.FLOATING);
                    dockedMode.setVisible(!floatingMode.isSelected());
                } else {
                    toolWindow.setType(oldType != null ? oldType : ToolWindowType.DOCKED);
                }
            } else if ("floatingLive".equals(actionCommand)) {
                toolWindow.setType(floatingLiveMode.isSelected() ? ToolWindowType.FLOATING_LIVE : ToolWindowType.DOCKED);
            } else if ("docked".equals(actionCommand)) {
                toolWindow.setType(dockedMode.isSelected() ? ToolWindowType.DOCKED : ToolWindowType.SLIDING);
            } else if ("pinned".equals(actionCommand)) {
                toolWindow.setAutoHide(!toolWindow.isAutoHide());
            } else if ("maximize".equals(actionCommand)) {
                toolWindow.setMaximized(!toolWindow.isMaximized());
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if ("autoHide".equals(evt.getPropertyName())) {
                pinnedMode.setState(!(Boolean) evt.getNewValue());
            } else if ("type".equals(evt.getPropertyName())) {
                ToolWindowType type = (ToolWindowType) evt.getNewValue();
                dockedMode.setState(type == ToolWindowType.DOCKED);
                dockedMode.setVisible(type != ToolWindowType.FLOATING);
                pinnedMode.setVisible(type != ToolWindowType.SLIDING);

                floatingMode.setState(type == ToolWindowType.FLOATING);
            } else if ("UI".equals(evt.getPropertyName())) {
                SwingUtilities.updateComponentTreeUI(popupMenu);

                DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
                SwingUtilities.updateComponentTreeUI(descriptor.getToolsMenu());
            }
        }

        public void showPopupMenu(Component source, int x, int y) {
            if (source == titleBar ||
                SwingUtil.hasParent(source, titleBar) ||
                source instanceof ToolWindowDescriptor.RepresentativeAnchor) {

                popupMenu.removeAll();
                popupMenu.add(pinnedMode);
                popupMenu.add(dockedMode);
                popupMenu.add(floatingMode);
                popupMenu.add(floatingLiveMode);
                popupMenu.add(moveTo);
                popupMenu.add(maximize);
                popupMenu.addSeparator();
                popupMenu.add(visible);
                popupMenu.add(aggregate);
                popupMenu.add(aggregateMenu);

                enableVisible();
                enableMoveToItem();
                enableUserDefined();
                enableMaximize();

                if (popupUpdater != null)
                    popupUpdater.update(source, popupMenu);

                popupMenu.show(source, x, y);
            }

        }


        protected void initPopupMenu() {
            popupMenu = new JPopupMenu("ToolWindowBarPopupMenu");
            popupMenu.setLightWeightPopupEnabled(false);

            // Visible
            visible = new JMenuItem();
            visible.setName("toolWindow.popup.visible." + toolWindow.getId());
            visible.setActionCommand("visible");
            visible.addActionListener(this);

            aggregate = new JMenuItem();
            aggregate.setName("toolWindow.popup.aggregate." + toolWindow.getId());
            aggregate.setText(resourceManager.getString("@@tool.aggregate"));
            aggregate.setActionCommand("aggregate");
            aggregate.addActionListener(this);

            aggregateMenu = new JMenu(resourceManager.getString("@@tool.aggregateMenu"));

            JMenuItem aggregateLeft = new JMenuItem();
            aggregateLeft.setName("toolWindow.popup.aggregate.left." + toolWindow.getId());
            aggregateLeft.setText(resourceManager.getString("@@tool.aggregate.left"));
            aggregateLeft.setActionCommand("aggregate.left");
            aggregateLeft.addActionListener(this);
            aggregateMenu.add(aggregateLeft);

            JMenuItem aggregateRight = new JMenuItem();
            aggregateRight.setName("toolWindow.popup.aggregate.right." + toolWindow.getId());
            aggregateRight.setText(resourceManager.getString("@@tool.aggregate.right"));
            aggregateRight.setActionCommand("aggregate.right");
            aggregateRight.addActionListener(this);
            aggregateMenu.add(aggregateRight);

            JMenuItem aggregateTop = new JMenuItem();
            aggregateTop.setName("toolWindow.popup.aggregate.top." + toolWindow.getId());
            aggregateTop.setText(resourceManager.getString("@@tool.aggregate.top"));
            aggregateTop.setActionCommand("aggregate.top");
            aggregateTop.addActionListener(this);
            aggregateMenu.add(aggregateTop);

            JMenuItem aggregateBottom = new JMenuItem();
            aggregateBottom.setName("toolWindow.popup.aggregate.bottom." + toolWindow.getId());
            aggregateBottom.setText(resourceManager.getString("@@tool.aggregate.bottom"));
            aggregateBottom.setActionCommand("aggregate.bottom");
            aggregateBottom.addActionListener(this);
            aggregateMenu.add(aggregateBottom);

            floatingMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING);
            floatingMode.setText(resourceManager.getString("@@tool.mode.floating"));
            floatingMode.setActionCommand("floating");
            floatingMode.addActionListener(this);

            floatingLiveMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING_LIVE);
            floatingLiveMode.setName("toolWindow.popup.floatingLive." + toolWindow.getId());
            floatingLiveMode.setText(resourceManager.getString("@@tool.mode.floatingLive"));
            floatingLiveMode.setActionCommand("floatingLive");
            floatingLiveMode.addActionListener(this);

            dockedMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.DOCKED);
            dockedMode.setText(resourceManager.getString("@@tool.mode.docked"));
            dockedMode.setActionCommand("docked");
            dockedMode.addActionListener(this);

            pinnedMode = new JCheckBoxMenuItem(null, !toolWindow.isAutoHide());
            pinnedMode.setText(resourceManager.getString("@@tool.mode.pinned"));
            pinnedMode.setActionCommand("pinned");
            pinnedMode.addActionListener(this);

            maximize = new JMenuItem();
            maximize.setText(resourceManager.getString("@@tool.maximize"));
            maximize.setActionCommand("maximize");
            maximize.addActionListener(this);

            // MoveTo SubMenu
            moveTo = new JMenu();
            moveTo.getPopupMenu().setLightWeightPopupEnabled(false);
            moveTo.setText(resourceManager.getString("@@tool.moveTo"));

            right = new JMenuItem();
            right.setText(resourceManager.getString("@@tool.move.right"));
            right.setActionCommand("move.right");
            right.addActionListener(this);

            left = new JMenuItem();
            left.setText(resourceManager.getString("@@tool.move.left"));
            left.setActionCommand("move.left");
            left.addActionListener(this);

            top = new JMenuItem();
            top.setText(resourceManager.getString("@@tool.move.top"));
            top.setActionCommand("move.top");
            top.addActionListener(this);

            bottom = new JMenuItem();
            bottom.setText(resourceManager.getString("@@tool.move.bottom"));
            bottom.setActionCommand("move.bottom");
            bottom.addActionListener(this);

            moveTo.add(right);
            moveTo.add(left);
            moveTo.add(top);
            moveTo.add(bottom);

            popupMenu.add(pinnedMode);
            popupMenu.add(dockedMode);
            popupMenu.add(floatingMode);
            popupMenu.add(floatingLiveMode);
            popupMenu.add(moveTo);
            popupMenu.add(maximize);
            popupMenu.addSeparator();
            popupMenu.add(visible);
            popupMenu.add(aggregate);
            popupMenu.add(aggregateMenu);
        }

        protected void enableVisible() {
            aggregate.setVisible(!toolWindow.isVisible());
            aggregateMenu.setVisible(aggregate.isVisible());
            visible.setText(toolWindow.isVisible() ?
                            resourceManager.getString("@@tool.hide") :
                            resourceManager.getString("@@tool.show"));

            if (toolWindow.getType() == ToolWindowType.DOCKED) {
                dockedMode.setVisible(descriptor.getTypeDescriptor(ToolWindowType.SLIDING).isEnabled());
                floatingMode.setVisible(descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
                floatingLiveMode.setState(false);
                floatingLiveMode.setVisible(descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).isEnabled());
            } else if (toolWindow.getType() == ToolWindowType.SLIDING) {
                floatingMode.setVisible(descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
                floatingLiveMode.setState(false);
                floatingLiveMode.setVisible(descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).isEnabled());
            } else if (toolWindow.getType() == ToolWindowType.FLOATING) {
                floatingLiveMode.setState(false);
                floatingLiveMode.setVisible(descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).isEnabled());
            } else if (toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
                dockedMode.setState(false);
                floatingMode.setState(false);
                floatingMode.setVisible(descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
            }
        }

        protected void enableMoveToItem() {
            ToolWindowAnchor anchor = toolWindow.getAnchor();
            if (anchor == ToolWindowAnchor.LEFT) {
                left.setVisible(false);
                right.setVisible(true);
                top.setVisible(true);
                bottom.setVisible(true);
            } else if (anchor == ToolWindowAnchor.RIGHT) {
                left.setVisible(true);
                right.setVisible(false);
                top.setVisible(true);
                bottom.setVisible(true);
            } else if (anchor == ToolWindowAnchor.BOTTOM) {
                left.setVisible(true);
                right.setVisible(true);
                top.setVisible(true);
                bottom.setVisible(false);
            } else if (anchor == ToolWindowAnchor.TOP) {
                left.setVisible(true);
                right.setVisible(true);
                top.setVisible(false);
                bottom.setVisible(true);
            }
        }

        protected void enableUserDefined() {
            DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
            if (old != null) {
                popupMenu.remove(old);
            }

            JMenu menu = descriptor.getToolsMenu();
            if (menu.getMenuComponentCount() > 0) {
                popupMenu.add(menu, 4);
                old = menu;
            }
        }

        protected void enableMaximize() {
            maximize.setVisible(toolWindow.isVisible());
            maximize.setText(toolWindow.isMaximized() ?
                             resourceManager.getString("@@tool.maximize.restore") :
                             resourceManager.getString("@@tool.maximize"));

        }
    }

    protected class DockedToolWindowListener implements Cleaner, ToolWindowListener, PropertyChangeListener {

        public DockedToolWindowListener() {
            descriptor.getCleaner().addBefore(DockedContainer.this, this);

            for (ToolWindowTab tab : toolWindow.getToolWindowTabs())
                tab.addPropertyChangeListener(this);
        }

        public void cleanup() {
            toolWindow.removeToolWindowListener(this);

            for (ToolWindowTab tab : toolWindow.getToolWindowTabs())
                tab.removePropertyChangeListener(this);
        }

        public boolean toolWindowTabRemoving(ToolWindowTabEvent event) {
            return true;
        }

        public void toolWindowTabAdded(ToolWindowTabEvent event) {
            ToolWindowTab tab = event.getToolWindowTab();
            tab.addPropertyChangeListener(this);
        }

        public void toolWindowTabRemoved(ToolWindowTabEvent event) {
            if (toolWindow.getToolWindowTabs().length == 0) {
                componentContainer.remove(event.getToolWindowTab().getComponent());
                SwingUtil.repaint(componentContainer);
            }

            event.getToolWindowTab().removePropertyChangeListener(this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            final ToolWindowTab tab = (ToolWindowTab) evt.getSource();
            String property = evt.getPropertyName();

            if ("selected".equals(property)) {
                if (evt.getNewValue() == Boolean.TRUE) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setMainComponent(tab.getComponent());

                            Component focusable = SwingUtil.findFocusable(tab.getComponent());
                            if (focusable != null)
                                focusable.requestFocus();
                            else
                                titleBarButtons.getFocusable().requestFocus();
                        }
                    });
                }
            } else if ("component".equals(property)) {
                if (descriptor.getComponent() == evt.getOldValue())
                    setMainComponent(tab.getComponent());
            }

        }
    }

    protected class FocusOwnerPropertyChangeListener implements PropertyChangeListener, Cleaner {
        protected Question parentOf;

        public FocusOwnerPropertyChangeListener(Question parentOf) {
            this.parentOf = parentOf;

            descriptor.getCleaner().addBefore(DockedContainer.this, this);
        }

        public void cleanup() {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (!toolWindow.isVisible() || valueAdjusting)
                return;

            Component component = (Component) evt.getNewValue();
            if (component == null) return;
            if (component instanceof JRootPane) return;

            valueAdjusting = true;

//            System.out.println(toolWindow.getId() + " - cmp = " + component);

            if (parentOf.is(component)) {
                toolWindow.setActive(true);
                if (focusRequester == null)
                    focusRequester = component;
                else {
                    if (!(focusRequester instanceof ToolWindowActiveButton))
                        focusRequester = component;
                    else {
                        if (focusRequester == titleBarButtons.getFocusable())
                            assignFocus();
                        else
                            focusRequester.requestFocusInWindow();
                    }
                }
            } else {
                descriptor.getToolBar().deactiveTool(toolWindow);

                if (toolWindow.isAutoHide() && toolWindow.getType() != ToolWindowType.EXTERN)
                    toolWindow.setVisible(false);
            }

            valueAdjusting = false;
        }

    }

    protected class ActivePropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor)
                return;

            boolean active = (Boolean) evt.getNewValue();
            titleBar.setEnabled(active);

            boolean found = false;
            for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                if (tab.isSelected()) {
                    found = true;
                    break;
                }
            }

            if (!found && toolWindow.getToolWindowTabs().length > 0)
                toolWindow.getToolWindowTabs()[0].setSelected(true);

            if (active && focusRequester != null && !valueAdjusting) {
//                System.out.println("focusRequester = " + focusRequester);
                if (focusRequester == titleBarButtons.getFocusable())
                    assignFocus();
                else
                    SwingUtil.requestFocus(focusRequester);

            }
        }

    }

    protected class TypePropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor)
                return;

            if (evt.getNewValue() == ToolWindowType.DOCKED) {
                titleBarButtons.toolWindowTypeChanged(ToolWindowType.DOCKED);
            }

            if (evt.getOldValue() == ToolWindowType.EXTERN) {
                setMainComponent(toolWindow.getToolWindowTabs()[0].getComponent());
            }
        }

    }

    protected class MaximizedBeforePropertyChangeListener implements PropertyChangeListener {
        ByteArrayOutputStream workspace;
        boolean valueAdj = false;

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor)
                return;

            if ((Boolean) evt.getNewValue()) {
                descriptor.getManager().getPersistenceDelegate().save(workspace = new ByteArrayOutputStream());
            } else if (workspace != null) {
                if (valueAdj)
                    return;

                valueAdj = true;
                try {
                    descriptor.getManager().getPersistenceDelegate().merge(new ByteArrayInputStream(workspace.toByteArray()),
                                                                           resourceManager.getObject(PersistenceDelegate.MergePolicy.class,
                                                                                                     PersistenceDelegate.MergePolicy.UNION));
                    workspace = null;
                } finally {
                    valueAdj = false;
                }
            }
        }

    }

}
