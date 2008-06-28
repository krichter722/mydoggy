package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindowBar;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowBarPanelUI extends BasicPanelUI {

    protected ToolWindowBar toolWindowBar;

    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowBarPanelUI((ToolWindowBar) c.getClientProperty(ToolWindowBar.class));
    }


    public ToolWindowBarPanelUI(ToolWindowBar toolWindowBar) {
        this.toolWindowBar = toolWindowBar;
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
}
