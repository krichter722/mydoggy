package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class StoreWorkspaceAction extends AbstractAction {
    private Component parentComponent;
    private ToolWindowManager toolWindowManager;

    public StoreWorkspaceAction(Component parentComponent, ToolWindowManager toolWindowManager) {
        super("Store Workspace");
        this.parentComponent = parentComponent;
        this.toolWindowManager = toolWindowManager;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            FileOutputStream output = new FileOutputStream("workspace.xml");
            toolWindowManager.getPersistenceDelegate().save(output);
            output.close();
            JOptionPane.showMessageDialog(parentComponent, "Workspace saved to 'workspace.xml'.");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}