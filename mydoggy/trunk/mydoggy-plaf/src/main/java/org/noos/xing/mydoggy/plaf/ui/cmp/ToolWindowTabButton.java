package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
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


    public ToolWindowTabButton(MyDoggyToolWindowManager manager,
                               MyDoggyToolWindowTab tab,
                               ToolWindowTabPanel toolWindowTabPanel,
                               DockedContainer dockedContainer) {
        putClientProperty(MyDoggyToolWindowManager.class, manager);
        putClientProperty(MyDoggyToolWindowTab.class, tab);
        putClientProperty(ToolWindowTabPanel.class, toolWindowTabPanel);
        putClientProperty(DockedContainer.class, dockedContainer);

        tab.getCleaner().addCleaner(this);

        updateUI();
    }


    public void cleanup() {
        putClientProperty(MyDoggyToolWindowManager.class, null);
        putClientProperty(MyDoggyToolWindowTab.class, null);
        putClientProperty(ToolWindowTabPanel.class, null);
        putClientProperty(DockedContainer.class, null);
    }


    public void updateUI() {
        if (getClientProperty(MyDoggyToolWindowManager.class) != null)
            setUI((ToolWindowTabButtonUI) UIManager.getUI(this));
    }

    public ToolWindowTabButtonUI getUI() {
        return (ToolWindowTabButtonUI) super.getUI();
    }

    public String getUIClassID() {
        return uiClassID;
    }


    public ToolWindowTab getToolWindowTab() {
        return (ToolWindowTab) getClientProperty(ToolWindowTab.class);
    }

    public void setUI(ToolWindowTabPanelUI ui) {
        super.setUI(ui);
    }

}
