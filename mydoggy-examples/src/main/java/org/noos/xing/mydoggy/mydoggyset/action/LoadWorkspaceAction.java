package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class LoadWorkspaceAction extends AbstractAction {
    private Component parentComponent;
    private ToolWindowManager toolWindowManager;

    public LoadWorkspaceAction(Component parentComponent, ToolWindowManager toolWindowManager) {
        super("Load Workspace");
        this.parentComponent = parentComponent;
        this.toolWindowManager = toolWindowManager;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            File workspaceFile = new File("workspace.xml");
            if (workspaceFile.exists()) {
                FileInputStream inputStream = new FileInputStream("workspace.xml");
                toolWindowManager.getPersistenceDelegate().apply(inputStream);
                inputStream.close();
            } else
                JOptionPane.showMessageDialog(parentComponent,
                                              "You must save the workspace before the load.");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
