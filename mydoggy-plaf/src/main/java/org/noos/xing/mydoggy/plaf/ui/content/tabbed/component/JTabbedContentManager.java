package org.noos.xing.mydoggy.plaf.ui.content.tabbed.component;

import org.noos.xing.mydoggy.TabbedContentUI;
import org.noos.xing.mydoggy.plaf.ui.icons.CompositeIcon;
import org.noos.xing.mydoggy.plaf.ui.icons.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Map;

public class JTabbedContentManager extends JTabbedPane {
    static final int BUTTONSIZE = 15;
    static final int WIDTHDELTA = 5;

    private JPopupMenu defaultContentPopupMenu;

    private Map<Accessible, ContentPage> contentPages;

    private boolean closeable;
    private boolean detachable;

    public JTabbedContentManager() {
        super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.contentPages = new Hashtable<Accessible, ContentPage>();
        this.closeable = this.detachable = true;
        setFocusable(false);

        MouseInputAdapter mouseInputAdapter = new MouseOverTabListener();
        addMouseListener(mouseInputAdapter);
        addMouseMotionListener(mouseInputAdapter);
    }


    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        if (tip == null)
            tip = "";
        super.insertTab(title, icon, component, tip, index);

        ContentPage contentPage = getContentPage(index);
        contentPage.setTitle(title);
        contentPage.setIcon(icon);

        super.setTitleAt(index, "");
        super.setIconAt(index, getContentPage(index).getContentIcon());
    }

    public String getToolTipText(MouseEvent event) {
        int index = indexAtLocation(event.getX(), event.getY());
        if (index != -1)
            return getContentPage(index).getToolTipTextAt(event, index, super.getToolTipTextAt(index));
        return super.getToolTipText(event);
    }

    public void setTitleAt(int index, String title) {
        getContentPage(index).setTitle(title);
    }

    public String getTitleAt(int index) {
        return "";
    }

    public void setIconAt(int index, Icon icon) {
        ContentPage contentPage = getContentPage(index);
        contentPage.setIcon(icon);
        super.setIconAt(index, contentPage.getContentIcon());
    }

    public Icon getIconAt(int index) {
        return getContentPage(index).getContentIcon();
    }

    public Icon getDisabledIconAt(int index) {
        return getContentPage(index).getContentIcon();
    }

    public void setPopupMenuAt(int index, JPopupMenu popupMenu) {
        checkIndex(index);
        getContentPage(index).setPopupMenu(popupMenu);
    }

    public JPopupMenu getPopupMenuAt(int index) {
        checkIndex(index);
        return getContentPage(index).getPopupMenu();
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.defaultContentPopupMenu = popupMenu;
    }

    public JPopupMenu getPopupMenu() {
        return defaultContentPopupMenu;
    }

    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
        for (ContentPage contentPage : contentPages.values()) {
            contentPage.setCloseable(closeable);
        }
    }

    public boolean isCloseable() {
        return closeable;
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        this.detachable = detachable;
        for (ContentPage contentPage : contentPages.values()) {
            contentPage.setDetachable(detachable);
        }
    }

    public void addTabListener(TabListener l) {
        listenerList.add(TabListener.class, l);
    }

    public void removeTabListener(TabListener l) {
        listenerList.remove(TabListener.class, l);
    }


    public ContentPage getContentPage(int index) {
        Accessible accessible = getAccessibleContext().getAccessibleChild(index);
        ContentPage contentPage = contentPages.get(accessible);
        if (contentPage == null) {
            contentPage = new ContentPage(this, (AccessibleContext) accessible);
            contentPages.put(accessible, contentPage);
        }
        return contentPage;
    }

    protected void checkIndex(int index) {
        if (index < 0 || index >= getTabCount())
            throw new IndexOutOfBoundsException("Index: " + index + ", Content count: " + getTabCount());
    }

    protected void fireCloseTabEvent(MouseEvent e, int overTabIndex) {
        TabEvent event = new TabEvent(this, TabEvent.ActionId.ON_CLOSE, e, null, overTabIndex);
        for (TabListener tabListener : getListeners(TabListener.class))
            tabListener.tabEventFired(event);
    }

    protected void fireDetachTabEvent(MouseEvent e, int overTabIndex) {
        TabEvent event = new TabEvent(this, TabEvent.ActionId.ON_DETACH, e, null, overTabIndex);
        for (TabListener tabListener : getListeners(TabListener.class))
            tabListener.tabEventFired(event);
    }


    static class ContentPage implements TabbedContentUI {
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

        private AccessibleContext accessible;

        private String title;
        private Icon icon;
        private JPopupMenu popupMenu;

        private boolean closable;
        private boolean detachable;

        private Icon contentIcon;

        public ContentPage(JTabbedContentManager tabbedPane, final AccessibleContext accessible) {
            this.tabbedPane = tabbedPane;
            this.accessible = accessible;
            this.closable = tabbedPane.isCloseable();
            this.detachable = tabbedPane.isDetachable();
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

        public String getToolTipTextAt(MouseEvent e, int index, String defaultTip) {
            if (index != -1) {
                CompositeIcon compositeIcon = (CompositeIcon) ((CompositeIcon) getContentIcon()).getRightIcon();

                Point point = SwingUtilities.convertPoint(tabbedPane, e.getPoint(), getDestination());

                if (isDetachable()) {
                    Rectangle detachIconRect = compositeIcon.getLastPaintedLeftRec();
                    if (point.getX() > detachIconRect.x && point.getX() < detachIconRect.x + detachIconRect.width) {
                        return "Detach";
                    }
                }

                if (isCloseable()) {
                    Rectangle closeIconRect = compositeIcon.getLastPaintedRightRec();
                    if (point.getX() > closeIconRect.x && point.getX() < closeIconRect.x + closeIconRect.width) {
                        return "Close";
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

    class MouseOverTabListener extends MouseInputAdapter {
        private int mouseOverTab = -1;


        public void mouseClicked(MouseEvent e) {
            if (mouseOverTab >= 0 && mouseOverTab < getTabCount()) {
                ContentPage contentPage = getContentPage(mouseOverTab);


                Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentManager.this, e.getPoint(),
                                                                       contentPage.getDestination());
                CompositeIcon uiCompositeIcon = contentPage.getUICompositeIcon();

                Rectangle detachIconRect = uiCompositeIcon.getLastPaintedLeftRec();
                if (contentPage.isDetachable() && ((relativeMousePoint.getX() > detachIconRect.x && relativeMousePoint.getX() < detachIconRect.x + detachIconRect.width) ||
                                                   (e.getX() > detachIconRect.x && e.getX() < detachIconRect.x + detachIconRect.width))) {
                    fireDetachTabEvent(e, mouseOverTab);
                    return;
                }

                Rectangle closeIconRect = uiCompositeIcon.getLastPaintedRightRec();
                if (contentPage.isCloseable() && ((relativeMousePoint.getX() > closeIconRect.x && relativeMousePoint.getX() < closeIconRect.x + closeIconRect.width) ||
                                                  (e.getX() > closeIconRect.x && e.getX() < closeIconRect.x + closeIconRect.width))) {
                    fireCloseTabEvent(e, mouseOverTab);
                    return;
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popupMenu = getPopupMenuAt(mouseOverTab);
                    if (popupMenu == null)
                        popupMenu = defaultContentPopupMenu;

                    if (popupMenu != null) {
                        popupMenu.show(JTabbedContentManager.this, e.getX(), e.getY());
                    }

                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
//                tabbedContentManager.firePopupOutsideTabEvent(e);
            }
        }

        public void mouseExited(MouseEvent e) {
            if (mouseOverTab != -1) {
                int tmp = mouseOverTab;
                mouseOverTab = -1;
                if (tmp < getTabCount())
                    repaint(getBoundsAt(tmp));
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (!JTabbedContentManager.this.isEnabled())
                return;

            if (mouseOverTab != -1) {
                int tmp = mouseOverTab;
                mouseOverTab = -1;
                if (tmp < getTabCount())
                    repaint(getBoundsAt(tmp));
            }

            int tabIndex = indexAtLocation(e.getX(), e.getY());
            if (tabIndex >= 0 && isEnabledAt(tabIndex)) {
                mouseOverTab = tabIndex;
                if (tabIndex < getTabCount())
                    repaint(getBoundsAt(tabIndex));
            }
        }
    }
}

