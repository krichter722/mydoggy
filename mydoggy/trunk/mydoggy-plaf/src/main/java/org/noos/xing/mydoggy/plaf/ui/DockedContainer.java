package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ResourceBundle;

/**
 * @author Angelo De Caro
 */
public class DockedContainer implements PropertyChangeListener, ToolWindowContainer {
    private static ResourceBundle resourceBundle = ResourceBoundles.getResourceBundle();

    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected ToolWindowUI toolWindowUI;

    private JPanel container;

    protected JPanel applicationBar;
    protected JLabel applicationBarTitle;

    private Component focusRequester;

    protected JButton floatingButton;
    protected JButton pinButton;
    protected JButton dockButton;
    protected JButton hideButton;
    protected JButton maximizeButton;

    private MouseAdapter applicationBarMouseAdapter;
    private PropertyChangeSupport propertyChangeSupport;

    boolean valueAdjusting;

    public DockedContainer(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
        this.toolWindowUI = descriptor.getToolWindowUI();

        initDockedComponents();
        initDockedListeners();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }

    public Container getContentContainer() {
        return container;
    }

    public void updateUI() {
        SwingUtilities.updateComponentTreeUI(getContentContainer());
    }

    public void uninstall() {
        Component toolWindowCmp = descriptor.getComponent();
        toolWindowCmp.removeMouseListener(applicationBarMouseAdapter);
    }

    protected void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    protected void removePropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(property, listener);
    }

    protected void setPinVisible(boolean visible) {
        pinButton.setVisible(visible);
        ((TableLayout) applicationBar.getLayout()).setColumn(7, (visible) ? 17 : 0);
    }

    protected void setFloatingVisible(boolean visible) {
        floatingButton.setVisible(visible);
        ((TableLayout) applicationBar.getLayout()).setColumn(5, (visible) ? 17 : 0);
    }

    protected void setDockedVisible(boolean visible) {
        dockButton.setVisible(visible);
        ((TableLayout) applicationBar.getLayout()).setColumn(3, (visible) ? 17 : 0);
    }

    protected void setSliding() {
        dockButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.DOCKED));
        dockButton.setToolTipText(ResourceBoundles.getResourceBundle().getString("@@tool.tooltip.dock"));
    }

    protected void setDocked() {
        dockButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.SLIDING));
        dockButton.setToolTipText(ResourceBoundles.getResourceBundle().getString("@@tool.tooltip.undock"));
    }

    protected void setFix() {
        floatingButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.FIX));
        floatingButton.setToolTipText(ResourceBoundles.getResourceBundle().getString("@@tool.tooltip.fix"));
    }

    protected void setFloating() {
        floatingButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.FLOATING));
        floatingButton.setToolTipText(ResourceBoundles.getResourceBundle().getString("@@tool.tooltip.float"));
    }

    private void initDockedComponents() {
        propertyChangeSupport = new PropertyChangeSupport(this);

        applicationBarMouseAdapter = new ApplicationBarMouseAdapter();
        ActionListener applicationBarActionListener = new ApplicationBarActionListener();

        // Container
        container = new JPanel(new ExtendedTableLayout(new double[][]{{TableLayout.FILL}, {16, TableLayout.FILL}}, false));
        container.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));
        container.setFocusCycleRoot(false);

        // Application Bar
        applicationBar = new JPanel(new ExtendedTableLayout(new double[][]{{3, TableLayout.FILL, 3, 15, 2, 15, 2, 15, 2, 15, 2, 15, 3}, {1, 14, 1}}, false)) {
            public void setUI(PanelUI ui) {
                if (ui instanceof ApplicationBarPanelUI)
                    super.setUI(ui);
            }
        };
        applicationBar.setBorder(null);
        applicationBar.setEnabled(false);
        applicationBar.setUI(new ApplicationBarPanelUI(descriptor, this));

        // Title
        applicationBarTitle = new JLabel(toolWindow.getTitle(), JLabel.LEFT);
        applicationBarTitle.setForeground(Color.WHITE);
        applicationBarTitle.setBackground(Color.LIGHT_GRAY);
        applicationBarTitle.setOpaque(false);
        applicationBarTitle.addMouseListener(applicationBarMouseAdapter);

        // Buttons
        hideButton = renderApplicationButton("visible", applicationBarActionListener, "@@tool.tooltip.hide");
        maximizeButton = renderApplicationButton("maximize", applicationBarActionListener, "@@tool.tooltip.maximize");
        pinButton = renderApplicationButton("pin", applicationBarActionListener, "@@tool.tooltip.unpin");
        floatingButton = renderApplicationButton("floating", applicationBarActionListener, "@@tool.tooltip.float");
        dockButton = renderApplicationButton("undock", applicationBarActionListener, "@@tool.tooltip.undock");

        // Set ApplicationBar content
        applicationBar.add(applicationBarTitle, "1,1");
        applicationBar.add(dockButton, "3,1");
        applicationBar.add(floatingButton, "5,1");
        applicationBar.add(pinButton, "7,1");
        applicationBar.add(maximizeButton, "9,1");
        applicationBar.add(hideButton, "11,1");

        Component toolWindowCmp = descriptor.getComponent();
        toolWindowCmp.addMouseListener(applicationBarMouseAdapter);

        // Set Container content
        container.add(applicationBar, "0,0");
        container.add(toolWindowCmp, "0,1");

        focusRequester = SwingUtil.findFocusable(toolWindowCmp);
        if (focusRequester == null) {
            hideButton.setFocusable(true);
            focusRequester = hideButton;
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", new FocusOwnerPropertyChangeListener());

        configureDockedIcons();
    }

    private void initDockedListeners() {
//        addPropertyChangeListener("focusOwner", new FocusOwnerPropertyChangeListener());
        addPropertyChangeListener("active", new ActivePropertyChangeListener());
        addPropertyChangeListener("anchor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
//                if (evt.getSource() == descriptor)
//                    descriptor.setDividerLocation(-1);
            }
        });
        addPropertyChangeListener("autoHide", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                boolean newValue = ((Boolean) evt.getNewValue());

                if (newValue) {
                    pinButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.AUTO_HIDE_ON));
                    pinButton.setToolTipText(resourceBundle.getString("@@tool.tooltip.pin"));
                } else {
                    pinButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.AUTO_HIDE_OFF));
                    pinButton.setToolTipText(resourceBundle.getString("@@tool.tooltip.unpin"));
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
        addPropertyChangeListener("maximized", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                if ((Boolean) evt.getNewValue()) {
                    maximizeButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.MINIMIZE));
                } else
                    maximizeButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.MAXIMIZE));

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
    }

    private JButton renderApplicationButton(String actionCommnad, ActionListener actionListener, String tooltip) {
        JButton button = new ApplicationButton();
        button.setUI((ButtonUI) BasicButtonUI.createUI(button));
        button.setName(actionCommnad);
        button.setRolloverEnabled(true);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setBorderPainted(false);
        button.setActionCommand(actionCommnad);
        button.addActionListener(actionListener);
        button.setToolTipText(resourceBundle.getString(tooltip));

        return button;
    }

    private void configureDockedIcons() {
        setPinVisible(true);

        setFloating();
        setFloatingVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());

        setDockedVisible(((SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING)).isEnabled());
        setDocked();
    }


    class ApplicationBarMouseAdapter extends MouseAdapter implements ActionListener, PropertyChangeListener {
        JPopupMenu popupMenu;

        JMenuItem visible;
        JMenuItem aggregate;
        JCheckBoxMenuItem floatingMode;
        JCheckBoxMenuItem dockedMode;
        JCheckBoxMenuItem pinnedMode;

        JMenu moveTo;
        JMenuItem right;
        JMenuItem left;
        JMenuItem top;
        JMenuItem bottom;

        public ApplicationBarMouseAdapter() {
            initPopupMenu();
            descriptor.getToolWindow().addInternalPropertyChangeListener(this);
        }

        public void mouseClicked(MouseEvent e) {
            if (!toolWindow.isAvailable())
                return;

            if (SwingUtilities.isLeftMouseButton(e)) {
                toolWindow.setActive(true);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (e.getComponent() == applicationBarTitle && 
                        ((DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED)).isPopupMenuEnabled()) {
                    enableVisible();
                    enableMoveToItem();
                    enableUserDefined();

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
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
            aggregate.setText(resourceBundle.getString("@@tool.aggregate"));
            aggregate.setActionCommand("aggregate");
            aggregate.addActionListener(this);

            floatingMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING);
            floatingMode.setText(resourceBundle.getString("@@tool.mode.floating"));
            floatingMode.setActionCommand("floating");
            floatingMode.addActionListener(this);

            dockedMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.DOCKED);
            dockedMode.setText(resourceBundle.getString("@@tool.mode.docked"));
            dockedMode.setActionCommand("docked");
            dockedMode.addActionListener(this);

            pinnedMode = new JCheckBoxMenuItem(null, !toolWindow.isAutoHide());
            pinnedMode.setText(resourceBundle.getString("@@tool.mode.pinned"));
            pinnedMode.setActionCommand("pinned");
            pinnedMode.addActionListener(this);

            // MoveTo SubMenu
            moveTo = new JMenu();
            moveTo.getPopupMenu().setLightWeightPopupEnabled(false);
            moveTo.setText(resourceBundle.getString("@@tool.moveTo"));

            right = new JMenuItem();
            right.setText(resourceBundle.getString("@@tool.move.right"));
            right.setActionCommand("move.right");
            right.addActionListener(this);

            left = new JMenuItem();
            left.setText(resourceBundle.getString("@@tool.move.left"));
            left.setActionCommand("move.left");
            left.addActionListener(this);

            top = new JMenuItem();
            top.setText(resourceBundle.getString("@@tool.move.top"));
            top.setActionCommand("move.top");
            top.addActionListener(this);

            bottom = new JMenuItem();
            bottom.setText(resourceBundle.getString("@@tool.move.bottom"));
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
                            resourceBundle.getString("@@tool.hide") :
                            resourceBundle.getString("@@tool.show"));

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


    }

    class ApplicationBarActionListener implements ActionListener {
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


    class FocusOwnerPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (!toolWindow.isVisible())
                return;

            Component component = (Component) evt.getNewValue();
            if (component == null) return;
            if (component instanceof JRootPane) return;

            valueAdjusting = true;

//            System.out.println("component = " + component);

            if (isInternalComponent(component)) {
                toolWindow.setActive(true);
                if (focusRequester == null)
                    focusRequester = component;
                else {
                    if (!(focusRequester instanceof ApplicationButton))
                        focusRequester = component;
                    else
                        focusRequester.requestFocusInWindow();
                }
            } else {
                toolWindow.setActive(false);
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

    class ActivePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor)
                return;

            boolean active = (Boolean) evt.getNewValue();
            applicationBar.setEnabled(active);

            if (active) {
                if (toolWindow.isAutoHide()) {
                    pinButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.AUTO_HIDE_ON));
                } else
                    pinButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.AUTO_HIDE_OFF));

                hideButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.HIDE_TOOL_WINDOW));

                if (toolWindow.getType() == ToolWindowType.SLIDING) {
                    dockButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.DOCKED));
                } else
                    dockButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.SLIDING));

                if (toolWindow.getType() == ToolWindowType.FLOATING) {
                    floatingButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.FIX));
                } else
                    floatingButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.FLOATING));

                if (toolWindow.isMaximized())
                    maximizeButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.MAXIMIZE));
                else
                    maximizeButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.MAXIMIZE));
            } else {
                if (toolWindow.isAutoHide()) {
                    pinButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.AUTO_HIDE_ON_INACTIVE));
                } else
                    pinButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.AUTO_HIDE_OFF_INACTIVE));

                hideButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.HIDE_TOOL_WINDOW_INACTIVE));

                if (toolWindow.getType() == ToolWindowType.SLIDING) {
                    dockButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.DOCKED_INACTIVE));
                } else
                    dockButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.SLIDING_INACTIVE));

                if (toolWindow.getType() == ToolWindowType.FLOATING) {
                    floatingButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.FIX_INACTIVE));
                } else
                    floatingButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.FLOATING_INACTIVE));

                if (toolWindow.isMaximized())
                    maximizeButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.MAXIMIZE_INACTIVE));
                else
                    maximizeButton.setIcon(toolWindowUI.getIcon(ToolWindowUI.IconId.MINIMIZE_INACTIVE));
            }

            if (active && focusRequester != null && !valueAdjusting) {
                SwingUtil.requestFocus(focusRequester);
            }
        }
    }

    static class ApplicationButton extends JButton {
        public void setUI(ButtonUI ui) {
            super.setUI((ButtonUI) BasicButtonUI.createUI(this));
            setRolloverEnabled(true);
            setOpaque(false);
            setFocusPainted(false);
            setFocusable(false);
            setBorderPainted(false);
        }
    }

}