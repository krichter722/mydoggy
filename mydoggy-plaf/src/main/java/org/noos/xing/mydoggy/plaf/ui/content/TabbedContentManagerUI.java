package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;

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
public class TabbedContentManagerUI extends MetalTabbedPaneUI {

    protected static final int BUTTONSIZE = 15;

    protected static final int WIDTHDELTA = 5;

    private static Color selectedColor = new Color(255, 255, 255);
    private static Color notSelectedColor = new Color(189, 187, 182);
    private boolean noIconSpace;

    private MouseInputListener mouseOverTabListener;
    private int mouseOverTab = -1;
    private boolean isCloseButtonEnabled = true;
    private boolean isDetachButtonEnabled = true;

    private Image closeImgI;
    private Image maxImgI;

    private Image closeImgD;
    private Image maxImgD;

    public static ComponentUI createUI(JComponent jcomponent) {
        return new TabbedContentManagerUI();
    }

    public TabbedContentManagerUI() {
        noIconSpace = false;

        maxImgI = loadImage("org/noos/xing/mydoggy/plaf/ui/icons/detach.png");
        closeImgI = loadImage("org/noos/xing/mydoggy/plaf/ui/icons/close.png");

        maxImgD = loadImage("org/noos/xing/mydoggy/plaf/ui/icons/detachInactive.png");
        closeImgD = loadImage("org/noos/xing/mydoggy/plaf/ui/icons/closeInactive.png");
    }


    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(tabAreaBackground);
            GraphicsUtil.fillRect(g, new Rectangle(0, 0, c.getWidth(), c.getHeight()), Color.WHITE, tabAreaBackground, null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
        }
        paint(g, c);
    }


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


    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        g.setColor(darkShadow);
        switch (tabPlacement) {
            case TOP:
                g.drawLine(x, y + 1, x, y + h - 1);
                g.drawLine(x + 1, y, x + w - 3, y);
                g.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 1);
                if (isSelected || tabIndex == mouseOverTab) {
                    g.setColor(new Color(230, 139, 44));
                    g.drawLine(x + 2, y, x + w - 3, y);
                    g.setColor(new Color(255, 199, 30));
                    g.drawLine(x + 1, y + 1, x + w - 3, y + 1);
                    g.drawLine(x + 1, y + 2, x + w - 3, y + 2);
                }
                break;
            case LEFT:
                g.drawLine(x + 1, y + 1, x + w - 1, y + 1);
                g.drawLine(x, y + 2, x, y + h - 2);
                g.drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);

                g.setColor(new Color(230, 139, 44));
                g.drawLine(x + 2, y, x + w - 3, y);
                g.setColor(new Color(255, 199, 30));
                g.drawLine(x + 1, y + 1, x + w - 3, y + 1);
                g.drawLine(x + 1, y + 2, x + w - 3, y + 2);
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

            g.drawString(title,
                         textRect.x - (noIconSpace ? 5 : 0) - 10,
                         textRect.y + metrics.getAscent());
        } else {
            g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
            g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            g.setColor(tabPane.getBackgroundAt(tabIndex).darker());

            g.drawString(title,
                         textRect.x - (noIconSpace ? 6 : 1) - 10,
                         textRect.y + metrics.getAscent() - 1);
        }
    }

//    private Rectangle[] my_rects;

    protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
        super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);    //To change body of overridden methods use File | Settings | File Templates.

//        my_rects = rects;
        Rectangle tabRect = rects[tabIndex];
        int selectedIndex = tabPane.getSelectedIndex();
        boolean isSelected = selectedIndex == tabIndex;
        boolean isOver = mouseOverTab == tabIndex;

        if (isOver || isSelected) {
            int dx = tabRect.x + tabRect.width - BUTTONSIZE - WIDTHDELTA;
            int dy = (tabRect.y + tabRect.height) / 2 - 6;

            if (isCloseButtonEnabled)
                paintCloseIcon(g, dx, dy, isOver, isSelected);
            if (isDetachButtonEnabled)
                paintMaxIcon(g, dx, dy, isOver, isSelected);
        }
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Color start, end;
        if (isSelected) {
            start = end = selectedColor;
        } else {
            start = new Color(250, 250, 249);
            end = new Color(236, 235, 229);
        }
        switch (tabPlacement) {
            case LEFT:
                GraphicsUtil.fillRect(g, new Rectangle(x + 1, y + 2, w - 2, h - 3),
                                      start, end, null, GraphicsUtil.BOTTOM_TO_UP_GRADIENT);
                break;
            case RIGHT:
                GraphicsUtil.fillRect(g, new Rectangle(x, y + 2, w - 1, h - 3),
                                      start, end, null, GraphicsUtil.BOTTOM_TO_UP_GRADIENT);
                break;
            case BOTTOM:
                GraphicsUtil.fillRect(g, new Rectangle(x + 1, y, w - 3, h - 1),
                                      start, end, null, GraphicsUtil.BOTTOM_TO_UP_GRADIENT);
                break;
            case TOP:
            default:
                GraphicsUtil.fillRect(g, new Rectangle(x + 1, y + 1, w - 2, h - 1),
                                      start, end, null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
                break;
        }
    }

    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
    }


    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return (int) ((double) super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) * 1.1000000000000001D);
    }

    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics fontmetrics) {
        Font font = fontmetrics.getFont();
        int k = super.calculateTabWidth(tabPlacement, tabIndex, tabPane.getFontMetrics(font));
        Font font1 = font.deriveFont(1);
        int l = super.calculateTabWidth(tabPlacement, tabIndex, tabPane.getFontMetrics(font1));

        int result = (int) ((double) Math.max(k, l) * 1.1000000000000001D);

        if (isCloseButtonEnabled)
            result += BUTTONSIZE + WIDTHDELTA;
        if (isDetachButtonEnabled)
            result += BUTTONSIZE + WIDTHDELTA;
        return result;
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


    public void setNoIconSpace(boolean noIconSpace) {
        this.noIconSpace = noIconSpace;
    }

    public boolean isCloseEnabled() {
        return isCloseButtonEnabled;
    }

    public boolean isDetachEnabled() {
        return isDetachButtonEnabled;
    }

    public void setCloseEnabled(boolean b) {
        isCloseButtonEnabled = b;
    }

    public void setDetachEnabled(boolean b) {
        isDetachButtonEnabled = b;
    }


    protected Image loadImage(String url) {
        return Toolkit.getDefaultToolkit().getImage(
                Thread.currentThread().getContextClassLoader().getResource(url));
    }

    protected void paintCloseIcon(Graphics g, int dx, int dy, boolean isOver, boolean isSelected) {
        if (isSelected)
            g.drawImage(closeImgI, dx, dy + 1, null);
        else
            g.drawImage(closeImgD, dx, dy + 1, null);

    }

    protected void paintMaxIcon(Graphics g, int dx, int dy, boolean isOver, boolean isSelected) {
        if (isCloseButtonEnabled)
            dx -= BUTTONSIZE;

        if (isSelected)
            g.drawImage(maxImgI, dx, dy + 1, null);
        else
            g.drawImage(maxImgD, dx, dy + 1, null);
    }

    private void ensureCurrentLayout() {
        if (!tabPane.isValid()) {
            tabPane.validate();
        }
        /* If tabPane doesn't have a peer yet, the validate() call will
         * silently fail.  We handle that by forcing a layout if tabPane
         * is still invalid.  See bug 4237677.
         */
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

    public String getToolTipTextAt(MouseEvent e, int index, String defaultTip) {
        if (index != -1) {
            if (isCloseButtonEnabled) {
                Rectangle tabRect = rects[index];
                if (e.getX() > tabRect.x + tabRect.width - BUTTONSIZE - WIDTHDELTA &&
                    e.getX() < tabRect.x + tabRect.width - BUTTONSIZE + 5) {
                    return "Close";
                }
            }

            if (isDetachButtonEnabled) {
                Rectangle tabRect = rects[index];
                if (e.getX() > tabRect.x + tabRect.width - BUTTONSIZE - BUTTONSIZE - WIDTHDELTA &&
                    e.getX() < tabRect.x + tabRect.width - BUTTONSIZE - BUTTONSIZE + 5) {
                    return "Detach";
                }
            }

            return defaultTip;
        }
        return defaultTip;
    }


    public class MouseOverTabListener extends MouseInputAdapter {

        public void mouseClicked(MouseEvent e) {
            if (mouseOverTab != -1) {
                if (isCloseButtonEnabled && mouseOverTab < tabPane.getTabCount()) {
                    Rectangle tabRect = getTabBounds(tabPane, mouseOverTab);

                    if (e.getX() > tabRect.x + tabRect.width - BUTTONSIZE - WIDTHDELTA &&
                        e.getX() < tabRect.x + tabRect.width - BUTTONSIZE + 5) {
                        ((JTabbedContentManager) tabPane).fireCloseTabEvent(e, mouseOverTab);
                    }
                }

                if (isDetachButtonEnabled && mouseOverTab < tabPane.getTabCount()) {
                    Rectangle tabRect = getTabBounds(tabPane, mouseOverTab);

                    if (e.getX() > tabRect.x + tabRect.width - BUTTONSIZE - BUTTONSIZE - WIDTHDELTA &&
                        e.getX() < tabRect.x + tabRect.width - BUTTONSIZE - BUTTONSIZE + 5) {
                        ((JTabbedContentManager) tabPane).fireDetachTabEvent(e, mouseOverTab);
                    }
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            if (mouseOverTab != -1) {
                int tmp = mouseOverTab;
                mouseOverTab = -1;
                if (tmp < tabPane.getTabCount())
                    tabPane.repaint(getTabBounds(tabPane, tmp));
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (!tabPane.isEnabled())
                return;

            if (mouseOverTab != -1) {
                int tmp = mouseOverTab;
                mouseOverTab = -1;
                if (tmp < tabPane.getTabCount())
                    tabPane.repaint(getTabBounds(tabPane, tmp));
            }

            int tabIndex = tabForCoordinate(tabPane, e.getX(), e.getY());
            if (tabIndex >= 0 && tabPane.isEnabledAt(tabIndex)) {
                mouseOverTab = tabIndex;
                if (tabIndex < tabPane.getTabCount())
                    tabPane.repaint(getTabBounds(tabPane, tabIndex));
            }
        }
    }
}