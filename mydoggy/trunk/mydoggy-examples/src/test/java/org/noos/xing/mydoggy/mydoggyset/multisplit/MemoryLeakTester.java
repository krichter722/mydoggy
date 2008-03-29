package org.noos.xing.mydoggy.mydoggyset.multisplit;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
                ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");
                debugTool.setActive(true);

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
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager(frame);
        this.toolWindowManager = myDoggyToolWindowManager;

        JButton button = new JButton("Debug Tool");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolWindowManager.unregisterToolWindow("Debug");
            }
        });

        // Register a Tool.
        toolWindowManager.registerToolWindow("Debug",                      // Id
                "Debug Tool",                 // Title
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
        dockedTypeDescriptor.setPreviewEnabled(true);
        dockedTypeDescriptor.setPreviewDelay(1500);
        dockedTypeDescriptor.setPreviewTransparentRatio(0.4f);

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
        JTree treeContent = new JTree();

        ContentManager contentManager = toolWindowManager.getContentManager();
        contentManager.addContentManagerListener(new ContentManagerListener() {
            public void contentAdded(ContentManagerEvent event) {
                event.getContent().addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        StringBuffer sb = new StringBuffer("Event : ");
                        sb.append(evt.getPropertyName())
                                .append(" ; ")
                                .append(evt.getOldValue())
                                .append(" -> ")
                                .append(evt.getNewValue())
                                .append(" ; ")
                                .append(evt.getSource());
                        System.out.println(sb);
//                new RuntimeException().printStackTrace();
//                System.out.println("----------------------------------------------------------");
                    }
                });
            }

            public void contentRemoved(ContentManagerEvent event) {
                System.out.println("Content removed " + event);
            }

            public void contentSelected(ContentManagerEvent event) {
            }
        });

        Content content = contentManager.addContent("Tree Key",
                "Tree Title",
                null,      // An icon
                treeContent);
        content.setToolTipText("Tree tip");
        content.setToolTipText(null);
        setupContentManagerUI();
    }

    protected void setupContentManagerUI() {
        ContentManager contentManager = toolWindowManager.getContentManager();
//        MultiSplitContentManagerUI contentManagerUI = new MyDoggyMultiSplitContentManagerUI();
//        contentManager.setContentManagerUI(contentManagerUI);
        TabbedContentManagerUI<TabbedContentUI> contentManagerUI = (TabbedContentManagerUI<TabbedContentUI>) contentManager.getContentManagerUI();

        contentManagerUI.setShowAlwaysTab(true);
        contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.BOTTOM);
        contentManagerUI.addContentManagerUIListener(new ContentManagerUIListener() {
            public boolean contentUIRemoving(ContentManagerUIEvent event) {
                return JOptionPane.showConfirmDialog(frame, "Are you sure?") == JOptionPane.OK_OPTION;
            }

            public void contentUIDetached(ContentManagerUIEvent event) {
//                JOptionPane.showMessageDialog(frame, "Hello World!!!");
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
        MemoryLeakTester test = new MemoryLeakTester();
        try {
            test.setUp();
            test.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}