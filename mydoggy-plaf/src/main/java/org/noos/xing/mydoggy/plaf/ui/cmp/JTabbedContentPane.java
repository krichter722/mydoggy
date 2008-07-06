package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JTabbedContentPane extends JTabbedPane implements PropertyChangeListener {
    protected MyDoggyToolWindowManager toolWindowManager;
    protected ResourceManager resourceManager;
    protected boolean dragEnabled;

    protected Map<Integer, Content> contentMap;
    protected Map<Content, Object> flashingContents;

    protected ExMultipleAggregateIcon aggregateIcon;
    protected TextIcon titleIcon;

    protected ByteArrayOutputStream tmpWorkspace = null;

    protected MouseInputAdapter mouseInputAdapter;
    protected String currentToolTip;

    // For drag tabs
    protected static final int LINEWIDTH = 3;
    protected static final String TRANSFERABLE_NAME = "TabbedTransferable";

    protected Rectangle2D lineRect = new Rectangle2D.Double();
    protected Color lineColor = new Color(0, 100, 255);
    protected DragSource dragSource = new DragSource();
    protected int dragTabIndex = -1;


    public JTabbedContentPane() {
        this(false);
    }

    public JTabbedContentPane(boolean dragEnabled) {
        super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.contentMap = new Hashtable<Integer, Content>();
        this.flashingContents = new HashMap<Content, Object>();
        this.dragEnabled = dragEnabled;

        // Init aggregate icon
        this.aggregateIcon = new ExMultipleAggregateIcon(6, SwingConstants.HORIZONTAL);
        aggregateIcon.setIconAt(1, this.titleIcon = new TextIcon(this, "", TextIcon.ROTATE_NONE));

        setFocusable(false);
        setInheritsPopupMenu(false);

        mouseInputAdapter = new MouseOverTabListener();
        addMouseListener(mouseInputAdapter);
        addMouseMotionListener(mouseInputAdapter);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (dragTabIndex >= 0) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(lineColor);
            g2.fill(lineRect);
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

            aggregateIcon.setVisibleAt(2, contentUI.isMinimizable());
            if (content.isMaximized())
                aggregateIcon.setIconAt(3, UIManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_RESTORE));
            else
                aggregateIcon.setIconAt(3, UIManager.getIcon(MyDoggyKeySpace.CONTENT_PAGE_MAXIMIZE));

            aggregateIcon.setVisibleAt(3, contentUI.isMaximizable());
            aggregateIcon.setVisibleAt(4, contentUI.isDetachable());
            aggregateIcon.setVisibleAt(5, contentUI.isCloseable());

            aggregateIcon.setIndex(index);

            return aggregateIcon;
        } else if (flashingContents.containsKey(getContentAt(index))) {
            Content content = getContentAt(index);
            Object o = flashingContents.get(content);
            if (o == null) {
                Icon icon = new AggregateIcon(UIManager.getIcon("STAR"),
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


    public void setToolWindowManager(MyDoggyToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        this.resourceManager = toolWindowManager.getResourceManager();

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
            for (Integer key : keys) {
                if (key >= index)
                    contentMap.put(key + 1, contentMap.remove(key));
            }

            insertTab(content.getTitle(),
                      content.getIcon(),
                      component,
                      tip,
                      index);

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

    public void setTargetLine(int index) {
        // TODO: do better...

        dragTabIndex = 1;
        if (getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM) {
            if (index < 0) {
                dragTabIndex = -1;
                lineRect.setRect(0, 0, 0, 0);
            } else if (index == getTabCount()) {
                Rectangle rect = getBoundsAt(getTabCount() - 1);
                lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
            } else if (index == 0) {
                Rectangle rect = getBoundsAt(0);
                lineRect.setRect(-LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
            } else {
                Rectangle rect = getBoundsAt(index - 1);
                lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
            }
        } else
            initTargetTopBottomLine(index);

        repaint();

        if (index < 0)
            dragTabIndex = -1;
    }


    protected void initDragListener() {
        // Init drag
        SwingUtil.registerDragGesture(this, new TabbedDragGestureAdapter(toolWindowManager));

        // Init drop
        setDropTarget(new DropTarget(this,
                                     DnDConstants.ACTION_COPY_OR_MOVE,
                                     new TabbedDropTargetListener(),
                                     true));
    }

    protected int getTargetTabIndex(Point point) {
        boolean isTB = getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM;

        for (int i = 0; i < getTabCount(); i++) {
            Rectangle r = getBoundsAt(i);

            if (isTB)
                r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
            else
                r.setRect(r.x, r.y - r.height / 2, r.width, r.height);

            if (r.contains(point))
                return i;
        }

        Rectangle r = getBoundsAt(getTabCount() - 1);
        if (isTB)
            r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
        else
            r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
        return r.contains(point) ? getTabCount() : -1;
    }

    protected void initTargetLeftRightLine(int next) {
        if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
            lineRect.setRect(0, 0, 0, 0);
        } else if (next == getTabCount()) {
            Rectangle rect = getBoundsAt(getTabCount() - 1);
            lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
        } else if (next == 0) {
            Rectangle rect = getBoundsAt(0);
            lineRect.setRect(-LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
        } else {
            Rectangle rect = getBoundsAt(next - 1);
            lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
        }
    }

    protected void initTargetTopBottomLine(int next) {
        if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
            lineRect.setRect(0, 0, 0, 0);
        } else if (next == getTabCount()) {
            Rectangle rect = getBoundsAt(getTabCount() - 1);
            lineRect.setRect(rect.x, rect.y + rect.height - LINEWIDTH / 2, rect.width, LINEWIDTH);
        } else if (next == 0) {
            Rectangle rect = getBoundsAt(0);
            lineRect.setRect(rect.x, -LINEWIDTH / 2, rect.width, LINEWIDTH);
        } else {
            Rectangle rect = getBoundsAt(next - 1);
            lineRect.setRect(rect.x, rect.y + rect.height - LINEWIDTH / 2, rect.width, LINEWIDTH);
        }
    }

    protected Rectangle getTabAreaBound() {
        Rectangle lastTab = getUI().getTabBounds(this, getTabCount() - 1);

        return new Rectangle(0, 0, getWidth(), lastTab.y + lastTab.height);
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
        }

        public void mouseClicked(MouseEvent e) {
            if (mouseOverTab >= 0 && mouseOverTab < getTabCount()) {
                if (mouseOverTab == mouseOverTabWhenPressed && !selectionOnPressed)
                    return;

                Content content = getContentAt(mouseOverTab);

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

                    if ((e.getClickCount() == 2 && contentUI.isMaximizable()) || isMaximizeFired(contentUI, e.getPoint()))
                        content.setMaximized(!content.isMaximized());

                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (toolWindowManager.getContentManager().getContentManagerUI().isPopupMenuEnabled())
                        showPopupMenu(e);
                }

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

            if (mouseOverTab == -1) {
                currentToolTip = null;
            } else {
                ContentUI contentUI = getContentAt(mouseOverTab).getContentUI();
                Point point = e.getPoint();
                if (isMinimizedFired(contentUI, point))
                    currentToolTip = resourceManager.getString("@@tabbed.page.minimize");
                else if (isMaximizeFired(contentUI, point))
                    currentToolTip = resourceManager.getString("@@tabbed.page.maximize");
                else if (isDetachFired(contentUI, point))
                    currentToolTip = resourceManager.getString("@@tabbed.page.detach");
                else if (isCloseFired(contentUI, point))
                    currentToolTip = resourceManager.getString("@@tabbed.page.close");
                else
                    currentToolTip = null;
            }
        }


        protected boolean isMinimizedFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentPane.this, point, getDestination());
            Rectangle iconBounds = aggregateIcon.getLastPaintedRecAt(2);

            return (contentUI.isMinimizable() && ((relativeMousePoint.getX() > iconBounds.x && relativeMousePoint.getX() < iconBounds.x + iconBounds.width) ||
                                                  (point.getX() > iconBounds.x && point.getX() < iconBounds.x + iconBounds.width)));
        }

        protected boolean isMaximizeFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentPane.this, point, getDestination());
            Rectangle iconsBounds = aggregateIcon.getLastPaintedRecAt(3);

            return (contentUI.isMaximizable() && ((relativeMousePoint.getX() > iconsBounds.x && relativeMousePoint.getX() < iconsBounds.x + iconsBounds.width) ||
                                                  (point.getX() > iconsBounds.x && point.getX() < iconsBounds.x + iconsBounds.width)));
        }

        protected boolean isDetachFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentPane.this, point, getDestination());
            Rectangle iconBounds = aggregateIcon.getLastPaintedRecAt(4);

            return (contentUI.isDetachable() && ((relativeMousePoint.getX() > iconBounds.x && relativeMousePoint.getX() < iconBounds.x + iconBounds.width) ||
                                                 (point.getX() > iconBounds.x && point.getX() < iconBounds.x + iconBounds.width)));
        }

        protected boolean isCloseFired(ContentUI contentUI, Point point) {
            Point relativeMousePoint = SwingUtilities.convertPoint(JTabbedContentPane.this, point, getDestination());
            Rectangle iconsBounds = aggregateIcon.getLastPaintedRecAt(5);

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
                stdPopupMenu.add(maximizeAction).setEnabled(contentAt.getContentUI().isMaximizable());
                boolean restore = contentAt.isMaximized() || isAContentMaximized();
                maximizeAction.putValue(Action.NAME, restore ?
                                                     resourceManager.getString("@@tabbed.page.restore") :
                                                     resourceManager.getString("@@tabbed.page.maximize")
                );

                if (!restore && contentAt.getContentUI().isMinimizable()) {
                    stdPopupMenu.add(new MinimizeAction(contentAt));
                }

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

        class MinimizeAction extends AbstractAction {
            Content content;

            public MinimizeAction(Content content) {
                super(resourceManager.getString("@@tabbed.page.minimize"));
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


    public class TabbedDropTargetListener implements DropTargetListener {


        public void dragEnter(DropTargetDragEvent e) {
            if (isDragAcceptable(e))
                e.acceptDrag(e.getDropAction());
            else
                e.rejectDrag();
        }

        public void dragExit(DropTargetEvent e) {
        }

        public void dropActionChanged(DropTargetDragEvent e) {
        }

        public void dragOver(final DropTargetDragEvent e) {
            if (getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM)
                initTargetLeftRightLine(getTargetTabIndex(e.getLocation()));
            else
                initTargetTopBottomLine(getTargetTabIndex(e.getLocation()));

            repaint();
        }

        public void drop(DropTargetDropEvent e) {
            if (isDropAcceptable(e)) {
                int targetIndex = getTargetTabIndex(e.getLocation());
                if (targetIndex >= 0) {
                    moveTab(dragTabIndex, targetIndex);
                    e.dropComplete(true);
                } else
                    e.dropComplete(false);
            } else {
                e.dropComplete(false);
            }

            repaint();
        }


        public boolean isDragAcceptable(DropTargetDragEvent e) {
            Transferable t = e.getTransferable();
            if (t == null)
                return false;

            DataFlavor[] f = e.getCurrentDataFlavors();
            return t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0;
        }

        public boolean isDropAcceptable(DropTargetDropEvent e) {
            Transferable t = e.getTransferable();
            if (t == null)
                return false;

            DataFlavor[] f = t.getTransferDataFlavors();
            return t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0;
        }
    }

    public class TabbedTransferable implements Transferable {
        private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, TRANSFERABLE_NAME);

        public Object getTransferData(DataFlavor flavor) {
            return JTabbedContentPane.this;
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] f = new DataFlavor[1];
            f[0] = this.FLAVOR;
            return f;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.getHumanPresentableName().equals(TRANSFERABLE_NAME);
        }
    }

    public class TabbedDragGestureAdapter extends DragGestureAdapter {


        public TabbedDragGestureAdapter(MyDoggyToolWindowManager manager) {
            super(manager);
        }


        public void dragGestureRecognized(DragGestureEvent dge) {
            Point tabPt = dge.getDragOrigin();

            dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
            if (dragTabIndex < 0)
                return;

            dge.startDrag(DragSource.DefaultMoveDrop,
                          new TabbedTransferable(),
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

        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            updateGhostImage(dsde.getLocation());
        }

        public void dragEnter(DragSourceDragEvent e) {
        }

        public void dragExit(DragSourceEvent e) {
            lineRect.setRect(0, 0, 0, 0);

//            toolWindowManager.getGlassPanel().setPoint(new Point(-1000, -1000));
//            toolWindowManager.getGlassPanel().repaint();
        }

        public void dragOver(DragSourceDragEvent e) {
            // This method returns a Point indicating the cursor location in screen coordinates at the moment

            Point tabPt = e.getLocation();
            SwingUtilities.convertPointFromScreen(tabPt, JTabbedContentPane.this);

            Point glassPt = e.getLocation();
            SwingUtilities.convertPointFromScreen(glassPt, toolWindowManager.getGlassPanel());
            int targetIdx = getTargetTabIndex(glassPt);

            if (getTabAreaBound().contains(tabPt) && targetIdx >= 0 &&
                targetIdx != dragTabIndex && targetIdx != dragTabIndex + 1) {
            }
        }

        public void dragDropEnd(DragSourceDropEvent e) {
            if (!e.getDropSuccess()) {
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

            // cleanup
            lineRect.setRect(0, 0, 0, 0);
            dragTabIndex = -1;

            cleanupGhostImage();
        }

        public void dropActionChanged(DragSourceDragEvent e) {
        }

    }
}