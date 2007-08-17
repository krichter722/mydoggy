package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowActiveButton;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ResourceBundleManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowUI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabPanel extends JComponent implements PropertyChangeListener {
    protected DockedContainer dockedContainer;
    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;

    protected JViewport viewport;
    protected JPanel tabContainer;
    protected TableLayout containerLayout;

    protected ToolWindowTab selectedTab;
    protected TabButton selecTabButton;
    protected PopupButton popupButton;

    public ToolWindowTabPanel(DockedContainer dockedContainer, ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
        this.dockedContainer = dockedContainer;

        initComponents();
        initListeners();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        if ("selected".equals(property)) {
            ToolWindowTab tab = (ToolWindowTab) evt.getSource();
            if (evt.getNewValue() == Boolean.TRUE) {
                if (selectedTab != null)
                    selectedTab.setSelected(false);
                selectedTab = tab;
            }
        }
    }

    public JViewport getViewport() {
        return viewport;
    }

    public JPanel getTabContainer() {
        return tabContainer;
    }


    protected void initComponents() {
        setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL, 1, 14}, {0, TableLayout.FILL, 0}}, false));
        setFocusable(false);

        tabContainer = new JPanel(containerLayout = new TableLayout(new double[][]{{0}, {14}}));
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
        dockedContainer.setPopupUpdater(new DockedContainer.PopupUpdater() {
            final JMenuItem nextTabItem = new JMenuItem(new SelectNextTabAction());
            final JMenuItem previousTabItem = new JMenuItem(new SelectPreviousTabAction());
            final JMenuItem closeAllItem = new JMenuItem(new CloseAllTabAction());

            public void update(MouseEvent e, JPopupMenu popupMenu) {
                if (e.getComponent() instanceof TabButton) {
                    TabButton tabButton = (TabButton) e.getComponent();

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
                }
            }

            class CloseTabAction extends AbstractAction {
                ToolWindowTab tab;

                public CloseTabAction(ToolWindowTab tab) {
                    super(ResourceBundleManager.getInstance().getString("@@tool.tab.close"));
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
                    super(ResourceBundleManager.getInstance().getString("@@tool.tab.closeAll"));
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
        toolWindow.addToolWindowListener(new ToolWindowListener() {
            public void toolWindowTabAdded(ToolWindowTabEvent event) {
                if (tabContainer.getComponentCount() == 0)
                    initTabs();
                else
                    addTab(event.getToolWindowTab());

                setPopupButtonVisibility();
            }

            public boolean toolWindowTabRemoving(ToolWindowTabEvent event) {
                return true;
            }

            public void toolWindowTabRemoved(ToolWindowTabEvent event) {
                ToolWindowTab nextTab = removeTab(event.getToolWindowTab());

                if (event.getToolWindowTab().isSelected()) {
                    ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
                    if (tabs.length > 0) {
                        if (nextTab != null)
                            nextTab.setSelected(true);
                        else
                            tabs[0].setSelected(true);
                    }
                }

                setPopupButtonVisibility();
            }
        });
        viewport.addMouseListener(dockedContainer.getTitleBarMouseAdapter());
    }

    protected void initTabs() {
        for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
            addTab(tab);
        }

        setPopupButtonVisibility();
    }

    protected void addTab(ToolWindowTab tab) {
        int column = containerLayout.getNumColumn();
        containerLayout.insertColumn(column, 0);
        containerLayout.insertColumn(column + 1, -2);
        containerLayout.insertColumn(column + 2, 10);

        TabButton tabButton = new TabButton(tab);
        tabButton.setName("toolWindow." + toolWindow.getId() + ".tabs." + tab.getTitle());
        tabContainer.add(tabButton, (column + 1) + ",0" + ",c,c");

        tab.removePropertyChangeListener(this);
        tab.addPropertyChangeListener(this);

        SwingUtil.repaint(tabContainer);
    }

    protected ToolWindowTab removeTab(ToolWindowTab toolWindowTab) {
        int nextTabCol = -1;
        for (Component component : tabContainer.getComponents()) {
            if (component instanceof TabButton) {
                TabButton tabButton = (TabButton) component;
                if (tabButton.tab == toolWindowTab) {
                    TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);
                    tabContainer.remove(tabButton);
                    tabButton.removePropertyChangeListener(this);

                    nextTabCol = constraints.col1;
                    int col = constraints.col1 - 1;
                    containerLayout.deleteColumn(col);
                    containerLayout.deleteColumn(col);
                    containerLayout.deleteColumn(col);
                    break;
                }
            }
        }

        SwingUtil.repaint(tabContainer);

        if (nextTabCol != -1)
            for (Component component : tabContainer.getComponents()) {
                if (component instanceof TabButton) {
                    TabButton tabButton = (TabButton) component;
                    TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                    if (constraints.col1 == nextTabCol)
                        return tabButton.tab;
                }
            }

        return null;
    }

    protected void setPopupButtonVisibility() {
        boolean visible = toolWindow.getToolWindowTabs().length > 1;
        popupButton.setVisible(visible);

        ((TableLayout)getLayout()).setColumn(2, visible ? 14 : 0);
    }
    

    class TabButton extends JLabel implements PropertyChangeListener {
        ToolWindowTab tab;
        boolean pressed = false;
        boolean inside = false;

        public TabButton(ToolWindowTab tab) {
            super(tab.getTitle());

            this.tab = tab;
            this.tab.addPropertyChangeListener(this);

            setForeground(descriptor.getToolWindowUI().getColor(ToolWindowUI.TW_APP_TAB_FOREGROUND_UNSELECTED));
            setOpaque(false);
            setFocusable(false);
            setIcon(tab.getIcon());
            setUI(new BasicLabelUI(){
                protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
                    if (pressed && inside)
                        super.paintEnabledText(l, g, s, textX + 1, textY + 1);
                    else
                        super.paintEnabledText(l, g, s, textX, textY);
                }
            });

            addMouseListener(dockedContainer.getTitleBarMouseAdapter());
            addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    toolWindow.setActive(true);

                    if (SwingUtilities.isLeftMouseButton(e) &&
                        // TODO: it's orrible
                        getForeground() != Color.WHITE) {
                        pressed = true;
                        repaint();
                    } else {
                        pressed = false;
                        repaint();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }

                public void mouseEntered(MouseEvent e) {
                    inside = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    inside = false;
                    repaint();
                }

                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                TabButton.this.tab.setSelected(true);
                            }
                        });
                    }
                }
            });
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            if ("selected".equals(property)) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    selecTabButton = null;
                    TabButton.this.setForeground(descriptor.getToolWindowUI().getColor(ToolWindowUI.TW_APP_TAB_FOREGROUND_UNSELECTED));
                } else {
                    selecTabButton = this;
                    // Ensure position
                    Rectangle cellBounds = getBounds();
                    cellBounds.x -= viewport.getViewPosition().x;
                    viewport.scrollRectToVisible(cellBounds);
                    
                    TabButton.this.setForeground(descriptor.getToolWindowUI().getColor(ToolWindowUI.TW_APP_TAB_FOREGROUND_SELECTED));
                }
            } else if ("title".equals(property)) {
                setText((String) evt.getNewValue());
                setName("toolWindow." + toolWindow.getId() + ".tabs." + tab.getTitle());
            } else if ("icon".equals(property)) {
                setIcon((Icon) evt.getNewValue());
            }
        }

    }

    class PopupButton extends ToolWindowActiveButton implements ActionListener {
        private JPopupMenu popupMenu;

        public PopupButton() {
            setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/down.png"));
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

        private void initPopup() {
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

    static class SelectTabAction extends AbstractAction {
        private ToolWindowTab tab;

        public SelectTabAction(ToolWindowTab tab) {
            super(tab.getTitle());
            this.tab = tab;
        }

        public void actionPerformed(ActionEvent e) {
            tab.setSelected(true);
        }
    }

    class WheelScroller implements MouseWheelListener {
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

    class SelectNextTabAction extends AbstractAction {

        public SelectNextTabAction() {
            super(ResourceBundleManager.getInstance().getString("@@tool.tab.selectNext"));
        }

        public void actionPerformed(ActionEvent e) {
            ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
            if (selectedTab != null && selecTabButton != null) {
                int nextTabCol = containerLayout.getConstraints(selecTabButton).col1 + 3;

                for (Component component : tabContainer.getComponents()) {
                    if (component instanceof TabButton) {
                        TabButton tabButton = (TabButton) component;
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

    class SelectPreviousTabAction extends AbstractAction {

        public SelectPreviousTabAction() {
            super(ResourceBundleManager.getInstance().getString("@@tool.tab.selectPreviuos"));
        }

        public void actionPerformed(ActionEvent e) {
            ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
            if (selectedTab != null && selecTabButton != null) {
                int nextTabCol = containerLayout.getConstraints(selecTabButton).col1 - 3;

                for (Component component : tabContainer.getComponents()) {
                    if (component instanceof TabButton) {
                        TabButton tabButton = (TabButton) component;
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
}
