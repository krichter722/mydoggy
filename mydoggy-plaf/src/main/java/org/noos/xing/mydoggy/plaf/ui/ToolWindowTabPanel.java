package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabPanel extends JPanel implements PropertyChangeListener {
    private ToolWindow toolWindow;
    private TableLayout layout;

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
            if (evt.getNewValue() == Boolean.FALSE) {

            }
        }
    }

    protected void initComponents() {
        setOpaque(false);
        setBorder(null);
        setLayout(layout = new TableLayout(new double[][]{{0},{14}}));

        this.selectedTab = toolWindow.getToolWindowTab()[0];

        initTabs();
    }

    protected void initTabs() {
        for (ToolWindowTab tab : toolWindow.getToolWindowTab()) {
            addTab(tab);    
        }
    }

    protected void addTab(ToolWindowTab tab) {
        int column = layout.getNumColumn();
        layout.insertColumn(column, 5);
        layout.insertColumn(column + 1, -2);
        layout.insertColumn(column + 2, 5);

        add(new TabLabel(tab), (column + 1) + ",0" + ",c,c");

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

    private class TabLabel extends JLabel implements PropertyChangeListener{
        private ToolWindowTab tab;

        public TabLabel(ToolWindowTab tab) {
            super(tab.getTitle());

            this.tab = tab;
            this.tab.addPropertyChangeListener(this);
            
            this.setForeground(Color.LIGHT_GRAY);
            this.setOpaque(false);
            this.setFocusable(false);

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    // TODO: attensionze
                    selectedTab.setSelected(false);
                    TabLabel.this.tab.setSelected(true);
                    selectedTab = TabLabel.this.tab;
                }
            });
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            if ("selected".equals(property)) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    TabLabel.this.setForeground(Color.LIGHT_GRAY);
                } else
                    TabLabel.this.setForeground(Color.WHITE);
            } else if ("title".equals(property)) {
                setText((String) evt.getNewValue());
            } else if ("icon".equals(property)) {
                setIcon((Icon) evt.getNewValue());
            } 
        }
    }

}
