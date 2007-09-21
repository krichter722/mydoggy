package org.noos.xing.mydoggy.mydoggyset.view.group.model;

import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.table.DefaultTableModel;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsInGroupTableModel extends DefaultTableModel {
    private final ToolWindowManager windowManager;

    public ToolsInGroupTableModel(ToolWindowManager windowManager) {
        this.windowManager = windowManager;
        setColumnIdentifiers(new Object[]{
                "Tool Id"
        });
    }

}
