package org.noos.xing.mydoggy.plaf.ui.content.tabbed.component;

import org.noos.xing.mydoggy.TabbedContentUI;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.icons.CompositeIcon;
import org.noos.xing.mydoggy.plaf.ui.icons.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.ResourceBoundles;

import javax.swing.*;
import javax.accessibility.AccessibleContext;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentPage implements TabbedContentUI {
    static Icon closeImgI;
    static Icon maxImgI;
    static Icon closeImgD;
    static Icon maxImgD;

    static {
        maxImgI = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/detach.png");
        closeImgI = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/close.png");

        maxImgD = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/detachInactive.png");
        closeImgD = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/closeInactive.png");
    }

    private JTabbedContentManager tabbedPane;

    private Content content;

    private AccessibleContext accessible;

    private String title;
    private Icon icon;
    private JPopupMenu popupMenu;

    private boolean closable;
    private boolean detachable;
    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;

    private Icon contentIcon;


    public ContentPage(Content content, JTabbedContentManager tabbedPane, final AccessibleContext accessible) {
        this.content = content;
        this.tabbedPane = tabbedPane;
        this.accessible = accessible;

        this.closable = tabbedPane.isCloseable();
        this.detachable = tabbedPane.isDetachable();
        this.transparentMode = true;
        this.transparentRatio = 0.8f;
        this.transparentDelay = 1000;
    }

    public ContentPage(JTabbedContentManager tabbedPane, final AccessibleContext accessible) {
        this(null, tabbedPane, accessible);
    }


    public Content getContent() {
        return content;
    }

    public boolean isCloseable() {
        return closable;
    }

    public void setCloseable(boolean closable) {
        this.closable = closable;
        SwingUtil.repaint(tabbedPane);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        this.detachable = detachable;
        SwingUtil.repaint(tabbedPane);
    }

    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        this.transparentMode = transparentMode;
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        this.transparentRatio = transparentRatio;
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public void setTransparentDelay(int transparentDelay) {
        this.transparentDelay = transparentDelay;
    }


    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    public void setTitle(String title) {
        this.title = title;
        contentIcon = null;
    }

    public String getTitle() {
        return title;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        contentIcon = null;
    }

    public AccessibleContext getAccessible() {
        return accessible;
    }

    public void setAccessible(AccessibleContext accessible) {
        this.accessible = accessible;
    }

    public String getToolTipTextAt(MouseEvent e, int index, String defaultTip) {
        if (index != -1) {
            CompositeIcon compositeIcon = (CompositeIcon) ((CompositeIcon) getContentIcon()).getRightIcon();

            Point point = SwingUtilities.convertPoint(tabbedPane, e.getPoint(), getDestination());

            if (isDetachable()) {
                Rectangle detachIconRect = compositeIcon.getLastPaintedLeftRec();
                if (point.getX() > detachIconRect.x && point.getX() < detachIconRect.x + detachIconRect.width) {
                    return ResourceBoundles.getResourceBundle().getString("@@tab.content.detach");
                }
            }

            if (isCloseable()) {
                Rectangle closeIconRect = compositeIcon.getLastPaintedRightRec();
                if (point.getX() > closeIconRect.x && point.getX() < closeIconRect.x + closeIconRect.width) {
                    return ResourceBoundles.getResourceBundle().getString("@@tab.content.close");
                }
            }
            return (defaultTip.equals("")) ? null : defaultTip;
        }
        return (defaultTip.equals("")) ? null : defaultTip;
    }

    public Icon getContentIcon() {
        if (contentIcon == null) {
            String title = getTitle();
            Icon icon = getIcon();

            // Left Part
            Icon titleIcon = null;
            if (title != null)
                titleIcon = new DynamicTextIcon();

            // Right Part
            contentIcon = new CompositeIcon(new CompositeIcon(icon, titleIcon, SwingConstants.LEFT),
                                            new DoubleIcon(), SwingConstants.LEFT,
                                            SwingConstants.LEFT, SwingConstants.TOP);
        }
        return contentIcon;
    }

    public boolean isDetachFired(Point point) {
        Point relativeMousePoint = SwingUtilities.convertPoint(tabbedPane, point, getDestination());
        CompositeIcon uiCompositeIcon = getUICompositeIcon();

        Rectangle detachIconRect = uiCompositeIcon.getLastPaintedLeftRec();
        return (isDetachable() && ((relativeMousePoint.getX() > detachIconRect.x && relativeMousePoint.getX() < detachIconRect.x + detachIconRect.width) ||
                                   (point.getX() > detachIconRect.x && point.getX() < detachIconRect.x + detachIconRect.width)));
    }

    public void setContent(Content content) {
        this.content = content;
    }
    
    public boolean isCloseFired(Point point) {
        Point relativeMousePoint = SwingUtilities.convertPoint(tabbedPane, point, getDestination());
        CompositeIcon uiCompositeIcon = getUICompositeIcon();

        Rectangle closeIconRect = uiCompositeIcon.getLastPaintedRightRec();
        return (isCloseable() && ((relativeMousePoint.getX() > closeIconRect.x && relativeMousePoint.getX() < closeIconRect.x + closeIconRect.width) ||
                                  (point.getX() > closeIconRect.x && point.getX() < closeIconRect.x + closeIconRect.width)));
    }


    private Component getDestination() {
        for (int i = 0, size = tabbedPane.getComponentCount(); i < size; i++) {
            if (tabbedPane.getComponent(i) instanceof JViewport)
                return ((JViewport) tabbedPane.getComponent(i)).getView();
        }
        return tabbedPane;
    }

    private CompositeIcon getUICompositeIcon() {
        return (CompositeIcon) ((CompositeIcon) getContentIcon()).getRightIcon();
    }


    class DynamicTextIcon extends TextIcon {

        public DynamicTextIcon() {
            super(tabbedPane, title, TextIcon.ROTATE_NONE);
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Component tabComponent = (Component) accessible.getAccessibleChild(0);

            boolean isSelected = false;
            int index = -1;
            for (int i = 0, size = tabbedPane.getTabCount(); i < size; i++) {
                if (tabbedPane.getComponentAt(i) == tabComponent) {
                    index = i;
                    if (tabbedPane.getSelectedIndex() == i)
                        isSelected = true;
                    break;
                }
            }

            if (index != -1) {
                if (isSelected) {
                    this.setForeground(tabbedPane.getForegroundAt(index));
                } else
                    this.setForeground(tabbedPane.getForegroundAt(index).brighter().brighter());
                super.paintIcon(c, g, x, y);
            } else
                throw new IllegalStateException("Invalid Content Index");
        }
    }

    class DoubleIcon extends CompositeIcon {
        private boolean isSelected;

        public DoubleIcon() {
            super(maxImgD, closeImgD, SwingConstants.LEFT);
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Component tabComponent = (Component) accessible.getAccessibleChild(0);
            isSelected = false;
            for (int i = 0, size = tabbedPane.getTabCount(); i < size; i++) {
                if (tabbedPane.getComponentAt(i) == tabComponent) {
                    if (tabbedPane.getSelectedIndex() == i)
                        isSelected = true;
                    break;
                }
            }
            super.paintIcon(c, g, x, y);
        }

        protected void paintLeftIcon(Component c, Graphics g, Icon icon, int x, int y, int width, int height, int horizontalOrientation, int verticalOrientation) {
            super.paintLeftIcon(c, g,
                                isSelected ? maxImgI : maxImgD,
                                x, y, width, height, horizontalOrientation, verticalOrientation);
        }

        protected void paintRightIcon(Component c, Graphics g, Icon icon, int x, int y, int width, int height, int horizontalOrientation, int verticalOrientation) {
            super.paintRightIcon(c, g,
                                 isSelected ? closeImgI : closeImgD,
                                 x, y, width, height, horizontalOrientation, verticalOrientation);
        }

        public boolean isLeftVisible() {
            return isDetachable();
        }

        public boolean isRightVisible() {
            return isCloseable();
        }

    }
}
