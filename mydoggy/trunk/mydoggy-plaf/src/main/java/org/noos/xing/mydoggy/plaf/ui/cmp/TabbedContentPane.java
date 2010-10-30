package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.drag.DragListener;
import org.noos.xing.mydoggy.plaf.ui.drag.DragListenerAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.RemoveNotifyDragListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TabbedContentPane extends JTabbedPane implements PropertyChangeListener,
        MultiDockableOwner {

    protected static final int LINEWIDTH = 3;
    protected static final String TRANSFERABLE_NAME = "TabbedTransferable";


    protected MyDoggyToolWindowManager toolWindowManager;

    // Support maps
    protected Map<Integer, Content> contentMap;
    protected Map<Content, Object> flashingContents;

    // Icons
    protected ExMultipleAggregateIcon aggregateIcon;
    protected TextIcon titleIcon;

    // Support stream for the maximazion
    protected ByteArrayOutputStream tmpWorkspace = null;

    // Support fields...
    protected MouseInputAdapter mouseInputAdapter;
    protected String currentToolTip;
    protected boolean dragEnabled;
    protected boolean showMaximize = true, showDetach = true, showClose = true, showMinimize = true;

    // Drag support fields

    protected RemoveNotifyDragListener removeNotifyDragListener;
    protected DragSource dragSource = new DragSource();
    protected DragListener dragListener;

    protected Point tabPointerLocation = new Point(-1, 0);
    protected Image tabPointer;
    protected int dragTabIndex = -1;
    protected int indexAtLocation;

    protected boolean pointerVisible;
    public boolean valueAdjusting = false;


    public TabbedContentPane() {
        this(false);
    }


    public TabbedContentPane(boolean dragEnabled) {
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setFocusable(false);
        setInheritsPopupMenu(false);

        // Init maps
        this.contentMap = new Hashtable<Integer, Content>();
        this.flashingContents = new HashMap<Content, Object>();

        // Init icons
        this.aggregateIcon = new ExMultipleAggregateIcon(6, SwingConstants.HORIZONTAL);
        this.aggregateIcon.setIconAt(1, this.titleIcon = new TextIcon(this, "", TextIcon.ROTATE_NONE));
        this.tabPointer = SwingUtil.getImage(MyDoggyKeySpace.CONTENT_POINTER);

        // Init support fields
        this.dragEnabled = dragEnabled;

        // Register mouse listeners
        addMouseListener(mouseInputAdapter = new MouseOverTabListener());
        addMouseMotionListener(mouseInputAdapter);
    }


    public Dockable getDockable() {
        return null;
    }

    public Dockable getDockable(int index) {
        return getContentAt(index);
    }

    public int getDockableIndex(Point point) {
        return indexAtLocation(point.x, point.y);
    }

    public int getDockableIndex() {
        return indexAtLocation;
    }

    public void setPointerVisible(boolean visible) {
        this.pointerVisible = visible;
        SwingUtil.repaint(this);
    }

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        if (tabPointerLocation.x < 0)
            return;

        if (pointerVisible && indexAtLocation != -1) {
            g.drawImage(tabPointer, tabPointerLocation.x, tabPointerLocation.y, this);
        }

        if (dragTabIndex >= 0) {
            g.drawImage(tabPointer, tabPointerLocation.x, tabPointerLocation.y, this);
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

            // Setup aggregate icon
            aggregateIcon.setIconAt(0, super.getIconAt(index));

            titleIcon.setText(super.getTitleAt(index));
            titleIcon.setUnderlinedIndex(SwingUtil.findDisplayedMnemonicIndex(super.getTitleAt(index),
                    getContentAt(index).getMnemonic()));


            aggregateIcon.setVisibleAt(2, contentUI.isMinimizable() && showMinimize);
            if (content.isMaximized() || getContentMaximized(content) != null)
                aggregateIcon.setIconAt(3, UIManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_RESTORE));
            else
                aggregateIcon.setIconAt(3, UIManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_MAXIMIZE));

            aggregateIcon.setVisibleAt(3, contentUI.isMaximizable() && showMaximize);
            aggregateIcon.setVisibleAt(4, contentUI.isDetachable() && showDetach);
            aggregateIcon.setVisibleAt(5, contentUI.isCloseable() && showClose);

            aggregateIcon.setIndex(index);

            return aggregateIcon;
        } else if (flashingContents.containsKey(getContentAt(index))) {
            Content content = getContentAt(index);
            Object o = flashingContents.get(content);
            if (o == null) {
                Icon icon = new AggregateIcon(UIManager.getIcon(MyDoggyKeySpace.CONTENT_FLASHING),
                        super.getIconAt(index),
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

    public String getToolTipText(MouseEvent event) {
        if (currentToolTip != null)
            return currentToolTip;
        return super.getToolTipText(event);
    }

    public void addNotify() {
        super.addNotify();

        // Setup drag
        if (dragListener != null) {
            toolWindowManager.removeRemoveNotifyListener(removeNotifyDragListener);
            toolWindowManager.addRemoveNotifyListener(removeNotifyDragListener = new RemoveNotifyDragListener(this, dragListener));
        }
    }

    public void removeNotify() {
        super.removeNotify();

        // Remove drag gesture
        removeNotifyDragListener.cleanup();
        toolWindowManager.removeRemoveNotifyListener(removeNotifyDragListener);
    }


    public boolean containsDockable(Dockable dockable) {
        for (Content content : contentMap.values()) {
            if (content == dockable)
                return true;
        }
        return false;
    }

    public void setComponent(Dockable dockable, Component component) {
        for (Integer index : contentMap.keySet()) {
            if (contentMap.get(index) == dockable) {
                setComponentAt(index, component);
                return;
            }
        }
    }

    public Component getComponent() {
        throw new IllegalStateException("Cannot call this method here.");
    }

    public void setComponent(Component component) {
        throw new IllegalStateException("Cannot call this method here.");
    }


    public void setToolWindowManager(MyDoggyToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;

        aggregateIcon.setIconAt(2, UIManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_MINIMIZE));
        aggregateIcon.setIconAt(3, UIManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_MAXIMIZE));
        aggregateIcon.setIconAt(4, UIManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_DETACH));
        aggregateIcon.setIconAt(5, UIManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_CLOSE));

        if (dragEnabled)
            initDragListener();
    }

    public void addTab(Content content) {
        String tip = content.getToolTipText();

        addTab(content.getTitle(),
                content.getIcon(),
                content.getComponent(),
                tip);

        int index = getTabCount() - 1;
        setForegroundAt(index, content.getForeground());
        setDisabledIconAt(index, content.getDisabledIcon());
        setMnemonicAt(index, content.getMnemonic());

        content.addPropertyChangeListener(this);
        contentMap.put(index, content);
    }

    public void addTab(Content content, Component component) {
        String tip = content.getToolTipText();

        if (component == null)
            component = content.getComponent();

        addTab(content.getTitle(),
                content.getIcon(),
                component,
                tip);

        int index = getTabCount() - 1;
        setForegroundAt(index, content.getForeground());
        setDisabledIconAt(index, content.getDisabledIcon());
        setMnemonicAt(index, content.getMnemonic());

        content.addPropertyChangeListener(this);
        contentMap.put(index, content);
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

            insertTab(content.getTitle(),
                    content.getIcon(),
                    component,
                    tip,
                    index);

            for (Integer key : keys) {
                if (key >= index && contentMap.containsKey(key))
                    contentMap.put(key + 1, contentMap.remove(key));
            }

            setForegroundAt(index, content.getForeground());
            setDisabledIconAt(index, content.getDisabledIcon());
            setMnemonicAt(index, content.getMnemonic());

            content.addPropertyChangeListener(this);
            contentMap.put(index, content);
            return index;
        }
    }

    public Content getContentAt(int index) {
        return contentMap.get(index);
    }

    public synchronized void setIndex(final Content content, Integer newIndex) {
        if (newIndex < 0 || newIndex >= getTabCount())
            throw new IllegalArgumentException("Invalid index");

        boolean selected = content.isSelected();

        valueAdjusting = true;
        try {
            int index = indexOfContent(content);
            if (index != -1) {
                content.setSelected(false);
                removeTabAt(index);
                addTab(content, content.getComponent(), newIndex);
            }
        } finally {
            valueAdjusting = false;
        }

        if (selected)
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    content.setSelected(true);
                }
            });
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

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;

        toolWindowManager.addRemoveNotifyListener(removeNotifyDragListener = new RemoveNotifyDragListener(this, dragListener));
    }

    public void addTabbedContentPaneListener(TabbedContentPaneListener listener) {
        listenerList.add(TabbedContentPaneListener.class, listener);
    }

    public void removeTabbedContentPaneListener(TabbedContentPaneListener listener) {
        listenerList.remove(TabbedContentPaneListener.class, listener);
    }


    public boolean isShowMaximize() {
        return showMaximize;
    }

    public void setShowMaximize(boolean showMaximize) {
        this.showMaximize = showMaximize;
    }

    public boolean isShowDetach() {
        return showDetach;
    }

    public void setShowDetach(boolean showDetach) {
        this.showDetach = showDetach;
    }

    public boolean isShowClose() {
        return showClose;
    }

    public void setShowClose(boolean showClose) {
        this.showClose = showClose;
    }

    public boolean isShowMinimize() {
        return showMinimize;
    }

    public void setShowMinimize(boolean showMinimize) {
        this.showMinimize = showMinimize;
    }


    protected void initDragListener() {
        // Init drag
        setDragListener(new TabbedDragListenerAdapter(toolWindowManager));

        // Init drop
        setDropTarget(new DropTarget(this,
                DnDConstants.ACTION_COPY_OR_MOVE,
                new TabbedDropTargetListener(),
                true));
    }

    protected void initTargetLeftRightLine(int next) {
        if (next < 0)
            tabPointerLocation.setLocation(-1, 0);
        else if (next == getTabCount()) {
            Rectangle rect = getBoundsAt(getTabCount() - 1);
            tabPointerLocation.setLocation(rect.x + rect.width - LINEWIDTH / 2, rect.y);
        } else if (dragTabIndex == next) {
            tabPointerLocation.setLocation(-1, 0);
        } else if (next == 0) {
            Rectangle rect = getBoundsAt(0);
            tabPointerLocation.setLocation(0, rect.y);
        } else {
            Rectangle rect = getBoundsAt(next - 1);
            tabPointerLocation.setLocation(rect.x + rect.width - LINEWIDTH / 2, rect.y);
        }
    }

    protected void initTargetTopBottomLine(int next) {
        if (next < 0)
            tabPointerLocation.setLocation(-1, 0);
        else if (next == getTabCount()) {
            Rectangle rect = getBoundsAt(getTabCount() - 1);
            tabPointerLocation.setLocation(rect.x, rect.y + rect.height - LINEWIDTH / 2);
        } else if (dragTabIndex == next) {
            tabPointerLocation.setLocation(-1, 0);
        } else if (next == 0) {
            Rectangle rect = getBoundsAt(0);
            tabPointerLocation.setLocation(rect.x, 0);
        } else {
            Rectangle rect = getBoundsAt(next - 1);
            tabPointerLocation.setLocation(rect.x, rect.y + rect.height - LINEWIDTH / 2);
        }
    }

    protected Rectangle getTabAreaBound() {
        Rectangle lastTab = getUI().getTabBounds(this, getTabCount() - 1);

        return new Rectangle(0, 0, getWidth(), lastTab.y + lastTab.height);
    }

    protected void moveTab(int prev, int next) {
        if (next < 0 || prev == next) {
            //System.out.println("press="+prev+" next="+next);
            return;
        }

        // Remove content tab
        Component cmp = getComponentAt(prev);
        Content content = getContentAt(prev);
        if (content == null)
            throw new IllegalArgumentException("Invalid index location.");
        content.removePropertyChangeListener(this);
        super.removeTabAt(prev);
        contentMap.remove(prev);
        Integer[] keys = contentMap.keySet().toArray(new Integer[contentMap.size()]);
        Arrays.sort(keys);
        for (Integer key : keys) {
            if (key > prev)
                contentMap.put(key - 1, contentMap.remove(key));
        }

        if (next == getTabCount()) {
            //System.out.println("last: press="+prev+" next="+next);
            addTab(content, cmp);
            setSelectedIndex(getTabCount() - 1);
        } else if (prev > next) {
            //System.out.println("   >: press="+prev+" next="+next);
            addTab(content, cmp, next);
            setSelectedIndex(next);
        } else {
            //System.out.println("   <: press="+prev+" next="+next);
            addTab(content, cmp, next - 1);
            setSelectedIndex(next - 1);
        }
    }


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

    protected Content getContentMaximized(Content content) {
        for (Content c : content.getDockableManager().getContents()) {
            if (c.isMaximized())
                return c;
        }
        return null;
    }


    public class MouseOverTabListener extends MouseInputAdapter {
        protected int mouseOverTab = -1;
        protected JPopupMenu stdPopupMenu;
        protected boolean selectionOnPressed;
        protected int mouseOverTabWhenPressed;


        public void mousePressed(MouseEvent e) {
            if (mouseOverTab >= 0 && mouseOverTab < getTabCount()) {
                selectionOnPressed = (aggregateIcon.getIndex() == mouseOverTab);
            }
            mouseOverTabWhenPressed = mouseOverTab;

            if (mouseOverTab != -1) {
                Content newSelected = getContentAt(mouseOverTab);
                if (newSelected != null && !newSelected.isMaximized()) {
                    Content oldSelected = toolWindowManager.getContentManager().getSelectedContent();
                    if (newSelected != oldSelected) {
                        newSelected.setSelected(true);
                    }
                }
            }
        }

        public void mouseClicked(MouseEvent e) {
            if (mouseOverTab >= 0 && mouseOverTab < getTabCount()) {
                if (mouseOverTab == mouseOverTabWhenPressed && !selectionOnPressed)
                    return;

                Content content = getContentAt(mouseOverTab);
                if (content == null)
                    return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    ContentUI contentUI = content.getContentUI();

                    if (isDetachFired(contentUI, e.getPoint())) {
                        fireDetachTabEvent(content);
                        return;
                    }

                    if (isCloseFired(contentUI, e.getPoint())) {
                        fireCloseTabEvent(content);
                        return;
                    }

                    if (isMinimizedFired(contentUI, e.getPoint())) {
                        content.setMinimized(!content.isMinimized());
                        return;
                    }

                    if ((e.getClickCount() == 2 && contentUI.isMaximizable()) || isMaximizeFired(contentUI, e.getPoint())) {
                        Content maximized = getContentMaximized(content);
                        if (maximized != null)
                            maximized.setMaximized(false);
                        else
                            content.setMaximized(!content.isMaximized());
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (toolWindowManager.getContentManager().getContentManagerUI().isPopupMenuEnabled())
                        showPopupMenu(e);
                }

            } else if (SwingUtilities.isRightMouseButton(e)) {
                JPopupMenu popupMenu = getComponentPopupMenu();
                if (popupMenu != null)
                    popupMenu.show(TabbedContentPane.this, e.getX(), e.getY());
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
            if (!TabbedContentPane.this.isEnabled())
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

            if (mouseOverTab == -1) {
                currentToolTip = null;
            } else {
                Content content = getContentAt(mouseOverTab);
                if (content == null)
                    return;

                ContentUI contentUI = content.getContentUI();

                Point point = e.getPoint();
                if (isMinimizedFired(contentUI, point))
                    currentToolTip = SwingUtil.getString("@@tabbed.page.minimize");
                else if (isMaximizeFired(contentUI, point))
                    currentToolTip = SwingUtil.getString("@@tabbed.page.maximize");
                else if (isDetachFired(contentUI, point))
                    currentToolTip = SwingUtil.getString("@@tabbed.page.detach");
                else if (isCloseFired(contentUI, point))
                    currentToolTip = SwingUtil.getString("@@tabbed.page.close");
                else
                    currentToolTip = null;
            }
        }


        protected boolean isMinimizedFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(TabbedContentPane.this, point, getDestination());
            Rectangle iconBounds = aggregateIcon.getLastPaintedRecAt(2);

            return iconBounds != null && (contentUI.isMinimizable() && showMinimize && ((relativeMousePoint.getX() > iconBounds.x && relativeMousePoint.getX() < iconBounds.x + iconBounds.width) ||
                    (point.getX() > iconBounds.x && point.getX() < iconBounds.x + iconBounds.width)));
        }

        protected boolean isMaximizeFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(TabbedContentPane.this, point, getDestination());
            Rectangle iconBounds = aggregateIcon.getLastPaintedRecAt(3);

            return iconBounds != null && (contentUI.isMaximizable() && showMaximize && ((relativeMousePoint.getX() > iconBounds.x && relativeMousePoint.getX() < iconBounds.x + iconBounds.width) ||
                    (point.getX() > iconBounds.x && point.getX() < iconBounds.x + iconBounds.width)));
        }

        protected boolean isDetachFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(TabbedContentPane.this, point, getDestination());
            Rectangle iconBounds = aggregateIcon.getLastPaintedRecAt(4);
            return iconBounds != null && (contentUI.isDetachable() && showDetach && ((relativeMousePoint.getX() > iconBounds.x && relativeMousePoint.getX() < iconBounds.x + iconBounds.width) ||
                    (point.getX() > iconBounds.x && point.getX() < iconBounds.x + iconBounds.width)));
        }

        protected boolean isCloseFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(TabbedContentPane.this, point, getDestination());
            Rectangle iconBounds = aggregateIcon.getLastPaintedRecAt(5);

            return iconBounds != null && (contentUI.isCloseable() && showClose && ((relativeMousePoint.getX() > iconBounds.x && relativeMousePoint.getX() < iconBounds.x + iconBounds.width) ||
                    (point.getX() > iconBounds.x && point.getX() < iconBounds.x + iconBounds.width)));

        }


        protected Component getDestination() {
            for (int i = 0, size = TabbedContentPane.this.getComponentCount(); i < size; i++) {
                if (TabbedContentPane.this.getComponent(i) instanceof JViewport)
                    return ((JViewport) TabbedContentPane.this.getComponent(i)).getView();
            }
            return TabbedContentPane.this;
        }

        protected void showPopupMenu(final MouseEvent mouseEvent) {
            final Content contentAt = getContentAt(mouseOverTab);

            JPopupMenu popupMenu = contentAt.getPopupMenu();
            if (popupMenu == null)
                popupMenu = getComponentPopupMenu();

            if (popupMenu == null) {
                // Init stdPopupMenu
                stdPopupMenu = new JPopupMenu("Content Page Popup");

                if (contentAt.getContentUI().isCloseable()) {
                    stdPopupMenu.add(new JMenuItem(new AbstractAction(SwingUtil.getString("@@tabbed.page.close")) {
                        public void actionPerformed(ActionEvent e) {
                            TabbedContentPane.this.fireCloseTabEvent(contentAt);
                        }
                    }));
                }

                stdPopupMenu.add(new JMenuItem(new AbstractAction(SwingUtil.getString("@@tabbed.page.closeAll")) {
                    public void actionPerformed(ActionEvent e) {
                        for (Content content : toolWindowManager.getContentManager().getContents()) {
                            if (content.getContentUI().isCloseable())
                                TabbedContentPane.this.fireCloseTabEvent(content);
                        }
                    }
                }));

                stdPopupMenu.add(new JMenuItem(new AbstractAction(SwingUtil.getString("@@tabbed.page.closeAllButThis")) {
                    public void actionPerformed(ActionEvent e) {
                        for (Content content : toolWindowManager.getContentManager().getContents()) {
                            if (content != contentAt && content.getContentUI().isCloseable())
                                TabbedContentPane.this.fireCloseTabEvent(content);
                        }
                    }
                }));

                boolean restore = false;
                if (contentAt.getContentUI().isDetachable() && showDetach) {
                    stdPopupMenu.addSeparator();
                    stdPopupMenu.add(new JMenuItem(new AbstractAction(SwingUtil.getString("@@tabbed.page.detach")) {
                        public void actionPerformed(ActionEvent e) {
                            TabbedContentPane.this.fireDetachTabEvent(contentAt);
                        }
                    }));
                }


                if (contentAt.getContentUI().isMaximizable() && showMaximize) {
                    MaximizeAction maximizeAction = new MaximizeAction(contentAt);
                    restore = contentAt.isMaximized() || isAContentMaximized();
                    maximizeAction.putValue(Action.NAME, restore ?
                            SwingUtil.getString("@@tabbed.page.restore") :
                            SwingUtil.getString("@@tabbed.page.maximize")
                    );

                    stdPopupMenu.add(maximizeAction);
                }

                if (!restore && contentAt.getContentUI().isMinimizable() && showMinimize) {
                    stdPopupMenu.add(new MinimizeAction(contentAt));
                }

                popupMenu = stdPopupMenu;
            }

            if (popupMenu != null)
                popupMenu.show(TabbedContentPane.this, mouseEvent.getX(), mouseEvent.getY());
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
                super(SwingUtil.getString("@@tabbed.page.maximize"));
                this.content = content;
            }

            public void actionPerformed(ActionEvent e) {
                content.setMaximized(!content.isMaximized());
            }
        }

        class MinimizeAction extends AbstractAction {
            Content content;

            public MinimizeAction(Content content) {
                super(SwingUtil.getString("@@tabbed.page.minimize"));
                this.content = content;
            }

            public void actionPerformed(ActionEvent e) {
                content.setMinimized(!content.isMaximized());
            }
        }
    }

    public class ExMultipleAggregateIcon extends MultipleAggregateIcon {
        protected int index;

        public ExMultipleAggregateIcon(int numIcon, int orientation) {
            super(numIcon, orientation);
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }


    public class TabbedTransferable implements Transferable {
        private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, TRANSFERABLE_NAME);

        public Object getTransferData(DataFlavor flavor) {
            if (isDataFlavorSupported(flavor))
                return TabbedContentPane.this;

            throw new IllegalArgumentException("Invalid data flavor!!!");
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{this.FLAVOR};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return TRANSFERABLE_NAME.equals(flavor.getHumanPresentableName());
        }
    }

    public class TabbedDropTargetListener implements DropTargetListener {

        public void dragEnter(DropTargetDragEvent e) {
            if (isDragAcceptable(e)) {
                e.acceptDrag(e.getDropAction());
            } else {
                e.rejectDrag();
            }
        }

        public void dragExit(DropTargetEvent e) {
        }

        public void dropActionChanged(DropTargetDragEvent e) {
        }

        public void dragOver(final DropTargetDragEvent e) {
            Point location = e.getLocation();

            if (getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM)
                initTargetLeftRightLine(indexAtLocation(location.x, location.y));
            else
                initTargetTopBottomLine(indexAtLocation(location.x, location.y));

            repaint();
        }

        public void drop(DropTargetDropEvent e) {
            if (isDropAcceptable(e)) {
                try {
                    Content dragContent = toolWindowManager.getContentManager().getContent(
                            e.getTransferable().getTransferData(MyDoggyTransferable.CONTENT_ID_DF)
                    );

                    Point location = e.getLocation();
                    int targetIndex = indexAtLocation(location.x, location.y);

                    if (dragContent.isDetached()) {
                        if (targetIndex >= 0) {
                            dragContent.reattach(targetIndex);
                        } else
                            dragContent.reattach(-1);

                        e.dropComplete(true);
                    } else {
                        if (targetIndex >= 0) {
                            setIndex(getContentAt(dragTabIndex), targetIndex);

                            e.dropComplete(true);
                        } else
                            e.dropComplete(false);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    e.dropComplete(false);
                }
            } else {
                e.dropComplete(false);
            }

            repaint();
        }


        public boolean isDragAcceptable(DropTargetDragEvent e) {
            Transferable transferable = e.getTransferable();
            if (transferable == null)
                return false;

            return transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF) /*&& dragTabIndex >= 0*/;
        }

        public boolean isDropAcceptable(DropTargetDropEvent e) {
            Transferable transferable = e.getTransferable();
            if (transferable == null)
                return false;

            return transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF) /*&& dragTabIndex >= 0*/;
        }
    }

    public class TabbedDragListenerAdapter extends DragListenerAdapter {

        protected Content draggingContent;

        public TabbedDragListenerAdapter(MyDoggyToolWindowManager manager) {
            super(manager);
        }


        public void dragGestureRecognized(DragGestureEvent dge) {
            super.dragGestureRecognized(dge);

            // Acquire locks
            if (!acquireLocks())
                return;

            Point tabPt = dge.getDragOrigin();

            dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
            if (dragTabIndex < 0)
                return;

            draggingContent = getContentAt(dragTabIndex);

            dge.startDrag(DragSource.DefaultMoveDrop,
                    new MyDoggyTransferable(manager, MyDoggyTransferable.CONTENT_ID_DF, draggingContent.getId()),
                    this);

            // Setup ghostImage
            if (SwingUtil.getBoolean("drag.icon.useDefault", false)) {
                setGhostImage(dge.getDragOrigin(),
                        SwingUtil.getImage(MyDoggyKeySpace.DRAG));
            } else {
                Component c = dge.getComponent();

                // Build ghost image
                Rectangle rect = getBoundsAt(dragTabIndex);
                BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics g = image.getGraphics();
                c.paint(g);
                image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);

                // setup ghost image;
                setGhostImage(dge.getDragOrigin(), image);
            }

//            removeTabAt(dragTabIndex);
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            if (!checkStatus())
                return;

            // Update ghost image
            updateGhostImage(dsde.getLocation());

            // Update drop target
            updateDropTarget(dsde);
        }

        public void dragEnter(DragSourceDragEvent e) {
        }

        public void dragExit(DragSourceEvent e) {
            tabPointerLocation.setLocation(-1, 0);
        }

        public void dragOver(DragSourceDragEvent e) {
        }

        public void dragDropEnd(DragSourceDropEvent e) {
            try {
                if (!checkStatus())
                    return;

                releaseLocksOne();

                if (!e.getDropSuccess()) {
                    // Finalize drag action...
                    if (lastDropPanel != null) {
                        lastDropPanel.drop(e.getDragSourceContext().getTransferable());
                    } else if (lastBarAnchor == null) {
                        if (SwingUtil.getBoolean(MyDoggyKeySpace.DND_CONTENT_OUTSIDE_FRAME, true)) {
                            // Detach content

                            Content content = getContentAt(dragTabIndex);
                            ContentUI contentUI = content.getContentUI();

                            Rectangle bounds = contentUI.getDetachedBounds();
                            if (bounds != null) {
                                bounds.setLocation(e.getLocation());
                            } else {
                                bounds = new Rectangle();
                                bounds.setLocation(e.getLocation());
                                bounds.setSize(toolWindowManager.getBoundsToScreen(content.getComponent().getBounds(),
                                        content.getComponent().getParent()).getSize());
                            }

                            contentUI.setDetachedBounds(bounds);
                            content.setDetached(true);
                        }
                    } else {
                        // restore content at the same position
//                        addTab(draggingContent, draggingContent.getComponent(), dragTabIndex);
//                        draggingContent.setSelected(true);
                        if (draggingContent != null && dragTabIndex != -1)
                            setIndex(draggingContent, dragTabIndex);
                    }
                }
                releaseLocksTwo();
            } finally {
                // End dockable drop gesture..
                dockableDropDragEnd();
            }

            // cleanup
            tabPointerLocation.setLocation(-1, 0);
            dragTabIndex = -1;

            cleanupGhostImage();
        }

        public void dropActionChanged(DragSourceDragEvent e) {
        }


        @Override
        protected boolean isDragEnabled() {
            return super.isDragEnabled() && SwingUtil.getBoolean(MyDoggyKeySpace.CONTENT_MANAGER_UI_DRAG_ENABLED, true);
        }
    }
}