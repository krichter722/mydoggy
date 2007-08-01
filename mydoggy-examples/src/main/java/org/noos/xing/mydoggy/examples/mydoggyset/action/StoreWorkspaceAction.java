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
public class StoreWorkspaceAction extends AbstractAction {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    public StoreWorkspaceAction(JFrame frame, ToolWindowManager toolWindowManager) {
        super("Store Workspace");
        this.frame = frame;
        this.toolWindowManager = toolWindowManager;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            FileOutputStream output = new FileOutputStream("workspace.xml");
            toolWindowManager.getPersistenceDelegate().save(output);
            output.close();
            JOptionPane.showMessageDialog(frame, "Workspace saved to 'workspace.xml'.");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}