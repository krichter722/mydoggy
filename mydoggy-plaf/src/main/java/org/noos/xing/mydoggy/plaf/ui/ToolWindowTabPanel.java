package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabPanel extends JPanel {
    private ToolWindow toolWindow;
    private TableLayout layout;

    public ToolWindowTabPanel(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;

        initComponents();
        initListeners();
    }

    protected void initComponents() {
        setOpaque(false);
        setBorder(null);
        setLayout(layout = new TableLayout(new double[][]{{-1},{14}}));

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
    }

    protected void removeTab(ToolWindowTab toolWindowTab) {
    }

    protected void initListeners() {
        toolWindow.addToolWindowListener(new ToolWindowListener() {
            public void toolWindowTabAdded(ToolWindowTabEvent event) {
                addTab(event.getToolWindowTab());
            }

            public void toolWindowTabRemoved(ToolWindowTabEvent event) {
                removeTab(event.getToolWindowTab());
            }
        });
    }

    private static class TabLabel extends JLabel {
        private ToolWindowTab tab;

        public TabLabel(ToolWindowTab tab) {
            super(tab.getTitle());
            
            this.tab = tab;
            this.setForeground(Color.LIGHT_GRAY);
            this.setOpaque(false);
            this.setFocusable(false);            
        }
    }

}
