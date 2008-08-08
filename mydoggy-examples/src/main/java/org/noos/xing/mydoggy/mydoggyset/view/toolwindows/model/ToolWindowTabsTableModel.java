package org.noos.xing.mydoggy.mydoggyset.view.toolwindows.model;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public final class ToolWindowTabsTableModel extends DefaultTableModel implements PropertyChangeListener,
                                                                                 ToolWindowListener {
    protected ToolWindow toolWindow;
    protected ToolWindowTab[] toolWindowTabs;


    public ToolWindowTabsTableModel() {
        setColumnIdentifiers(new Object[]{"Owner", "Title", "Flashing"});
    }


    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    public int getRowCount() {
        return toolWindowTabs != null ? toolWindowTabs.length : 0;
    }

    public void setValueAt(final Object aValue, final int row, int column) {
        switch (column) {
            case 1:
                toolWindowTabs[row].setTitle((String) aValue);
                break;
            case 2:
                toolWindowTabs[row].setFlashing((Boolean) aValue);
                break;
        }

        fireTableChanged(new TableModelEvent(this, row));
    }

    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return toolWindowTabs[row].getOwner().getId();

            case 1:
                return toolWindowTabs[row].getTitle();

            case 2:
                return toolWindowTabs[row].isFlashing();

            default:
                return toolWindowTabs[row];
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateModel();

        ToolWindowTab toolWindowTab = (ToolWindowTab) evt.getSource();
        if (toolWindowTabs != null) {
            for (int i = 0; i < toolWindowTabs.length; i++) {
                if (toolWindowTab == toolWindowTabs[i]) {
                    fireTableRowsUpdated(i, i);
                    return;
                }
            }
        }

    }


    public void toolWindowTabAdded(ToolWindowTabEvent event) {
        event.getToolWindowTab().addPropertyChangeListener(this);

        updateModel();
    }

    public boolean toolWindowTabRemoving(ToolWindowTabEvent event) {
        return true;
    }

    public void toolWindowTabRemoved(ToolWindowTabEvent event) {
        event.getToolWindowTab().removePropertyChangeListener(this);

        updateModel();
    }


    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public void setToolWindow(ToolWindow toolWindow) {
        unistallListeners();

        this.toolWindow = toolWindow;

        installListeners();
        updateModel();
    }


    protected void installListeners() {
        if (toolWindow != null) {
            toolWindow.addToolWindowListener(this);

            for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                tab.addPropertyChangeListener(this);
            }
        }
    }

    protected void unistallListeners() {
        if (toolWindow != null) {
            toolWindow.removeToolWindowListener(this);

            for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                tab.removePropertyChangeListener(this);
            }
        }
    }

    protected void updateModel() {
        int oldSize = (toolWindowTabs != null) ? toolWindowTabs.length : 0;
        toolWindowTabs = (toolWindow != null) ? toolWindow.getToolWindowTabs() : new ToolWindowTab[0];
        if (oldSize == toolWindowTabs.length)
            return;

        fireTableDataChanged();
    }

}