package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.common.Question;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.common.context.DefaultMutableContext;
import org.noos.xing.mydoggy.plaf.support.CleanablePropertyChangeSupport;
import org.noos.xing.mydoggy.plaf.ui.cmp.*;
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
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
    protected ToolWindowTitleBar titleBar;
    protected JPanel componentContainer;

    protected CleanablePropertyChangeSupport propertyChangeSupport;
    protected PropertyChangeListener focusListener;

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
        descriptor.showPopupMenu(c, x, y);
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }


    public ToolWindowDescriptor getToolWindowDescriptor() {
        return descriptor;
    }

    public Container getContentContainer() {
        return container;
    }

    public ToolWindowTitleButtonPanel getToolWindowTitleButtonPanel() {
        return titleBar.getToolWindowTitleButtonPanel();
    }

    public ToolWindowTabPanel getTitleBarTabs() {
        return titleBar.getToolWindowTabPanel();
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

    public void setPopupUpdater(PopupUpdater popupUpdater) {
        this.popupUpdater = popupUpdater;
    }


    protected void initComponents() {
        propertyChangeSupport = new CleanablePropertyChangeSupport(this);
        descriptor.getCleaner().addCleaner(propertyChangeSupport);

        // Container
        container = (JPanel) resourceManager.createComponent(MyDoggyKeySpace.TOOL_WINDOW_CONTAINER,
                                                             descriptor.getManager().getContext());
        container.setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL},
                                                                   {SwingUtil.getInt("ToolWindowTitleBarUI.length", 16), TableLayout.FILL}},
                                                    false));
        container.setName("toolWindow.container." + toolWindow.getId());
        container.setFocusTraversalPolicyProvider(true);
        container.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
//        container.setFocusCycleRoot(true);
        container.setFocusable(false);
        container.putClientProperty(ToolWindow.class, toolWindow);

        // Title Bar
        titleBar = new ToolWindowTitleBar(descriptor, this);
        titleBar.setName("toolWindow.titleBar." + toolWindow.getId());
        titleBar.setEnabled(false);
//        titleBar.setFocusTraversalPolicyProvider(true);
//        titleBar.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
//        titleBar.setFocusCycleRoot(true);
        titleBar.setFocusable(false);

        toolWindow.getToolWindowTabs()[0].setSelected(true);

        // Set Component container
        componentContainer = new JPanel();
        componentContainer.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        componentContainer.setOpaque(false);
        componentContainer.add(descriptor.getComponent(), "0,0,FULL,FULL");
        componentContainer.setFocusable(false);

        // Set Container content
        container.add(titleBar, "0,0");
        container.add(componentContainer, "0,1");

        focusRequester = SwingUtil.findFocusable(descriptor.getComponent());
        ToolWindowTitleButtonPanel toolWindowTitleButtonPanel = titleBar.getToolWindowTitleButtonPanel();
        if (focusRequester == null) {
            toolWindowTitleButtonPanel.getFocusable().setFocusable(true);
            focusRequester = toolWindowTitleButtonPanel.getFocusable();
        }
    }

    protected void initListeners() {
        focusListener = new FocusOwnerPropertyChangeListener(
                resourceManager.createInstance(ParentOfQuestion.class,
                                               new DefaultMutableContext(ToolWindow.class, toolWindow,
                                                                         Component.class, container))
        );

        addPropertyChangeListener("active", new ActivePropertyChangeListener());
        addPropertyChangeListener("type", new TypePropertyChangeListener());
        addPropertyChangeListener("maximized.before", new MaximizedBeforePropertyChangeListener());
        addPropertyChangeListener("manager.window.ancestor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null) {
                    KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                    keyboardFocusManager.removePropertyChangeListener("focusOwner", focusListener);
                    keyboardFocusManager.addPropertyChangeListener("focusOwner", focusListener);
                } else {
                    toolWindow.setFlashing(false);
                }
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", focusListener);

        toolWindow.addToolWindowListener(new DockedToolWindowListener());
    }

    protected void assignFocus() {
        focusRequester = SwingUtil.findFocusable(descriptor.getComponent());
        ToolWindowTitleButtonPanel toolWindowTitleButtonPanel = titleBar.getToolWindowTitleButtonPanel();

        if (focusRequester == null) {
            toolWindowTitleButtonPanel.getFocusable().setFocusable(true);
            focusRequester = toolWindowTitleButtonPanel.getFocusable();
        } else {
            toolWindowTitleButtonPanel.getFocusable().setFocusable(false);
        }
        SwingUtil.requestFocus(focusRequester);
    }


    public class DockedToolWindowListener implements Cleaner, ToolWindowListener, PropertyChangeListener {

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
                                titleBar.getToolWindowTitleButtonPanel().getFocusable().requestFocus();
                        }
                    });
                }
            } else if ("component".equals(property)) {
                if (descriptor.getComponent() == evt.getOldValue())
                    setMainComponent(tab.getComponent());
            }

        }
    }

    public class FocusOwnerPropertyChangeListener implements PropertyChangeListener, Cleaner {
        protected Question<Component, Boolean> parentOf;

        public FocusOwnerPropertyChangeListener(Question<Component, Boolean> parentOf) {
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

            System.out.println(toolWindow.getId() + " - cmp = " + component);

            if (parentOf.getAnswer(component)) {
                toolWindow.setActive(true);
                if (focusRequester == null)
                    focusRequester = component;
                else {
                    if (!(focusRequester instanceof ToolWindowTitleButton))
                        focusRequester = component;
                    else {
                        if (focusRequester == titleBar.getToolWindowTitleButtonPanel().getFocusable())
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

    public class ActivePropertyChangeListener implements PropertyChangeListener {

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
                if (focusRequester == titleBar.getToolWindowTitleButtonPanel().getFocusable())
                    assignFocus();
                else
                    SwingUtil.requestFocus(focusRequester);

            }
        }

    }

    public class TypePropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor)
                return;

            if (evt.getOldValue() == ToolWindowType.EXTERN) {
                setMainComponent(toolWindow.getToolWindowTabs()[0].getComponent());
            }
        }

    }

    public class MaximizedBeforePropertyChangeListener implements PropertyChangeListener {
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
