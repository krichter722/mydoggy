package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleBarUI;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTitleBar extends JComponent {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowTitleBarUI";


    public ToolWindowTitleBar(ToolWindowDescriptor toolWindowDescriptor) {
        putClientProperty(ToolWindowDescriptor.class, toolWindowDescriptor);
        setLayout(null);
        setDoubleBuffered(true);
        updateUI();
    }


    public void updateUI() {
        setUI((ToolWindowTitleBarUI) UIManager.getUI(this));
    }

    public void setUI(ToolWindowTitleBarUI ui) {
        super.setUI(ui);
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }
}
