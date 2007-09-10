package org.noos.xing.yasaf.plaf.action;

import org.noos.xing.yasaf.plaf.view.YasafViewContextManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ViewContextAction extends AbstractAction {
    private Object contextKey;
    private Object inContextKey;

    public ViewContextAction(Object contextKey, Object inContextKey) {
        this.contextKey = contextKey;
        this.inContextKey = inContextKey;
    }

    public ViewContextAction(String name, Object contextKey, Object inContextKey) {
        super(name);
        this.contextKey = contextKey;
        this.inContextKey = inContextKey;
    }

    public ViewContextAction(String name, Icon icon, Object contextKey, Object inContextKey) {
        super(name, icon);
        this.contextKey = contextKey;
        this.inContextKey = inContextKey;
    }

    public void actionPerformed(ActionEvent e) {
        YasafViewContextManager.getInstance().getViewContext(contextKey).put(inContextKey, e);
    }
}
