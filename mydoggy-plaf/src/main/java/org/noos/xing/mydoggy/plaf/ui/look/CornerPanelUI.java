package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.plaf.ui.cmp.CornerPanel;

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


    protected CornerPanel cornerPanel;


    public CornerPanelUI() {
    }

    @Override
    public void installUI(JComponent c) {
        // Init fields
        this.cornerPanel = (CornerPanel) c;

        super.installUI(c);
    }


    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        
        // Reset fields
        this.cornerPanel = null;
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
