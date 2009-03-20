package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.PersistenceDelegate.MergePolicy;
import org.noos.xing.mydoggy.TabbedContentManagerUI.TabPlacement;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FlashingWindowsOnContentRestoreScenario extends JFrame {

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
                    FlashingWindowsOnContentRestoreScenario frame = new FlashingWindowsOnContentRestoreScenario();
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
    public FlashingWindowsOnContentRestoreScenario() {
        super();
        setBounds(100, 100, 500, 375);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        final JMenu fileMenu = new JMenu();
        fileMenu.setText("File");
        menuBar.add(fileMenu);

        final JMenuItem newContentMenu = new JMenuItem();
        newContentMenu.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                addNewContent("" + System.currentTimeMillis());
            }
        });
        newContentMenu.setText("New Content");
        fileMenu.add(newContentMenu);

        fileMenu.addSeparator();

        final JMenuItem saveMenu = new JMenuItem();
        saveMenu.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                onSave();
            }
        });
        saveMenu.setText("Save");
        fileMenu.add(saveMenu);

        final JMenuItem loadMenu = new JMenuItem();
        loadMenu.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                onLoad();
            }
        });
        loadMenu.setText("Load");
        fileMenu.add(loadMenu);
        //
        MyDoggyToolWindowManager myDoggyToolWindowManager = new
                MyDoggyToolWindowManager();
        toolWindowManager = myDoggyToolWindowManager;
        ContentManager contentManager = toolWindowManager.getContentManager();
        MultiSplitContentManagerUI contentManagerUI = new
                MyDoggyMultiSplitContentManagerUI();
        contentManager.setContentManagerUI(contentManagerUI);

        contentManagerUI.setShowAlwaysTab(true);
        contentManagerUI.setTabPlacement(TabPlacement.TOP);

        getContentPane().add(myDoggyToolWindowManager, BorderLayout.CENTER);

        doTheTest();
    }

    protected void onLoad() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("test.xml");
            toolWindowManager.getPersistenceDelegate().merge(inputStream,
                                                             MergePolicy.UNION,
                                                             new PersistenceDelegateCallback() {
                                                                 public Content contentNotFound(ToolWindowManager toolWindowManager, String
                                                                         id,
                                                                                                PersistenceNode node) {
                                                                     Content content = addNewContent(id);
                                                                     return content;
                                                                 }

                                                                 public ToolWindow toolwindowNotFound(ToolWindowManager toolWindowManager,
                                                                                                      String id,
                                                                                                      PersistenceNode node) {
                                                                     return null;
                                                                 }

                                                                 public String validate(PersistenceNode node, String attribute, String
                                                                         attributeValue,
                                                                                        Object attributeDefaultValue) {
                                                                     return attributeValue;
                                                                 }

                                                             });
        } catch (Exception ex) {
        } finally {
//            IOUtils.closeQuietly(inputStream);
        }
    }

    protected void onSave() {
        OutputStream os = null;
        try {
            os = new FileOutputStream("test.xml");
            toolWindowManager.getPersistenceDelegate().save(os);
        } catch (IOException ex) {
        } finally {
//            IOUtils.closeQuietly(os);
        }
    }

    protected Content addNewContent(String name) {
        ContentManager contentManager = toolWindowManager.getContentManager();
        Content content = contentManager.addContent(name, name, null, new JButton("Hello"));
        content.getContentUI().setDetachable(true);
        return content;
    }

    protected void doTheTest() {
        ContentManager contentManager = toolWindowManager.getContentManager();
        // do test here
        for (int index = 0; index < 2; index++) {
            // create content
            addNewContent("" + System.currentTimeMillis());
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);    //To change body of overridden methods use File | Settings | File Templates.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // detach it
                for (Content content : toolWindowManager.getContentManager().getContents()) {
                    content.setDetached(true);
                }
        // save
        onSave();
        for (Content content : toolWindowManager.getContentManager().getContents()) {
            toolWindowManager.getContentManager().removeContent(content);
        }
        // reload it
        onLoad();
            }
        });
    }
}
