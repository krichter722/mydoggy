package org.noos.xing.yasaf.plaf.view.listener;

import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContextPutTableListSelectionListener implements ListSelectionListener {
    private ViewContext viewContext;
    private Object key;
    private int column;
    private JTable table;

    public ContextPutTableListSelectionListener(ViewContext viewContext, Object key, JTable table, int column) {
        this.viewContext = viewContext;
        this.key = key;
        this.table = table;
        this.column = column;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (table.getSelectedRow() != -1)
            viewContext.put(key, table.getValueAt(table.getSelectedRow(), column));
        else
            viewContext.put(key, null);
    }

}