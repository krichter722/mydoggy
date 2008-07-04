package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.ToolWindowBar;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowBarPanelUI;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleBarUI;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowBarPanel extends JPanel {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowBarPanelUI";


    protected ToolWindowBar toolWindowBar;


    public ToolWindowBarPanel(ToolWindowBar toolWindowBar) {
        this.toolWindowBar = toolWindowBar;
        updateUI();
    }


    public void updateUI() {
        if (toolWindowBar != null)
            setUI((ToolWindowBarPanelUI) UIManager.getUI(this));
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    public void setUI(ToolWindowTitleBarUI ui) {
        super.setUI(ui);
    }

    public ToolWindowBar getToolWindowBar() {
        return toolWindowBar;
    }
}
