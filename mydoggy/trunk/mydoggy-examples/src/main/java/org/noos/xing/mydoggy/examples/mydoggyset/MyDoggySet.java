package org.noos.xing.mydoggy.examples.mydoggyset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.examples.mydoggyset.action.*;
import org.noos.xing.mydoggy.examples.mydoggyset.content.*;
import org.noos.xing.mydoggy.examples.mydoggyset.ui.MonitorPanel;
import org.noos.xing.mydoggy.examples.mydoggyset.ui.RuntimeMemoryMonitorSource;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggySet {
    // Possible Look & Feels
    private String currentLookAndFeel;

    public JFrame frame;
    private ToolWindowManager toolWindowManager;

    private Action wellcomeContentAction;

    private Component toolsContentComponent;
    private Component groupEditorContentComponent;
    private Component contentsContentComponent;
    private Component managerContentComponent;
    private Component wellcomeContentComponent;
    private Component interactiveTestContentComponent;

    private JMenu lafMenu;

    private ContentManagerUI defaultManagerUI;


    public JFrame getFrame() {
        return frame;
    }

    public ToolWindowManager getToolWindowManager() {
        return toolWindowManager;
    }

    public void setLookAndFeel(String laf) {
        if (!currentLookAndFeel.equals(laf)) {
            currentLookAndFeel = laf;

            updateLookAndFeel();

            for (int i = 0; i < lafMenu.getItemCount(); i++) {
                JMenuItem item = lafMenu.getItem(i);
                item.setSelected(item.getActionCommand().equals(laf));
            }
        }
    }


    protected void setUp() {
        initComponents();

        initToolWindowManager();
    }

    protected void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                wellcomeContentAction.actionPerformed(null);
                frame.setVisible(true);
            }
        });
    }


    protected void initComponents() {
        this.frame = new JFrame("MyDoggy-Set...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));

        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager(frame, Locale.US, null);
/*
        myDoggyToolWindowManager.getResourceManager().setTransparencyManager(
                new JNAWindowTransparencyManager()
        );
*/
        this.toolWindowManager = myDoggyToolWindowManager;
        ((DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED)).setIdVisibleOnTitleBar(false);



        initMenuBar();
    }

    protected void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new LoadWorkspaceAction(frame, toolWindowManager));
        fileMenu.add(new StoreWorkspaceAction(frame, toolWindowManager));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(frame));

        // Content Menu
        JMenu contentMenu = new JMenu("Content");
        contentMenu.add(wellcomeContentAction = new AddContentAction(toolWindowManager,
                                                                     "Wellcome", "Wellcome", null,
                                                                     wellcomeContentComponent = new WellcomeContentComponent(),
                                                                     "Wellcome", (int) 'W'));
        contentMenu.add(new AddContentAction(toolWindowManager,
                                             "Manager", "Manager", null,
                                             managerContentComponent = new ManagerContentComponent(toolWindowManager),
                                             "Manager", (int) 'M'));
        contentMenu.add(new AddContentAction(toolWindowManager,
                                             "Tools", "Tools", null,
                                             toolsContentComponent = new ToolsContentComponent(toolWindowManager),
                                             "ToolWindows", (int) 'T'));
        contentMenu.add(new AddContentAction(toolWindowManager,
                                             "Groups", "Group Editor", null,
                                             groupEditorContentComponent = new GroupEditorContentComponent(toolWindowManager),
                                             "Groups", (int) 'G'));
        contentMenu.add(new AddContentAction(toolWindowManager,
                                             "Contents", "Contents", null,
                                             contentsContentComponent = new ContentsContentComponent(toolWindowManager),
                                             "Contents", (int) 'C'));
        contentMenu.add(new AddContentAction(toolWindowManager,
                                             "ITests", "Interactive Tests", null,
                                             interactiveTestContentComponent = new InteractiveTestContentComponent(frame, toolWindowManager),
                                             "Interactive Tests", (int) 'I'));

        // L&F Menu
        lafMenu = new JMenu("Looks");

        String currentLaF = UIManager.getLookAndFeel().getName();

        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo aLafInfo : lafInfo) {
            JMenuItem mi = ChangeLookAndFeelAction.createLafMenuItem(this, lafMenu, aLafInfo.getName(), aLafInfo.getClassName());
            if (currentLaF.equals(aLafInfo.getName())) {
                mi.setSelected(true);
                currentLookAndFeel = aLafInfo.getClassName();
            }
        }

        menuBar.add(fileMenu);
        menuBar.add(contentMenu);
        menuBar.add(lafMenu);

        this.frame.setJMenuBar(menuBar);
    }

    protected void initToolWindowManager() {
        FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        typeDescriptor.setTransparentDelay(0);

        JPanel panel = new JPanel(new TableLayout(new double[][]{{20, -1, 20}, {20, -1, 20}}));
        panel.add(new JButton("Hello World 2"), "1,1,FULL,FULL");

        toolWindowManager.registerToolWindow("Tool 1", "Title 1", null, new JButton("Hello World 1"), ToolWindowAnchor.LEFT);
        toolWindowManager.registerToolWindow("Tool 2", "Title 2", null, panel, ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 3", "Title 3",
                                             SwingUtil.loadIcon("org/noos/xing/mydoggy/examples/mydoggyset/icons/save.png"),
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

        for (ToolWindow window : toolWindowManager.getToolWindows()) {
            window.setAvailable(true);
        }

        // Create two groups
        ToolWindowGroup mainGroup = toolWindowManager.getToolWindowGroup("Main");
        ToolWindowGroup submainGroup = toolWindowManager.getToolWindowGroup("SubMain");

        // Set TypeDescriptor properties for tool window 1
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Tool 1");

        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        dockedTypeDescriptor.setPopupMenuEnabled(false);
        dockedTypeDescriptor.setDockLength(200);

        mainGroup.addToolWindow(toolWindow);

        // Set properties for tool window 2
        toolWindow = toolWindowManager.getToolWindow("Tool 2");
        dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        dockedTypeDescriptor.getToolsMenu().add(new JMenuItem("Prova"));

        toolWindow.setType(ToolWindowType.FLOATING_FREE);

        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_FREE);
        descriptor.setLocation(100, 100);
        descriptor.setSize(250, 250);

        submainGroup.addToolWindow(toolWindow);

        toolWindow = toolWindowManager.getToolWindow("Tool 3");
        dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);

        JMenuItem menuItem = new JMenuItem("Hello World!!!");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Hello World!!!");
            }
        });
        dockedTypeDescriptor.getToolsMenu().add(menuItem);

        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
        slidingTypeDescriptor.setEnabled(false);


        mainGroup.addToolWindow(toolWindow);

        // Set properties for tool window 4
        toolWindow = toolWindowManager.getToolWindow("Tool 4");
        toolWindow.setType(ToolWindowType.FLOATING_FREE);
        submainGroup.addToolWindow(toolWindow);

        // Set properties for tool window 5
        toolWindow = toolWindowManager.getToolWindow("Tool 5");
        toolWindow.setType(ToolWindowType.FLOATING_FREE);

        submainGroup.addToolWindow(toolWindowManager.getToolWindow("Tool 6"));

        // Set properties for tool window 7
        toolWindow = toolWindowManager.getToolWindow("Tool 7");
        toolWindow.setType(ToolWindowType.FLOATING);

        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setModal(true);

        defaultManagerUI = toolWindowManager.getContentManager().getContentManagerUI();
        TabbedContentManagerUI tabbedContentManagerUI = (TabbedContentManagerUI) defaultManagerUI;
        tabbedContentManagerUI.setShowAlwaysTab(false);
        tabbedContentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.BOTTOM);
        tabbedContentManagerUI.setTabLayout(TabbedContentManagerUI.TabLayout.WRAP);

        defaultManagerUI.addContentManagerUIListener(new ContentManagerUIListener() {
            public boolean contentUIRemoving(ContentManagerUIEvent event) {
                return JOptionPane.showConfirmDialog(frame, "Are you sure?") == JOptionPane.OK_OPTION;
            }

            public void contentUIDetached(ContentManagerUIEvent event) {
            }
        });

        ToolWindowManagerDescriptor managerDescriptor = toolWindowManager.getToolWindowManagerDescriptor();
//        managerDescriptor.setCornerComponent(NORD_WEST, new JLabel("NW"));
//        managerDescriptor.setCornerComponent(SOUTH_WEST, new JLabel("SW"));
//        managerDescriptor.setCornerComponent(NORD_EAST, new JLabel("NE"));
//        managerDescriptor.setCornerComponent(SOUTH_EAST, new JLabel("SE"));

        // Add MyDoggyToolWindowManager to frame
        this.frame.getContentPane().add((Component) toolWindowManager, "1,1,");
    }


    protected void updateLookAndFeel() {
        try {
            UIManager.setLookAndFeel(currentLookAndFeel);

            SwingUtilities.updateComponentTreeUI(frame);

            SwingUtilities.updateComponentTreeUI(groupEditorContentComponent);
            SwingUtilities.updateComponentTreeUI(toolsContentComponent);
            SwingUtilities.updateComponentTreeUI(contentsContentComponent);
            SwingUtilities.updateComponentTreeUI(managerContentComponent);
            SwingUtilities.updateComponentTreeUI(wellcomeContentComponent);
            SwingUtilities.updateComponentTreeUI(interactiveTestContentComponent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void dispose() {
        this.frame.setVisible(false);
        this.frame.dispose();
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
