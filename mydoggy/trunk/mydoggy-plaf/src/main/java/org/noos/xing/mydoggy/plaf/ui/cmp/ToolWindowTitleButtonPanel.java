package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleButtonPanelUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTitleButtonPanel extends JPanel implements Cleaner {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowTitleButtonPanelUI";

    
    protected ToolWindowDescriptor toolWindowDescriptor;


    public ToolWindowTitleButtonPanel(ToolWindowDescriptor toolWindowDescriptor) {
        this.toolWindowDescriptor = toolWindowDescriptor;

        toolWindowDescriptor.getCleaner().addCleaner(this);

        updateUI();
    }


    public void cleanup() {
        toolWindowDescriptor = null;
    }

    public void updateUI() {
        if (toolWindowDescriptor != null)
            setUI((ToolWindowTitleButtonPanelUI) UIManager.getUI(this));
    }

    public void setUI(ToolWindowTitleButtonPanelUI ui) {
        super.setUI(ui);
    }

    public ToolWindowTitleButtonPanelUI getUI() {
        return (ToolWindowTitleButtonPanelUI) super.getUI();
    }

    public String getUIClassID() {
        return uiClassID;
    }


    public Component getFocusable() {
        return getUI().getFocusable();
    }

    public ToolWindowDescriptor getToolWindowDescriptor() {
        return toolWindowDescriptor;
    }
}
