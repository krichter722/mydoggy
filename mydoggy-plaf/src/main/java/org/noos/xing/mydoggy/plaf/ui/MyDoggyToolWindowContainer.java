package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleButtonPanel;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class MyDoggyToolWindowContainer implements ToolWindowContainer {
    protected DockedContainer dockedContainer;
    protected ResourceManager resourceManager;

    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;

    protected Component titleBar;
    protected ToolWindowTabPanel toolWindowTabContainer;
    protected ToolWindowTitleButtonPanel titleBarButtons;


    public MyDoggyToolWindowContainer(DockedContainer dockedContainer) {
        this.dockedContainer = dockedContainer;
        this.resourceManager = dockedContainer.getResourceManager();

        this.descriptor = dockedContainer.getToolWindowDescriptor();
        this.toolWindow = descriptor.getToolWindow();

        this.titleBarButtons = dockedContainer.getToolWindowTitleButtonPanel();
        this.toolWindowTabContainer = dockedContainer.getTitleBarTabs();
        this.titleBar = dockedContainer.getTitleBar();

        descriptor.getCleaner().addCleaner(this);
    }


    public ResourceManager getResourceManager() {
        return dockedContainer.getResourceManager();
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        dockedContainer.addPropertyChangeListener(property, listener);
    }

    public void updateUI() {
        dockedContainer.updateUI();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        dockedContainer.propertyChange(evt);
    }

    public void showPopupMenu(Component c, int x, int y) {
        descriptor.showPopupMenu(c, x, y);
    }

    public void cleanup() {
        // Finalize
        dockedContainer = null;
        resourceManager = null;
        descriptor = null;
        toolWindow = null;

        titleBar = null;
        toolWindowTabContainer = null;
        titleBarButtons = null;
    }
}
