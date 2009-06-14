package org.noos.xing.mydoggy.scenarioset.action;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.persistence.PersistenceDelegateCallbackAdapter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class LoadWorkspaceAction extends AbstractAction {
    protected JFrame frame;
    protected ToolWindowManager toolWindowManager;

    public LoadWorkspaceAction(JFrame frame, ToolWindowManager toolWindowManager) {
        super("Load Workspace");
        this.toolWindowManager = toolWindowManager;
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent event) {
        File file = getFileSelection("load");
        if (file == null)
            return;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            toolWindowManager.getPersistenceDelegate().merge(fileInputStream, PersistenceDelegate.MergePolicy.RESET,
                                                             new PersistenceDelegateCallbackAdapter(){
                                                                 @Override
                                                                 public ToolWindow toolwindowNotFound(ToolWindowManager toolWindowManager, String toolWindowId, PersistenceNode node) {
                                                                     return toolWindowManager.registerToolWindow(toolWindowId, toolWindowId, null, new JButton(toolWindowId), ToolWindowAnchor.LEFT);
                                                                 }

                                                                 @Override
                                                                 public Content contentNotFound(ToolWindowManager toolWindowManager, String contentId, PersistenceNode node) {
                                                                     return toolWindowManager.getContentManager().addContent(contentId, contentId, null, new JButton(contentId));
                                                                 }
                                                             });

            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected File getFileSelection(String action) {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        if (fileChooser.showDialog(frame, action) == JFileChooser.CANCEL_OPTION)
            return null;
        return fileChooser.getSelectedFile();
    }

}
