package org.noos.xing.mydoggy.mydoggyset;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXTitledPanel;
import org.noos.common.Question;
import org.noos.common.context.Context;
import org.noos.common.object.ObjectCreator;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.mydoggyset.action.*;
import org.noos.xing.mydoggy.mydoggyset.context.MyDoggySetContext;
import org.noos.xing.mydoggy.mydoggyset.ui.DoggyTable;
import org.noos.xing.mydoggy.mydoggyset.ui.LookAndFeelMenuItem;
import org.noos.xing.mydoggy.mydoggyset.ui.MonitorPanel;
import org.noos.xing.mydoggy.mydoggyset.ui.RuntimeMemoryMonitorSource;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.actions.HideToolWindowAction;
import org.noos.xing.mydoggy.plaf.ui.CustomDockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.look.MyDoggyResourceManager;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowRepresentativeAnchorUI;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleBarUI;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.StringUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggySet {

    protected JFrame frame;

    protected ToolWindowManager toolWindowManager;

    protected ViewContext myDoggySetContext;
    protected DockableDescriptor memoryMonitorDescriptor;


    public void setUp() {
        initComponents();
        initToolWindows();
    }

    public void start(final Runnable runnable) {
        myDoggySetContext.put(MyDoggySet.class, null);

        SwingUtil.centrePositionOnScreen(frame);

        frame.setVisible(true);

        memoryMonitorDescriptor.setAvailable(true);
        memoryMonitorDescriptor.setAnchor(ToolWindowAnchor.BOTTOM, 0);
        memoryMonitorDescriptor.setAnchorPositionLocked(true);

        if (runnable != null) {
            Thread t = new Thread(runnable);
            t.start();
        }
    }

    public void run(final Runnable runnable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start(runnable);
            }
        });
    }


    public ToolWindowManager getToolWindowManager() {
        return toolWindowManager;
    }

    public ViewContext getMyDoggySetContext() {
        return myDoggySetContext;
    }


    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("MyDoggy-Set 1.5.0 ...");
        this.frame.setSize(800, 600);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));

        // Init ToolWindowManager
        long start = System.currentTimeMillis();
        final MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        long end = System.currentTimeMillis();
        System.out.println("Time (millis) too load the manager : " + (end - start));

        this.toolWindowManager = myDoggyToolWindowManager;

        // Add MyDoggyToolWindowManager to frame
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");

        // Apply now all customization if necessary
        customizeToolWindowManager(myDoggyToolWindowManager);

        // Init the context
        this.myDoggySetContext = new MyDoggySetContext(toolWindowManager, frame);

        // Load Menu Bar
        initMenuBar();
    }

    protected void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new LoadWorkspaceAction(myDoggySetContext, frame, toolWindowManager));
        fileMenu.add(new StoreWorkspaceAction(frame, toolWindowManager));
        fileMenu.addSeparator();
        fileMenu.add(new FrameshotAction(frame));
        fileMenu.add(new FramePieceshotAction(frame));
        fileMenu.add(new MagnifierAction(frame));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(frame));

        // Content Menu
        JMenu contentMenu = new JMenu("Content");
        contentMenu.add(new ContentManagerEnabledAction(toolWindowManager));
        contentMenu.addSeparator();
        contentMenu.add(new ViewContextAction("Welcome", myDoggySetContext, MyDoggySet.class));
        contentMenu.add(new ViewContextAction("Manager", myDoggySetContext, ToolWindowManager.class));
        contentMenu.add(new ViewContextAction("ToolWindows", myDoggySetContext, ToolWindow.class));
        contentMenu.add(new ViewContextAction("Contents", myDoggySetContext, Content.class));
        contentMenu.add(new ViewContextAction("Groups", myDoggySetContext, ToolWindowGroup.class));
        contentMenu.add(new ViewContextAction("ITests", myDoggySetContext, InteractiveTest.class));
        contentMenu.add(new ViewContextAction("Customize", myDoggySetContext, ResourceManager.class));
        contentMenu.add(new ViewContextAction("Nested Manager", myDoggySetContext, MyDoggySetContext.ActionKey.NEST_TOOLMANAGER));

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

    protected void initToolWindows() {
        // Register tools

        // JXDatePicker panel
        JPanel toolOnePanel = new JPanel();

        final JLabel label = new JLabel();
        label.setText("Choose Date by selecting below.");
        toolOnePanel.add(label, BorderLayout.NORTH);
        final JXDatePicker datePicker = new JXDatePicker(System.currentTimeMillis());
        datePicker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText(datePicker.getDate().toString());
            }
        });
        toolOnePanel.add(datePicker, BorderLayout.CENTER);

        toolWindowManager.registerToolWindow("Tool 1", "Title 1", null, toolOnePanel, ToolWindowAnchor.BOTTOM);

        JPanel toolTwoPanel = new JPanel(new ExtendedTableLayout(new double[][]{{20, -1, 20}, {20, -1, 20}}));
        toolTwoPanel.add(new JButton("Hello World 2"), "1,1,FULL,FULL");
        toolWindowManager.registerToolWindow("Tool 2", "Title 2", null, toolTwoPanel, ToolWindowAnchor.RIGHT);

        toolWindowManager.registerToolWindow("Tool 3", "Title 3", SwingUtil.loadIcon("org/noos/xing/mydoggy/mydoggyset/icons/save.png"), new MainPanel(), ToolWindowAnchor.LEFT);
        toolWindowManager.registerToolWindow("Tool 4", "Title 4", null, new JButton("Hello World 4"), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("Tool 5", "Title 5", null, new JButton("Hello World 5"), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("Tool 6", "Title 6", null, new JButton("Hello World 6"), ToolWindowAnchor.BOTTOM);
        toolWindowManager.registerToolWindow("Tool 7", "Title 7", null, new MonitorPanel(new RuntimeMemoryMonitorSource()).start(), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("Tool 8", "Title 8", null, new JButton("Hello World 8"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 9", "Title 9", null, new JButton("Hello World 9"), ToolWindowAnchor.RIGHT);

        JPanel toolTenPanel = new JPanel();
        toolTenPanel.setFocusCycleRoot(true);
        toolTenPanel.add(new JTextField(10));
        toolWindowManager.registerToolWindow("Tool 10", "Title 10", null, toolTenPanel, ToolWindowAnchor.RIGHT);

        toolWindowManager.registerToolWindow("Tool 11", "Title 11", null, new JButton("Hello World 11"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 12", "Title 12", null, new JButton("Hello World 12"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 13", "Title 13", null, new JButton("Hello World 13"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Some Doggy Table", "Doggy Style", null, new JScrollPane(new DoggyTable()), ToolWindowAnchor.TOP);

        // Make all tools available
        for (ToolWindow window : toolWindowManager.getToolWindows()) {
            window.setAvailable(true);
        }

        TabbedContentManagerUI contentManagerUI = (TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI();
        contentManagerUI.setShowAlwaysTab(true);

//        customizeToolWindows();
    }


    protected void initContentManager() {
        // Setup ContentManagerUI
//        toolWindowManager.getContentManager().setContentManagerUI(new MyDoggyMultiSplitContentManagerUI());

//        MultiSplitContentManagerUI contentManagerUI = (MultiSplitContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI();
//        contentManagerUI.setPopupMenuEnabled(false);
//        contentManagerUI.setCloseable(false);
//        contentManagerUI.setDetachable(false);
//        contentManagerUI.setMinimizable(false);
//        contentManagerUI.setMaximizable(false);

        TabbedContentManagerUI contentManagerUI = (TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI();
        contentManagerUI.setShowAlwaysTab(true);
//        contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.BOTTOM);
//        contentManagerUI.setTabLayout(TabbedContentManagerUI.TabLayout.WRAP);
//        contentManagerUI.addContentManagerUIListener(new ContentManagerUIListener() {
//            public boolean contentUIRemoving(ContentManagerUIEvent event) {
//                return JOptionPane.showConfirmDialog(frame, "Are you sure?") == JOptionPane.OK_OPTION;
//            }
//
//            public void contentUIDetached(ContentManagerUIEvent event) {
//            }
//        });

//        contentManagerUI.setMinimizable(false);
    }


    protected void customizeToolWindows() {
        ToolWindow toolWindow;
        DockedTypeDescriptor dockedTypeDescriptor;

        // Setup Tool 1
        toolWindow = toolWindowManager.getToolWindow("Tool 1");
        toolWindow.getRepresentativeAnchorDescriptor().setTitle("Hello  World 1!!!");
        toolWindow.setAutoHide(true);
        dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
//        representativeAnchorDescriptor.setPopupMenuEnabled(false);
        dockedTypeDescriptor.setTitleBarButtonsVisible(false);
        dockedTypeDescriptor.setTitleBarVisible(false);
        dockedTypeDescriptor.setDockLength(200);

//        UIManager.put(MyDoggyKeySpace.DEBUG, false);

        toolWindow.addToolWindowAction(new ToolWindowCloseAction());

//        dockedTypeDescriptor.setToolWindowActionHandler(new ToolWindowActionHandler() {
//            public void onHideButtonClick(final ToolWindow toolWindow) {
//                toolWindowManager.unregisterToolWindow(toolWindow.getId());
//            }
//        });

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
        toolWindow.getRepresentativeAnchorDescriptor().setTitle("Tool 3 !!!");
        dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);

        JMenuItem menuItem = new JMenuItem("Hello World!!!");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Hello World!!!");
            }
        });
        dockedTypeDescriptor.getToolsMenu().add(menuItem);
        toolWindow.getRepresentativeAnchorDescriptor().setPreviewDelay(1500);
        dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.HIDE_ACTION_ID).setVisibleOnTitleBar(false);
        dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.MAXIMIZE_ACTION_ID).setVisible(false);

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
//        floatingTypeDescriptor.setOsDecorated(true);
        floatingTypeDescriptor.setAnimating(false);

        // Setup ContentManager
        toolWindowManager.getContentManager().addContentManagerListener(new ContentManagerListener() {
            public void contentAdded(ContentManagerEvent event) {
                event.getContent().addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
/*
                        StringBuffer sb = new StringBuffer("Event : ");
                        sb.append(evt.getPropertyName())
                                .append(" ; ")
                                .append(evt.getOldValue())
                                .append(" -> ")
                                .append(evt.getNewValue())
                                .append(" ; ")
                                .append(evt.getSource());
                        System.out.println(sb);
*/
//                new RuntimeException().printStackTrace();
//                System.out.println("----------------------------------------------------------");
                    }
                });
            }

            public void contentRemoved(ContentManagerEvent event) {
//                System.out.println("Content removed " + event);
            }

            public void contentSelected(ContentManagerEvent event) {
            }
        });
    }

    protected void customizeToolWindowManager(MyDoggyToolWindowManager myDoggyToolWindowManager) {

// Setup ContentManagerUI
//        toolWindowManager.getContentManager().setContentManagerUI(new MyDoggyMultiSplitContentManagerUI());

//        MultiSplitContentManagerUI contentManagerUI = (MultiSplitContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI();
//        contentManagerUI.setPopupMenuEnabled(false);
//        contentManagerUI.setCloseable(false);
//        contentManagerUI.setDetachable(false);
//        contentManagerUI.setMinimizable(false);
//        contentManagerUI.setMaximizable(false);

//        contentManagerUI.setShowAlwaysTab(true);
//        contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.BOTTOM);
//        contentManagerUI.setTabLayout(TabbedContentManagerUI.TabLayout.WRAP);
//        contentManagerUI.addContentManagerUIListener(new ContentManagerUIListener() {
//            public boolean contentUIRemoving(ContentManagerUIEvent event) {
//                return JOptionPane.showConfirmDialog(frame, "Are you sure?") == JOptionPane.OK_OPTION;
//            }
//
//            public void contentUIDetached(ContentManagerUIEvent event) {
//            }
//        });

//        contentManagerUI.setMinimizable(false);


        ToolWindowManagerDescriptor descriptor = myDoggyToolWindowManager.getToolWindowManagerDescriptor();
        descriptor.setShowUnavailableTools(true);

        ResourceManager resourceManager = myDoggyToolWindowManager.getResourceManager();
        // Add customization here. See the page http://mydoggy.sourceforge.net/mydoggy-plaf/resourceManagerUsing.html

/*
        resourceManager.putProperty("ContentManagerDropTarget.enabled", "true");
*/
        resourceManager.putProperty("ContentManagerUI.ContentManagerUiListener.import", "true");
        resourceManager.putBoolean(MyDoggyKeySpace.TOOL_WINDOW_PREVIEW_FULL, true);
/*
        resourceManager.setUserBundle(new ResourceBundle() {
            protected Object handleGetObject(String key) {
                if ("Tool 3".equals(key))
                    return "ciao";
                return key;
            }

            public Enumeration<String> getKeys() {
                return null;
            }
        });
*/

/*
        //TODO: move this property name to a class...
        resourceManager.putProperty(MyDoggyKeySpace.DRAG_ICON_TRANSPARENCY, "false");
        resourceManager.putProperty("drag.icon.useDefault", "true");
        resourceManager.putBoolean("drag.toolwindow.asTab", true);
*/
//        UIManager.put(MyDoggyKeySpace.DRAG_ENABLED, false);

        MyDoggyResourceManager myDoggyResourceManager = (MyDoggyResourceManager) myDoggyToolWindowManager.getResourceManager();

/*
        resourceManager.putColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START, Color.BLUE);
        resourceManager.putColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END, Color.GREEN);
        resourceManager.putColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START, Color.BLACK);
        resourceManager.putColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END, Color.GREEN.darker());
*/

/*
        resourceManager.putColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED, Color.GREEN);
        resourceManager.putColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED, Color.DARK_GRAY);
*/

/*
        resourceManager.putColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_START, Color.RED);
        resourceManager.putColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_END, Color.ORANGE);
*/

/*
        resourceManager.putColor(MyDoggyKeySpace.TWRA_FOREGROUND, Color.BLUE);
*/

//        UIManager.put("ToolWindowTitleButtonPanelUI", "org.noos.xing.mydoggy.plaf.ui.look.MenuToolWindowTitleButtonPanelUI");
//        UIManager.put("ToolWindowTitleBarUI", "org.noos.xing.mydoggy.mydoggyset.MyDoggySet$CustomToolWindowTitleBarUI");
//        UIManager.put("ToolWindowRepresentativeAnchorUI", "org.noos.xing.mydoggy.mydoggyset.MyDoggySet$CustomToolWindowRepresentativeAnchorUI");

        UIManager.put(MyDoggyKeySpace.MODAL_WINDOW_BORDER_LENGTH, 4);
        UIManager.put(MyDoggyKeySpace.DND_CONTENT_OUTSIDE_FRAME, true);

        UIManager.put(MyDoggyKeySpace.TWRA_ROTATE_ICON_ON_ANCHOR, false);

//        UIManager.put("ToolWindowTabTitleUI.font", new Font("Verdana", Font.BOLD, 15));
        UIManager.put("ToolWindowTitleBarUI.font", new Font("Verdana", Font.BOLD, 15));
//        UIManager.put("ToolWindowRepresentativeAnchorUI.font", new Font("Verdana", Font.BOLD, 15));

        myDoggyResourceManager.putInstanceCreator(ParentOfQuestion.class, new ObjectCreator() {
            public Object create(Context context) {
                return new CustomParentOfQuestion(context.get(Component.class),
                        context.get(ToolWindow.class));
            }
        });

        memoryMonitorDescriptor = new MemoryMonitorDockableDescriptor(myDoggyToolWindowManager, ToolWindowAnchor.BOTTOM);
    }


    public static void main(String[] args) {
        final MyDoggySet test = new MyDoggySet();
        try {
//            test.run(null);
//            test.run(new MultiSplitRandomConstraints(test));
//            test.run(new TabbedRandomConstraints(test));
            test.run(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class CustomParentOfQuestion implements Question<Component, Boolean> {
        protected Component parent;
        protected ToolWindow toolWindow;

        public CustomParentOfQuestion(Component parent, ToolWindow toolWindow) {
            this.parent = parent;
            this.toolWindow = toolWindow;
        }

        public Boolean getAnswer(Component param) {
            if (param == null)
                return false;

//            System.out.println("--------------------------------");

            Component cursor = param;
            try {
                while (cursor != null) {
//                    System.out.println("cursor = " + cursor);
                    if ((cursor instanceof JXMonthView && toolWindow.isActive()) || cursor == parent)
                        return true;
                    cursor = cursor.getParent();
                }
                return false;
            } finally {
//                System.out.println("--------------------------------");

            }
        }

    }

    public static class CustomToolWindowTitleBarUI extends ToolWindowTitleBarUI {

        public static ComponentUI createUI(JComponent c) {
            return new CustomToolWindowTitleBarUI();
        }

        public CustomToolWindowTitleBarUI() {
        }

        protected void updateToolWindowTitleBar(Graphics g, JComponent c, Color backgroundStart, Color backgroundEnd, Color idBackgroundColor, Color idColor) {
            Rectangle r = c.getBounds();
            r.x = r.y = 0;

            GraphicsUtil.fillRect(g, r,
                    backgroundStart, backgroundEnd,
                    null,
                    GraphicsUtil.LEFT_TO_RIGHT_GRADIENT);

            if (descriptor.isIdVisibleOnTitleBar() ||
                    toolWindow.getType() == ToolWindowType.FLOATING ||
                    toolWindow.getType() == ToolWindowType.FLOATING_FREE ||
                    toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {

                String id = SwingUtil.getUserString(descriptor.getToolWindow().getId());
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

    }

    public static class CustomToolWindowRepresentativeAnchorUI extends ToolWindowRepresentativeAnchorUI {

        public static ComponentUI createUI(JComponent c) {
            return new CustomToolWindowRepresentativeAnchorUI();
        }

        public CustomToolWindowRepresentativeAnchorUI() {
        }

        @Override
        protected void updateAnchor(Graphics g, JComponent c, Color backgroundStart,
                                    Color backgroundEnd, boolean active, boolean flashing) {
            Rectangle r = c.getBounds();
            r.x = r.y = 0;

            if (flashing || active) {
                GraphicsUtil.fillRect(g,
                        r,
                        backgroundStart,
                        backgroundEnd,
                        null,
                        GraphicsUtil.BOTTOM_TO_UP_GRADIENT);
            } else {
                g.setColor(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE));
                g.fillRect(0, 0, r.width, r.height);
            }
        }
    }

    public static class MemoryMonitorDockableDescriptor extends CustomDockableDescriptor {

        public MemoryMonitorDockableDescriptor(MyDoggyToolWindowManager manager, ToolWindowAnchor anchor) {
            super(manager, anchor);
        }

        public void updateRepresentativeAnchor() {
        }

        public JComponent getRepresentativeAnchor(Component parent) {
            if (representativeAnchor == null) {
                representativeAnchor = new MemoryMonitorPanel(anchor);
            }
            return representativeAnchor;
        }

        public boolean isAvailableCountable() {
            return false;
        }

        public class MemoryMonitorPanel extends JPanel {
            int sleepTime;

            public MemoryMonitorPanel(ToolWindowAnchor anchor) {
                sleepTime = 1000;

                final JProgressBar memoryUsage = new JProgressBar();
                memoryUsage.setStringPainted(true);

                JButton gc = new JButton(SwingUtil.loadIcon("org/noos/xing/mydoggy/mydoggyset/icons/gc.png"));
                gc.setBorderPainted(true);
                gc.setFocusable(false);
                gc.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                gc.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.gc();
                    }
                });

                Thread memoryThread = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            String grabbed = StringUtil.bytes2MBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
                            String total = StringUtil.bytes2MBytes(Runtime.getRuntime().totalMemory());

                            memoryUsage.setMaximum(Integer.parseInt(total));
                            memoryUsage.setValue(Integer.parseInt(grabbed));

                            memoryUsage.setString(grabbed + " MB of " + total + " MB");
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                });
                memoryThread.setDaemon(true);
                memoryThread.setPriority(Thread.MIN_PRIORITY);
                memoryThread.start();

                switch (anchor) {
                    case BOTTOM:
                    case TOP:
                        memoryUsage.setOrientation(SwingConstants.HORIZONTAL);
                        setLayout(new TableLayout(new double[][]{{120, 1, 17}, {-1}}));
                        add(memoryUsage, "0,0,FULL,FULL");
                        add(gc, "2,0,FULL,FULL");
                        break;
                    case LEFT:
                        memoryUsage.setOrientation(SwingConstants.VERTICAL);
                        setLayout(new TableLayout(new double[][]{{-1}, {120, 1, 17}}));
                        add(memoryUsage, "0,0,FULL,FULL");
                        add(gc, "0,2,FULL,FULL");
                        break;
                    case RIGHT:
                        memoryUsage.setOrientation(SwingConstants.VERTICAL);
                        setLayout(new TableLayout(new double[][]{{-1}, {17, 1, 120}}));
                        add(gc, "0,0,FULL,FULL");
                        add(memoryUsage, "0,2,FULL,FULL");
                        break;
                }

                registerDragListener(memoryUsage);
                registerDragListener(gc);
                registerDragListener(this);
            }

            public void setSleepTime(int sleepTime) {
                this.sleepTime = sleepTime;
            }

        }

    }


    public class MainPanel extends JPanel {

        protected JXTitledPanel navigatorPanel = null;
        protected JXTitledPanel infoPanel = null;


        public MainPanel() {
            super();
            init();
        }

        /**
         * Initialize the main panel
         */
        protected void init() {
            navigatorPanel = new JXTitledPanel("Navigation");


            JPanel jPanel = new JPanel();
            double size[][] = {{TableLayoutConstants.FILL}, // Columns
                    {25, 25, 25, 25, TableLayoutConstants.FILL}}; // Rows
            jPanel.setLayout(new TableLayout(size));
            jPanel.add(new JTextField("textfield1"), "0,0");
            jPanel.add(new JTextField("textfield2"), "0,1");
            jPanel.add(new JTextField("textfield3"), "0,2");
            jPanel.add(new JTextField("textfield4"), "0,3");
            infoPanel = new JXTitledPanel("Information", jPanel);


            JSplitPane mainSP = createSplitPane(200, JSplitPane.HORIZONTAL_SPLIT);
            mainSP.setLeftComponent(navigatorPanel);
            mainSP.setRightComponent(infoPanel);

            this.setLayout(new BorderLayout());
            this.add(infoPanel);
        }

        private JSplitPane createSplitPane(int dividerLocation, int orientation) {
            JSplitPane splitPane = new JSplitPane(orientation);
            splitPane.setDividerLocation(dividerLocation);
            splitPane.setBorder(null);
            ((BasicSplitPaneUI) splitPane.getUI()).getDivider().setBorder(BorderFactory.createEmptyBorder());
            return splitPane;
        }


    }

    public class ToolWindowCloseAction extends HideToolWindowAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            toolWindowManager.unregisterToolWindow(toolWindow.getId());
        }

    }
}
