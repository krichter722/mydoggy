package org.noos.xing.mydoggy.plaf.ui.look;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTitleButtonUI extends BasicButtonUI {
    // Shared UI object
    private final static ToolWindowTitleButtonUI buttonUI = new ToolWindowTitleButtonUI();

    
    public static ComponentUI createUI(JComponent c) {
        return buttonUI;
    }


    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        
        b.setRolloverEnabled(true);
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setFocusable(false);
        b.setBorder(null);
        b.setBorderPainted(false);
        b.setForeground(Color.WHITE);

        defaultTextShiftOffset = 1;
    }

    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        setTextShiftOffset();
    }

}
