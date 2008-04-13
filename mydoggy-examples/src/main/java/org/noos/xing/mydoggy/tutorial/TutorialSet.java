package org.noos.xing.mydoggy.tutorial;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TutorialSet {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() {
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("Sample App...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a simple JMenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        this.frame.setJMenuBar(menuBar);

        this.frame.getContentPane().setLayout(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
    }

    protected void initToolWindowManager() {
        // Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        this.toolWindowManager = myDoggyToolWindowManager;

        // Register a Tool.
        toolWindowManager.registerToolWindow("Debug",                      // Id
                "Debug Tool",                 // Title
                null,                         // Icon
                new JButton("Debug Tool"),    // Component
                ToolWindowAnchor.LEFT);       // Anchor

        // Register another Tool.
        toolWindowManager.registerToolWindow("Run",                      // Id
                "Run Tool",                 // Title
                null,                       // Icon
                new JButton("Run Tool"),    // Component
                ToolWindowAnchor.LEFT);     // Anchor

        // Made all tools available
        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);

        initContentManager();

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,FULL,FULL");
    }


    protected void initContentManager() {
        ContentManager contentManager = toolWindowManager.getContentManager();
        final Content content = contentManager.addContent("Tree1", "Tree1", null, new JButton("Help"));
        content.addPropertyChangeListener("detached", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ((Boolean)evt.getNewValue()) {
                    RootPaneContainer rootPaneContainer = (RootPaneContainer) SwingUtilities.getWindowAncestor(content.getComponent());

                    ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");
                    debugTool.setAvailable(false);

                    // Init new manager
                    MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();

                    // Import debug tool...but tabs???
                    myDoggyToolWindowManager.registerToolWindow(
                            debugTool.getId(), debugTool.getTitle(), debugTool.getIcon(),
                            debugTool.getComponent(), debugTool.getAnchor()
                    );

                    // Made all tools available
                    for (ToolWindow window : myDoggyToolWindowManager.getToolWindows())
                        window.setAvailable(true);

                    // Import content...
                    myDoggyToolWindowManager.getContentManager().addContent(
                            content.getId(), content.getTitle(), content.getIcon(),
                            content.getComponent(), content.getToolTipText()
                    );

                    Container container = rootPaneContainer.getContentPane();
                    container.removeAll();
                    container.add(myDoggyToolWindowManager, "0,0,FULL,FULL");
                } else {
                    ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");
                    debugTool.setAvailable(true);
                }
            }

        });
        contentManager.addContent("Tree2", "Tree2", null, new JButton("I need somebody"));
        setupContentManagerUI();
    }

    protected void setupContentManagerUI() {
        ContentManager contentManager = toolWindowManager.getContentManager();
        ContentManagerUI contentManagerUI = contentManager.getContentManagerUI();

        contentManagerUI.addContentManagerUIListener(new ContentManagerUIListener() {

            public boolean contentUIRemoving(ContentManagerUIEvent event) {
                return JOptionPane.showConfirmDialog(frame, "Are you sure?") == JOptionPane.OK_OPTION;
            }

            public void contentUIDetached(ContentManagerUIEvent event) {
            }
            
        });
    }

    public static void main(String[] args) {
        TutorialSet test = new TutorialSet();
        try {
            test.setUp();
            test.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}