package org.noos.xing.mydoggy.mydoggyset;

import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowManagerDescriptor.Corner.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.mydoggyset.action.*;
import org.noos.xing.mydoggy.mydoggyset.context.MyDoggySetContext;
import org.noos.xing.mydoggy.mydoggyset.ui.LookAndFeelMenuItem;
import org.noos.xing.mydoggy.mydoggyset.ui.MonitorPanel;
import org.noos.xing.mydoggy.mydoggyset.ui.RuntimeMemoryMonitorSource;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggySet {
    protected JFrame frame;
    protected ToolWindowManager toolWindowManager;
    protected ViewContext myDoggySetContext;


    protected void setUp() {
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                myDoggySetContext.put(MyDoggySet.class, null);
                frame.setVisible(true);
            }
        });
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("MyDoggy-Set 1.4.0 ...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));

        // Init ToolWindowManager
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager(frame, Locale.US, null);

        // Apply now all customization if necessary
//        myDoggyToolWindowManager.getResourceManager().putProperty("dialog.owner.enabled", "false");
//        myDoggyToolWindowManager.getResourceManager().putProperty(""ContentManagerDropTarget.enabled"", "true");
        customizeToolWindowManager();

        this.toolWindowManager = myDoggyToolWindowManager;
        this.myDoggySetContext = new MyDoggySetContext(toolWindowManager, frame);
        initMenuBar();
    }

    protected void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new LoadWorkspaceAction(frame, toolWindowManager));
        fileMenu.add(new StoreWorkspaceAction(frame, toolWindowManager));
        fileMenu.addSeparator();
        fileMenu.add(new FrameshotAction(frame));
        fileMenu.add(new FramePieceshotAction(frame));
        fileMenu.add(new MagnifierAction(frame));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(frame));

        // Content Menu
        JMenu contentMenu = new JMenu("Content");
        contentMenu.add(new ViewContextAction("Welcome", myDoggySetContext, MyDoggySet.class));
        contentMenu.add(new ViewContextAction("Manager", myDoggySetContext, ToolWindowManager.class));
        contentMenu.add(new ViewContextAction("ToolWindows", myDoggySetContext, ToolWindow.class));
        contentMenu.add(new ViewContextAction("Contents", myDoggySetContext, Content.class));
        contentMenu.add(new ViewContextAction("Groups", myDoggySetContext, ToolWindowGroup.class));
        contentMenu.add(new ViewContextAction("ITests", myDoggySetContext, InteractiveTest.class));
        contentMenu.add(new ViewContextAction("Customize", myDoggySetContext, ResourceManager.class));

        // L&F Menu
        JMenu lafMenu = new JMenu("Looks");

        String currentLaF = UIManager.getLookAndFeel().getName();

        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo aLafInfo : lafInfo) {
            JMenuItem menuItem = new LookAndFeelMenuItem(myDoggySetContext, aLafInfo.getName(), aLafInfo.getClassName());
            lafMenu.add(menuItem);

            if (currentLaF.equals(aLafInfo.getName()))
                menuItem.setSelected(true);
        }

        menuBar.add(fileMenu);
        menuBar.add(contentMenu);
        menuBar.add(lafMenu);

        this.frame.setJMenuBar(menuBar);
    }

    protected void initToolWindowManager() {
        // Setup type descriptor templates...
        FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        typeDescriptor.setTransparentDelay(0);

        // Register tools
        JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{20, -1, 20}, {20, -1, 20}}));
        panel.add(new JButton("Hello World 2"), "1,1,FULL,FULL");

        toolWindowManager.registerToolWindow("Tool 1", "Title 1", null, new JButton("Hello World 1"), ToolWindowAnchor.LEFT);
        toolWindowManager.registerToolWindow("Tool 2", "Title 2", null, panel, ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 3", "Title 3",
                                             SwingUtil.loadIcon("org/noos/xing/mydoggy/mydoggyset/icons/save.png"),
                                             new JButton("Hello World 3"), ToolWindowAnchor.LEFT);
        toolWindowManager.registerToolWindow("Tool 4", "Title 4", null, new JButton("Hello World 4"), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("Tool 5", "Title 5", null, new JButton("Hello World 5"), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("Tool 6", "Title 6", null, new JButton("Hello World 6"), ToolWindowAnchor.BOTTOM);

        MonitorPanel monitorPanel = new MonitorPanel(new RuntimeMemoryMonitorSource());
        monitorPanel.start();
        toolWindowManager.registerToolWindow("Tool 7", "Title 7", null, monitorPanel, ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("Tool 8", "Title 8", null, new JButton("Hello World 8"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 9", "Title 9", null, new JButton("Hello World 9"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 10", "Title 10", null, new JButton("Hello World 10"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 11", "Title 11", null, new JButton("Hello World 11"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 12", "Title 12", null, new JButton("Hello World 12"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 13", "Title 13", null, new JButton("Hello World 13"), ToolWindowAnchor.RIGHT);

        // Make all available
        for (ToolWindow window : toolWindowManager.getToolWindows()) {
            window.setAvailable(true);
        }

        // Setup Tool 1
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Tool 1");

        DockedTypeDescriptor dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
        dockedTypeDescriptor.setPopupMenuEnabled(false);
        dockedTypeDescriptor.setDockLength(200);

        // Setup Tool 2
        toolWindow = toolWindowManager.getToolWindow("Tool 2");
        dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
        dockedTypeDescriptor.getToolsMenu().add(new JMenuItem("Prova"));

        toolWindow.setType(ToolWindowType.FLOATING_FREE);

        FloatingTypeDescriptor descriptor = toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class);
        descriptor.setLocation(100, 100);
        descriptor.setSize(250, 250);

        // Setup Tool 3
        toolWindow = toolWindowManager.getToolWindow("Tool 3");
        dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);

        JMenuItem menuItem = new JMenuItem("Hello World!!!");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Hello World!!!");
            }
        });
        dockedTypeDescriptor.getToolsMenu().add(menuItem);
        dockedTypeDescriptor.setPreviewDelay(1500);

        SlidingTypeDescriptor slidingTypeDescriptor = toolWindow.getTypeDescriptor(SlidingTypeDescriptor.class);
        slidingTypeDescriptor.setEnabled(false);

        // Setup Tool 4 and 5
        toolWindowManager.getToolWindow("Tool 4").setType(ToolWindowType.FLOATING_FREE);
        toolWindowManager.getToolWindow("Tool 5").setType(ToolWindowType.FLOATING_FREE);

        // Setup Tool 7
        toolWindow = toolWindowManager.getToolWindow("Tool 7");
        toolWindow.setType(ToolWindowType.FLOATING);

        FloatingTypeDescriptor floatingTypeDescriptor = toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class);
        floatingTypeDescriptor.setModal(true);
        floatingTypeDescriptor.setAnimating(false);

        // Setup ContentManagerUI
        toolWindowManager.getContentManager().setContentManagerUI(new MyDoggyMultiSplitContentManagerUI());

        MultiSplitContentManagerUI contentManagerUI = (MultiSplitContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI();
        contentManagerUI.setShowAlwaysTab(false);
        contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.BOTTOM);
        contentManagerUI.setTabLayout(TabbedContentManagerUI.TabLayout.WRAP);
        contentManagerUI.addContentManagerUIListener(new ContentManagerUIListener() {
            public boolean contentUIRemoving(ContentManagerUIEvent event) {
                return JOptionPane.showConfirmDialog(frame, "Are you sure?") == JOptionPane.OK_OPTION;
            }

            public void contentUIDetached(ContentManagerUIEvent event) {
            }
        });

        // Setup Corner Components
        ToolWindowManagerDescriptor managerDescriptor = toolWindowManager.getToolWindowManagerDescriptor();
        managerDescriptor.setCornerComponent(NORD_WEST, new JLabel("NW"));
        managerDescriptor.setCornerComponent(SOUTH_WEST, new JLabel("SW"));
        managerDescriptor.setCornerComponent(NORD_EAST, new JLabel("NE"));
        managerDescriptor.setCornerComponent(SOUTH_EAST, new JLabel("SE"));

        // Add MyDoggyToolWindowManager to frame
        this.frame.getContentPane().add((Component) toolWindowManager, "1,1,");
    }

    protected void customizeToolWindowManager() {
        // Add customization here. See the page http://mydoggy.sourceforge.net/mydoggy-plaf/resourceManagerUsing.html
    }

    protected void dispose() {
        frame.setVisible(false);
        frame.dispose();
    }


    public static void main(String[] args) {
        MyDoggySet test = new MyDoggySet();
        try {
            test.setUp();
            test.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
