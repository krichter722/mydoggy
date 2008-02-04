package org.noos.xing.mydoggy.mydoggyset.view.customize.model;

import org.noos.xing.mydoggy.plaf.ui.ResourceManager;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

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
        Map<String, Icon> colorMap = resourceManager.getIcons();

        String[] keys = colorMap.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        for (String key : keys) {
            addRow(new Object[]{key, colorMap.get(key)});
        }
        fireTableDataChanged();
    }

}