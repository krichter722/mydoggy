package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Comparator;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JTabbedContentPane extends JTabbedPane {
    protected ResourceManager resourceManager;

    protected Map<Integer, Content> contentMap;

    protected Icon selectedTabIcon;
    protected TextIcon titleIcon;
    protected AggregateIcon tabIconTitle;
    protected AggregateIcon closeDetachIcon;

    protected Icon closeIcon;
    protected Icon detachIcon;


    public JTabbedContentPane() {
        super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.contentMap = new Hashtable<Integer, Content>();
        this.titleIcon = new TextIcon(this, "", TextIcon.ROTATE_NONE);
        this.tabIconTitle = new AggregateIcon(null, titleIcon, SwingConstants.HORIZONTAL);
        this.closeDetachIcon = new AggregateIcon(detachIcon, closeIcon, SwingConstants.HORIZONTAL);
        this.selectedTabIcon = new AggregateIcon(tabIconTitle,
                                                 closeDetachIcon,
                                                 SwingConstants.HORIZONTAL);
        setFocusable(false);

        MouseInputAdapter mouseInputAdapter = new MouseOverTabListener();
        addMouseListener(mouseInputAdapter);
        addMouseMotionListener(mouseInputAdapter);
    }


    public String getTitleAt(int index) {
        if (getSelectedIndex() == index)
            return null;
        return super.getTitleAt(index);
    }

    public Icon getIconAt(int index) {
        if (getSelectedIndex() == index) {
            ContentUI contentUI = contentMap.get(index).getContentUi();

            titleIcon.setText(super.getTitleAt(index));
            titleIcon.setUnderlinedIndex(super.getMnemonicAt(index));

            tabIconTitle.setLeftIcon(super.getIconAt(index));

            closeDetachIcon.setLeftVisible(contentUI.isDetachable());
            closeDetachIcon.setRightVisible(contentUI.isCloseable());

            return selectedTabIcon;
        }
        return super.getIconAt(index);
    }

    public void removeTabAt(int index) {
        super.removeTabAt(index);
        contentMap.remove(index);

        Integer[] keys = contentMap.keySet().toArray(new Integer[contentMap.size()]);
        Arrays.sort(keys);
        for (Integer key : keys) {
            if (key > index) 
                contentMap.put(key - 1, contentMap.remove(key));
        }
    }


    public void setToolWindowManager(MyDoggyToolWindowManager toolWindowManager) {
        this.resourceManager = toolWindowManager.getResourceManager();

        initIcons();
        this.closeDetachIcon.setLeftIcon(detachIcon);
        this.closeDetachIcon.setRightIcon(closeIcon);
    }

    public void addTab(Content content) {
        String tip = content.getToolTipText();
        if (tip == null)
            tip = "";

        addTab(content.getTitle(),
               content.getIcon(),
               content.getComponent(),
               tip);
        contentMap.put(getTabCount() - 1, content);
    }

    public void addTab(Content content, Component component) {
        String tip = content.getToolTipText();
        if (tip == null)
            tip = "";

        if (component == null)
            component = content.getComponent();
        
        addTab(content.getTitle(),
               content.getIcon(),
               component,
               tip);
        contentMap.put(getTabCount() - 1, content);
    }

    public void addTab(Content content, Component component, int index) {
        if (index < 0)
            addTab(content, component);
        else {
            String tip = content.getToolTipText();
            if (tip == null)
                tip = "";

            if (component == null)
                component = content.getComponent();

            Integer[] keys = contentMap.keySet().toArray(new Integer[contentMap.size()]);
            Arrays.sort(keys, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return (o1<o2 ? 1 : (o1.equals(o2) ? 0 : -11));
                }
            });
            // TODO: test this...
            for (Integer key : keys) {
                if (key >= index)
                    contentMap.put(key + 1, contentMap.remove(key));
            }

            insertTab(content.getTitle(),
                      content.getIcon(),
                      component,
                      tip,
                      index);

            contentMap.put(index, content);
        }
    }

    public void setPopupMenuAt(int index, JPopupMenu jPopupMenu) {
        // TODO
    }

    public Content getContentAt(int index) {
        return contentMap.get(index);
    }

    public void addTabbedContentPaneListener(TabbedContentPaneListener listener) {
        listenerList.add(TabbedContentPaneListener.class, listener);
    }

    public void removeTabbedContentPaneListener(TabbedContentPaneListener listener) {
        listenerList.remove(TabbedContentPaneListener.class, listener);
    }

    public int indexOfContent(Content content) {
        for (Integer key : contentMap.keySet()) {
            if (contentMap.get(key) == content)
                return key;
        }
        return -1;
    }


    protected void initIcons() {
        detachIcon = resourceManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_DETACH);
        closeIcon = resourceManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_CLOSE);
    }

    protected void fireCloseTabEvent(MouseEvent e, Content content, int overTabIndex) {
        TabbedContentPaneEvent event = new TabbedContentPaneEvent(this, TabbedContentPaneEvent.ActionId.ON_CLOSE, content, e, null, overTabIndex);
        for (TabbedContentPaneListener tabListener : getListeners(TabbedContentPaneListener.class))
            tabListener.tabbedContentPaneEventFired(event);
    }

    protected void fireDetachTabEvent(MouseEvent e, Content content, int overTabIndex) {
        TabbedContentPaneEvent event = new TabbedContentPaneEvent(this, TabbedContentPaneEvent.ActionId.ON_DETACH, content, e, null, overTabIndex);
        for (TabbedContentPaneListener tabListener : getListeners(TabbedContentPaneListener.class))
            tabListener.tabbedContentPaneEventFired(event);
    }



    protected class MouseOverTabListener extends MouseInputAdapter {
        protected int mouseOverTab = -1;

        public void mouseClicked(MouseEvent e) {
            if (mouseOverTab >= 0 && mouseOverTab < getTabCount()) {
                Content content = getContentAt(mouseOverTab);

                if (isDetachFired(content.getContentUi(), e.getPoint())) {
                    fireDetachTabEvent(e, content, mouseOverTab);
                    return;
                }

                if (isCloseFired(content.getContentUi(), e.getPoint())) {
                    fireCloseTabEvent(e, content, mouseOverTab);
                    return;
                }

                if (e.getClickCount() == 2) {
                    // Maximization
//                    setMaximized(!isMaximized());
                } else {
//                    if (SwingUtilities.isRightMouseButton(e)) {
//                        getContentPage(mouseOverTab).showPopupMenu(
//                                JTabbedContentManager.this, e, mouseOverTab, popupMenu
//                        );
//                    }
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
//                if (popupMenu != null)
//                    popupMenu.show(JTabbedContentManager.this, e.getX(), e.getY());
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
            if (!JTabbedContentPane.this.isEnabled())
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


        protected boolean isDetachFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentPane.this, point, getDestination());
            Rectangle detachIconRect = closeDetachIcon.getLastPaintedLeftRec();

            return (contentUI.isDetachable() && ((relativeMousePoint.getX() > detachIconRect.x && relativeMousePoint.getX() < detachIconRect.x + detachIconRect.width) ||
                                       (point.getX() > detachIconRect.x && point.getX() < detachIconRect.x + detachIconRect.width)));
        }

        protected boolean isCloseFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentPane.this, point, getDestination());
            Rectangle closeIconRect = closeDetachIcon.getLastPaintedRightRec();

            return (contentUI.isCloseable() && ((relativeMousePoint.getX() > closeIconRect.x && relativeMousePoint.getX() < closeIconRect.x + closeIconRect.width) ||
                                      (point.getX() > closeIconRect.x && point.getX() < closeIconRect.x + closeIconRect.width)));
        }

        protected Component getDestination() {
            for (int i = 0, size = JTabbedContentPane.this.getComponentCount(); i < size; i++) {
                if (JTabbedContentPane.this.getComponent(i) instanceof JViewport)
                    return ((JViewport) JTabbedContentPane.this.getComponent(i)).getView();
            }
            return JTabbedContentPane.this;
        }

    }
}
