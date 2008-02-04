package org.noos.xing.yasaf.plaf.action;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ActionAdapter extends AbstractAction {
    protected ActionListener actionListener;

    public ActionAdapter(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public ActionAdapter(String name, ActionListener actionListener) {
        super(name);
        this.actionListener = actionListener;
    }

    public ActionAdapter(String name, Icon icon, ActionListener actionListener) {
        super(name, icon);
        this.actionListener = actionListener;
    }

    public void actionPerformed(ActionEvent e) {
        actionListener.actionPerformed(e);
    }
}
