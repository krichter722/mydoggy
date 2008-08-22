package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.*;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class LoadWorkspaceApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
//                <GEOMETRY X="107" Y="131" WIDTH="1065" HEIGHT="912" EXT_STATE="6" />

//                frame.setLocation(107,131);
//                frame.setSize(1065,912);
                frame.setSize(640,480);

                final MyDoggyToolWindowManager manager = new MyDoggyToolWindowManager();

                frame.getContentPane().add(manager);

                frame.setVisible(true);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            manager.getPersistenceDelegate().merge(
                                new FileInputStream("1.xml"),
                                PersistenceDelegate.MergePolicy.RESET,
                                    new PersistenceDelegateCallback() {
                                        public ToolWindow toolwindowNotFound(ToolWindowManager toolWindowManager, String toolWindowId, PersistenceNode node) {
                                            return toolWindowManager.registerToolWindow(toolWindowId, toolWindowId + " Title", null, new JButton("H"), ToolWindowAnchor.LEFT);
                                        }

                                        public Content contentNotFound(ToolWindowManager toolWindowManager, String contentId, PersistenceNode node) {
                                            return null;
                                        }
                                    }
                            );
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });




    }

}
