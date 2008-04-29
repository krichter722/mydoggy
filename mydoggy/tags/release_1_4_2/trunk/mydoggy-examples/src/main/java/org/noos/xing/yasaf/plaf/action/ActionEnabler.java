package org.noos.xing.yasaf.plaf.action;

import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class ActionEnabler implements ViewContextChangeListener {
    protected Action action;

    public ActionEnabler(Action action) {
        this.action = action;
        init();
    }

    public ActionEnabler(Action action, ViewContext viewContext, Object contextId) {
        this.action = action;
        viewContext.addViewContextChangeListener(contextId, this);
        init();
    }

    public Action getAction() {
        return action;
    }

    protected void init() {
    }
}
