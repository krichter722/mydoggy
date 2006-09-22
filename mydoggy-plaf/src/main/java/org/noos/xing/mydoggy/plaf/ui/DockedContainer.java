package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.collections.ResolvableHashtable;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Angelo De Caro
 */
public class DockedContainer implements PropertyChangeListener, ToolWindowContainer {
    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;

    private JPanel container;

    protected JPanel applicationBar;
    protected JLabel applicationBarTitle;

    private Component focusRequester;

    protected JButton floatingButton;
    protected JButton pinButton;
    protected JButton dockButton;
    protected JButton hideButton;

    private MouseAdapter applicationBarMouseAdapter;
    private Map<String, List<PropertyChangeListener>> propertyChangeListeners;

    boolean valueAdjusting;

    public DockedContainer(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();

        initDockedComponents();
        initDockedListeners();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        List<PropertyChangeListener> listeners = propertyChangeListeners.get(evt.getPropertyName());
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(evt);
        }
    }

    public Container getContentContainer() {
        return container;
    }

    public void uninstall() {
        Component toolWindowCmp = descriptor.getComponent();
        toolWindowCmp.removeMouseListener(applicationBarMouseAdapter);
    }

    protected void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        List<PropertyChangeListener> listeners = propertyChangeListeners.get(property);
        listeners.add(listener);
    }

    protected void removePropertyChangeListener(String property, PropertyChangeListener listener) {
        List<PropertyChangeListener> listeners = propertyChangeListeners.get(property);
        listeners.remove(listener);
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
        dockButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/docked.png"));
        dockButton.setToolTipText(ResourceBoundles.getResourceBoundle().getString("@@tool.tooltip.dock"));
    }

    protected void setDocked() {
        dockButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/sliding.png"));
        dockButton.setToolTipText(ResourceBoundles.getResourceBoundle().getString("@@tool.tooltip.undock"));
    }

    protected void setFix() {
        floatingButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fix.png"));
        floatingButton.setToolTipText(ResourceBoundles.getResourceBoundle().getString("@@tool.tooltip.fix"));
    }

    protected void setFloating() {
        floatingButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floating.png"));
        floatingButton.setToolTipText(ResourceBoundles.getResourceBoundle().getString("@@tool.tooltip.float"));
    }


    private void initDockedComponents() {
        this.propertyChangeListeners = new ResolvableHashtable<String, List<PropertyChangeListener>>(
                new ResolvableHashtable.Resolver<List<PropertyChangeListener>>() {
                    public List<PropertyChangeListener> get(Object key) {
                        List<PropertyChangeListener> result = new LinkedList<PropertyChangeListener>();
                        propertyChangeListeners.put((String) key, result);
                        return result;
                    }
                }
        );

        applicationBarMouseAdapter = new ApplicationBarMouseAdapter();
        ActionListener applicationBarActionListener = new ApplicationBarActionListener();

        // Container
        container = new JPanel(new TableLayout(new double[][]{{TableLayout.FILL}, {16, TableLayout.FILL}}));
        container.setBorder(new LineBorder(Color.GRAY, 1, true, 3,3));
        container.setFocusCycleRoot(false);

        // Application Bar
        applicationBar = new JPanel(new TableLayout(new double[][]{{3, TableLayout.FILL, 3, 15, 2, 15, 2, 15, 2, 15, 3}, {1, 14, 1}}));
        applicationBar.setBorder(null);
        applicationBar.setUI(new ApplicationBarPanelUI(descriptor, this));

        // Title
        applicationBarTitle = new JLabel(toolWindow.getTitle(), JLabel.LEFT);
        applicationBarTitle.setForeground(Color.WHITE);
        applicationBarTitle.setBackground(Color.LIGHT_GRAY);
        applicationBarTitle.setOpaque(false);
        applicationBarTitle.addMouseListener(applicationBarMouseAdapter);

        // Buttons
        hideButton = renderApplicationButton("hideToolWindow", "visible", applicationBarActionListener, "@@tool.tooltip.hide");
        pinButton = renderApplicationButton("autohideOff", "pin", applicationBarActionListener, "@@tool.tooltip.unpin");
        floatingButton = renderApplicationButton("floating", "floating", applicationBarActionListener, "@@tool.tooltip.float");
        dockButton = renderApplicationButton("sliding", "undock", applicationBarActionListener, "@@tool.tooltip.undock");
        hideButton.setFocusable(true);

        // Set ApplicationBar content
        applicationBar.add(applicationBarTitle, "1,1");
        applicationBar.add(dockButton, "3,1");
        applicationBar.add(floatingButton, "5,1");
        applicationBar.add(pinButton, "7,1");
        applicationBar.add(hideButton, "9,1");
        focusRequester = hideButton;

        Component toolWindowCmp = descriptor.getComponent();
        toolWindowCmp.addMouseListener(applicationBarMouseAdapter);

        // Set Container content
        container.add(applicationBar, "0,0");
        container.add(toolWindowCmp, "0,1");

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", new FocusOwnerPropertyChangeListener());
    }

    private void initDockedListeners() {
        addPropertyChangeListener("focusOwner", new FocusOwnerPropertyChangeListener());
        addPropertyChangeListener("active", new ActivePropertyChangeListener());
        addPropertyChangeListener("anchor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
//                if (evt.getSource() == descriptor)
//                    descriptor.setDivederLocation(-1);
            }
        });
        addPropertyChangeListener("autoHide", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                boolean newValue = ((Boolean) evt.getNewValue());

                if (newValue) {
                    pinButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOn.png"));
                    pinButton.setToolTipText(ResourceBoundles.getResourceBoundle().getString("@@tool.tooltip.pin"));
                } else {
                    pinButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOff.png"));
                    pinButton.setToolTipText(ResourceBoundles.getResourceBoundle().getString("@@tool.tooltip.unpin"));
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
    }

    private JButton renderApplicationButton(String iconName, String actionCommnad, ActionListener actionListener, String tooltip) {
        JButton button = new JButton();
        button.setUI((ButtonUI) BasicButtonUI.createUI(button));
        button.setName(actionCommnad);
        button.setRolloverEnabled(true);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setActionCommand(actionCommnad);
        button.setBorderPainted(false);
        button.addActionListener(actionListener);
        button.setToolTipText(ResourceBoundles.getResourceBoundle().getString(tooltip));
        button.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/" + iconName + ".png"));

        return button;
    }

    private void configureDockedIcons() {
        setPinVisible(true);
        setFloating();
        setFloatingVisible(true);
        setDockedVisible(true);
        setDocked();
    }


    class ApplicationBarMouseAdapter extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            toolWindow.setActive(true);
        }
    }

    class ApplicationBarActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            toolWindow.setActive(true);

            String actionCommnad = e.getActionCommand();
            if ("visible".equals(actionCommnad)) {
                toolWindow.setVisible(false);
            } else if ("pin".equals(actionCommnad)) {
                toolWindow.setAutoHide(!toolWindow.isAutoHide());
            } else if ("floating".equals(actionCommnad)) {
                ToolWindowType type = toolWindow.getType();
                if (type == ToolWindowType.FLOATING || type == ToolWindowType.FLOATING_WINDOW) {
                    toolWindow.setType(ToolWindowType.DOCKED);
                } else if (type == ToolWindowType.DOCKED || type == ToolWindowType.SLIDING) {
                    toolWindow.setType(descriptor.isFloatingWindow() ? ToolWindowType.FLOATING_WINDOW : ToolWindowType.FLOATING);
                }
            } else if ("undock".equals(actionCommnad)) {
                ToolWindowType type = toolWindow.getType();
                if (type == ToolWindowType.DOCKED) {
                    toolWindow.setType(ToolWindowType.SLIDING);
                } else if (type == ToolWindowType.SLIDING) {
                    toolWindow.setType(ToolWindowType.DOCKED);
                }
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

            if (isInternalComponent(component)) {
                toolWindow.setActive(true);
                focusRequester = component;
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
                    pinButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOn.png"));
                } else
                    pinButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOff.png"));

                hideButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindow.png"));

                if (toolWindow.getType() == ToolWindowType.SLIDING) {
                    dockButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/docked.png"));
                } else
                    dockButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/sliding.png"));

                if (toolWindow.getType() == ToolWindowType.FLOATING) {
                    floatingButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fix.png"));
                } else
                    floatingButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floating.png"));

            } else {
                if (toolWindow.isAutoHide()) {
                    pinButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOnInactive.png"));
                } else
                    pinButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOffInactive.png"));

                hideButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindowInactive.png"));

                if (toolWindow.getType() == ToolWindowType.SLIDING) {
                    dockButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/dockedInactive.png"));
                } else
                    dockButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/slidingInactive.png"));

                if (toolWindow.getType() == ToolWindowType.FLOATING) {
                    floatingButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fixInactive.png"));
                } else
                    floatingButton.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floatingInactive.png"));
            }

            if (active && focusRequester != null && !valueAdjusting) {
                SwingUtil.requestFocus(focusRequester);
            }
        }
    }
}
