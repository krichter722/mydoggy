package org.noos.xing.mydoggy.mydoggyset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowManagerDescriptor.Corner.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.itest.impl.ui.JBalloonTip;
import org.noos.xing.mydoggy.mydoggyset.action.*;
import org.noos.xing.mydoggy.mydoggyset.context.ContentContext;
import org.noos.xing.mydoggy.mydoggyset.ui.MonitorPanel;
import org.noos.xing.mydoggy.mydoggyset.ui.RuntimeMemoryMonitorSource;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.SimpliedTitleBarButtons;
import org.noos.xing.mydoggy.plaf.ui.look.MyDoggyResourceManager;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleBarUI;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
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
/*
        try {
            UIManager.setLookAndFeel(new BernsteinLookAndFeel());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
*/
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                contentContext.put(MyDoggySet.class, null);

                JBalloonTip balloonTip = new JBalloonTip(frame);
                balloonTip.setText("Hello World");
                balloonTip.show(150,150);

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
        fileMenu.add(new FrameshotAction(frame));
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
        contentMenu.add(new ViewContextAction("Customize", contentContext, ResourceManager.class));

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

    protected void customizeToolWindowManager() {
        // CUSTOMIZATION....

        // Customize toolwindow TitleBar ...
        MyDoggyToolWindowManager manager = ((MyDoggyToolWindowManager) toolWindowManager);
        ResourceManager resourceManager = manager.getResourceManager();

        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START, Color.RED);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END, Color.PINK);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START, Color.BLACK);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END, Color.GRAY);

        resourceManager.putColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED, Color.BLUE);
        resourceManager.putColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED, Color.LIGHT_GRAY);

        resourceManager.putColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START, Color.RED);
        resourceManager.putColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END, Color.PINK);

        resourceManager.putColor(MyDoggyKeySpace.RAB_FOREGROUND, Color.CYAN);

        // More deep customization ...
        // Change the way the background is drawing
        MyDoggyResourceManager myDoggyResourceManager = (MyDoggyResourceManager) manager.getResourceManager();
        myDoggyResourceManager.putComponentUICreator(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR_UI, new MyDoggyResourceManager.ComponentUICreator() {

            public ComponentUI createComponentUI(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
                return new ToolWindowTitleBarUI((ToolWindowDescriptor) args[0],
                                                (DockedContainer) args[1]) {
                    protected void updateToolWindowTitleBar(Graphics g, JComponent c, Color backgroundStart, Color backgroundEnd, Color idBackgroundColor, Color idColor) {
                        Rectangle r = c.getBounds();
                        r.x = r.y = 0;

                        GraphicsUtil.fillRect(g, r,
                                              backgroundStart, backgroundEnd,
                                              null,
                                              GraphicsUtil.LEFT_TO_RIGHT_GRADIENT);

                        if (descriptor.getDockedTypeDescriptor().isIdVisibleOnTitleBar() ||
                            toolWindow.getType() == ToolWindowType.FLOATING ||
                            toolWindow.getType() == ToolWindowType.FLOATING_FREE ||
                            toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {

                            String id = resourceManager.getUserString(descriptor.getToolWindow().getId());
                            r.width = g.getFontMetrics().stringWidth(id) + 8;

                            int halfHeigh = (r.height / 2);
                            GraphicsUtil.fillRect(g, r,
                                                  Color.WHITE,
                                                  idBackgroundColor,
                                                  new Polygon(new int[]{r.x, r.x + r.width - halfHeigh, r.x + r.width - halfHeigh, r.x},
                                                              new int[]{r.y, r.y, r.y + r.height, r.y + r.height},
                                                              4),
                                                  GraphicsUtil.LEFT_TO_RIGHT_GRADIENT);


                            Polygon polygon = new Polygon();
                            polygon.addPoint(r.x + r.width - halfHeigh, r.y);
                            polygon.addPoint(r.x + r.width - halfHeigh + 8, r.y + (r.height / 2));
                            polygon.addPoint(r.x + r.width - halfHeigh, r.y + r.height);

                            GraphicsUtil.fillRect(g, r,
                                                  Color.WHITE,
                                                  idBackgroundColor,
                                                  polygon,
                                                  GraphicsUtil.LEFT_TO_RIGHT_GRADIENT);

                            g.setColor(idColor);
                            g.drawString(id, r.x + 2, r.y + g.getFontMetrics().getAscent());
                        }
                    }
                };
            }
        });

        // Change title bar buttons dispositions
        myDoggyResourceManager.putInstanceCreator(TitleBarButtons.class,
                                                  new MyDoggyResourceManager.InstanceCreator() {
                public Object createComponent(Object... args) {
                    return new SimpliedTitleBarButtons(
                            (ToolWindowDescriptor) args[0],
                            (DockedContainer) args[1]
                    );
                }
            }
        );

        // END CUSTOMIZATION....
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
