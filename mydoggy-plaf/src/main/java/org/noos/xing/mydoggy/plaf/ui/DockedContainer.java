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
public class DockedContainer implements ToolWindowContainer {
    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected ResourceManager resourceManager;

    protected JPanel container;

    protected JPanel titleBar;
    protected ToolWindowTabPanel titleBarTabs;

    protected Component focusRequester;

    protected TitleBarButtons titleBarButtons;

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


    protected void initDockedComponents() {
        propertyChangeSupport = new PropertyChangeSupport(this);

        titleBarMouseAdapter = new TitleBarMouseAdapter();

        // Container
        container = new JPanel(new ExtendedTableLayout(new double[][]{{TableLayout.FILL}, {16, TableLayout.FILL}}, false));
        container.setName("toolWindow.container." + toolWindow.getId());
        container.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));
        container.setFocusCycleRoot(true);
        container.putClientProperty(ToolWindow.class, toolWindow);

        String id = toolWindow.getId();

        // Title Bar
        ExtendedTableLayout titleBarLayout = new ExtendedTableLayout(new double[][]{{3, TableLayout.FILL, 2, -2, 3}, {1, 14, 1}}, false);
        titleBar = (JPanel) resourceManager.createComponent(
                MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR, descriptor.getManager(),
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
        titleBarButtons = resourceManager.createInstance(TitleBarButtons.class, descriptor, this);

        // Set TitleBar content
        titleBar.add(titleBarTabs, "1,1");
        titleBar.add(titleBarButtons.getButtonsContainer(), "3,1");

        // Set Container content
        container.add(titleBar, "0,0");
        container.add(descriptor.getComponent(), "0,1");

        focusRequester = SwingUtil.findFocusable(descriptor.getComponent());
        if (focusRequester == null) {
            titleBarButtons.getFocusable().setFocusable(true);
            focusRequester = titleBarButtons.getFocusable();
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", new FocusOwnerPropertyChangeListener());

        titleBarButtons.configureIcons(ToolWindowType.DOCKED);
    }

    protected void initDockedListeners() {
        addPropertyChangeListener("active", new ActivePropertyChangeListener());
        addPropertyChangeListener("type", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                if (evt.getNewValue() == ToolWindowType.DOCKED) {
                    titleBarButtons.configureIcons(ToolWindowType.DOCKED);
                }

                if (evt.getOldValue() == ToolWindowType.TABBED) {
                    setMainComponent(toolWindow.getToolWindowTabs()[0].getComponent());
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
                } else if (workspace != null) {
                    if (valueAdj)
                        return;

                    valueAdj = true;
                    try {
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

                            if (!newValue && toolWindow.getType() == ToolWindowType.FLOATING)
                                toolWindow.setType(ToolWindowType.DOCKED);
                        }
                    }
                }
        );

        toolWindow.addToolWindowListener(new DockedToolWindowListener());
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

    public void showPopupMenu(Component c, int x, int y) {
        titleBarMouseAdapter.showPopupMenu(c, x, y);
    }

    public ToolWindowDescriptor getToolWindowDescriptor() {
        return descriptor;
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


    public interface PopupUpdater {

        void update(Component source, JPopupMenu popupMenu);

    }


    protected class TitleBarMouseAdapter extends MouseAdapter implements ActionListener, PropertyChangeListener {
        protected JPopupMenu popupMenu;

        protected JMenuItem visible;
        protected JMenuItem aggregate;
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
                if (((DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED)).isPopupMenuEnabled())
                    showPopupMenu(e.getComponent(), e.getX(), e.getY());
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
        }

        protected void enableVisible() {
            aggregate.setVisible(!toolWindow.isVisible());
            visible.setText(toolWindow.isVisible() ?
                            resourceManager.getString("@@tool.hide") :
                            resourceManager.getString("@@tool.show"));

            if (toolWindow.getType() == ToolWindowType.DOCKED) {
                dockedMode.setVisible(((SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING)).isEnabled());
                floatingMode.setVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());
                floatingLiveMode.setState(false);
                floatingLiveMode.setVisible(((FloatingLiveTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE)).isEnabled());
            } else if (toolWindow.getType() == ToolWindowType.SLIDING) {
                floatingMode.setVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());
                floatingLiveMode.setState(false);
                floatingLiveMode.setVisible(((FloatingLiveTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE)).isEnabled());
            } else if (toolWindow.getType() == ToolWindowType.FLOATING) {
                floatingLiveMode.setState(false);
                floatingLiveMode.setVisible(((FloatingLiveTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE)).isEnabled());
            } else if (toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
                dockedMode.setState(false);
                floatingMode.setState(false);
                floatingMode.setVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());
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
            tab.addPropertyChangeListener(this);
        }

        public void toolWindowTabRemoved(ToolWindowTabEvent event) {
            if (toolWindow.getToolWindowTabs().length == 0) {
                container.remove(event.getToolWindowTab().getComponent());
                SwingUtil.repaint(container);
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
                        if (focusRequester == titleBarButtons.getFocusable())
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

            if (active && focusRequester != null && !valueAdjusting) {
//                System.out.println("focusRequester = " + focusRequester);
                if (focusRequester == titleBarButtons.getFocusable())
                    assignFocus();
                else
                    SwingUtil.requestFocus(focusRequester);

            }
        }

    }

}
