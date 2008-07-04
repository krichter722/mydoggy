package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class CornerPanelUI extends BasicPanelUI {

    public static ComponentUI createUI(JComponent c) {
        return new CornerPanelUI();
    }


    protected ToolWindowManagerDescriptor.Corner corner;


    public CornerPanelUI() {
    }

    @Override
    public void installUI(JComponent c) {
        this.corner = (ToolWindowManagerDescriptor.Corner) c.getClientProperty(ToolWindowManagerDescriptor.Corner.class);
        
        super.installUI(c);
    }

    @Override
    protected void installDefaults(JPanel p) {
        super.installDefaults(p);
        LookAndFeel.installColorsAndFont(p,
                                         "CornerPanelUI.background",
                                         "CornerPanelUI.foreground",
                                         "CornerPanelUI.font");
        LookAndFeel.installBorder(p, "CornerPanelUI.border");
    }

}
