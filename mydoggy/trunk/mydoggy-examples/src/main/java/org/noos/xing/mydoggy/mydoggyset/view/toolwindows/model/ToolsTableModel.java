package org.noos.xing.mydoggy.mydoggyset.view.toolwindows.model;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public final class ToolsTableModel extends DefaultTableModel implements PropertyChangeListener {
    protected ToolWindowManager windowManager;
    protected ToolWindow[] toolWindows;

    public ToolsTableModel(ToolWindowManager windowManager) {
        this.windowManager = windowManager;
        setColumnIdentifiers(new Object[]{
                "Id", "Title", "Type", "Anchor", "Available", "Visible", "Active", "Index", "Flashing"
        });
        initToolsListeners();
        updateModel();
    }

    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    public int getRowCount() {
        return toolWindows != null ? toolWindows.length : 0;
    }

    public void setValueAt(final Object aValue, final int row, int column) {
        switch (column) {
            case 1:
                toolWindows[row].setTitle((String) aValue);
                break;
            case 2:
                toolWindows[row].setType((ToolWindowType) aValue);
                break;
            case 3:
                toolWindows[row].setAnchor((ToolWindowAnchor) aValue);
                break;
            case 4:
                toolWindows[row].setAvailable((Boolean) aValue);
                break;
            case 5:
                toolWindows[row].setVisible((Boolean) aValue);
                break;
            case 6:
                toolWindows[row].setActive((Boolean) aValue);
                break;
            case 7:
                toolWindows[row].setIndex((Integer) aValue);
                break;
            case 8:
                toolWindows[row].setFlashing((Boolean) aValue);
                break;
        }
        fireTableChanged(new TableModelEvent(this, row));
    }

    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return toolWindows[row].getId();

            case 1:
                return toolWindows[row].getTitle();

            case 2:
                return toolWindows[row].getType();

            case 3:
                return toolWindows[row].getAnchor();

            case 4:
                return toolWindows[row].isAvailable();

            case 5:
                return toolWindows[row].isVisible();

            case 6:
                return toolWindows[row].isActive();

            case 7:
                return toolWindows[row].getIndex();

            case 8:
                return toolWindows[row].isFlashing();

            default:
                return toolWindows[row];
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateModel();

        ToolWindow toolWindow = (ToolWindow) evt.getSource();
        if (toolWindows != null) {
            for (int i = 0; i < toolWindows.length; i++) {
                if (toolWindow == toolWindows[i]) {
                    fireTableRowsUpdated(i, i);
                    return;
                }
            }
        }

    }

    protected void initToolsListeners() {
        windowManager.addToolWindowManagerListener(new ToolWindowManagerListener() {
            public void toolWindowRegistered(ToolWindowManagerEvent event) {
                event.getToolWindow().addPropertyChangeListener(ToolsTableModel.this);
            }

            public void toolWindowUnregistered(ToolWindowManagerEvent event) {
                event.getToolWindow().removePropertyChangeListener(ToolsTableModel.this);
            }

            public void toolWindowGroupAdded(ToolWindowManagerEvent event) {
            }

            public void toolWindowGroupRemoved(ToolWindowManagerEvent event) {
            }
        });
        ToolWindow[] toolWindows = windowManager.getToolWindows();
        for (ToolWindow toolWindow : toolWindows) {
            toolWindow.addPropertyChangeListener(this);
        }
    }

    protected void updateModel() {
        int oldSize = (toolWindows != null) ? toolWindows.length : 0;
        toolWindows = windowManager.getToolWindows();
        if (oldSize == toolWindows.length)
            return;

        fireTableDataChanged();
    }

}
