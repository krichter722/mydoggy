package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class LoadWorkspaceAction extends AbstractAction {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    public LoadWorkspaceAction(JFrame frame, ToolWindowManager toolWindowManager) {
        super("Load Workspace");
        this.frame = frame;
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
                JOptionPane.showMessageDialog(frame,
                                              "You must save the workspace before the load.");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
