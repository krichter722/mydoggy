package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleBar;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleButtonPanel;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class MyDoggyToolWindowContainer implements ToolWindowContainer {
    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;

    protected ToolWindowPanel toolWindowPanel;
    protected ToolWindowTitleBar toolWindowTitleBar;
    protected ToolWindowTabPanel toolWindowTabPanel;
    protected ToolWindowTitleButtonPanel titleBarButtons;


    public MyDoggyToolWindowContainer(ToolWindowDescriptor toolWindowDescriptor) {
        this.descriptor = toolWindowDescriptor;
        this.toolWindow = descriptor.getToolWindow();

        this.toolWindowPanel = descriptor.getToolWindowPanel();

        this.toolWindowTitleBar = toolWindowPanel.getToolWindowTitleBar();
        this.titleBarButtons = toolWindowTitleBar.getToolWindowTitleButtonPanel();
        this.toolWindowTabPanel = toolWindowTitleBar.getToolWindowTabPanel();

        descriptor.getCleaner().addCleaner(this);
    }


    public void updateUI() {
        SwingUtilities.updateComponentTreeUI(toolWindowPanel);
    }

    public void cleanup() {
        // Clean fields...
        descriptor = null;
        toolWindow = null;

        toolWindowPanel = null;
        toolWindowTitleBar = null;
        titleBarButtons = null;
        toolWindowTabPanel = null;
    }
}
