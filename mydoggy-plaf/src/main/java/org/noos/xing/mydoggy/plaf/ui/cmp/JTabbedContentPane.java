package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JTabbedContentPane extends JTabbedPane implements PropertyChangeListener {
    protected ToolWindowManager toolWindowManager;
    protected ResourceManager resourceManager;

    protected Map<Integer, Content> contentMap;
    protected Map<Content, Object> flashingContents;

    protected Icon selectedTabIcon;
    protected TextIcon titleIcon;
    protected AggregateIcon tabIconTitle;
    protected AggregateIcon minCloseDetachIcon;
    protected AggregateIcon closeDetachIcon;

    protected Icon closeIcon;
    protected Icon detachIcon;
    protected Icon minimizeIcon;

    protected ByteArrayOutputStream tmpWorkspace = null;

    protected MouseInputAdapter mouseInputAdapter;

    public JTabbedContentPane() {
        this(false);
    }

    public JTabbedContentPane(boolean enabledDrag) {
        super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.contentMap = new Hashtable<Integer, Content>();
        this.titleIcon = new TextIcon(this, "", TextIcon.ROTATE_NONE);
        this.tabIconTitle = new AggregateIcon(null, titleIcon, SwingConstants.HORIZONTAL);
        this.closeDetachIcon = new AggregateIcon(detachIcon, closeIcon, SwingConstants.HORIZONTAL);
        this.minCloseDetachIcon = new AggregateIcon(minimizeIcon, closeDetachIcon, SwingConstants.HORIZONTAL);
        this.selectedTabIcon = new ExAggregateIcon(tabIconTitle,
                                                   minCloseDetachIcon,
                                                   SwingConstants.HORIZONTAL);
        this.flashingContents = new HashMap<Content, Object>();

        setFocusable(false);
        setInheritsPopupMenu(false);

        mouseInputAdapter = new MouseOverTabListener();
        addMouseListener(mouseInputAdapter);
        addMouseMotionListener(mouseInputAdapter);

        if (enabledDrag) {
            // TODO: enabled drag
        }
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if ("flash".equals(evt.getPropertyName())) {
            if (evt.getNewValue() == Boolean.TRUE) {
                flashingContents.put((Content) evt.getSource(), null);
                SwingUtil.repaint(this);
            } else {
                flashingContents.remove((Content) evt.getSource());
                SwingUtil.repaint(this);
            }
        } else if ("selected".equals(evt.getPropertyName())) {
            flashingContents.remove((Content) evt.getSource());
            SwingUtil.repaint(this);
        }
    }

    public String getTitleAt(int index) {
        if (getSelectedIndex() == index)
            return "";
        return super.getTitleAt(index);
    }

    public Icon getIconAt(int index) {
        if (getSelectedIndex() == index) {
            Content content = contentMap.get(index);
            if (content == null)
                return super.getIconAt(index);

            ContentUI contentUI = content.getContentUI();
            if (contentUI == null)
                return super.getIconAt(index);

            titleIcon.setText(super.getTitleAt(index));
            titleIcon.setUnderlinedIndex(
                    SwingUtil.findDisplayedMnemonicIndex(super.getTitleAt(index),
                                                         getContentAt(index).getMnemonic())
            );

            tabIconTitle.setLeftIcon(super.getIconAt(index));

            minCloseDetachIcon.setLeftVisible(contentUI.isMinimizable());
            closeDetachIcon.setLeftVisible(contentUI.isDetachable());
            closeDetachIcon.setRightVisible(contentUI.isCloseable());

            ((ExAggregateIcon) selectedTabIcon).setIndex(index);

            return selectedTabIcon;
        } else if (flashingContents.containsKey(getContentAt(index))) {
            Content content = getContentAt(index);
            Object o = flashingContents.get(content);
            if (o == null) {
                TextIcon textIcon = new TextIcon(this, super.getTitleAt(index), TextIcon.ROTATE_NONE);
                textIcon.setUnderlinedIndex(
                        SwingUtil.findDisplayedMnemonicIndex(super.getTitleAt(index),
                                                             getContentAt(index).getMnemonic())
                );

                Icon icon = new AggregateIcon(new AggregateIcon(super.getIconAt(index),
                                                                textIcon,
                                                                SwingConstants.HORIZONTAL),
                                              resourceManager.getIcon("AUTO_HIDE_ON"),
                                              SwingConstants.HORIZONTAL);
                flashingContents.put(content, icon);
                return icon;
            } else
                return (Icon) o;
        }
        return super.getIconAt(index);
    }

    public void removeTabAt(int index) {
        Content content = getContentAt(index);
        if (content == null)
            throw new IllegalArgumentException("Invalid index location.");

        content.removePropertyChangeListener(this);
        super.removeTabAt(index);
        contentMap.remove(index);

        Integer[] keys = contentMap.keySet().toArray(new Integer[contentMap.size()]);
        Arrays.sort(keys);
        for (Integer key : keys) {
            if (key > index)
                contentMap.put(key - 1, contentMap.remove(key));
        }
    }

    public void setUI(TabbedPaneUI ui) {
        super.setUI(ui);
        setFocusable(false);
        setInheritsPopupMenu(false);

        removeMouseListener(mouseInputAdapter);
        removeMouseMotionListener(mouseInputAdapter);

        addMouseListener(mouseInputAdapter);
        addMouseMotionListener(mouseInputAdapter);
    }

    public void setToolWindowManager(MyDoggyToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        this.resourceManager = toolWindowManager.getResourceManager();

        minimizeIcon = resourceManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_MINIMIZE);
        detachIcon = resourceManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_DETACH);
        closeIcon = resourceManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_CLOSE);

        this.minCloseDetachIcon.setLeftIcon(minimizeIcon);
        this.closeDetachIcon.setLeftIcon(detachIcon);
        this.closeDetachIcon.setRightIcon(closeIcon);
    }

    public void addTab(Content content) {
        String tip = content.getToolTipText();

        addTab(content.getTitle(),
               content.getIcon(),
               content.getComponent(),
               tip);

        content.addPropertyChangeListener(this);
        contentMap.put(getTabCount() - 1, content);
    }

    public void addTab(Content content, Component component) {
        String tip = content.getToolTipText();

        if (component == null)
            component = content.getComponent();

        addTab(content.getTitle(),
               content.getIcon(),
               component,
               tip);

        content.addPropertyChangeListener(this);
        contentMap.put(getTabCount() - 1, content);
    }

    public int addTab(Content content, Component component, int index) {
        if (index < 0 || index >= getTabCount()) {
            addTab(content, component);
            return getTabCount() - 1;
        } else {
            String tip = content.getToolTipText();

            if (component == null)
                component = content.getComponent();

            Integer[] keys = contentMap.keySet().toArray(new Integer[contentMap.size()]);
            Arrays.sort(keys, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return (o1 < o2 ? 1 : (o1.equals(o2) ? 0 : -11));
                }
            });
            for (Integer key : keys) {
                if (key >= index)
                    contentMap.put(key + 1, contentMap.remove(key));
            }

            insertTab(content.getTitle(),
                      content.getIcon(),
                      component,
                      tip,
                      index);

            content.addPropertyChangeListener(this);
            contentMap.put(index, content);
            return index;
        }
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

    public synchronized void setIndex(Content content, Integer newIndex) {
        if (newIndex < 0 || newIndex >= getTabCount())
            throw new IllegalArgumentException("Invalid index");

        int index = indexOfContent(content);
        if (index != -1) {
            removeTabAt(index);
            addTab(content, content.getComponent(), newIndex);
        }
    }

    public int indexOfContent(Content content) {
        for (Integer key : contentMap.keySet()) {
            if (contentMap.get(key) == content)
                return key;
        }
        return -1;
    }

    public Object getSelectedContent() {
        int index = getSelectedIndex();
        return (index != -1) ? getContentAt(index) : null;
    }

//    // Used for debug...
//    public void setSelectedIndex(int index) {
//        super.setSelectedIndex(index);
//    }

    protected void fireCloseTabEvent(Content content) {
        TabbedContentPaneEvent event = new TabbedContentPaneEvent(this,
                                                                  TabbedContentPaneEvent.ActionId.ON_CLOSE,
                                                                  content);
        for (TabbedContentPaneListener tabListener : getListeners(TabbedContentPaneListener.class))
            tabListener.tabbedContentPaneEventFired(event);
    }

    protected void fireDetachTabEvent(Content content) {
        TabbedContentPaneEvent event = new TabbedContentPaneEvent(this,
                                                                  TabbedContentPaneEvent.ActionId.ON_DETACH,
                                                                  content);
        for (TabbedContentPaneListener tabListener : getListeners(TabbedContentPaneListener.class))
            tabListener.tabbedContentPaneEventFired(event);
    }


    protected class TabbedContent {
        Content content;
    }

    protected class MouseOverTabListener extends MouseInputAdapter {
        protected int mouseOverTab = -1;
        protected JPopupMenu stdPopupMenu;
        protected boolean selectionOnPressed;
        protected int mouseOverTabWhenPressed;


        public void mousePressed(MouseEvent e) {
            if (mouseOverTab >= 0 && mouseOverTab < getTabCount()) {
                selectionOnPressed = (((ExAggregateIcon) selectedTabIcon).getIndex() == mouseOverTab);
            }
            mouseOverTabWhenPressed = mouseOverTab;
        }

        public void mouseClicked(MouseEvent e) {
            if (mouseOverTab >= 0 && mouseOverTab < getTabCount()) {
                if (mouseOverTab == mouseOverTabWhenPressed && !selectionOnPressed)
                    return;

                Content content = getContentAt(mouseOverTab);

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isDetachFired(content.getContentUI(), e.getPoint())) {
                        fireDetachTabEvent(content);
                        return;
                    }

                    if (isCloseFired(content.getContentUI(), e.getPoint())) {
                        fireCloseTabEvent(content);
                        return;
                    }

                    if (isMinimizedFired(content.getContentUI(), e.getPoint())) {
                        content.setMinimzed(!content.isMinimzed());
                        return;
                    }

                    if (e.getClickCount() == 2)
                        content.setMaximized(!content.isMaximized());
                } else if (SwingUtilities.isRightMouseButton(e))
                    showPopupMenu(e);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                JPopupMenu popupMenu = getComponentPopupMenu();
                if (popupMenu != null)
                    popupMenu.show(JTabbedContentPane.this, e.getX(), e.getY());
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


        protected boolean isMinimizedFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentPane.this, point, getDestination());
            Rectangle iconBounds = minCloseDetachIcon.getLastPaintedLeftRec();

            return (contentUI.isMinimizable() && ((relativeMousePoint.getX() > iconBounds.x && relativeMousePoint.getX() < iconBounds.x + iconBounds.width) ||
                                                 (point.getX() > iconBounds.x && point.getX() < iconBounds.x + iconBounds.width)));
        }

        protected boolean isDetachFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentPane.this, point, getDestination());
            Rectangle iconBounds = closeDetachIcon.getLastPaintedLeftRec();

            return (contentUI.isDetachable() && ((relativeMousePoint.getX() > iconBounds.x && relativeMousePoint.getX() < iconBounds.x + iconBounds.width) ||
                                                 (point.getX() > iconBounds.x && point.getX() < iconBounds.x + iconBounds.width)));
        }

        protected boolean isCloseFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentPane.this, point, getDestination());
            Rectangle iconsBounds = closeDetachIcon.getLastPaintedRightRec();

            return (contentUI.isCloseable() && ((relativeMousePoint.getX() > iconsBounds.x && relativeMousePoint.getX() < iconsBounds.x + iconsBounds.width) ||
                                                (point.getX() > iconsBounds.x && point.getX() < iconsBounds.x + iconsBounds.width)));
        }

        protected Component getDestination() {
            for (int i = 0, size = JTabbedContentPane.this.getComponentCount(); i < size; i++) {
                if (JTabbedContentPane.this.getComponent(i) instanceof JViewport)
                    return ((JViewport) JTabbedContentPane.this.getComponent(i)).getView();
            }
            return JTabbedContentPane.this;
        }

        protected void showPopupMenu(final MouseEvent mouseEvent) {
            final Content contentAt = getContentAt(mouseOverTab);
            JPopupMenu popupMenu = contentAt.getPopupMenu();
            if (popupMenu == null)
                popupMenu = getComponentPopupMenu();

            if (popupMenu == null) {
                // Init stdPopupMenu
                stdPopupMenu = new JPopupMenu("Content Page Popup");
                stdPopupMenu.add(new JMenuItem(new AbstractAction(resourceManager.getString("@@tabbed.page.close")) {
                    public void actionPerformed(ActionEvent e) {
                        JTabbedContentPane.this.fireCloseTabEvent(contentAt);
                    }
                })).setEnabled(contentAt.getContentUI().isCloseable());

                stdPopupMenu.add(new JMenuItem(new AbstractAction(resourceManager.getString("@@tabbed.page.closeAll")) {
                    public void actionPerformed(ActionEvent e) {
                        for (Content content : toolWindowManager.getContentManager().getContents()) {
                            if (content.getContentUI().isCloseable())
                                JTabbedContentPane.this.fireCloseTabEvent(content);
                        }
                    }
                }));

                stdPopupMenu.add(new JMenuItem(new AbstractAction(resourceManager.getString("@@tabbed.page.closeAllButThis")) {
                    public void actionPerformed(ActionEvent e) {
                        for (Content content : toolWindowManager.getContentManager().getContents()) {
                            if (content != contentAt && content.getContentUI().isCloseable())
                                JTabbedContentPane.this.fireCloseTabEvent(content);
                        }
                    }
                }));
                stdPopupMenu.addSeparator();
                stdPopupMenu.add(new JMenuItem(new AbstractAction(resourceManager.getString("@@tabbed.page.detach")) {
                    public void actionPerformed(ActionEvent e) {
                        JTabbedContentPane.this.fireDetachTabEvent(contentAt);
                    }
                })).setEnabled(contentAt.getContentUI().isDetachable());

                MaximizeAction maximizeAction = new MaximizeAction(contentAt);
                stdPopupMenu.add(maximizeAction);
                maximizeAction.putValue(Action.NAME, contentAt.isMaximized() || isAContentMaximized() ?
                                                     resourceManager.getString("@@tabbed.page.restore") :
                                                     resourceManager.getString("@@tabbed.page.maximize")
                );
                popupMenu = stdPopupMenu;
            }

            if (popupMenu != null)
                popupMenu.show(JTabbedContentPane.this, mouseEvent.getX(), mouseEvent.getY());
        }

        protected boolean isAContentMaximized() {
            for (Content content : toolWindowManager.getContentManager().getContents())
                if (content.isMaximized())
                    return true;

            return false;
        }

        class MaximizeAction extends AbstractAction {
            Content content;

            public MaximizeAction(Content content) {
                super(resourceManager.getString("@@tabbed.page.maximize"));
                this.content = content;
            }

            public void actionPerformed(ActionEvent e) {
                content.setMaximized(!content.isMaximized());
            }
        }

    }

    protected class ExAggregateIcon extends AggregateIcon {
        protected int index;

        public ExAggregateIcon(Icon leftIcon, Icon rightIcon, int orientation) {
            super(leftIcon, rightIcon, orientation);
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
