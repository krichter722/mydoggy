package org.noos.xing.mydoggy.tutorialset.basic;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TutorialSet12 {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;


    protected void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
    }

    protected void setUp() {
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        // Activate "Debug" Tool
        ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");
        debugTool.setActive(true);

        // Aggregate "Run" tool
        ToolWindow runTool = toolWindowManager.getToolWindow("Run");
        runTool.aggregate(AggregationPosition.TOP);

        // Aggregate "Properties" tool
        ToolWindow propertiesTool = toolWindowManager.getToolWindow("Properties");
        propertiesTool.aggregate(AggregationPosition.RIGHT);

        frame.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("Sample App...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        // Register a Tool.
        toolWindowManager.registerToolWindow("Debug",                      // Id
                                             "Debug Tool",                 // Title
                                             null,                         // Icon
                                             new JButton("Debug Tool"),    // Component
                                             ToolWindowAnchor.LEFT);       // Anchor

        setupDebugTool();
        // Register another Tool.
        toolWindowManager.registerToolWindow("Run",                      // Id
                                             "Run Tool",                 // Title
                                             null,                       // Icon
                                             new JButton("Run Tool"),    // Component
                                             ToolWindowAnchor.LEFT);     // Anchor

        // Register another Tool.
        toolWindowManager.registerToolWindow("Properties",                      // Id
                                             "Properties Tool",                 // Title
                                             null,                              // Icon
                                             new JButton("Properties Tool"),    // Component
                                             ToolWindowAnchor.LEFT);            // Anchor

        // Made all tools available
        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);

        initContentManager();

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");
    }


    protected void setupDebugTool() {
        ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");

        // RepresentativeAnchorDescriptor
        RepresentativeAnchorDescriptor representativeAnchorDescriptor = debugTool.getRepresentativeAnchorDescriptor();
        representativeAnchorDescriptor.setPreviewEnabled(true);
        representativeAnchorDescriptor.setPreviewDelay(1500);
        representativeAnchorDescriptor.setPreviewTransparentRatio(0.4f);

        // DockedTypeDescriptor
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

        // SlidingTypeDescriptor
        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) debugTool.getTypeDescriptor(ToolWindowType.SLIDING);
        slidingTypeDescriptor.setEnabled(false);
        slidingTypeDescriptor.setTransparentMode(true);
        slidingTypeDescriptor.setTransparentRatio(0.8f);
        slidingTypeDescriptor.setTransparentDelay(0);
        slidingTypeDescriptor.setAnimating(true);

        // FloatingTypeDescriptor
        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) debugTool.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setEnabled(true);
        floatingTypeDescriptor.setLocation(150,200);
        floatingTypeDescriptor.setSize(320,200);
        floatingTypeDescriptor.setModal(false);
        floatingTypeDescriptor.setTransparentMode(true);
        floatingTypeDescriptor.setTransparentRatio(0.2f);
        floatingTypeDescriptor.setTransparentDelay(1000);
        floatingTypeDescriptor.setAnimating(true);

        // Setup Tabs
        initTabs();
    }

    protected void initTabs() {
        ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");
        ToolWindowTab profilingTab = debugTool.addToolWindowTab("Profiling", new JButton("Profiling"));
        profilingTab.setCloseable(true);
    }

    protected void initContentManager() {
         JTree treeContent = new JTree();

        ContentManager contentManager = toolWindowManager.getContentManager();
        Content content = contentManager.addContent("Tree Key",
                                                    "Tree Title",
                                                    null,      // An icon
                                                    treeContent);
        content.setToolTipText("Tree tip");

        setupContentManagerUI();

        contentManager.setEnabled(false);

    }

    protected void setupContentManagerUI() {
        ContentManager contentManager = toolWindowManager.getContentManager();
        MultiSplitContentManagerUI contentManagerUI = new MyDoggyMultiSplitContentManagerUI();
        contentManager.setContentManagerUI(contentManagerUI);

        contentManagerUI.setShowAlwaysTab(true);
        contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.BOTTOM);
        contentManagerUI.addContentManagerUIListener(new ContentManagerUIListener() {
            public boolean contentUIRemoving(ContentManagerUIEvent event) {
                return JOptionPane.showConfirmDialog(frame, "Are you sure?") == JOptionPane.OK_OPTION;
            }

            public void contentUIDetached(ContentManagerUIEvent event) {
                JOptionPane.showMessageDialog(frame, "Hello World!!!");
            }
        });

        TabbedContentUI contentUI = contentManagerUI.getContentUI(toolWindowManager.getContentManager().getContent(0));

        contentUI.setCloseable(true);
        contentUI.setDetachable(true);
        contentUI.setTransparentMode(true);
        contentUI.setTransparentRatio(0.7f);
        contentUI.setTransparentDelay(1000);

        // Now Register two other contents...
        contentManager.addContent("Tree Key 2", "Tree Title 2", null, new JTree(), null,
                                 new MultiSplitConstraint(contentManager.getContent(0), 0));

        contentManager.addContent("Tree Key 3", "Tree Title 3", null, new JTree(), null,
                                 new MultiSplitConstraint(AggregationPosition.RIGHT));
    }

    public static void main(String[] args) {
        TutorialSet12 test = new TutorialSet12();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}