package org.noos.xing.mydoggy.mydoggyset.view.group.model;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsInGroupTableModel extends DefaultTableModel {
    private ToolWindowGroup windowGroup;
    private List tools;

    public ToolsInGroupTableModel(final ViewContext viewContext) {
        this.tools = new ArrayList();
        setColumnIdentifiers(new Object[]{
                "Tool Id"
        });
        viewContext.addViewContextChangeListener(ToolWindowGroup.class, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                windowGroup = (ToolWindowGroup) evt.getNewValue();
                updateModel();
            }
        });
        viewContext.addViewContextChangeListener("addTool", new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                windowGroup.addToolWindow(evt.getViewContext().get(ToolWindowManager.class).getToolWindow(
                        evt.getViewContext().get("toolId")
                ));
                updateModel();
            }
        });
        viewContext.addViewContextChangeListener("removeTool", new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                windowGroup.removeToolWindow(evt.getViewContext().get(ToolWindowManager.class).getToolWindow(
                        evt.getViewContext().get("toolInGroupId")
                ));
                updateModel();
            }
        });
    }

    public int getRowCount() {
        return (tools != null) ? tools.size() : 0;
    }

    public Object getValueAt(int row, int column) {
        return tools.get(row);
    }

    protected void updateModel() {
        tools.clear();
        for (ToolWindow tool : windowGroup.getToolsWindow()) {
            tools.add(tool.getId());
        }
        fireTableDataChanged();
    }

}
