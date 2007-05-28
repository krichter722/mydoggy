package org.noos.xing.mydoggy.plaf.ui;

import com.sun.java.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowActiveButton extends JButton {

    public ToolWindowActiveButton() {
    }

    public ToolWindowActiveButton(String text) {
        super(text);
    }

    public void setUI(ButtonUI ui) {
        UIManager.put("Button.textShiftOffset", 1);
        super.setUI(new BasicButtonUI() {

            protected void paintButtonPressed(Graphics g, AbstractButton b) {
                setTextShiftOffset();
            }

            protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
                AbstractButton b = (AbstractButton) c;
                FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
                int mnemonicIndex = b.getDisplayedMnemonicIndex();

                g.setColor(b.getForeground());
                SwingUtilities2.drawStringUnderlineCharAt(c, g, text, mnemonicIndex,
                                                          textRect.x + getTextShiftOffset(),
                                                          textRect.y + fm.getAscent() + getTextShiftOffset());
            }

            protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                Icon icon = b.getIcon();
                Icon tmpIcon = null;

                if (icon == null) {
                    return;
                }

                if (model.isPressed() && model.isArmed()) {
                    tmpIcon = b.getPressedIcon();
                    if (tmpIcon != null) {
                        // revert back to 0 offset
                        clearTextShiftOffset();
                    }
                } else if (b.isRolloverEnabled() && model.isRollover()) {
                    if (model.isSelected()) {
                        tmpIcon = b.getRolloverSelectedIcon();
                    } else {
                        tmpIcon = b.getRolloverIcon();
                    }
                } else if (model.isSelected()) {
                    tmpIcon = b.getSelectedIcon();
                }

                if (tmpIcon != null) {
                    icon = tmpIcon;
                }

                if (model.isPressed() && model.isArmed()) {
                    icon.paintIcon(c, g, iconRect.x + getTextShiftOffset(),
                                   iconRect.y + getTextShiftOffset());
                } else {
                    icon.paintIcon(c, g, iconRect.x, iconRect.y);
                }
            }
        });

        setRolloverEnabled(true);
        setOpaque(false);
        setFocusPainted(false);
        setFocusable(false);
        setBorder(null);
        setBorderPainted(false);
    }
}
