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


    public ToolWindowTabTitle(ToolWindowTab toolWindowTab) {
        putClientProperty(ToolWindowTab.class, toolWindowTab);
        updateUI();
    }



    public void updateUI() {
        if (getClientProperty(ToolWindowTab.class) != null)
            setUI((ToolWindowTabTitleUI) UIManager.getUI(this));
    }

    public void setUI(ToolWindowTabTitleUI ui) {
        super.setUI(ui);
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

}
