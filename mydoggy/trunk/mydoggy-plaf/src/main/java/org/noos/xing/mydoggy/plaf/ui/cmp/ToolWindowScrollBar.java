package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.ToolWindowBar;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowScrollBarUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowScrollBar extends JComponent {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowScrollBarUI";


    protected ToolWindowBar toolWindowBar;
    protected Container container;


    public ToolWindowScrollBar(ToolWindowBar toolWindowBar, Container container) {
        this.toolWindowBar = toolWindowBar;
        this.container = container;

        updateUI();
    }


    public void updateUI() {
        if (toolWindowBar != null)
            setUI((ToolWindowScrollBarUI) UIManager.getUI(this));
    }

    public ToolWindowScrollBarUI getUI() {
        return (ToolWindowScrollBarUI) ui;
    }

    public void setUI(ToolWindowScrollBarUI ui) {
        super.setUI(ui);
    }

    public String getUIClassID() {
        return uiClassID;
    }

    
    public ToolWindowBar getToolWindowBar() {
        return toolWindowBar;
    }

    public Container getContainer() {
        return container;
    }

    public void ensureVisible(Component component) {
        getUI().ensureVisible(component);
    }
}
