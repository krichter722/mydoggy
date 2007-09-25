package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowActiveButton;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Angelo De Caro
 */
public class DockedContainer implements PropertyChangeListener, ToolWindowContainer {
    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected ResourceManager resourceManager;

    protected JPanel container;

    protected JPanel titleBar;
    protected ToolWindowTabPanel titleBarTabs;

    protected Component focusRequester;

    protected JButton floatingButton;
    protected JButton pinButton;
    protected JButton dockButton;
    protected JButton hideButton;
    protected JButton maximizeButton;

    protected TitleBarMouseAdapter titleBarMouseAdapter;
    protected PropertyChangeSupport propertyChangeSupport;

    protected PopupUpdater popupUpdater;

    boolean valueAdjusting;


    public DockedContainer(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
        this.resourceManager = descriptor.getResourceManager();

        initDockedComponents();
        initDockedListeners();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }

    public void updateUI() {
        SwingUtilities.updateComponentTreeUI(getContentContainer());
    }

    public void uninstall() {
        Component cmp = descriptor.getComponent();
        cmp.removeMouseListener(titleBarMouseAdapter);
    }


    public Container getContentContainer() {
        return container;
    }

    public void setMainComponent(Component component) {
        container.remove(descriptor.getComponent());
        descriptor.setComponent(component);
        container.add(component, "0,1");

        SwingUtil.repaint(container);
    }

    public MouseListener getTitleBarMouseAdapter() {
        return titleBarMouseAdapter;
    }

    public void setPopupUpdater(PopupUpdater popupUpdater) {
        this.popupUpdater = popupUpdater;
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(property, listener);
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    
    protected void setPinVisible(boolean visible) {
        pinButton.setVisible(visible);
        TableLayout tableLayout = (TableLayout) titleBar.getLayout();
        tableLayout.setColumn(7, (visible) ? 17 : 0);
        tableLayout.setColumn(8, (visible) ? 2 : 0);
    }

    protected void setFloatingVisible(boolean visible) {
        floatingButton.setVisible(visible);
        TableLayout tableLayout = (TableLayout) titleBar.getLayout();
        tableLayout.setColumn(5, (visible) ? 17 : 0);
        tableLayout.setColumn(6, (visible) ? 2 : 0);
    }

    protected void setDockedVisible(boolean visible) {
        dockButton.setVisible(visible);
        TableLayout tableLayout = (TableLayout) titleBar.getLayout();
        tableLayout.setColumn(3, (visible) ? 17 : 0);
        tableLayout.setColumn(4, (visible) ? 2 : 0);
    }

    protected void setSliding() {
        dockButton.setIcon(resourceManager.getIcon(ResourceManager.DOCKED));
        dockButton.setToolTipText(resourceManager.getString("@@tool.tooltip.dock"));
    }

    protected void setDocked() {
        dockButton.setIcon(resourceManager.getIcon(ResourceManager.SLIDING));
        dockButton.setToolTipText(resourceManager.getString("@@tool.tooltip.undock"));
    }

    protected void setFix() {
        floatingButton.setIcon(resourceManager.getIcon(ResourceManager.FIX));
        floatingButton.setToolTipText(resourceManager.getString("@@tool.tooltip.fix"));
    }

    protected void setFloating() {
        floatingButton.setIcon(resourceManager.getIcon(ResourceManager.FLOATING));
        floatingButton.setToolTipText(resourceManager.getString("@@tool.tooltip.float"));
    }

    protected void assignFocus() {
        focusRequester = SwingUtil.findFocusable(descriptor.getComponent());
        if (focusRequester == null) {
            hideButton.setFocusable(true);
            focusRequester = hideButton;
        } else {
            hideButton.setFocusable(false);
        }
        SwingUtil.requestFocus(focusRequester);
    }


    protected void initDockedComponents() {
        propertyChangeSupport = new PropertyChangeSupport(this);

        titleBarMouseAdapter = new TitleBarMouseAdapter();
        ActionListener titleBarActionListener = new TitleBarActionListener();

        // Container
        container = new JPanel(new ExtendedTableLayout(new double[][]{{TableLayout.FILL}, {16, TableLayout.FILL}}, false));
        container.setName("toolWindow.container." + toolWindow.getId());
        container.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));
        container.setFocusCycleRoot(true);

        String id = toolWindow.getId();

        // Title Bar
        ExtendedTableLayout titleBarLayout = new ExtendedTableLayout(new double[][]{{3, TableLayout.FILL, 2, 15, 2, 15, 2, 15, 2, 15, 2, 15, 3}, {1, 14, 1}}, false);
        titleBar = (JPanel) resourceManager.createComponent(
                ResourceManager.TOOL_WINDOW_TITLE_BAR, descriptor.getManager(),
                descriptor,
                this
        );
        titleBar.setLayout(titleBarLayout);
        titleBar.setName("toolWindow.titleBar." + toolWindow.getId());
        titleBar.setEnabled(false);
        titleBar.addMouseListener(titleBarMouseAdapter);

        if (descriptor.getDockedTypeDescriptor().isIdVisibleOnTitleBar())
            titleBarLayout.setColumn(0, titleBar.getFontMetrics(
                    titleBar.getFont()
            ).stringWidth(resourceManager.getUserString(id)) + 12);

        // Tabs
        titleBarTabs = new ToolWindowTabPanel(this, descriptor);
        toolWindow.getToolWindowTabs()[0].setSelected(true);

        // Buttons
        hideButton = renderTitleButton("visible", titleBarActionListener,
                                             "@@tool.tooltip.hide", ResourceManager.HIDE_TOOL_WINDOW_INACTIVE,
                                             null);
        maximizeButton = renderTitleButton("maximize", titleBarActionListener, "@@tool.tooltip.maximize", ResourceManager.MAXIMIZE_INACTIVE, null);
        pinButton = renderTitleButton("pin", titleBarActionListener, "@@tool.tooltip.unpin", ResourceManager.AUTO_HIDE_OFF_INACTIVE, null);
        floatingButton = renderTitleButton("floating", titleBarActionListener,
                                                 "@@tool.tooltip.float", ResourceManager.FLOATING_INACTIVE,
                                                 "toolWindow.floatingButton." + toolWindow.getId());
        dockButton = renderTitleButton("undock", titleBarActionListener,
                                             "@@tool.tooltip.undock", ResourceManager.DOCKED_INACTIVE,
                                             "toolWindow.dockButton." + toolWindow.getId());

        // Set TitleBar content
        titleBar.add(titleBarTabs, "1,1");

        titleBar.add(dockButton, "3,1");
        titleBar.add(floatingButton, "5,1");
        titleBar.add(pinButton, "7,1");
        titleBar.add(maximizeButton, "9,1");
        titleBar.add(hideButton, "11,1");

        Component toolWindowCmp = descriptor.getComponent();
//        toolWindowCmp.addMouseListener(titleBarMouseAdapter);

        // Set Container content
        container.add(titleBar, "0,0");
        container.add(toolWindowCmp, "0,1");

        focusRequester = SwingUtil.findFocusable(descriptor.getComponent());
        if (focusRequester == null) {
            hideButton.setFocusable(true);
            focusRequester = hideButton;
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", new FocusOwnerPropertyChangeListener());

        configureDockedIcons();
    }

    protected void initDockedListeners() {
        addPropertyChangeListener("active", new ActivePropertyChangeListener());
        addPropertyChangeListener("autoHide", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                boolean newValue = ((Boolean) evt.getNewValue());

                if (newValue) {
                    pinButton.setIcon(resourceManager.getIcon(ResourceManager.AUTO_HIDE_ON));
                    pinButton.setToolTipText(resourceManager.getString("@@tool.tooltip.pin"));
                } else {
                    pinButton.setIcon(resourceManager.getIcon(ResourceManager.AUTO_HIDE_OFF));
                    pinButton.setToolTipText(resourceManager.getString("@@tool.tooltip.unpin"));
                }
            }
        });
        addPropertyChangeListener("type", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                if (evt.getNewValue() == ToolWindowType.DOCKED) {
                    configureDockedIcons();
                }
            }
        });
        addPropertyChangeListener("maximized.before", new PropertyChangeListener() {
            ByteArrayOutputStream workspace;
            boolean valueAdj = false;

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                if ((Boolean) evt.getNewValue()) {
                    descriptor.getManager().getPersistenceDelegate().save(workspace = new ByteArrayOutputStream());

                    maximizeButton.setIcon(resourceManager.getIcon(ResourceManager.MINIMIZE));
                } else if (workspace != null) {
                    if (valueAdj)
                        return;

                    valueAdj = true;
                    try {
                        maximizeButton.setIcon(resourceManager.getIcon(ResourceManager.MAXIMIZE));
                        descriptor.getManager().getPersistenceDelegate().merge(new ByteArrayInputStream(workspace.toByteArray()),
                                                                               PersistenceDelegate.MergePolicy.UNION);
                        workspace = null;
                    } finally {
                        valueAdj = false;
                    }
                }
            }
        });

        descriptor.getDockedTypeDescriptor().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("idVisibleOnTitleBar".equals(evt.getPropertyName())) {
                    if ((Boolean) evt.getNewValue()) {
                        enableIdOnTitleBar();
                    } else {
                        disableIdOnTitleBar();
                    }

                }
            }
        });

        ((SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING)).addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("enabled".equals(evt.getPropertyName())) {
                            boolean newValue = (Boolean) evt.getNewValue();
                            setDockedVisible(newValue);

                            if (!newValue && toolWindow.getType() == ToolWindowType.SLIDING)
                                toolWindow.setType(ToolWindowType.DOCKED);
                        }
                    }
                }
        );
        ((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("enabled".equals(evt.getPropertyName())) {
                            boolean newValue = (Boolean) evt.getNewValue();
                            setFloatingVisible(newValue);

                            if (!newValue && toolWindow.getType() == ToolWindowType.FLOATING)
                                toolWindow.setType(ToolWindowType.DOCKED);
                        }
                    }
                }
        );

        toolWindow.addToolWindowListener(new DockedToolWindowListener());
    }


    protected JButton renderTitleButton(String actionCommnad, ActionListener actionListener, String tooltip, String iconId, String name) {
        JButton button = (JButton) resourceManager.createComponent(
                ResourceManager.TOOL_WINDOW_TITLE_BUTTON,
                descriptor.getManager()
        );
        button.setName(name);
        button.setActionCommand(actionCommnad);
        button.addActionListener(actionListener);
        button.setToolTipText(resourceManager.getString(tooltip));
        button.setIcon(resourceManager.getIcon(iconId));

        return button;
    }

    protected void configureDockedIcons() {
        setPinVisible(true);

        setFloating();
        setFloatingVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());

        setDockedVisible(((SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING)).isEnabled());
        setDocked();
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

        // TODO: non si aggiorna correttamente ....
        SwingUtil.repaint(titleBar);
    }

    protected void disableIdOnTitleBar() {
        TableLayout layout = (TableLayout) titleBar.getLayout();
        layout.setColumn(0, 3);

        SwingUtil.repaint(titleBar);
    }


    public interface PopupUpdater {

        void update(MouseEvent e, JPopupMenu popupMenu);

    }


    protected class TitleBarMouseAdapter extends MouseAdapter implements ActionListener, PropertyChangeListener {
        protected JPopupMenu popupMenu;

        protected JMenuItem visible;
        protected JMenuItem aggregate;
        protected JCheckBoxMenuItem floatingMode;
        protected JCheckBoxMenuItem dockedMode;
        protected JCheckBoxMenuItem pinnedMode;

        protected JMenu moveTo;
        protected JMenuItem right;
        protected JMenuItem left;
        protected JMenuItem top;
        protected JMenuItem bottom;

        public TitleBarMouseAdapter() {
            initPopupMenu();
            descriptor.getToolWindow().addInternalPropertyChangeListener(this);

        }

        public void mouseClicked(MouseEvent e) {
            if (!toolWindow.isAvailable())
                return;

            if (SwingUtilities.isLeftMouseButton(e)) {
                toolWindow.setActive(true);

                if (e.getClickCount() == 2)
                    toolWindow.setMaximized(!toolWindow.isMaximized());
            } else if (SwingUtilities.isRightMouseButton(e)) {
                showPopupMenu(e);
            }
        }

        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if ("visible".equals(actionCommand)) {
                if (toolWindow.isActive()) {
                    toolWindow.setActive(false);
                    toolWindow.setVisible(false);
                } else if (toolWindow.isVisible())
                    toolWindow.setVisible(false);
                else
                    toolWindow.setActive(true);
            } else if ("aggregate".equals(actionCommand)) {
                if (toolWindow.isActive()) {
                    toolWindow.setActive(false);
                    toolWindow.setVisible(false);
                } else if (toolWindow.isVisible())
                    toolWindow.setVisible(false);
                else {
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
                } else
                    toolWindow.setType(ToolWindowType.DOCKED);
            } else if ("docked".equals(actionCommand)) {
                toolWindow.setType(dockedMode.isSelected() ? ToolWindowType.DOCKED : ToolWindowType.SLIDING);
            } else if ("pinned".equals(actionCommand)) {
                toolWindow.setAutoHide(!toolWindow.isAutoHide());
            }
//            if (toolWindow.isActive()) {
//                SwingUtilities.invokeLater(new Runnable() {
//                    public void run() {
//                        toolWindow.setActive(true);
//                    }
//                });
//            }
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


        protected void initPopupMenu() {
            popupMenu = new JPopupMenu("ToolWindowBarPopupMenu");
            popupMenu.setLightWeightPopupEnabled(false);

            // Visible
            visible = new JMenuItem();
            visible.setActionCommand("visible");
            visible.addActionListener(this);

            aggregate = new JMenuItem();
            aggregate.setText(resourceManager.getString("@@tool.aggregate"));
            aggregate.setActionCommand("aggregate");
            aggregate.addActionListener(this);

            floatingMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING);
            floatingMode.setText(resourceManager.getString("@@tool.mode.floating"));
            floatingMode.setActionCommand("floating");
            floatingMode.addActionListener(this);

            dockedMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.DOCKED);
            dockedMode.setText(resourceManager.getString("@@tool.mode.docked"));
            dockedMode.setActionCommand("docked");
            dockedMode.addActionListener(this);

            pinnedMode = new JCheckBoxMenuItem(null, !toolWindow.isAutoHide());
            pinnedMode.setText(resourceManager.getString("@@tool.mode.pinned"));
            pinnedMode.setActionCommand("pinned");
            pinnedMode.addActionListener(this);

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
            popupMenu.add(moveTo);
            popupMenu.addSeparator();
            popupMenu.add(visible);
            popupMenu.add(aggregate);
        }

        protected void enableVisible() {
            aggregate.setVisible(!toolWindow.isVisible());
            visible.setText(toolWindow.isVisible() ?
                            resourceManager.getString("@@tool.hide") :
                            resourceManager.getString("@@tool.show"));

            if (toolWindow.getType() == ToolWindowType.DOCKED) {
                dockedMode.setVisible(((SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING)).isEnabled());
                floatingMode.setVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());
            } else if (toolWindow.getType() == ToolWindowType.SLIDING)
                floatingMode.setVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());
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

        private JMenu old;

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


        public void showPopupMenu(MouseEvent e) {
            if ((e.getComponent() == titleBar || SwingUtil.hasParent(e.getComponent(), titleBar)) &&
                ((DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED)).isPopupMenuEnabled()) {

                popupMenu.removeAll();
                popupMenu.add(pinnedMode);
                popupMenu.add(dockedMode);
                popupMenu.add(floatingMode);
                popupMenu.add(moveTo);
                popupMenu.addSeparator();
                popupMenu.add(visible);
                popupMenu.add(aggregate);

                enableVisible();
                enableMoveToItem();
                enableUserDefined();

                if (popupUpdater != null)
                    popupUpdater.update(e, popupMenu);

                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }

        }
    }

    protected class TitleBarActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String actionCommnad = e.getActionCommand();
            if (!"visible".equals(actionCommnad))
                toolWindow.setActive(true);

            if ("visible".equals(actionCommnad)) {
                ToolWindowActionHandler toolWindowActionHandler = ((DockedTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.DOCKED)).getToolWindowActionHandler();
                if (toolWindowActionHandler != null)
                    toolWindowActionHandler.onHideButtonClick(toolWindow);
                else
                    toolWindow.setVisible(false);
            } else if ("pin".equals(actionCommnad)) {
                toolWindow.setAutoHide(!toolWindow.isAutoHide());
            } else if ("floating".equals(actionCommnad)) {
                ToolWindowType type = toolWindow.getType();
                if (type == ToolWindowType.FLOATING || type == ToolWindowType.FLOATING_FREE) {
                    toolWindow.setType(ToolWindowType.DOCKED);
                } else if (type == ToolWindowType.DOCKED || type == ToolWindowType.SLIDING) {
                    toolWindow.setType(descriptor.isFloatingWindow() ? ToolWindowType.FLOATING_FREE : ToolWindowType.FLOATING);
                }
            } else if ("undock".equals(actionCommnad)) {
                ToolWindowType type = toolWindow.getType();
                if (type == ToolWindowType.DOCKED) {
                    toolWindow.setType(ToolWindowType.SLIDING);
                } else if (type == ToolWindowType.SLIDING) {
                    toolWindow.setType(ToolWindowType.DOCKED);
                }
            } else if ("maximize".equals(actionCommnad)) {
                toolWindow.setMaximized(!toolWindow.isMaximized());
            }
        }

    }


    protected class DockedToolWindowListener implements ToolWindowListener, PropertyChangeListener {

        public DockedToolWindowListener() {
            for (ToolWindowTab tab : toolWindow.getToolWindowTabs())
                tab.addPropertyChangeListener(this);
        }

        public boolean toolWindowTabRemoving(ToolWindowTabEvent event) {
            return true;
        }

        public void toolWindowTabAdded(ToolWindowTabEvent event) {
            ToolWindowTab tab = event.getToolWindowTab();
            tab.getComponent().addMouseListener(titleBarMouseAdapter);
            tab.addPropertyChangeListener(this);
        }

        public void toolWindowTabRemoved(ToolWindowTabEvent event) {
            if (toolWindow.getToolWindowTabs().length == 0) {
                container.remove(event.getToolWindowTab().getComponent());
                SwingUtil.repaint(container);
            }

            event.getToolWindowTab().getComponent().removeMouseListener(titleBarMouseAdapter);
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
                                hideButton.requestFocus();
                        }
                    });
                }
            } else if ("component".equals(property)) {
                if (descriptor.getComponent() == evt.getOldValue())
                    setMainComponent(tab.getComponent());
            }

        }
    }

    protected class FocusOwnerPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (!toolWindow.isVisible())
                return;

            Component component = (Component) evt.getNewValue();
            if (component == null) return;
            if (component instanceof JRootPane) return;

            valueAdjusting = true;

//            System.out.println(toolWindow.getId() + " - cmp = " + component);

            if (isInternalComponent(component)) {
                toolWindow.setActive(true);
                if (focusRequester == null)
                    focusRequester = component;
                else {
                    if (!(focusRequester instanceof ToolWindowActiveButton))
                        focusRequester = component;
                    else {
                        if (focusRequester == hideButton)
                            assignFocus();
                        else
                            focusRequester.requestFocusInWindow();
                    }
                }
            } else {
                descriptor.getToolBar().deactiveTool(toolWindow);

                if (toolWindow.isAutoHide())
                    toolWindow.setVisible(false);
            }

            valueAdjusting = false;
        }

        protected final boolean isInternalComponent(Component component) {
            if (component == null)
                return false;
            Component traverser = component;
            while (traverser.getParent() != null) {
                if (traverser.getParent() == container) {
                    return true;
                }
                traverser = traverser.getParent();
            }
            return false;
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

            if (active) {
                if (toolWindow.isAutoHide()) {
                    pinButton.setIcon(resourceManager.getIcon(ResourceManager.AUTO_HIDE_ON));
                } else
                    pinButton.setIcon(resourceManager.getIcon(ResourceManager.AUTO_HIDE_OFF));

                hideButton.setIcon(resourceManager.getIcon(ResourceManager.HIDE_TOOL_WINDOW));

                if (toolWindow.getType() == ToolWindowType.SLIDING) {
                    dockButton.setIcon(resourceManager.getIcon(ResourceManager.DOCKED));
                } else
                    dockButton.setIcon(resourceManager.getIcon(ResourceManager.SLIDING));

                if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                    floatingButton.setIcon(resourceManager.getIcon(ResourceManager.FIX));
                } else
                    floatingButton.setIcon(resourceManager.getIcon(ResourceManager.FLOATING));

                if (toolWindow.isMaximized())
                    maximizeButton.setIcon(resourceManager.getIcon(ResourceManager.MINIMIZE));
                else
                    maximizeButton.setIcon(resourceManager.getIcon(ResourceManager.MAXIMIZE));
            } else {
                if (toolWindow.isAutoHide()) {
                    pinButton.setIcon(resourceManager.getIcon(ResourceManager.AUTO_HIDE_ON_INACTIVE));
                } else
                    pinButton.setIcon(resourceManager.getIcon(ResourceManager.AUTO_HIDE_OFF_INACTIVE));

                hideButton.setIcon(resourceManager.getIcon(ResourceManager.HIDE_TOOL_WINDOW_INACTIVE));

                if (toolWindow.getType() == ToolWindowType.SLIDING) {
                    dockButton.setIcon(resourceManager.getIcon(ResourceManager.DOCKED_INACTIVE));
                } else
                    dockButton.setIcon(resourceManager.getIcon(ResourceManager.SLIDING_INACTIVE));

                if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                    floatingButton.setIcon(resourceManager.getIcon(ResourceManager.FIX_INACTIVE));
                } else
                    floatingButton.setIcon(resourceManager.getIcon(ResourceManager.FLOATING_INACTIVE));

                if (toolWindow.isMaximized())
                    maximizeButton.setIcon(resourceManager.getIcon(ResourceManager.MINIMIZE_INACTIVE));
                else
                    maximizeButton.setIcon(resourceManager.getIcon(ResourceManager.MAXIMIZE_INACTIVE));
            }

            if (active && focusRequester != null && !valueAdjusting) {
//                System.out.println("focusRequester = " + focusRequester);
                if (focusRequester == hideButton)
                    assignFocus();
                else
                    SwingUtil.requestFocus(focusRequester);

            }
        }
    }

}
