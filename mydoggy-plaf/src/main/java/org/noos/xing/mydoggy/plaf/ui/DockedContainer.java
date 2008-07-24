package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
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

    boolean valueAdjusting;


    public DockedContainer(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();

        descriptor.getCleaner().addCleaner(this);

        initListeners();
    }


    public void cleanup() {
        // Finalize
        toolWindow = null;
        descriptor = null;
    }

    public void updateUI() {
        SwingUtilities.updateComponentTreeUI(descriptor.getToolWindowPanel());
    }

    public void propertyChange(PropertyChangeEvent evt) {
//        propertyChangeSupport.firePropertyChange(evt);
    }


    public ToolWindowDescriptor getToolWindowDescriptor() {
        return descriptor;
    }


    protected void initListeners() {
        // Init tool window properties listeners
        PropertyChangeEventSource toolWindowSource = descriptor.getToolWindow();
        toolWindowSource.addPlafPropertyChangeListener("type", new TypePropertyChangeListener());
        toolWindowSource.addPlafPropertyChangeListener("maximized.before", new MaximizedBeforePropertyChangeListener());

        toolWindow.addToolWindowListener(new DockedToolWindowListener());
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
                JPanel componentContainer = descriptor.getToolWindowPanel().getComponentContainer();
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
                            descriptor.setMainComponent(tab.getComponent());

                            Component focusable = SwingUtil.findFocusable(tab.getComponent());
                            if (focusable != null)
                                focusable.requestFocus();
                            else
                                descriptor.getToolWindowPanel().getToolWindowTitleBar().getToolWindowTitleButtonPanel().getFocusable().requestFocus();
                        }
                    });
                }
            } else if ("component".equals(property)) {
                if (descriptor.getComponent() == evt.getOldValue())
                    descriptor.setMainComponent(tab.getComponent());
            }

        }
    }

    public class TypePropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getOldValue() == ToolWindowType.EXTERN)
                descriptor.setMainComponent(toolWindow.getToolWindowTabs()[0].getComponent());
        }

    }

    public class MaximizedBeforePropertyChangeListener implements PropertyChangeListener {
        ByteArrayOutputStream workspace;
        boolean valueAdj = false;

        public void propertyChange(PropertyChangeEvent evt) {
            if ((Boolean) evt.getNewValue()) {
                descriptor.getManager().getPersistenceDelegate().save(workspace = new ByteArrayOutputStream());
            } else if (workspace != null) {
                if (valueAdj)
                    return;

                valueAdj = true;
                try {
                    descriptor.getManager().getPersistenceDelegate().merge(new ByteArrayInputStream(workspace.toByteArray()),
                                                                           descriptor.getManager().getResourceManager().getObject(PersistenceDelegate.MergePolicy.class,
                                                                                                                                  PersistenceDelegate.MergePolicy.UNION));
                    workspace = null;
                } finally {
                    valueAdj = false;
                }
            }
        }

    }

}
