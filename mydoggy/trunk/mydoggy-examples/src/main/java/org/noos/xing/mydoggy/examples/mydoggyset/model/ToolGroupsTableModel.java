package org.noos.xing.mydoggy.examples.mydoggyset.model;

import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.table.DefaultTableModel;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public final class ToolGroupsTableModel extends DefaultTableModel {
    private final ToolWindowManager windowManager;

    public ToolGroupsTableModel(ToolWindowManager windowManager) {
        this.windowManager = windowManager;
        setColumnIdentifiers(new Object[]{
                "Name", "#Tools", "Implicit"
        });
        updateModel();
    }

    public boolean isCellEditable(int row, int column) {
        return (column == 2);
    }

    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
        if (column == 2) {
            windowManager.getToolWindowGroups()[row].setImplicit(
                    (Boolean) aValue 
            );
        }
        updateModel();
    }

    protected void updateModel() {
        getDataVector().clear();

        ToolWindowGroup[] toolWindowsGroups = windowManager.getToolWindowGroups();
        for (ToolWindowGroup toolWindowGroup : toolWindowsGroups) {
            addRow(new Object[]{
                    toolWindowGroup.getName(),
                    toolWindowGroup.getToolsWindow().length,
                    toolWindowGroup.isImplicit(),
            });
        }

        fireTableDataChanged();
    }

}
