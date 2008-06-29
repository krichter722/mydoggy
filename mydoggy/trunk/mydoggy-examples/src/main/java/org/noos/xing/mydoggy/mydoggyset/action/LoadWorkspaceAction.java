package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.mydoggyset.MyDoggySet;
import org.noos.xing.mydoggy.mydoggyset.context.MyDoggySetContext;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class LoadWorkspaceAction extends AbstractAction {
    protected Component parentComponent;
    protected ToolWindowManager toolWindowManager;
    protected ViewContext myDoggySetContext;

    public LoadWorkspaceAction(ViewContext myDoggySetContext, Component parentComponent, ToolWindowManager toolWindowManager) {
        super("Load Workspace");
        this.myDoggySetContext = myDoggySetContext;
        this.parentComponent = parentComponent;
        this.toolWindowManager = toolWindowManager;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            File workspaceFile = new File("workspace.xml");
            if (workspaceFile.exists()) {
                FileInputStream inputStream = new FileInputStream("workspace.xml");
                toolWindowManager.getPersistenceDelegate().merge(
                        inputStream,
                        PersistenceDelegate.MergePolicy.RESET,
                        new PersistenceDelegateCallback() {
                            public Content contentNotFound(ToolWindowManager toolWindowManager, String contentId) {
                                if ("Welcome".equals(contentId))
                                    myDoggySetContext.put(MyDoggySet.class, null);
                                else if ("Manager".equals(contentId))
                                    myDoggySetContext.put(ToolWindowManager.class, null);
                                else if ("Tools".equals(contentId))
                                    myDoggySetContext.put(ToolWindow.class, null);
                                else if ("Groups".equals(contentId))
                                    myDoggySetContext.put(ToolWindowGroup.class, null);
                                else if ("Contents".equals(contentId))
                                    myDoggySetContext.put(Content.class, null);
                                else if ("ITests".equals(contentId))
                                    myDoggySetContext.put(InteractiveTest.class, null);
                                else if ("Customize".equals(contentId))
                                    myDoggySetContext.put(ResourceManager.class, null);
                                else if ("Nested Manager".equals(contentId))
                                    myDoggySetContext.put(MyDoggySetContext.ActionKey.NEST_TOOLMANAGER, null);

                                return toolWindowManager.getContentManager().getContent(contentId);
                            }
                        });
                inputStream.close();
            } else
                JOptionPane.showMessageDialog(parentComponent,
                                              "You must save the workspace before the load.");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
