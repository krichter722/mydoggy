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
import org.noos.xing.mydoggy.mydoggyset.action.*;
import org.noos.xing.mydoggy.mydoggyset.context.MyDoggySetContext;
import org.noos.xing.mydoggy.mydoggyset.ui.DoggyTable;
import org.noos.xing.mydoggy.mydoggyset.ui.LookAndFeelMenuItem;
import org.noos.xing.mydoggy.mydoggyset.ui.MonitorPanel;
import org.noos.xing.mydoggy.mydoggyset.ui.RuntimeMemoryMonitorSource;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.CustomDockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.look.MyDoggyResourceManager;
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.StringUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        System.out.println("> Time (millis) too load the manager : " + (end - start));

        this.toolWindowManager = myDoggyToolWindowManager;

        // Add MyDoggyToolWindowManager to frame
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");

        // Init additional dockable
        this.memoryMonitorDescriptor = new MemoryMonitorDockableDescriptor((MyDoggyToolWindowManager) toolWindowManager, ToolWindowAnchor.BOTTOM);

        // Apply now all customization if necessary
        customize();

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
    }

    protected void customize() {
        MyDoggyToolWindowManager myDoggyToolWindowManager = (MyDoggyToolWindowManager) toolWindowManager;
        ResourceManager resourceManager = myDoggyToolWindowManager.getResourceManager();
        ((MyDoggyResourceManager) resourceManager).putInstanceCreator(ParentOfQuestion.class, new ObjectCreator() {
            public Object create(Context context) {
                return new CustomParentOfQuestion(context.get(Component.class), context.get(ToolWindow.class));
            }
        });
    }


    public static void main(String[] args) {
/*
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                System.out.println("event = " + event);
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);

        System.out.println("UIManager.getLookAndFeel() = " + UIManager.getLookAndFeel());

        Enumeration<Object> keysEnumeration = UIManager.getDefaults().keys();
        while (keysEnumeration.hasMoreElements()) {
            Object key = keysEnumeration.nextElement();
            System.out.printf("%s - %s\n", key, UIManager.get(key));
        }
*/

        final MyDoggySet test = new MyDoggySet();
        test.run(null);
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

}
