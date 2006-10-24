package org.noos.xing.mydoggy.examples.mydoggyset.model;

import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.table.DefaultTableModel;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolGroupsTableModel extends DefaultTableModel {
    private ToolWindowManager windowManager;

    public ToolGroupsTableModel(ToolWindowManager windowManager) {
        this.windowManager = windowManager;
        setColumnIdentifiers(new Object[]{
                "Name", "#Tools"
        });
        updateModel();
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    protected void updateModel() {
        getDataVector().clear();

        ToolWindowGroup[] toolWindowsGroups = windowManager.getToolWindowGroups();
        for (ToolWindowGroup toolWindowGroup : toolWindowsGroups) {
            addRow(new Object[]{
                    toolWindowGroup.getName(), toolWindowGroup.getToolsWindow().length
            });
        }

        fireTableDataChanged();
    }

}
