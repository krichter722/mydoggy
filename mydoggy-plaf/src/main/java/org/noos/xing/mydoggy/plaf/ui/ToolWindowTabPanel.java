package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabPanel extends JComponent implements PropertyChangeListener {
    private ToolWindow toolWindow;

    private JViewport viewport;
    private JPanel tabContainer;
    private TableLayout containerLayout;

    ToolWindowTab selectedTab;

    public ToolWindowTabPanel(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;

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

    protected void initComponents() {
        setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL, 1, 14}, {0, TableLayout.FILL, 0}}, false));

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
        add(new PopupButton(), "2,1,FULL,FULL");

        initTabs();
    }

    protected void initTabs() {
        for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
            addTab(tab);
        }
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

                    nextTabCol = constraints.col1;
                    int col = constraints.col1 - 1;
                    containerLayout.deleteColumn(col);
                    containerLayout.deleteColumn(col);
                    containerLayout.deleteColumn(col);
                    break;
                }
            }
        }

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

    protected void initListeners() {
        toolWindow.addToolWindowListener(new ToolWindowListener() {
            public void toolWindowTabAdded(ToolWindowTabEvent event) {
                if (getComponentCount() == 0)
                    initTabs();
                else {
                    addTab(event.getToolWindowTab());
                }
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

            }
        });
    }


    class TabButton extends ToolWindowActiveButton implements PropertyChangeListener {
        ToolWindowTab tab;

        public TabButton(ToolWindowTab tab) {
            super(tab.getTitle());

            this.tab = tab;
            this.tab.addPropertyChangeListener(this);

            setForeground(Color.LIGHT_GRAY);
            setOpaque(false);
            setFocusable(false);
            setIcon(tab.getIcon());

            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            toolWindow.setActive(true);
                            TabButton.this.tab.setSelected(true);
                        }
                    });
                }
            });
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    toolWindow.setActive(true);
                }
            });
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            if ("selected".equals(property)) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    TabButton.this.setForeground(Color.LIGHT_GRAY);
                } else {
                    // Ensure position
                    Rectangle cellBounds = getBounds();
                    cellBounds.x -= viewport.getViewPosition().x;
                    viewport.scrollRectToVisible(cellBounds);
                    
                    TabButton.this.setForeground(Color.WHITE);
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

        private class SelectNextTabAction extends AbstractAction {

            public SelectNextTabAction() {
                super("Next Tab");  // TODO: use resource boundle.
            }

            public void actionPerformed(ActionEvent e) {
                if (selectedTab != null) {
                    ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();

                }
            }
        }

        private class SelectPreviousTabAction extends AbstractAction {

            public SelectPreviousTabAction() {
                super("Previous Tab");  // TODO: use resource boundle.
            }

            public void actionPerformed(ActionEvent e) {
                if (selectedTab != null) {
                    ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();

                }
            }
        }

        private class SelectTabAction extends AbstractAction {
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

}
