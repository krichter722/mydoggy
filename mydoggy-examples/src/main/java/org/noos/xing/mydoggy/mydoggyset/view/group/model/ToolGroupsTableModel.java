package org.noos.xing.mydoggy.mydoggyset.view.group.model;

import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.view.group.GroupKeySpace;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolGroupsTableModel extends DefaultTableModel implements ViewContextChangeListener {
    protected ToolWindowManager windowManager;
    protected List<Object[]> groups;

    public ToolGroupsTableModel(ViewContext viewContext) {
        this.windowManager = viewContext.get(ToolWindowManager.class);
        this.groups = new ArrayList<Object[]>();
        setColumnIdentifiers(new Object[]{
                "Name", "Implicit"
        });
        updateModel();

        viewContext.addViewContextChangeListener(GroupKeySpace.REFRESH_GROUPS, this);
    }

    public void contextChange(ViewContextChangeEvent evt) {
        updateModel();
    }

    public int getRowCount() {
        return (groups != null) ? groups.size() : 0; 
    }

    public boolean isCellEditable(int row, int column) {
        return (column == 1);
    }

    public Object getValueAt(int row, int column) {
        if (column == -1)
            return groups.get(row)[2];
        return groups.get(row)[column];
    }

    public void setValueAt(Object aValue, int row, int column) {
        groups.get(row)[column] = aValue;
        if (column == 1) {
            windowManager.getToolWindowGroups()[row].setImplicit(
                    (Boolean) aValue 
            );
        }
        updateModel();
    }

    protected void updateModel() {
        ToolWindowGroup[] toolWindowsGroups = windowManager.getToolWindowGroups();

        groups.clear();
        for (ToolWindowGroup toolWindowGroup : toolWindowsGroups) {
            groups.add(new Object[]{
                    toolWindowGroup.getName(),
                    toolWindowGroup.isImplicit(),
                    toolWindowGroup
            });
        }

        fireTableDataChanged();
    }

}
