package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeBridge;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGesture;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureDelegate;
import org.noos.xing.mydoggy.plaf.ui.util.MouseEventDispatcher;
import org.noos.xing.mydoggy.plaf.ui.util.MyDoggyUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @todo: (-) implemets maximized
 */
public class ToolWindowTabPanel extends JComponent implements ToolWindowTabContainer, Cleaner {
    protected DockedContainer dockedContainer;
    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected ResourceManager resourceManager;

    protected JViewport viewport;
    protected JPanel tabContainer;
    protected TableLayout containerLayout;

    protected ToolWindowTab selectedTab;
    protected Component selecTabButton;
    protected PopupButton popupButton;

    protected DragGestureDelegate dragGestureDelegate;
    protected MouseEventDispatcher mouseEventDispatcher;

    protected PropertyChangeBridge propertyChangeBridge;


    public ToolWindowTabPanel(DockedContainer dockedContainer, ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
        this.resourceManager = descriptor.getResourceManager();
        this.dockedContainer = dockedContainer;
        this.mouseEventDispatcher = new MouseEventDispatcher();
        this.dragGestureDelegate = new DragGestureDelegate();

        descriptor.getCleaner().addCleaner(this);

        initComponents();
        initListeners();
    }


    public void cleanup() {
        //Finalize

        toolWindow = null;
        descriptor = null;
        resourceManager = null;
        dockedContainer = null;

        selectedTab = null;
        selecTabButton = null;
        dragGestureDelegate = null;
    }

    public void setDragGesture(DragGesture dragGesture) {
        dragGestureDelegate.setDragGesture(dragGesture);
    }


    public DragGesture getDragGesture() {
        return dragGestureDelegate;
    }

    public MouseEventDispatcher getMouseEventDispatcher() {
        return mouseEventDispatcher;
    }

    public void addEventDispatcherlListener(EventListener eventListener) {
        mouseEventDispatcher.addListener(eventListener);
    }

    public void removeEventDispatcherlListener(EventListener eventListener) {
        mouseEventDispatcher.removeListener(eventListener);
    }

    public void ensureVisible(final Rectangle bounds) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                bounds.x -= viewport.getViewPosition().x;
                viewport.scrollRectToVisible(bounds);
            }
        });
    }


    protected void initComponents() {
        propertyChangeBridge = new PropertyChangeBridge();
        descriptor.getCleaner().addCleaner(propertyChangeBridge);

        setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL, 1, 14}, {0, TableLayout.FILL, 0}}, false));
        setFocusable(false);
        setBorder(null);

        tabContainer = new JPanel(containerLayout = new TableLayout(new double[][]{{0},
                                                                                   {MyDoggyUtil.getInt("ToolWindowTitleBarUI.length", 16)}}));
        tabContainer.setName("toolWindow.tabContainer." + descriptor.getToolWindow().getId());
        tabContainer.setOpaque(false);
        tabContainer.setBorder(null);
        tabContainer.setFocusable(false);

        viewport = new JViewport();
        viewport.setBorder(null);
        viewport.setOpaque(false);
        viewport.setFocusable(false);
        viewport.setView(tabContainer);

        add(viewport, "0,1,FULL,FULL");
        add(popupButton = new PopupButton(), "2,1,FULL,FULL");

        viewport.addMouseWheelListener(new WheelScroller());

        initTabs();
    }

    protected void initListeners() {
        propertyChangeBridge.addBridgePropertyChangeListener("selected", new TabSelectedPropertyChangeListener());

        dockedContainer.setPopupUpdater(new DockedContainer.PopupUpdater() {
            final JMenuItem nextTabItem = new JMenuItem(new SelectNextTabAction());
            final JMenuItem previousTabItem = new JMenuItem(new SelectPreviousTabAction());
            final JMenuItem closeAllItem = new JMenuItem(new CloseAllTabAction());

            public void update(Component source, JPopupMenu popupMenu) {
                if (source.getParent() instanceof ToolWindowTabButtonPanel) {
                    boolean enableByTabsCount = toolWindow.getToolWindowTabs().length > 1;

                    ToolWindowTabButtonPanel tabButton = (ToolWindowTabButtonPanel) source.getParent();

                    int index = 0;
                    if (tabButton.tab.isCloseable()) {
                        final JMenuItem closeItem = new JMenuItem(new CloseTabAction(tabButton.tab));
                        popupMenu.add(closeItem, index++);
                        popupMenu.add(closeAllItem, index++);
                        popupMenu.add(new JSeparator(), index++);
                    }
                    popupMenu.add(nextTabItem, index++);
                    popupMenu.add(previousTabItem, index++);
                    popupMenu.add(new JSeparator(), index);

                    nextTabItem.setEnabled(enableByTabsCount);
                    previousTabItem.setEnabled(enableByTabsCount);
                }
            }

            class CloseTabAction extends AbstractAction {
                ToolWindowTab tab;

                public CloseTabAction(ToolWindowTab tab) {
                    super(resourceManager.getString("@@tool.tab.close"));
                    this.tab = tab;
                }

                public void actionPerformed(ActionEvent e) {
                    if (tab.isCloseable()) {
                        ToolWindowTabEvent event = new ToolWindowTabEvent(this, ToolWindowTabEvent.ActionId.TAB_REMOVING,
                                                                          toolWindow, tab);
                        for (ToolWindowListener listener : toolWindow.getToolWindowListeners()) {
                            boolean result = listener.toolWindowTabRemoving(event);
                            if (!result)
                                break;
                        }

                        toolWindow.removeToolWindowTab(tab);
                    }
                }
            }

            class CloseAllTabAction extends AbstractAction {
                public CloseAllTabAction() {
                    super(resourceManager.getString("@@tool.tab.closeAll"));
                }

                public void actionPerformed(ActionEvent e) {
                    ToolWindowTab selectedTab = null;

                    for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                        if (tab.isSelected()) {
                            selectedTab = tab;
                            continue;
                        }
                        tryToClose(tab);
                    }

                    tryToClose(selectedTab);
                }

                protected void tryToClose(ToolWindowTab tab) {
                    if (tab != null && tab.isCloseable()) {
                        ToolWindowTabEvent event = new ToolWindowTabEvent(this, ToolWindowTabEvent.ActionId.TAB_REMOVING,
                                                                          toolWindow, tab);

                        for (ToolWindowListener listener : toolWindow.getToolWindowListeners()) {
                            boolean result = listener.toolWindowTabRemoving(event);
                            if (!result)
                                break;
                        }

                        toolWindow.removeToolWindowTab(tab);
                    }
                }
            }

        });

        toolWindow.addToolWindowListener(new TabToolWindowListener());

        viewport.addMouseListener(dockedContainer.getTitleBarMouseAdapter());
        viewport.addMouseListener(mouseEventDispatcher);
        viewport.addMouseMotionListener(mouseEventDispatcher);

        // Register drag gesture
        SwingUtil.registerDragGesture(viewport, dragGestureDelegate);
    }

    protected void initTabs() {
        for (ToolWindowTab tab : toolWindow.getToolWindowTabs())
            addTab((MyDoggyToolWindowTab) tab);

        checkPopupButton();
    }

    protected void addTab(Component tabButton) {
        int column = containerLayout.getNumColumn();
        containerLayout.insertColumn(column, 0);
        containerLayout.insertColumn(column + 1, -2);
        containerLayout.insertColumn(column + 2, 0);

        tabContainer.add(tabButton, (column + 1) + ",0" + ",FULL,FULL");

        SwingUtil.repaint(tabContainer);
    }

    protected void addTab(MyDoggyToolWindowTab tab) {
        ToolWindowTabButtonPanel tabButton = new ToolWindowTabButtonPanel(descriptor.getManager(), tab,
                                                                          this, dockedContainer);
        tab.removePlafPropertyChangeListener(propertyChangeBridge);
        tab.addPlafPropertyChangeListener(propertyChangeBridge);

        addTab(tabButton);
    }


    protected ToolWindowTab getNextTab(ToolWindowTab toolWindowTab) {
        int nextTabCol = -1;

        for (Component component : tabContainer.getComponents()) {
            if (component instanceof ToolWindowTabButton) {
                ToolWindowTabButton tabButton = (ToolWindowTabButton) component;

                if (tabButton.getToolWindowTab() == toolWindowTab) {
                    TableLayoutConstraints constraints = containerLayout.getConstraints(component);

                    nextTabCol = constraints.col1;
                    // TOOD: va bene ?????????
                    break;
                }
            }

        }

        ToolWindowTab firstTab = null;
        if (nextTabCol != -1) {
            for (Component component : tabContainer.getComponents()) {
                if (component instanceof ToolWindowTabButtonPanel) {
                    ToolWindowTabButtonPanel tabButton = (ToolWindowTabButtonPanel) component;
                    TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                    if (constraints.col1 == nextTabCol)
                        return tabButton.tab;

                    if (constraints.col1 == 2)
                        firstTab = tabButton.tab;
                }
            }
        }
        return firstTab;

    }

    protected Component removeTab(ToolWindowTab toolWindowTab, boolean flag) {
        for (Component component : tabContainer.getComponents()) {
            if (component instanceof ToolWindowTabButton) {
                ToolWindowTabButton tabButton = (ToolWindowTabButton) component;

                if (tabButton.getToolWindowTab() == toolWindowTab) {
                    TableLayoutConstraints constraints = containerLayout.getConstraints(component);
                    tabContainer.remove(component);

                    if (flag)
                        tabButton.removePropertyChangeListener(propertyChangeBridge);

                    int col = constraints.col1 - 1;
                    containerLayout.deleteColumn(col);
                    containerLayout.deleteColumn(col);
                    containerLayout.deleteColumn(col);
                    return component;
                }
            }
        }
        return null;
    }

    protected void checkPopupButton() {
        boolean visible = toolWindow.getToolWindowTabs().length > 1;
        popupButton.setVisible(visible);

        ((TableLayout) getLayout()).setColumn(2, visible ? 14 : 0);
    }


    protected class TabToolWindowListener implements ToolWindowListener, PropertyChangeListener, Cleaner {
        protected Map<ToolWindowTab, Component> minimizedTabs;

        public TabToolWindowListener() {
            this.minimizedTabs = new HashMap<ToolWindowTab, Component>();

            descriptor.getCleaner().addBefore(ToolWindowTabPanel.this, this);
        }


        public void cleanup() {
            descriptor.getToolWindow().removeToolWindowListener(this);
        }

        public void toolWindowTabAdded(ToolWindowTabEvent event) {
            if (tabContainer.getComponentCount() == 0)
                initTabs();
            else
                addTab((MyDoggyToolWindowTab) event.getToolWindowTab());

            checkPopupButton();
        }

        public boolean toolWindowTabRemoving(ToolWindowTabEvent event) {
            return true;
        }

        public void toolWindowTabRemoved(ToolWindowTabEvent event) {
            ToolWindowTab nextTab = getNextTab(event.getToolWindowTab());
            removeTab(event.getToolWindowTab(), true);

            if (event.getToolWindowTab().isSelected()) {
                ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
                if (tabs.length > 0) {
                    if (nextTab != null)
                        nextTab.setSelected(true);
                    else
                        tabs[0].setSelected(true);
                } else {
                    selectedTab = null;
                    selecTabButton = null;
                }
            }

            checkPopupButton();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            ToolWindowTab tab = (ToolWindowTab) evt.getSource();

            if ("minimized".equals(property)) {
                if (evt.getNewValue() == Boolean.TRUE) {
                    ToolWindowTab nextTab = getNextTab(tab);
                    minimizedTabs.put(tab, removeTab(tab, false));
                    if (nextTab != null)
                        nextTab.setSelected(true);
                } else {
                    addTab(minimizedTabs.remove(tab));
                }
            }
        }
    }

    protected class PopupButton extends ToolWindowActiveButton implements ActionListener {
        protected JPopupMenu popupMenu;

        public PopupButton() {
            setIcon(UIManager.getIcon(MyDoggyKeySpace.TOO_WINDOW_TAB_POPUP));
            addActionListener(this);
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    toolWindow.setActive(true);
                }
            });
            setVisible(false);
        }

        public void actionPerformed(ActionEvent e) {
            initPopup();
            popupMenu.show(this, 10, 10);
        }

        protected void initPopup() {
            if (popupMenu == null) {
                popupMenu = new JPopupMenu("TabPopup");
                popupMenu.add(new SelectNextTabAction());
                popupMenu.add(new SelectPreviousTabAction());
                popupMenu.addSeparator();
            }

            for (int i = 3, size = popupMenu.getComponentCount(); i < size; i++)
                popupMenu.remove(3);

            for (ToolWindowTab tab : toolWindow.getToolWindowTabs())
                popupMenu.add(new SelectTabAction(tab));
        }

    }

    protected class WheelScroller implements MouseWheelListener {

        public void mouseWheelMoved(MouseWheelEvent e) {
            switch (e.getWheelRotation()) {
                case 1:
                    Rectangle visRect = viewport.getViewRect();
                    Rectangle bounds = tabContainer.getBounds();

                    visRect.x += e.getUnitsToScroll() * 2;
                    if (visRect.x + visRect.width >= bounds.width)
                        visRect.x = bounds.width - visRect.width;

                    viewport.setViewPosition(new Point(visRect.x, visRect.y));
                    break;
                case -1:
                    visRect = viewport.getViewRect();

                    visRect.x += e.getUnitsToScroll() * 2;
                    if (visRect.x < 0)
                        visRect.x = 0;
                    viewport.setViewPosition(new Point(visRect.x, visRect.y));
                    break;
            }
        }
    }

    protected class TabSelectedPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowTab tab = (ToolWindowTab) evt.getSource();
            if (evt.getNewValue() == Boolean.TRUE) {
                if (selectedTab != null)
                    selectedTab.setSelected(false);
                selectedTab = tab;
            }
        }
    }

    protected class SelectNextTabAction extends AbstractAction {

        public SelectNextTabAction() {
            super(resourceManager.getString("@@tool.tab.selectNext"));
        }

        public void actionPerformed(ActionEvent e) {
            ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
            if (selectedTab != null && selecTabButton != null) {
                int nextTabCol = containerLayout.getConstraints(selecTabButton).col1 + 3;

                for (Component component : tabContainer.getComponents()) {
                    if (component instanceof ToolWindowTabButtonPanel) {
                        ToolWindowTabButtonPanel tabButton = (ToolWindowTabButtonPanel) component;
                        TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                        if (constraints.col1 == nextTabCol) {
                            tabButton.tab.setSelected(true);
                            return;
                        }
                    }
                }
                if (tabs.length > 0)
                    tabs[0].setSelected(true);
            } else if (tabs.length > 0)
                tabs[0].setSelected(true);
        }
    }

    protected class SelectPreviousTabAction extends AbstractAction {

        public SelectPreviousTabAction() {
            super(resourceManager.getString("@@tool.tab.selectPreviuos"));
        }

        public void actionPerformed(ActionEvent e) {
            ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
            if (selectedTab != null && selecTabButton != null) {
                int nextTabCol = containerLayout.getConstraints(selecTabButton).col1 - 3;

                for (Component component : tabContainer.getComponents()) {
                    if (component instanceof ToolWindowTabButtonPanel) {
                        ToolWindowTabButtonPanel tabButton = (ToolWindowTabButtonPanel) component;
                        TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                        if (constraints.col1 == nextTabCol) {
                            tabButton.tab.setSelected(true);
                            return;
                        }
                    }
                }
                if (tabs.length > 0)
                    tabs[tabs.length - 1].setSelected(true);
            } else if (tabs.length > 0)
                tabs[tabs.length - 1].setSelected(true);
        }
    }

    protected class SelectTabAction extends AbstractAction {
        private ToolWindowTab tab;

        public SelectTabAction(ToolWindowTab tab) {
            super(tab.getTitle());
            this.tab = tab;
        }

        public void actionPerformed(ActionEvent e) {
            tab.setSelected(true);
        }
    }

}
