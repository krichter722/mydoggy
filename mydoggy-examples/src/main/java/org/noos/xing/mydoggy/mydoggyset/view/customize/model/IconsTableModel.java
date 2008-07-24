package org.noos.xing.mydoggy.mydoggyset.view.customize.model;

import org.noos.xing.mydoggy.plaf.ui.ResourceManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class IconsTableModel extends DefaultTableModel {
    protected ResourceManager resourceManager;

    public IconsTableModel(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        setColumnIdentifiers(new Object[]{"Icon Key", "Icon"});
        updateModel();
    }

    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);

        resourceManager.putColor((String) getValueAt(row, 0), (Color) aValue);
    }

    protected void updateModel() {
        getDataVector().clear();
        java.util.List<String> icons = resourceManager.getIcons();

        String[] keys = icons.toArray(new String[icons.size()]);
        Arrays.sort(keys);

        for (String key : keys) {
            addRow(new Object[]{key, UIManager.getIcon(key)});
        }
        fireTableDataChanged();
    }

}