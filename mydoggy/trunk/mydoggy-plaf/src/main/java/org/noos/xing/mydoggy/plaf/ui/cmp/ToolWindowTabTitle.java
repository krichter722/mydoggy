package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTabTitleUI;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabTitle extends JLabel {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowTabTitleUI";


    protected ToolWindowTab toolWindowTab;


    public ToolWindowTabTitle(ToolWindowTab toolWindowTab) {
        this.toolWindowTab = toolWindowTab;
        updateUI();
    }


    public void updateUI() {
        if (toolWindowTab != null)
            setUI((ToolWindowTabTitleUI) UIManager.getUI(this));
    }

    public void setUI(ToolWindowTabTitleUI ui) {
        super.setUI(ui);
    }

    public ToolWindowTabTitleUI getUI() {
        return (ToolWindowTabTitleUI) super.getUI();
    }

    public String getUIClassID() {
        return uiClassID;
    }

    public ToolWindowTab getToolWindowTab() {
        return toolWindowTab;
    }
}
