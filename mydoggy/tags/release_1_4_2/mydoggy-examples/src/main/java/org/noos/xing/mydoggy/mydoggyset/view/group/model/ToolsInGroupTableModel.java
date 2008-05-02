package org.noos.xing.mydoggy.mydoggyset.view.group.model;

import org.noos.xing.mydoggy.ToolWindow;
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
public class ToolsInGroupTableModel extends DefaultTableModel {
    private ToolWindowGroup windowGroup;
    private List<String> tools;

    public ToolsInGroupTableModel(final ViewContext viewContext) {
        this.tools = new ArrayList<String>();
        setColumnIdentifiers(new Object[]{
                "Tool Id"
        });
        viewContext.addViewContextChangeListener(ToolWindowGroup.class, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                windowGroup = (ToolWindowGroup) evt.getNewValue();
                updateModel();
            }
        });
        viewContext.addViewContextChangeListener(GroupKeySpace.ADD_TOOL, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                windowGroup.addToolWindow(evt.getViewContext().get(ToolWindowManager.class).getToolWindow(
                        evt.getViewContext().get(GroupKeySpace.TOOL_ID)
                ));
                updateModel();
            }
        });
        viewContext.addViewContextChangeListener(GroupKeySpace.REMOVE_TOOL, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                windowGroup.removeToolWindow(evt.getViewContext().get(ToolWindowManager.class).getToolWindow(
                        evt.getViewContext().get(GroupKeySpace.TOOL_IN_GROUP_ID)
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

        if (windowGroup != null)
            for (ToolWindow tool : windowGroup.getToolsWindow()) {
                tools.add(tool.getId());
            }
        fireTableDataChanged();
    }

}
