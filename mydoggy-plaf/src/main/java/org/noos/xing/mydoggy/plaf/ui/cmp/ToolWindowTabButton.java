package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.cleaner.CleanerProvider;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTabButtonUI;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTabPanelUI;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabButton extends JPanel implements Cleaner {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowTabButtonUI";


    protected ToolWindowTab toolWindowTab;
    protected ToolWindowTabPanel toolWindowTabPanel;


    public ToolWindowTabButton(ToolWindowTab tab, ToolWindowTabPanel toolWindowTabPanel) {
        this.toolWindowTab = tab;
        this.toolWindowTabPanel = toolWindowTabPanel;

        ((CleanerProvider) tab).getCleanerAggregator().addCleaner(this);

        updateUI();
    }


    public void cleanup() {
        this.toolWindowTab = null;
        this.toolWindowTabPanel = null;
    }


    public void updateUI() {
        if (toolWindowTab != null)
            setUI((ToolWindowTabButtonUI) UIManager.getUI(this));
    }

    public ToolWindowTabButtonUI getUI() {
        return (ToolWindowTabButtonUI) super.getUI();
    }

    public String getUIClassID() {
        return uiClassID;
    }


    public ToolWindowTab getToolWindowTab() {
        return toolWindowTab;
    }

    public void setUI(ToolWindowTabPanelUI ui) {
        super.setUI(ui);
    }

    public ToolWindowTabPanel getToolWindowTabPanel() {
        return toolWindowTabPanel;
    }
}
