package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.MultiSplitContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentManagerUI.TabPlacement;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PopupMenuContentScenario extends JFrame {

    private ToolWindowManager toolWindowManager;

    /**
     * Launch the application
     *
     * @param args
     */
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PopupMenuContentScenario frame = new PopupMenuContentScenario();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame
     */
    public PopupMenuContentScenario() {
        setBounds(100, 100, 500, 375);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        toolWindowManager = myDoggyToolWindowManager;
        ContentManager contentManager = toolWindowManager.getContentManager();
        MultiSplitContentManagerUI contentManagerUI = new MyDoggyMultiSplitContentManagerUI();
        contentManager.setContentManagerUI(contentManagerUI);

        contentManagerUI.setShowAlwaysTab(true);
        contentManagerUI.setTabPlacement(TabPlacement.TOP);

        getContentPane().add(myDoggyToolWindowManager, BorderLayout.CENTER);

        JPopupMenu popupMenu = new JPopupMenu("CPM");
        popupMenu.add("1");
        popupMenu.add("2");
        popupMenu.add("3");

        Content content = contentManager.addContent("Hello", "Hello", null, new JButton("Hello"));
//        content.setPopupMenu(popupMenu);

        contentManager.setPopupMenu(popupMenu);
    }

}