package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleBar;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleButtonPanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class MyDoggyToolWindowContainer implements ToolWindowContainer {
    protected DockedContainer dockedContainer;

    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;

    protected ToolWindowPanel toolWindowPanel;
    protected ToolWindowTitleBar toolWindowTitleBar;
    protected ToolWindowTabPanel toolWindowTabPanel;
    protected ToolWindowTitleButtonPanel titleBarButtons;


    public MyDoggyToolWindowContainer(DockedContainer dockedContainer) {
        this.dockedContainer = dockedContainer;

        this.descriptor = dockedContainer.getToolWindowDescriptor();
        this.toolWindow = descriptor.getToolWindow();

        this.toolWindowPanel = descriptor.getToolWindowPanel();
        this.toolWindowTitleBar = toolWindowPanel.getToolWindowTitleBar();
        this.titleBarButtons = toolWindowTitleBar.getToolWindowTitleButtonPanel();
        this.toolWindowTabPanel = toolWindowTitleBar.getToolWindowTabPanel();

        descriptor.getCleaner().addCleaner(this);
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

    public void cleanup() {
        // Finalize
        dockedContainer = null;
        descriptor = null;
        toolWindow = null;

        toolWindowPanel = null;
        toolWindowTitleBar = null;
        titleBarButtons = null;
        toolWindowTabPanel = null;
    }
}
