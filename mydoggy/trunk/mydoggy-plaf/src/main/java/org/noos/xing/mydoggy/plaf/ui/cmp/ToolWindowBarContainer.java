package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.ToolWindowBar;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowBarContainerUI;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleBarUI;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowBarContainer extends JPanel {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowBarContainerUI";


    public ToolWindowBarContainer(ToolWindowBar toolWindowBar) {
        putClientProperty(ToolWindowBar.class, toolWindowBar);
        updateUI();
    }


    public void updateUI() {
        if (getClientProperty(ToolWindowBar.class) != null)
            setUI((ToolWindowBarContainerUI) UIManager.getUI(this));
    }

    public void setUI(ToolWindowTitleBarUI ui) {
        super.setUI(ui);
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

}
