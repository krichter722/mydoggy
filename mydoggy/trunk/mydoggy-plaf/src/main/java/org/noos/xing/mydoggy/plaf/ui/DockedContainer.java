package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.PersistenceDelegate;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.ToolWindowType;
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
public class DockedContainer extends MyDoggyToolWindowContainer {


    public DockedContainer(ToolWindowDescriptor descriptor) {
        super(descriptor);

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
            if (toolWindow.getToolWindowTabs().length == 0)
                descriptor.getToolWindowPanel().removeComponent(event.getToolWindowTab().getComponent());

            event.getToolWindowTab().removePropertyChangeListener(this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            final ToolWindowTab tab = (ToolWindowTab) evt.getSource();
            String property = evt.getPropertyName();

            if ("selected".equals(property)) {
                if (evt.getNewValue() == Boolean.TRUE) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Component focusable = descriptor.getToolWindowPanel().getFocusable();
                            focusable.setFocusable(true);
                            focusable.requestFocusInWindow();
                        }
                    });
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Component toolWindowPanelFocusable = descriptor.getToolWindowPanel().getFocusable();

                            if (toolWindowPanelFocusable.isFocusOwner()) {
                                descriptor.setComponent(tab.getComponent());

                                Component focusable = SwingUtil.findFocusable(tab.getComponent());
                                if (focusable != null) {
                                    focusable.requestFocusInWindow();
                                    toolWindowPanelFocusable.setFocusable(false);
                                } else
                                    toolWindowPanelFocusable.requestFocusInWindow();
                            } else
                                SwingUtilities.invokeLater(this);
                        }
                    });
                }
            } else if ("component".equals(property)) {
                if (descriptor.getComponent() == evt.getOldValue())
                    descriptor.setComponent(tab.getComponent());
            }

        }
    }

    public class TypePropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getOldValue() == ToolWindowType.EXTERN)
                descriptor.setComponent(toolWindow.getToolWindowTabs()[0].getComponent());
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
