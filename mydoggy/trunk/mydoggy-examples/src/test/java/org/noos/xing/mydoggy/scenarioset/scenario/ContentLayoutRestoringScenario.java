package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.TabbedContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentManagerUI.TabLayout;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ContentLayoutRestoringScenario {
    private static MyDoggyToolWindowManager init() {
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();

        initContentManager(myDoggyToolWindowManager);

        loadLayout(myDoggyToolWindowManager);

        return myDoggyToolWindowManager;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MyDoggy Test");
        final MyDoggyToolWindowManager myDoggyToolWindowManager = init();
        frame.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
                System.out.println("[pa] #windowClosed");
                saveLayout(myDoggyToolWindowManager);
            }

            public void windowClosing(WindowEvent e) {
                System.out.println("[pa] #windowClosing");
                saveLayout(myDoggyToolWindowManager);
            }

            public void windowDeactivated(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowOpened(WindowEvent e) {
            }
        });
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(myDoggyToolWindowManager, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(500, 500));
        frame.setVisible(true);
    }

    private static void initContentManager(MyDoggyToolWindowManager myDoggyToolWindowManager) {
        ContentManager contentManager = myDoggyToolWindowManager.getContentManager();
        TabbedContentManagerUI contentManagerUI = new MyDoggyMultiSplitContentManagerUI();
        contentManager.setContentManagerUI(contentManagerUI);
        contentManagerUI.setPopupMenuEnabled(false);
        contentManagerUI.setMinimizable(false);
        contentManagerUI.setMaximizable(false);
        contentManagerUI.setDetachable(true);
        contentManagerUI.setCloseable(true);
        contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.TOP);
        contentManagerUI.setShowAlwaysTab(true);
        contentManagerUI.setTabLayout(TabLayout.WRAP);

        for (int index = 0; index < 3; index++) {
            String contentId = "Content_" + index;
            Content content = contentManager.addContent(contentId, contentId, null, new JPanel());
            content.getContentUI().setDetachable(true);
        }
    }

    private static void loadLayout(MyDoggyToolWindowManager myDoggyToolWindowManager) {
        FileInputStream inputStream = null;
        try {
            String fileName = "Layout.xml";

            File file = new File(fileName);
            inputStream = new FileInputStream(file);
            if (inputStream.available() > 0) {
                myDoggyToolWindowManager.getPersistenceDelegate().apply(inputStream);
            }
        }
        catch (Exception e1) {
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                }
                catch (IOException e) {
                }
            }
        }
    }

    private static void saveLayout(MyDoggyToolWindowManager myDoggyToolWindowManager) {
        FileOutputStream output = null;
        String fileName = null;
        try {
            fileName = "Layout.xml";
            output = new FileOutputStream(fileName);
            myDoggyToolWindowManager.getPersistenceDelegate().save(output);
        }
        catch (Exception e1) {
        }
        finally {
            if (output != null) {
                try {
                    output.close();
                    output = null;
                }
                catch (IOException e) {
                }
            }
        }
    }
}
