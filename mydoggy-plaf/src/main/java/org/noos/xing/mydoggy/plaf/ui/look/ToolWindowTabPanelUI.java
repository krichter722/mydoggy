package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeBridge;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.PopupUpdater;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabButton;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleButton;
import org.noos.xing.mydoggy.plaf.ui.drag.DragListener;
import org.noos.xing.mydoggy.plaf.ui.drag.DragListenerDelegate;
import org.noos.xing.mydoggy.plaf.ui.util.MouseEventDispatcher;
import org.noos.xing.mydoggy.plaf.ui.util.RemoveNotifyDragListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabPanelUI extends BasicPanelUI implements Cleaner {


    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowTabPanelUI();
    }


    protected ToolWindowTabPanel toolWindowTabPanel;

    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;

    protected JViewport viewport;
    protected JPanel tabContainer;
    protected TableLayout containerLayout;

    protected ToolWindowTab selectedTab;
    protected Component selecTabButton;
    protected PopupButton popupButton;

    // Listeners

    protected MouseEventDispatcher mouseEventDispatcher;
    protected PropertyChangeBridge propertyChangeBridge;
    protected ToolWindowListener toolWindowListener;
    protected MouseListener titleBarMouseListener;
    protected PopupUpdater popupUpdater;

    // Drag gesture

    protected DragListenerDelegate dragListenerDelegate;
    protected RemoveNotifyDragListener removeNotifyDragListener;


    public ToolWindowTabPanelUI() {
    }


    public void cleanup() {
        uninstallUI(toolWindowTabPanel);
    }


    public void installUI(JComponent c) {
        // Init fields
        this.toolWindowTabPanel = (ToolWindowTabPanel) c;
        this.descriptor = toolWindowTabPanel.getToolWindowDescriptor();
        this.toolWindow = descriptor.getToolWindow();

        this.mouseEventDispatcher = new MouseEventDispatcher();
        this.dragListenerDelegate = new DragListenerDelegate();

        super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        //Finalize
        toolWindow = null;
        descriptor = null;

        selectedTab = null;
        selecTabButton = null;
        dragListenerDelegate = null;
    }

    protected void installDefaults(JPanel p) {
        super.installDefaults(p);

        installComponents();
        installListeners();
    }

    protected void uninstallDefaults(JPanel p) {
        super.uninstallDefaults(p);

        uninstallListeners();
    }

    protected void installComponents() {
        toolWindowTabPanel.setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL, 1, 14},
                                                                            {0, TableLayout.FILL, 0}},
                                                             false));
        toolWindowTabPanel.setFocusable(false);
        toolWindowTabPanel.setBorder(null);
        toolWindowTabPanel.setOpaque(false);

        containerLayout = new TableLayout(new double[][]{{0},
                                                         {SwingUtil.getInt("ToolWindowTitleBarUI.length", 16)}});
        tabContainer = new JPanel();
        tabContainer.setLayout(containerLayout);
        tabContainer.setName("toolWindow.tabContainer." + descriptor.getToolWindow().getId());
        tabContainer.setOpaque(false);
        tabContainer.setBorder(null);
        tabContainer.setFocusable(false);

        viewport = new JViewport();
        viewport.setBorder(null);
        viewport.setOpaque(false);
        viewport.setFocusable(false);
        viewport.setView(tabContainer);

        toolWindowTabPanel.add(viewport, "0,1,FULL,FULL");
        toolWindowTabPanel.add(popupButton = new PopupButton(), "2,1,FULL,FULL");

        viewport.addMouseWheelListener(new WheelScroller());

        propertyChangeBridge = new PropertyChangeBridge();
        propertyChangeBridge.addBridgePropertyChangeListener("selected", new TabSelectedPropertyChangeListener());
        propertyChangeBridge.addBridgePropertyChangeListener("minimized", new TabMinimizedPropertyChangeListener());

        initTabs();
    }

    protected void installListeners() {
        // cleaner
        descriptor.getCleaner().addCleaner(propertyChangeBridge);
        descriptor.getCleaner().addCleaner(this);
        descriptor.addPopupUpdater(popupUpdater = new TabPopupUpdater());

        // toolwindow listeners
        toolWindow.addToolWindowListener(toolWindowListener = new TabPanelToolWindowListener());

        // viewport listeners
        viewport.addMouseListener(titleBarMouseListener = new TitleBarMouseAdapter(descriptor));
        viewport.addMouseListener(mouseEventDispatcher);
        viewport.addMouseMotionListener(mouseEventDispatcher);

        // Register drag gesture
        descriptor.getManager().addRemoveNotifyListener(removeNotifyDragListener = new RemoveNotifyDragListener(viewport, dragListenerDelegate));
    }

    protected void uninstallListeners() {
        // cleaner
        propertyChangeBridge.cleanup();
        descriptor.getCleaner().removeCleaner(propertyChangeBridge);
        descriptor.getCleaner().removeCleaner(this);
        descriptor.removePopupUpdater(popupUpdater);

        // toolwindow
        descriptor.getCleaner().removeCleaner((Cleaner) toolWindowListener);
        toolWindow.removeToolWindowListener(toolWindowListener);

        // viewport
        viewport.removeMouseListener(titleBarMouseListener);
        viewport.removeMouseListener(mouseEventDispatcher);
        viewport.removeMouseMotionListener(mouseEventDispatcher);

        // drag gesture
        removeNotifyDragListener.cleanup();
        descriptor.getManager().removeRemoveNotifyListener(removeNotifyDragListener);
    }


    public void setDragListener(DragListener dragListener) {
        dragListenerDelegate.setDragListener(dragListener);
    }

    public DragListener getDragListener() {
        return dragListenerDelegate;
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
        ToolWindowTabButton tabButton = new ToolWindowTabButton(tab, toolWindowTabPanel);
        tab.removePlafPropertyChangeListener(propertyChangeBridge);
        tab.addPlafPropertyChangeListener(propertyChangeBridge);

        addTab(tabButton);
    }


    protected ToolWindowTab getNextTab(ToolWindowTab toolWindowTab) {
        // Look for column of the next tab in respeto to <code>toolWindowTab</code> 
        int nextTabCol = -1;
        for (Component component : tabContainer.getComponents()) {
            if (component instanceof ToolWindowTabButton) {
                ToolWindowTabButton tabButton = (ToolWindowTabButton) component;
                if (tabButton.getToolWindowTab() == toolWindowTab) {
                    TableLayoutConstraints constraints = containerLayout.getConstraints(component);
                    nextTabCol = constraints.col1 + 3;
                    break;
                }
            }

        }

        if (nextTabCol != -1) {
            for (Component component : tabContainer.getComponents()) {
                if (component instanceof ToolWindowTabButton) {
                    ToolWindowTabButton tabButton = (ToolWindowTabButton) component;
                    TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                    if (constraints.col1 == nextTabCol)
                        return tabButton.getToolWindowTab();
                }
            }
            for (ToolWindowTab windowTab : toolWindow.getToolWindowTabs()) {
                if (windowTab != toolWindowTab)
                    return windowTab;
            }
        }

        return null;
    }

    protected Component removeTab(ToolWindowTab toolWindowTab, boolean flag) {
        for (Component component : tabContainer.getComponents()) {
            if (component instanceof ToolWindowTabButton) {
                ToolWindowTabButton tabButton = (ToolWindowTabButton) component;

                if (tabButton.getToolWindowTab() == toolWindowTab) {
                    TableLayoutConstraints constraints = containerLayout.getConstraints(component);
                    tabButton.setUI(null);
                    tabContainer.remove(component);

                    if (flag)
                        toolWindowTab.removePropertyChangeListener(propertyChangeBridge);

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

        ((TableLayout) toolWindowTabPanel.getLayout()).setColumn(2, visible ? 14 : 0);
    }


    public class TabPanelToolWindowListener implements ToolWindowListener, Cleaner {
        protected Map<ToolWindowTab, Component> minimizedTabs;

        
        public TabPanelToolWindowListener() {
            this.minimizedTabs = new HashMap<ToolWindowTab, Component>();

            descriptor.getCleaner().addBefore(toolWindowTabPanel, this);
        }


        public void cleanup() {
            descriptor.getToolWindow().removeToolWindowListener(this);

            // Remove this cleaner
            descriptor.getCleaner().removeCleaner(this);
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
            ToolWindowTab toolWindowTab = event.getToolWindowTab();

            // Store support fields
            boolean isSelected = toolWindowTab.isSelected();
            ToolWindowTab nextTab = (isSelected) ? getNextTab(toolWindowTab) : null;

            // Remove the tab
            removeTab(event.getToolWindowTab(), true);

            // Select the next if necessary
            if (isSelected) {
                if (nextTab != null) {
                    nextTab.setSelected(true);
                } else {
                    selectedTab = null;
                    selecTabButton = null;
                }
            }

            checkPopupButton();
        }

    }

    public class PopupButton extends ToolWindowTitleButton implements ActionListener, MouseListener {
        protected JPopupMenu popupMenu;


        public PopupButton() {
            super(UIManager.getIcon(MyDoggyKeySpace.TOO_WINDOW_TAB_POPUP));
            setVisible(false);
            initListeners();
        }


        public void actionPerformed(ActionEvent e) {
            initPopup();
            popupMenu.show(this, 10, 10);
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            toolWindow.setActive(true);
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }


        protected void initListeners() {
            addActionListener(this);
            addMouseListener(this);
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

    public class WheelScroller implements MouseWheelListener {

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

    public class TabPopupUpdater implements PopupUpdater {
        protected JMenuItem nextTabItem = new JMenuItem(new SelectNextTabAction());
        protected JMenuItem previousTabItem = new JMenuItem(new SelectPreviousTabAction());
        protected JMenuItem closeAllItem = new JMenuItem(new CloseAllTabAction());

        public void update(Component source, JPopupMenu popupMenu) {
            if (source.getParent() instanceof ToolWindowTabButton) {
                boolean enableByTabsCount = toolWindow.getToolWindowTabs().length > 1;

                ToolWindowTabButton tabButton = (ToolWindowTabButton) source.getParent();

                int index = 0;
                if (tabButton.getToolWindowTab().isCloseable()) {
                    final JMenuItem closeItem = new JMenuItem(new CloseTabAction(tabButton.getToolWindowTab()));
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
                super(SwingUtil.getString("@@tool.tab.close"));
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
                super(SwingUtil.getString("@@tool.tab.closeAll"));
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

    }


    public class TabSelectedPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowTab tab = (ToolWindowTab) evt.getSource();
            if (evt.getNewValue() == Boolean.TRUE) {
                if (selectedTab != null)
                    selectedTab.setSelected(false);
                selectedTab = tab;
            }
        }
    }

    public class TabMinimizedPropertyChangeListener implements PropertyChangeListener {
        protected Map<ToolWindowTab, Component> minimizedTabs;


        public TabMinimizedPropertyChangeListener() {
            this.minimizedTabs = new HashMap<ToolWindowTab, Component>();
        }


        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowTab tab = (ToolWindowTab) evt.getSource();

            if (evt.getNewValue() == Boolean.TRUE) {
                ToolWindowTab nextTab = getNextTab(tab);

                if (tab.isSelected())
                    tab.setSelected(false);

                minimizedTabs.put(tab, removeTab(tab, false));

                if (nextTab != null) {
                    nextTab.setSelected(true);
                }
            } else {
                addTab(minimizedTabs.remove(tab));
            }
            SwingUtil.repaint(toolWindowTabPanel);
        }
    }


    public class SelectNextTabAction extends AbstractAction {

        public SelectNextTabAction() {
            super(SwingUtil.getString("@@tool.tab.selectNext"));
        }

        public void actionPerformed(ActionEvent e) {
            ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
            if (selectedTab != null && selecTabButton != null) {
                int nextTabCol = containerLayout.getConstraints(selecTabButton).col1 + 3;

                for (Component component : tabContainer.getComponents()) {
                    if (component instanceof ToolWindowTabButton) {
                        ToolWindowTabButton tabButton = (ToolWindowTabButton) component;
                        TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                        if (constraints.col1 == nextTabCol) {
                            tabButton.getToolWindowTab().setSelected(true);
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

    public class SelectPreviousTabAction extends AbstractAction {

        public SelectPreviousTabAction() {
            super(SwingUtil.getString("@@tool.tab.selectPreviuos"));
        }

        public void actionPerformed(ActionEvent e) {
            ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
            if (selectedTab != null && selecTabButton != null) {
                int nextTabCol = containerLayout.getConstraints(selecTabButton).col1 - 3;

                for (Component component : tabContainer.getComponents()) {
                    if (component instanceof ToolWindowTabButton) {
                        ToolWindowTabButton tabButton = (ToolWindowTabButton) component;
                        TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                        if (constraints.col1 == nextTabCol) {
                            tabButton.getToolWindowTab().setSelected(true);
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

    public class SelectTabAction extends AbstractAction {
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
