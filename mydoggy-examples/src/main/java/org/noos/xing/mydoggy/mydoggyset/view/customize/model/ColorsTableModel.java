package org.noos.xing.mydoggy.mydoggyset.view.customize.model;

import org.noos.xing.mydoggy.plaf.ui.ResourceManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ColorsTableModel extends DefaultTableModel {
    protected ResourceManager resourceManager;

    public ColorsTableModel(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        setColumnIdentifiers(new Object[]{"Color Key", "Color"});
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

        java.util.List<String> colors = resourceManager.getColors();

        String[] keys = colors.toArray(new String[colors.size()]);
        Arrays.sort(keys);

        for (String key : keys) {
            addRow(new Object[]{key, UIManager.getColor(key)});
        }
        fireTableDataChanged();
    }

}