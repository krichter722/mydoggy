package org.noos.xing.yasaf.plaf.action;

import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ViewContextAction extends AbstractAction implements ViewContextChangeListener {
    private ViewContext viewContext;
    private Object key;
    private Object enableKey;

    public ViewContextAction(ViewContext viewContext, Object key) {
        this.viewContext = viewContext;
        this.key = key;
    }

    public ViewContextAction(String name, ViewContext viewContext, Object key) {
        super(name);
        this.viewContext = viewContext;
        this.key = key;
    }

    public ViewContextAction(String name, Icon icon, ViewContext viewContext, Object key) {
        super(name, icon);
        this.viewContext = viewContext;
        this.key = key;
    }

    public ViewContextAction(String name, Icon icon, ViewContext viewContext, Object key, Object enableKey) {
        super(name, icon);
        this.viewContext = viewContext;
        this.key = key;
        this.enableKey = enableKey;
        if (enableKey != null) {
            viewContext.addViewContextChangeListener(enableKey, this);
            setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        viewContext.put(key, e);
    }

    public void contextChange(ViewContextChangeEvent evt) {
        setEnabled(evt.getNewValue() != null);
    }

}
