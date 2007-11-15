package org.noos.xing.mydoggy.plaf.ui.look;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MultiSplitTabbedPaneUI extends MetalTabbedPaneUI {

    private static Color selectedColor = new Color(247, 243, 239);
    private static Color notSelectedColor = new Color(189, 187, 182);
    private boolean noIconSpace;

    private MouseInputListener mouseOverTabListener;
    private int mouseOverTab = -1;


    public static ComponentUI createUI(JComponent jcomponent) {
        return new MultiSplitTabbedPaneUI();
    }

    public MultiSplitTabbedPaneUI() {
        noIconSpace = false;
    }

    public void installUI(JComponent c) {
        super.installUI(c);    //To change body of overridden methods use File | Settings | File Templates.
    }

/*
    protected void installListeners() {
        super.installListeners();
        mouseOverTabListener = new MouseOverTabListener();
        tabPane.addMouseListener(mouseOverTabListener);
        tabPane.addMouseMotionListener(mouseOverTabListener);
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        tabPane.removeMouseListener(mouseOverTabListener);
        tabPane.removeMouseMotionListener(mouseOverTabListener);
    }
*/

    public void update(Graphics g, JComponent c) {
        shadow = Color.RED;
        super.update(g, c);    //To change body of overridden methods use File | Settings | File Templates.
    }

/*
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        g.setColor(darkShadow);
        switch (tabPlacement) {
            case TOP:
                if (isSelected || tabIndex == mouseOverTab) {
                    g.drawLine(x, y + 1, x, y + h - 3);
                    g.drawLine(x + 1, y, x + w - 2, y);
                    g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 3);

//                    g.setColor(new Color(255,212,151));
//                    g.drawLine(x, y, w, y);
                } else {
                    g.drawLine(x, y + 1, x, y + h - 1);
                    g.drawLine(x + 1, y, x + w - 3, y);
                    g.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 1);

//                    if (tabIndex == mouseOverTab) {
//                        g.setColor(new Color(255,212,151));
//                        g.drawLine(x, y, w, y);
//                    }
                }
                break;

            case LEFT:
                g.drawLine(x + 1, y + 1, x + w - 1, y + 1);
                g.drawLine(x, y + 2, x, y + h - 2);
                g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);
                break;

            case BOTTOM:
                if (isSelected) {
                    g.drawLine(x, y, x, y + h - 2);
                    g.drawLine(x + 1, y + h - 1, x + w - 2, y + h - 1);
                    g.drawLine(x + w - 1, y, x + w - 1, y + h - 2);
                } else {
                    g.drawLine(x, y, x, y + h - 1);
                    g.drawLine(x + 1, y + h - 1, x + w - 3, y + h - 1);
                    g.drawLine(x + w - 2, y, x + w - 2, y + h - 1);
                }
                break;

            case RIGHT:
                g.drawLine(x, y + 1, x + w - 2, y + 1);
                g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 2);
                g.drawLine(x, y + h - 1, x + w - 2, y + h - 1);
                break;

            default:
                throw new IllegalArgumentException("unknown tabPlacement: " + tabPlacement);
        }
    }

    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect,
                             boolean isSelected) {
        if (isSelected)
            g.setFont(font.deriveFont(1));
        else
            g.setFont(font.deriveFont(0));
        if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
            g.setColor(tabPane.getForegroundAt(tabIndex));
            g.drawString(title, textRect.x - (noIconSpace ? 5 : 0), textRect.y + metrics.getAscent());
        } else {
            g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
            g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
            g.drawString(title, textRect.x - (noIconSpace ? 6 : 1), textRect.y + metrics.getAscent() - 1);
        }
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
                                      boolean isSelected) {

        if (isSelected)
            g.setColor(selectedColor);
        else
            g.setColor(notSelectedColor);
        switch (tabPlacement) {
            case LEFT:
                g.fillRect(x + 1, y + 2, w - 2, h - 3);
                break;
            case RIGHT:
                g.fillRect(x, y + 2, w - 1, h - 3);
                break;
            case BOTTOM:
                g.fillRect(x + 1, y, w - 3, h - 1);
                break;
            case TOP:
            default:
                g.fillRect(x + 1, y + 1, w - 2, h - 1);
                break;
        }
//        if (tabIndex == mouseOverTab) {
//            g.setColor(new Color(255,244,204));
//            g.drawLine(x, y + 1, w, y + 1);
//            g.drawLine(x, y + 2, w, y + 2);
//        }
    }

    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return (int) ((double) super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) * 1.1000000000000001D);
    }

    protected int calculateMaxTabHeight(int tabPlacement) {
        FontMetrics fontmetrics = getFontMetrics();
        int j = tabPane.getTabCount();
        int k = 0;
        int l = fontmetrics.getHeight();
        for (int i1 = 0; i1 < j; i1++)
            k = Math.max(calculateTabHeight(tabPlacement, i1, l), k);

        return k;
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics fontmetrics) {
        Font font = fontmetrics.getFont();
        int k = super.calculateTabWidth(tabPlacement, tabIndex, tabPane.getFontMetrics(font));
        Font font1 = font.deriveFont(1);
        int l = super.calculateTabWidth(tabPlacement, tabIndex, tabPane.getFontMetrics(font1));
        return (int) ((double) Math.max(k, l) * 1.1000000000000001D);
    }

    public void setNoIconSpace(boolean noIconSpace) {
        this.noIconSpace = noIconSpace;
    }

    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
    }

    private void ensureCurrentLayout() {
        if (!tabPane.isValid()) {
            tabPane.validate();
        }
        */
/* If tabPane doesn't have a peer yet, the validate() call will
         * silently fail.  We handle that by forcing a layout if tabPane
         * is still invalid.  See bug 4237677.
         */
/*
        if (!tabPane.isValid()) {
            TabbedPaneLayout layout = (TabbedPaneLayout) tabPane.getLayout();
            layout.calculateLayoutInfo();
        }
    }

    private int getTabAtLocation(int x, int y) {
        ensureCurrentLayout();
        int tabCount = tabPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if (rects[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }
*/

/*
    class MouseOverTabListener extends MouseInputAdapter {

        public void mouseExited(MouseEvent e) {
            if (mouseOverTab != -1) {
                int tmp = mouseOverTab;
                mouseOverTab = -1;
                tabPane.repaint(getTabBounds(tabPane, tmp));
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (!tabPane.isEnabled())
                return;

            if (mouseOverTab != -1) {
                int tmp = mouseOverTab;
                mouseOverTab = -1;
                tabPane.repaint(getTabBounds(tabPane, tmp));
            }

            int tabIndex = getTabAtLocation(e.getX(), e.getY());
            if (tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
                mouseOverTab = tabIndex;
                tabPane.repaint(getTabBounds(tabPane, tabIndex));
            }
        }


    }
*/
}
