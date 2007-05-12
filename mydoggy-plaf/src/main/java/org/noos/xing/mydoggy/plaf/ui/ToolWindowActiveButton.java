package org.noos.xing.mydoggy.plaf.ui;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ToolWindowActiveButton extends JButton {

    public void setUI(ButtonUI ui) {
        UIManager.put("Button.textShiftOffset", 1);
        super.setUI(new BasicButtonUI() {
            protected void paintButtonPressed(Graphics g, AbstractButton b) {
                setTextShiftOffset();
            }
        });
        setRolloverEnabled(true);
        setOpaque(false);
        setFocusPainted(false);
        setFocusable(false);
        setBorderPainted(false);
    }
}
