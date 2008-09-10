package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.cleaner.CleanerProvider;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabTitle;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ToolWindowTabTitleUI extends BasicLabelUI implements MouseListener, Cleaner {


    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowTabTitleUI();
    }


    protected ToolWindowTab tab;
    protected ToolWindow toolWindow;

    protected ToolWindowTabTitle toolWindowTabTitle;

    protected boolean pressed;
    protected boolean inside;


    public ToolWindowTabTitleUI() {
    }


    public void cleanup() {
        uninstallUI(toolWindowTabTitle);
    }


    public void mousePressed(MouseEvent e) {
        toolWindow.setActive(true);

        if (SwingUtilities.isLeftMouseButton(e) && !tab.isSelected()) {
            pressed = true;
            toolWindowTabTitle.repaint();
        } else {
            pressed = false;
            toolWindowTabTitle.repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        pressed = false;
        toolWindowTabTitle.repaint();
    }

    public void mouseEntered(MouseEvent e) {
        inside = true;
        toolWindowTabTitle.repaint();
    }

    public void mouseExited(MouseEvent e) {
        inside = false;
        toolWindowTabTitle.repaint();
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


    public void installUI(JComponent c) {
        // Init Fields
        this.toolWindowTabTitle = (ToolWindowTabTitle) c;
        this.tab = toolWindowTabTitle.getToolWindowTab();
        this.toolWindow = tab.getOwner();

        super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        // Reset Fields
        this.toolWindowTabTitle = null;
        this.tab = null;
        this.toolWindow = null;
    }

    protected void installDefaults(JLabel c) {
        super.installDefaults(c);

        this.pressed = this.inside = false;

        if (c.getFont() != null)
            c.setFont(c.getFont().deriveFont(SwingUtil.getFloat("ToolWindowTabTitleUI.font.size", 12)));
        
        c.setText(tab.getTitle());
        c.setIcon(tab.getIcon());
        c.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
        c.setOpaque(false);
        c.setFocusable(false);

        SwingUtil.installFont(c, "ToolWindowTabTitleUI.font");
    }

    protected void installListeners(JLabel c) {
        super.installListeners(c);

        ((CleanerProvider) tab).getCleanerAggregator().addCleaner(this);
    }

    protected void uninstallListeners(JLabel c) {
        super.uninstallListeners(c); 

        ((CleanerProvider) tab).getCleanerAggregator().removeCleaner(this);
    }


    public void update(Graphics g, JComponent c) {
        if (tab.isFlashing() && toolWindow.isVisible()) {
            Boolean flashingState = SwingUtil.getClientProperty(
                    (JComponent) c.getParent(),
                    "mydoggy.flashingState"
            );
            if (flashingState == null || flashingState) {
                toolWindowTabTitle.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED));
            } else {
                toolWindowTabTitle.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
            }
        } else {
            if (tab.isSelected())
                toolWindowTabTitle.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED));
            else
                toolWindowTabTitle.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
        }

        super.update(g, c);
    }


    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        if (pressed && inside)
            super.paintEnabledText(l, g, s, textX + 1, textY + 1);
        else
            super.paintEnabledText(l, g, s, textX, textY);
    }

}
