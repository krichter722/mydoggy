package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindowBar;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowBarPanel;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

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


    public void installUI(JComponent c) {
        // Init fields
        this.toolWindowBarPanel = (ToolWindowBarPanel) c;
        this.toolWindowBar = toolWindowBarPanel.getToolWindowBar();

        super.installUI(c);
    }

    protected void installDefaults(JPanel p) {
        super.installDefaults(p);

        SwingUtil.installColorsAndFont(p,
                "ToolWindowBarPanelUI.background",
                "ToolWindowBarPanelUI.foreground",
                "ToolWindowBarPanelUI.font");
        SwingUtil.installBorder(p, "ToolWindowBarPanelUI.border");
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        // Reset fields
        this.toolWindowBarPanel = null;
        this.toolWindowBar = null;
    }
}
