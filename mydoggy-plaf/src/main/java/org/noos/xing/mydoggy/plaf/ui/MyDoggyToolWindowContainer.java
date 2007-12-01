package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowContainer implements ToolWindowContainer {
    protected DockedContainer dockedContainer;
    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected ToolWindowTabPanel titleBarTabs;
    protected Component titleBar;
    protected TitleBarButtons titleBarButtons;

    public MyDoggyToolWindowContainer(DockedContainer dockedContainer) {
        this.dockedContainer = dockedContainer;
        this.dockedContainer = dockedContainer;
        this.descriptor = dockedContainer.getToolWindowDescriptor();
        this.toolWindow = descriptor.getToolWindow();
        this.titleBarButtons = dockedContainer.getTitleBarButtons();
        this.titleBarTabs = dockedContainer.getTitleBarTabs();
        this.titleBar = dockedContainer.getTitleBar();
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

    public void uninstall() {        
    }

    public void propertyChange(PropertyChangeEvent evt) {
        dockedContainer.propertyChange(evt);
    }

    public void showPopupMenu(Component c, int x, int y) {
        dockedContainer.showPopupMenu(c, x, y);
    }
}
