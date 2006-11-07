package org.noos.xing.mydoggy.plaf.ui.content.tabbed;

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

    public JTabbedContentManager() {
        super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.contentPages = new Hashtable<Accessible, ContentPage>();
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


    public static class ContentPage {
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
            this.closable = detachable = true;
        }


        public boolean isClosable() {
            return closable;
        }

        public void setClosable(boolean closable) {
            this.closable = closable;
        }

        public boolean isDetachable() {
            return detachable;
        }

        public void setDetachable(boolean detachable) {
            this.detachable = detachable;
        }

        public JPopupMenu getPopupMenu() {
            return popupMenu;
        }

        public void setPopupMenu(JPopupMenu popupMenu) {
            this.popupMenu = popupMenu;
        }

        public String getToolTipTextAt(MouseEvent e, int index, String defaultTip) {
            if (index != -1) {
                CompositeIcon compositeIcon = (CompositeIcon) ((CompositeIcon) getContentIcon()).getIcon2();

                if (isDetachable()) {
                    Rectangle detachIconRect = compositeIcon.getIcon1Rec();
                    if (e.getX() > detachIconRect.x && e.getX() < detachIconRect.x + detachIconRect.width) {
                        return "Detach";
                    }
                }

                if (isClosable()) {
                    Rectangle closeIconRect = compositeIcon.getIcon2Rec();
                    if (e.getX() > closeIconRect.x && e.getX() < closeIconRect.x + closeIconRect.width) {
                        return "Close";
                    }
                }
                return (defaultTip.equals("")) ? null : defaultTip;
            }
            return (defaultTip.equals("")) ? null : defaultTip;
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

        public Icon getContentIcon() {
            if (contentIcon == null) {
                String title = getTitle();
                Icon icon = getIcon();

                // Left Part
                Icon leftIcon;
                Icon titleIcon = null;
                if (title != null)
                    titleIcon = new TextIcon(tabbedPane, title, TextIcon.ROTATE_NONE) {
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
                    };
                leftIcon = new CompositeIcon(titleIcon, icon, SwingConstants.LEFT);

                // Right Part
                Icon rightIcon = new CompositeIcon(maxImgD, closeImgD, SwingConstants.LEFT) {
                    boolean isSelected;
                    int count;

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
                        count = 0;
                        super.paintIcon(c, g, x, y);
                    }

                    protected void paintIconInternal(Component c, Graphics g, Icon icon, int x, int y, int width, int height, int horizontalOrientation, int verticalOrientation) {
                        if (count == 0) {
                            super.paintIconInternal(c, g,
                                                    isSelected ? maxImgI : maxImgD,
                                                    x, y, width, height, horizontalOrientation, verticalOrientation);

                        } else if (count == 1) {
                            super.paintIconInternal(c, g,
                                                    isSelected ? closeImgI : closeImgD,
                                                    x, y, width, height, horizontalOrientation, verticalOrientation);
                        }
                        count = (count + 1) % 2;
                    }

                    protected void paint(Component c, Graphics g, Icon icon, int x, int y) {
                        super.paint(c, g, icon, x, y);
                        if (count == 0) {
                            icon1Rec = new Rectangle(x, y, getIconWidth(icon1), getIconHeight(icon1));
                        } else
                            icon2Rec = new Rectangle(x, y, getIconWidth(icon2), getIconHeight(icon2));
                    }
                };

                contentIcon = new CompositeIcon(leftIcon, rightIcon, SwingConstants.LEFT);
            }
            return contentIcon;
        }
    }

    class MouseOverTabListener extends MouseInputAdapter {
        private int mouseOverTab = -1;

        public void mouseClicked(MouseEvent e) {
            if (mouseOverTab >= 0 && mouseOverTab < getTabCount()) {
                CompositeIcon compositeIcon = (CompositeIcon) ((CompositeIcon) getContentPage(mouseOverTab).getContentIcon()).getIcon2();

                Rectangle detachIconRect = compositeIcon.getIcon1Rec();
                if (e.getX() > detachIconRect.x && e.getX() < detachIconRect.x + detachIconRect.width) {
                    fireDetachTabEvent(e, mouseOverTab);
                    return;
                }

                Rectangle closeIconRect = compositeIcon.getIcon2Rec();
                if (e.getX() > closeIconRect.x && e.getX() < closeIconRect.x + closeIconRect.width) {
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

