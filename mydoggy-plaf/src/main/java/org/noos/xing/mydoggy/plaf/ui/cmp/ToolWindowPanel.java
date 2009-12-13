package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowPanelUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowPanel extends JPanel implements DockableOwner {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowPanelUI";


    protected ToolWindowDescriptor toolWindowDescriptor;

    protected ToolWindowTitleBar toolWindowTitleBar;
    protected JPanel componentContainer;
    protected Component component;


    public ToolWindowPanel(ToolWindowDescriptor toolWindowDescriptor) {
        this.toolWindowDescriptor = toolWindowDescriptor;

        ToolWindow toolWindow = toolWindowDescriptor.getToolWindow();

        setName("toolWindow.container." + toolWindow.getId());
        setFocusTraversalPolicyProvider(true);
        setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
        setFocusable(false);
        putClientProperty(ToolWindow.class, toolWindowDescriptor.getToolWindow());

        // Title Bar
        toolWindowTitleBar = new ToolWindowTitleBar(toolWindowDescriptor);
        toolWindowTitleBar.setName("toolWindow.titleBar." + toolWindow.getId());
        toolWindowTitleBar.setEnabled(false);
        toolWindowTitleBar.setFocusable(false);

        // Set Component container
        componentContainer = new JPanel();
        componentContainer.setOpaque(false);
        componentContainer.setFocusable(false);

        // Now we can update the ui...
        updateUI();
    }


    public Dockable getDockable() {
        return toolWindowDescriptor.getToolWindow();
    }

    public void updateUI() {
        if (toolWindowDescriptor != null)
            setUI((ToolWindowPanelUI) UIManager.getUI(this));
    }

    public String getUIClassID() {
        return uiClassID;
    }

    public ToolWindowPanelUI getUI() {
        return (ToolWindowPanelUI) super.getUI();
    }

    public void setUI(ToolWindowPanelUI ui) {
        super.setUI(ui);
    }

    public ToolWindowDescriptor getToolWindowDescriptor() {
        return toolWindowDescriptor;
    }

    public ToolWindowTitleBar getToolWindowTitleBar() {
        return toolWindowTitleBar;
    }

    public JPanel getComponentContainer() {
        return componentContainer;
    }

    public void setComponent(Component component) {
        this.component = component;
        getUI().updateComponent();
    }

    public Component getComponent() {
        return component;
    }

    public void removeComponent(Component component) {
        getUI().removeComponent();
        this.component = null;
    }

    public Component getFocusable() {
        return getToolWindowTitleBar().getToolWindowTitleButtonPanel().getFocusable();
    }
}
