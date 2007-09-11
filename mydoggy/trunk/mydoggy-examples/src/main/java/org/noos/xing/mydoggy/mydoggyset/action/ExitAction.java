package org.noos.xing.mydoggy.mydoggyset.action;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ExitAction extends AbstractAction {
    private JFrame frame;

    public ExitAction(JFrame frame) {
        super("Exit");
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent e) {
        frame.setVisible(false);
        frame.dispose();
    }
}