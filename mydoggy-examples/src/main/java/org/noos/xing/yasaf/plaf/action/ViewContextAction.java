package org.noos.xing.yasaf.plaf.action;

import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ViewContextAction extends AbstractAction {
    private ViewContext viewContext;
    private Object key;

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

    public void actionPerformed(ActionEvent e) {
        viewContext.put(key, e);
    }
}
