package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindowBar;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowBarContainerUI extends BasicPanelUI {

    protected ToolWindowBar toolWindowBar;

    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowBarContainerUI((ToolWindowBar) c.getClientProperty(ToolWindowBar.class));
    }


    public ToolWindowBarContainerUI(ToolWindowBar toolWindowBar) {
        this.toolWindowBar = toolWindowBar;
    }


    @Override
    protected void installDefaults(JPanel p) {
        super.installDefaults(p);
        LookAndFeel.installColorsAndFont(p,
                                         "ToolWindowBarContainerUI.background",
                                         "ToolWindowBarContainerUI.foreground",
                                         "ToolWindowBarContainerUI.font");
        LookAndFeel.installBorder(p, "ToolWindowBarContainerUI.border");
    }
}
