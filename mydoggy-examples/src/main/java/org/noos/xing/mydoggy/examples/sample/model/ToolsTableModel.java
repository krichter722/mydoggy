package org.noos.xing.mydoggy.examples.sample.model;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.table.DefaultTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsTableModel extends DefaultTableModel implements PropertyChangeListener {
    private ToolWindowManager windowManager;

    public ToolsTableModel(ToolWindowManager windowManager) {
        this.windowManager = windowManager;
        setColumnIdentifiers(new Object[]{
                "Id", "Title", "Type", "Anchor", "Available", "Visible", "Active"
        });
        initToolsListeners();
        updateModel();
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateModel();
    }


    protected void initToolsListeners() {
        ToolWindow[] toolWindows = windowManager.getToolWindows();
        for (ToolWindow toolWindow : toolWindows) {
            toolWindow.addPropertyChangeListener(this);
        }
    }

    protected void updateModel() {
        getDataVector().clear();

        ToolWindow[] toolWindows = windowManager.getToolWindows();
        for (ToolWindow toolWindow : toolWindows) {
            addRow(new Object[]{
                    toolWindow.getId(), toolWindow.getTitle(), toolWindow.getType(), toolWindow.getAnchor(),
                    toolWindow.isAvailable(), toolWindow.isVisible(), toolWindow.isActive()
            });
        }

        fireTableDataChanged();
    }

}
