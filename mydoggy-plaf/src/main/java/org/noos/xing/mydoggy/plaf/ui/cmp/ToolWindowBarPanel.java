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


    public ToolWindowBarPanel(ToolWindowBar toolWindowBar) {
        putClientProperty(ToolWindowBar.class, toolWindowBar);
        updateUI();
    }


    public void updateUI() {
        if (getClientProperty(ToolWindowBar.class) != null)
            setUI((ToolWindowBarPanelUI) UIManager.getUI(this));
    }

    public void setUI(ToolWindowTitleBarUI ui) {
        super.setUI(ui);
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

}
