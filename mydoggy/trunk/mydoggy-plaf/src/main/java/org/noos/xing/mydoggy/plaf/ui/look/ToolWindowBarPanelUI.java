package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindowBar;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowBarPanel;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowBarPanelUI extends BasicPanelUI {


    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowBarPanelUI();
    }


    protected ToolWindowBarPanel toolWindowBarPanel;
    protected ToolWindowBar toolWindowBar;


    public ToolWindowBarPanelUI() {
    }

    @Override
    public void installUI(JComponent c) {
        this.toolWindowBarPanel = (ToolWindowBarPanel) c;
        this.toolWindowBar = toolWindowBarPanel.getToolWindowBar();

        super.installUI(c);
    }

    @Override
    protected void installDefaults(JPanel p) {
        super.installDefaults(p);

        LookAndFeel.installColorsAndFont(p,
                                         "ToolWindowBarPanelUI.background",
                                         "ToolWindowBarPanelUI.foreground",
                                         "ToolWindowBarPanelUI.font");
        LookAndFeel.installBorder(p, "ToolWindowBarPanelUI.border");
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        // cleanup
        this.toolWindowBarPanel = null;
        this.toolWindowBar = null;
    }
}
