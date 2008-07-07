package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleBarUI;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTitleBar extends JPanel implements Cleaner {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowTitleBarUI";

    protected ToolWindowDescriptor toolWindowDescriptor;
    protected ToolWindowTabPanel toolWindowTabPanel;
    protected ToolWindowTitleButtonPanel toolWindowTitleButtonPanel;


    public ToolWindowTitleBar(ToolWindowDescriptor toolWindowDescriptor, DockedContainer dockedContainer) {
        this.toolWindowDescriptor = toolWindowDescriptor;
        this.toolWindowTabPanel = new ToolWindowTabPanel(toolWindowDescriptor, dockedContainer);
        this.toolWindowTitleButtonPanel = new ToolWindowTitleButtonPanel(toolWindowDescriptor, dockedContainer);

        toolWindowDescriptor.getCleaner().addAfter(dockedContainer, this);

        updateUI();
    }


    public void cleanup() {
        this.toolWindowDescriptor = null;
        this.toolWindowTabPanel = null;
        this.toolWindowTitleButtonPanel = null;
    }

    public void updateUI() {
        if (toolWindowDescriptor != null)
            setUI((ToolWindowTitleBarUI) UIManager.getUI(this));
    }

    public void setUI(ToolWindowTitleBarUI ui) {
        super.setUI(ui);
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }


    public ToolWindowDescriptor getToolWindowDescriptor() {
        return toolWindowDescriptor;
    }

    public ToolWindowTabPanel getToolWindowTabPanel() {
        return toolWindowTabPanel;
    }

    public ToolWindowTitleButtonPanel getToolWindowTitleButtonPanel() {
        return toolWindowTitleButtonPanel;
    }
}
