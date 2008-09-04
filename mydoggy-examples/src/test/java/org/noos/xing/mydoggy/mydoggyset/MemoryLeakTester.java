package org.noos.xing.mydoggy.mydoggyset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MemoryLeakTester {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() {
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Activate "Debug" Tool
/*
                ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");
                debugTool.setActive(true);

                ToolWindow profileTool = toolWindowManager.getToolWindow("Profile");
                profileTool.aggregate();

                ToolWindow memoryTool = toolWindowManager.getToolWindow("Memory");
                memoryTool.aggregate(AggregationPosition.RIGHT);
*/

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

        // Set a layout manager. I love TableLayout. It's powerful.
        this.frame.getContentPane().setLayout(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
    }

    protected void initToolWindowManager() {
        // Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        this.toolWindowManager = myDoggyToolWindowManager;

        JButton button = new JButton("Unregister Tool");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolWindowManager.unregisterAllToolWindow();
            }
        });

        // Register a Tool.
        toolWindowManager.registerToolWindow("Debug",                      // Id
                "Debug Tool",                 // Title
                null,                         // Icon
                button,    // Component
                ToolWindowAnchor.LEFT);       // Anchor

        toolWindowManager.registerToolWindow("Profile",                      // Id
                "Profile Tool",                 // Title
                null,                         // Icon
                button,    // Component
                ToolWindowAnchor.LEFT);       // Anchor

        toolWindowManager.registerToolWindow("Memory",                      // Id
                "Memory Tool",                 // Title
                null,                         // Icon
                button,    // Component
                ToolWindowAnchor.LEFT);       // Anchor

        setupDebugTool();

        // Made all tools available
        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);

        initContentManager();

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");
    }


    protected void setupDebugTool() {
        ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");

        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) debugTool.getTypeDescriptor(ToolWindowType.DOCKED);

        dockedTypeDescriptor.setDockLength(300);
        dockedTypeDescriptor.setPopupMenuEnabled(true);
        JMenu toolsMenu = dockedTypeDescriptor.getToolsMenu();
        toolsMenu.add(new AbstractAction("Hello World!!!") {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Hello World!!!");
            }
        });
        dockedTypeDescriptor.setToolWindowActionHandler(new ToolWindowActionHandler() {
            public void onHideButtonClick(ToolWindow toolWindow) {
                JOptionPane.showMessageDialog(frame, "Hiding...");
                toolWindow.setVisible(false);
            }
        });
        dockedTypeDescriptor.setAnimating(true);

        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) debugTool.getTypeDescriptor(ToolWindowType.SLIDING);
        slidingTypeDescriptor.setEnabled(true);
        slidingTypeDescriptor.setTransparentMode(true);
        slidingTypeDescriptor.setTransparentRatio(0.8f);
        slidingTypeDescriptor.setTransparentDelay(0);
        slidingTypeDescriptor.setAnimating(true);

        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) debugTool.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setEnabled(true);
        floatingTypeDescriptor.setLocation(150, 200);
        floatingTypeDescriptor.setSize(320, 200);
        floatingTypeDescriptor.setModal(false);
        floatingTypeDescriptor.setTransparentMode(true);
        floatingTypeDescriptor.setTransparentRatio(0.2f);
        floatingTypeDescriptor.setTransparentDelay(1000);
        floatingTypeDescriptor.setAnimating(true);

    }

    protected void initContentManager() {
        ContentManager contentManager = toolWindowManager.getContentManager();

        JButton button = new JButton("Remove Content");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolWindowManager.getContentManager().removeContent(0);
            }
        });

        Content content = contentManager.addContent("Tree Key",
                "Tree Title",
                null,      // An icon
                button);
        content.setToolTipText("Tree tip");
        content.setToolTipText(null);

        setupContentManagerUI();
    }

    protected void setupContentManagerUI() {
        ContentManager contentManager = toolWindowManager.getContentManager();

//        TabbedContentManagerUI managerUI = (TabbedContentManagerUI) contentManager.getContentManagerUI();
//        managerUI.setShowAlwaysTab(true);

        contentManager.setContentManagerUI(new MyDoggyMultiSplitContentManagerUI());
        MultiSplitContentManagerUI managerUI = (MultiSplitContentManagerUI) contentManager.getContentManagerUI();
        managerUI.setShowAlwaysTab(true);
    }

    public static void main(String[] args) {
        MemoryLeakTester test = new MemoryLeakTester();
        try {
            test.setUp();
            test.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}