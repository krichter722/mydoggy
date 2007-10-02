package org.noos.xing.mydoggy.mydoggyset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowManagerDescriptor.Corner.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.mydoggyset.action.ChangeLookAndFeelAction;
import org.noos.xing.mydoggy.mydoggyset.action.ExitAction;
import org.noos.xing.mydoggy.mydoggyset.action.LoadWorkspaceAction;
import org.noos.xing.mydoggy.mydoggyset.action.StoreWorkspaceAction;
import org.noos.xing.mydoggy.mydoggyset.context.ContentContext;
import org.noos.xing.mydoggy.mydoggyset.ui.MonitorPanel;
import org.noos.xing.mydoggy.mydoggyset.ui.RuntimeMemoryMonitorSource;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
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
    private String currentLookAndFeel;
    private JFrame frame;
    private ToolWindowManager toolWindowManager;
    private JMenu lafMenu;

    private ViewContext contentContext;

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
                contentContext.put(MyDoggySet.class, null);
                frame.setVisible(true);
            }
        });
    }


    protected void initComponents() {
        this.frame = new JFrame("MyDoggy-Set 1.3.2 ...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));

        this.toolWindowManager = new MyDoggyToolWindowManager(frame, Locale.US, null);

        this.contentContext = new ContentContext(toolWindowManager, frame);

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
        contentMenu.add(new ViewContextAction("Wellcome", contentContext, MyDoggySet.class));
        contentMenu.add(new ViewContextAction("Manager", contentContext, ToolWindowManager.class));
        contentMenu.add(new ViewContextAction("ToolWindows", contentContext, ToolWindow.class));
        contentMenu.add(new ViewContextAction("Contents", contentContext, Content.class));
        contentMenu.add(new ViewContextAction("Groups", contentContext, ToolWindowGroup.class));
        contentMenu.add(new ViewContextAction("ITests", contentContext, InteractiveTest.class));

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

        for (ToolWindow window : toolWindowManager.getToolWindows()) {
            window.setAvailable(true);
        }

        // Set TypeDescriptor properties for tool window 1
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Tool 1");

        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        dockedTypeDescriptor.setPopupMenuEnabled(false);
        dockedTypeDescriptor.setDockLength(200);

        // Set properties for tool window 2
        toolWindow = toolWindowManager.getToolWindow("Tool 2");
        dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        dockedTypeDescriptor.getToolsMenu().add(new JMenuItem("Prova"));

        toolWindow.setType(ToolWindowType.FLOATING_FREE);

        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_FREE);
        descriptor.setLocation(100, 100);
        descriptor.setSize(250, 250);

        toolWindow = toolWindowManager.getToolWindow("Tool 3");
        dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);

        JMenuItem menuItem = new JMenuItem("Hello World!!!");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Hello World!!!");
            }
        });
        dockedTypeDescriptor.getToolsMenu().add(menuItem);
        dockedTypeDescriptor.setPreviewDelay(1500);

        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
        slidingTypeDescriptor.setEnabled(false);


        // Set properties for tool window 4
        toolWindow = toolWindowManager.getToolWindow("Tool 4");
        toolWindow.setType(ToolWindowType.FLOATING_FREE);

        // Set properties for tool window 5
        toolWindow = toolWindowManager.getToolWindow("Tool 5");
        toolWindow.setType(ToolWindowType.FLOATING_FREE);


        // Set properties for tool window 7
        toolWindow = toolWindowManager.getToolWindow("Tool 7");
        toolWindow.setType(ToolWindowType.FLOATING);

        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setModal(true);
        floatingTypeDescriptor.setAnimating(false);

        ContentManagerUI defaultManagerUI = toolWindowManager.getContentManager().getContentManagerUI();
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
        managerDescriptor.setCornerComponent(NORD_WEST, new JLabel("NW"));
        managerDescriptor.setCornerComponent(SOUTH_WEST, new JLabel("SW"));
        managerDescriptor.setCornerComponent(NORD_EAST, new JLabel("NE"));
        managerDescriptor.setCornerComponent(SOUTH_EAST, new JLabel("SE"));

        // Add MyDoggyToolWindowManager to frame
        this.frame.getContentPane().add((Component) toolWindowManager, "1,1,");
    }


    protected void updateLookAndFeel() {
        try {
            UIManager.setLookAndFeel(currentLookAndFeel);
            SwingUtilities.updateComponentTreeUI(frame);
            contentContext.put(UIManager.class, null);
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
