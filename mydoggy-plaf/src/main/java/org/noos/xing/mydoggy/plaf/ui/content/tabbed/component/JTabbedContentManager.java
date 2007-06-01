package org.noos.xing.mydoggy.plaf.ui.content.tabbed.component;

import org.noos.xing.mydoggy.TabbedContentUI;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.PersistenceDelegate;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

public class JTabbedContentManager extends JTabbedPane {
    static final int BUTTONSIZE = 15;
    static final int WIDTHDELTA = 5;

    private ToolWindowManager toolWindowManager;

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
        this.insertTab(title, icon, component, tip, index, null);
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


    public void addTab(String title, Icon icon, Component component, String tip, TabbedContentUI tabbedContentUI) {
        insertTab(title, icon, component, tip, getTabCount(), tabbedContentUI);
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

    public ContentPage getContentPage(int index) {
        return getContentPage(index, null);
    }

    public void setToolWindowManager(MyDoggyToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }

    public void addTabListener(TabListener l) {
        listenerList.add(TabListener.class, l);
    }

    public void removeTabListener(TabListener l) {
        listenerList.remove(TabListener.class, l);
    }


    protected void insertTab(String title, Icon icon, Component component, String tip, int index, TabbedContentUI tabbedContentUI) {
        if (tip == null)
            tip = "";
        super.insertTab(title, icon, component, tip, index);

        ContentPage contentPage = getContentPage(index, tabbedContentUI);
        contentPage.setTitle(title);
        contentPage.setIcon(icon);

        super.setTitleAt(index, "");
        super.setIconAt(index, getContentPage(index).getContentIcon());
    }

    protected ContentPage getContentPage(int index, TabbedContentUI tabbedContentUI) {
        Accessible accessible = getAccessibleContext().getAccessibleChild(index);
        ContentPage contentPage = contentPages.get(accessible);
        if (contentPage == null) {
            if (tabbedContentUI == null)
                contentPage = new ContentPage(this, (AccessibleContext) accessible);
            else {
                contentPage = (ContentPage) tabbedContentUI;
                contentPage.setAccessible((AccessibleContext) accessible);
            }
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


    class MouseOverTabListener extends MouseInputAdapter {
        private int mouseOverTab = -1;
        private ByteArrayOutputStream workspace;

        public void mouseClicked(MouseEvent e) {
            if (mouseOverTab >= 0 && mouseOverTab < getTabCount()) {
                ContentPage contentPage = getContentPage(mouseOverTab);

                if (contentPage.isDetachFired(e.getPoint())) {
                    fireDetachTabEvent(e, mouseOverTab);
                    return;
                }

                if (contentPage.isCloseFired(e.getPoint())) {
                    fireCloseTabEvent(e, mouseOverTab);
                    return;
                }

                if (e.getClickCount() == 2) {
                    // Maximization
                    if (workspace == null) {
                        toolWindowManager.getPersistenceDelegate().save(workspace = new ByteArrayOutputStream());
                        toolWindowManager.getToolWindowGroup().setVisible(false);
                    } else {
                        toolWindowManager.getPersistenceDelegate().merge(new ByteArrayInputStream(workspace.toByteArray()),
                                                                         PersistenceDelegate.MergePolicy.UNION);
                        workspace = null;
                    }
                } else {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        getContentPage(mouseOverTab).showPopupMenu(
                                JTabbedContentManager.this, e.getX(), e.getY(), defaultContentPopupMenu
                        );
                    }
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
//               TODO: what should i do tabbedContentManager.firePopupOutsideTabEvent(e);
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

