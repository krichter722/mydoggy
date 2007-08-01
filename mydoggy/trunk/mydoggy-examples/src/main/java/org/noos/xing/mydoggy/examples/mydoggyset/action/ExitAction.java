package org.noos.xing.mydoggy.examples.mydoggyset.action;

import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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