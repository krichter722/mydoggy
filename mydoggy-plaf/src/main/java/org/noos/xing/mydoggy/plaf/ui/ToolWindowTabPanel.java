package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
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

    private ToolWindowTab selectedTab;

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
        for (ToolWindowTab tab : toolWindow.getToolWindowTab()) {
            addTab(tab);
        }
    }

    protected void addTab(ToolWindowTab tab) {
        int column = containerLayout.getNumColumn();
        containerLayout.insertColumn(column, 5);
        containerLayout.insertColumn(column + 1, -2);
        containerLayout.insertColumn(column + 2, 5);

        tabContainer.add(new TabLabel(tab), (column + 1) + ",0" + ",c,c");

        tab.removePropertyChangeListener(this);
        tab.addPropertyChangeListener(this);
    }

    protected void removeTab(ToolWindowTab toolWindowTab) {
    }

    protected void initListeners() {
        toolWindow.addToolWindowListener(new ToolWindowListener() {
            public void toolWindowTabAdded(ToolWindowTabEvent event) {
                if (getComponentCount() == 0)
                    initTabs();
                else
                    addTab(event.getToolWindowTab());
            }

            public void toolWindowTabRemoved(ToolWindowTabEvent event) {
                removeTab(event.getToolWindowTab());
            }
        });
    }


    private class TabLabel extends ToolWindowActiveButton implements PropertyChangeListener {
        private ToolWindowTab tab;

        public TabLabel(ToolWindowTab tab) {
            super(tab.getTitle());

            this.tab = tab;
            this.tab.addPropertyChangeListener(this);

            setForeground(Color.LIGHT_GRAY);
            setOpaque(false);
            setFocusable(false);

            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    toolWindow.setActive(true);
                    TabLabel.this.tab.setSelected(true);
                }
            });
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            if ("selected".equals(property)) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    TabLabel.this.setForeground(Color.LIGHT_GRAY);
                } else {
                    // Ensure position
                    Rectangle cellBounds = getBounds();
                    cellBounds.x -= viewport.getViewPosition().x;
                    viewport.scrollRectToVisible(cellBounds);
                    
                    TabLabel.this.setForeground(Color.WHITE);
                }
            } else if ("title".equals(property)) {
                setText((String) evt.getNewValue());
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
        }

        public void actionPerformed(ActionEvent e) {
            initPopup();
            popupMenu.show(this, 10, 10);
        }

        private void initPopup() {
            if (popupMenu == null) {
                popupMenu = new JPopupMenu("Popup");    // TODO: change name
            }
            popupMenu.removeAll();
            popupMenu.add(new JMenuItem("Next Tab"));
            popupMenu.add(new JMenuItem("Previous Tab"));
            popupMenu.addSeparator();

            for (ToolWindowTab tab : toolWindow.getToolWindowTab()) {
                popupMenu.add(new SelectTabAction(tab));
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
