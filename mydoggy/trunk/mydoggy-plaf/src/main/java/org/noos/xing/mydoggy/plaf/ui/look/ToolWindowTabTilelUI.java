package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ToolWindowTabTilelUI extends BasicLabelUI implements MouseListener {
    protected MyDoggyToolWindowTab tab;
    protected ToolWindow toolWindow;

    protected JLabel titleLabel;

    protected boolean pressed;
    protected boolean inside;


    public ToolWindowTabTilelUI(MyDoggyToolWindowTab tab) {
        this.tab = tab;
        this.toolWindow = tab.getOwner();
        this.pressed = this.inside = false;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        this.titleLabel = (JLabel) c;
    }

    @Override
    protected void installDefaults(JLabel c) {
        super.installDefaults(c);
    }

    public void update(Graphics g, JComponent c) {
        if (tab.isFlashing() && toolWindow.isVisible()) {
            // TODO: how to manager flashingState???
//            if (flashingState) {
                titleLabel.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED));
//            } else {
//                titleLabel.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
//            }
        } else {
            if (tab.isSelected())
                titleLabel.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED));
            else
                titleLabel.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
        }

        super.update(g, c);
    }

    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        if (pressed && inside)
            super.paintEnabledText(l, g, s, textX + 1, textY + 1);
        else
            super.paintEnabledText(l, g, s, textX, textY);
    }


    public void mousePressed(MouseEvent e) {
        toolWindow.setActive(true);

        if (SwingUtilities.isLeftMouseButton(e) && !tab.isSelected()) {
            pressed = true;
            titleLabel.repaint();
        } else {
            pressed = false;
            titleLabel.repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        pressed = false;
        titleLabel.repaint();
    }

    public void mouseEntered(MouseEvent e) {
        inside = true;
        titleLabel.repaint();
    }

    public void mouseExited(MouseEvent e) {
        inside = false;
        titleLabel.repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tab.setSelected(true);
                }
            });
        }
    }

}
