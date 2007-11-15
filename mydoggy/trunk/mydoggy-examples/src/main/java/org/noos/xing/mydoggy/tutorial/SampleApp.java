package org.noos.xing.mydoggy.tutorial;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SampleApp {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() {
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Set debug tool active
                ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");
                debugTool.setActive(true);

                ToolWindow runTool = toolWindowManager.getToolWindow("Run");
                runTool.aggregate();

                frame.setVisible(true);
            }
        });
    }

    protected void initComponents() {
        this.frame = new JFrame("Sample App...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // I love TableLayout. It's great.
        this.frame.getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));

        // Store (on close) and load (on start) the toolwindow manager workspace.
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                try {
                    File workspaceFile = new File("workspace.xml");
                    if (workspaceFile.exists()) {
                        FileInputStream inputStream = new FileInputStream("workspace.xml");
//                        toolWindowManager.getPersistenceDelegate().apply(inputStream);
                        inputStream.close();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            public void windowClosing(WindowEvent e) {
                try {
                    FileOutputStream output = new FileOutputStream("workspace.xml");
                    toolWindowManager.getPersistenceDelegate().save(output);
                    output.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    protected void initToolWindowManager() {
        // Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager(frame);
        this.toolWindowManager = myDoggyToolWindowManager;
        toolWindowManager.getToolWindowManagerDescriptor().setAggregateMode(ToolWindowAnchor.LEFT, true);

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
        dockedTypeDescriptor.setPreviewEnabled(true);
        dockedTypeDescriptor.setPreviewDelay(1500);
        dockedTypeDescriptor.setPreviewTransparentRatio(0.4f);

        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) debugTool.getTypeDescriptor(ToolWindowType.SLIDING);
        slidingTypeDescriptor.setEnabled(false);
        slidingTypeDescriptor.setTransparentMode(true);
        slidingTypeDescriptor.setTransparentRatio(0.8f);
        slidingTypeDescriptor.setTransparentDelay(0);
        slidingTypeDescriptor.setAnimating(true);

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
    }

    protected void setupContentManagerUI() {
        TabbedContentManagerUI contentManagerUI = (TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI();
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
    }

    public static void main(String[] args) {
        SampleApp test = new SampleApp();
        try {
            test.setUp();
            test.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}