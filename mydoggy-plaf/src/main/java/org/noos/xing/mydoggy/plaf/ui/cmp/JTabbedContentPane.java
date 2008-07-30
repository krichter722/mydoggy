package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
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

    // For drag tabs
    protected boolean hasGhost = true;
    protected static final int LINEWIDTH = 3;
    protected static final String NAME = "test";
    protected Rectangle2D lineRect = new Rectangle2D.Double();
    protected Color lineColor = new Color(0, 100, 255);
    protected DragSource dragSource = new DragSource();
    protected DropTarget dropTarget;
    protected int dragTabIndex = -1;


    public JTabbedContentPane() {
        this(false);
    }

    public JTabbedContentPane(boolean dragEnabled) {
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
        this.dragEnabled = dragEnabled;

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


    protected void initDragListener() {
        final DragSourceListener dsl = new TabbedDragSourceListener();
        final Transferable t = new TabbedTransferable();
        final DragGestureListener dgl = new TabbedDragGestureListener(t, dsl);

        dropTarget = new DropTarget(toolWindowManager.getGlassPanel(),
                                    DnDConstants.ACTION_COPY_OR_MOVE,
                                    new TabbedDropTargetListener(),
                                    true);
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
    }

    public void setPaintGhost(boolean flag) {
        hasGhost = flag;
    }

    public boolean hasGhost() {
        return hasGhost;
    }

    protected int getTargetTabIndex(Point glassPt) {
        Point tabPt = SwingUtilities.convertPoint(toolWindowManager.getGlassPanel(),
                                                  glassPt,
                                                  JTabbedContentPane.this);
        boolean isTB = getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM;
        for (int i = 0; i < getTabCount(); i++) {
            Rectangle r = getBoundsAt(i);
            if (isTB) r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
            else r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
            if (r.contains(tabPt)) return i;
        }
        Rectangle r = getBoundsAt(getTabCount() - 1);
        if (isTB) r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
        else r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
        return r.contains(tabPt) ? getTabCount() : -1;
    }

    protected void convertTab(int prev, int next) {
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

    protected void initGlassPane(Component c, Point tabPt) {
        //Point p = (Point) pt.clone();
        if (hasGhost()) {
            Rectangle rect = getBoundsAt(dragTabIndex);
            BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            c.paint(g);
            image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            toolWindowManager.getGlassPanel().setDraggingImage(image);
        }
        Point glassPt = SwingUtilities.convertPoint(c, tabPt, toolWindowManager.getGlassPanel());
        toolWindowManager.getGlassPanel().setPoint(glassPt);
        toolWindowManager.getGlassPanel().setVisible(true);
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
                        content.setMinimized(!content.isMinimized());
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


    protected class TabbedDropTargetListener implements DropTargetListener {
        public void dragEnter(DropTargetDragEvent e) {
            if (isDragAcceptable(e)) e.acceptDrag(e.getDropAction());
            else e.rejectDrag();
        }

        public void dragExit(DropTargetEvent e) {
        }

        public void dropActionChanged(DropTargetDragEvent e) {
        }

        public void dragOver(final DropTargetDragEvent e) {
            if (getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM) {
                initTargetLeftRightLine(getTargetTabIndex(e.getLocation()));
            } else {
                initTargetTopBottomLine(getTargetTabIndex(e.getLocation()));
            }
            repaint();
            if (hasGhost()) {
                toolWindowManager.getGlassPanel().setPoint(e.getLocation());
                toolWindowManager.getGlassPanel().repaint();
            }
        }

        public void drop(DropTargetDropEvent e) {
            if (isDropAcceptable(e)) {
                convertTab(dragTabIndex, getTargetTabIndex(e.getLocation()));
                e.dropComplete(true);
            } else {
                e.dropComplete(false);
            }
            repaint();
        }

        public boolean isDragAcceptable(DropTargetDragEvent e) {
            Transferable t = e.getTransferable();
            if (t == null) return false;
            DataFlavor[] f = e.getCurrentDataFlavors();
            if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }

        public boolean isDropAcceptable(DropTargetDropEvent e) {
            Transferable t = e.getTransferable();
            if (t == null) return false;
            DataFlavor[] f = t.getTransferDataFlavors();
            if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }
    }

    protected class TabbedDragSourceListener implements DragSourceListener {
        public void dragEnter(DragSourceDragEvent e) {
//            e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
        }

        public void dragExit(DragSourceEvent e) {
//            e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            lineRect.setRect(0, 0, 0, 0);
            toolWindowManager.getGlassPanel().setPoint(new Point(-1000, -1000));
            toolWindowManager.getGlassPanel().repaint();
        }

        public void dragOver(DragSourceDragEvent e) {
            //e.getLocation()
            //This method returns a Point indicating the cursor location in screen coordinates at the moment
            Point tabPt = e.getLocation();
            SwingUtilities.convertPointFromScreen(tabPt, JTabbedContentPane.this);
            Point glassPt = e.getLocation();
            SwingUtilities.convertPointFromScreen(glassPt, toolWindowManager.getGlassPanel());
            int targetIdx = getTargetTabIndex(glassPt);

            if (getTabAreaBound().contains(tabPt) && targetIdx >= 0 &&
                targetIdx != dragTabIndex && targetIdx != dragTabIndex + 1) {
//                e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            } else {
//                e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            }
        }

        public void dragDropEnd(DragSourceDropEvent e) {
            lineRect.setRect(0, 0, 0, 0);
            dragTabIndex = -1;
            if (hasGhost()) {
                toolWindowManager.getGlassPanel().setVisible(false);
                toolWindowManager.getGlassPanel().setDraggingImage(null);
            }
        }

        public void dropActionChanged(DragSourceDragEvent e) {
        }
    }

    protected class TabbedTransferable implements Transferable {
        private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);

        public Object getTransferData(DataFlavor flavor) {
            return JTabbedContentPane.this;
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] f = new DataFlavor[1];
            f[0] = this.FLAVOR;
            return f;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.getHumanPresentableName().equals(NAME);
        }
    }

    protected class TabbedDragGestureListener implements DragGestureListener {
        private final Transferable t;
        private final DragSourceListener dsl;

        public TabbedDragGestureListener(Transferable t, DragSourceListener dsl) {
            this.t = t;
            this.dsl = dsl;
        }

        public void dragGestureRecognized(DragGestureEvent e) {
            Point tabPt = e.getDragOrigin();
            dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
            if (dragTabIndex < 0)
                return;
            initGlassPane(e.getComponent(), e.getDragOrigin());
            try {
                e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
            } catch (InvalidDnDOperationException idoe) {
            }
        }
    }
}