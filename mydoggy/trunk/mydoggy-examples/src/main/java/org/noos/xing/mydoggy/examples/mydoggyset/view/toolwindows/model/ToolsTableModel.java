package org.noos.xing.mydoggy.examples.mydoggyset.view.toolwindows.model;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;

import javax.swing.table.DefaultTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public final class ToolsTableModel extends DefaultTableModel implements PropertyChangeListener {
    private final ToolWindowManager windowManager;

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

    public void setValueAt(Object aValue, int row, int column) {
        switch (column) {
            case 1:
                windowManager.getToolWindow(
                        getValueAt(row, 0)
                ).setTitle((String) aValue);
                break;
            case 2:
                windowManager.getToolWindow(
                        getValueAt(row, 0)
                ).setType((ToolWindowType) aValue);
                break;
            case 3:
                windowManager.getToolWindow(
                        getValueAt(row, 0)
                ).setAnchor((ToolWindowAnchor) aValue);
                break;
            case 4:
                windowManager.getToolWindow(
                        getValueAt(row, 0)
                ).setAvailable((Boolean) aValue);
                break;
            case 5:
                windowManager.getToolWindow(
                        getValueAt(row, 0)
                ).setVisible((Boolean) aValue);
                break;
            case 6:
                windowManager.getToolWindow(
                        getValueAt(row, 0)
                ).setActive((Boolean) aValue);
                break;
            case 7:
                windowManager.getToolWindow(
                        getValueAt(row, 0)
                ).setIndex((Integer) aValue);
                break;
            case 8:
                windowManager.getToolWindow(
                        getValueAt(row, 0)
                ).setFlashing((Boolean) aValue);
                break;
        }
    }

    public Object getValueAt(int row, int column) {
        if (column == -1)
            return windowManager.getToolWindow(getValueAt(row, 0));

        return super.getValueAt(row, column);

    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateModel();
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
        dataVector.clear();

        ToolWindow[] toolWindows = windowManager.getToolWindows();
        for (ToolWindow toolWindow : toolWindows) {
            dataVector.add(convertToVector(new Object[]{
                    toolWindow.getId(), toolWindow.getTitle(), toolWindow.getType(), toolWindow.getAnchor(),
                    toolWindow.isAvailable(), toolWindow.isVisible(), toolWindow.isActive(),
                    toolWindow.getIndex(), toolWindow.isFlashing()
            }));
        }

        fireTableDataChanged();
    }

}