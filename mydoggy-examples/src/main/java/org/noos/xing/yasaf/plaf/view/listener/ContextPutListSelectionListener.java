package org.noos.xing.yasaf.plaf.view.listener;

import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContextPutListSelectionListener implements ListSelectionListener {
    private ViewContext viewContext;
    private Object key;
    private JList list;

    public ContextPutListSelectionListener(ViewContext viewContext, Object key, JList list) {
        this.viewContext = viewContext;
        this.key = key;
        this.list = list;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (list.getSelectedIndex() != -1)
            viewContext.put(key, list.getSelectedValue());
        else
            viewContext.put(key, null);
    }

}