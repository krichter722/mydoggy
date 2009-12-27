package org.noos.xing.mydoggy.mydoggyset;

import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXMonthView;
import org.noos.common.Question;
import org.noos.common.context.Context;
import org.noos.common.object.ObjectCreator;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.mydoggyset.action.*;
import org.noos.xing.mydoggy.mydoggyset.context.MyDoggySetContext;
import org.noos.xing.mydoggy.mydoggyset.ui.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.CustomDockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowPanel;
import org.noos.xing.mydoggy.plaf.ui.look.MyDoggyResourceManager;
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.StringUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

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
        contentMenu.add(new ViewContextAction("Tools", myDoggySetContext, ToolWindow.class));
        contentMenu.add(new ViewContextAction("Contents", myDoggySetContext, Content.class));
        contentMenu.add(new ViewContextAction("Groups", myDoggySetContext, ToolWindowGroup.class));
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

    protected void initToolWindows() {
        // Register tools
        Random random = new Random();
        for (int i = 1; i < 11; i++) {
            toolWindowManager.registerToolWindow("Tool " + i, "Title " + i, null, new JButton("Hello World " + i), ToolWindowAnchor.values()[random.nextInt(4)]);
        }


        toolWindowManager.registerToolWindow("Navigation Panel", "Navigation Panel", null, new NavigationPanel(), ToolWindowAnchor.values()[random.nextInt(4)]);
        toolWindowManager.registerToolWindow("Date Picker", "Date Picker", null, new DatePicker(), ToolWindowAnchor.values()[random.nextInt(4)]);
        toolWindowManager.registerToolWindow("Monitor Panel", "Monitor Panel", null, new MonitorPanel(new RuntimeMemoryMonitorSource()).start(), ToolWindowAnchor.values()[random.nextInt(4)]);
        toolWindowManager.registerToolWindow("Doggy Table", "Doggy Table", null, new JScrollPane(new DoggyTable()), ToolWindowAnchor.values()[random.nextInt(4)]);

        final JButton jButton = new JButton();
        jButton.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ToolWindowPanel toolWindowPanel = SwingUtil.getParent(jButton, ToolWindowPanel.class);

                SwingUtilities.updateComponentTreeUI(toolWindowPanel);

            }
        });
        toolWindowManager.registerToolWindow("Change UI", "Change UI", null, jButton, ToolWindowAnchor.values()[random.nextInt(4)]);

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
                return new DatePickerParentOfSupport(context.get(Component.class), context.get(ToolWindow.class));
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


    public static class DatePickerParentOfSupport implements Question<Component, Boolean> {
        protected Component parent;
        protected ToolWindow toolWindow;

        public DatePickerParentOfSupport(Component parent, ToolWindow toolWindow) {
            this.parent = parent;
            this.toolWindow = toolWindow;
        }

        public Boolean getAnswer(Component param) {
            if (param == null)
                return false;

            Component cursor = param;
            try {
                while (cursor != null) {
                    if ((cursor instanceof JXMonthView && toolWindow.isActive()) || cursor == parent)
                        return true;
                    cursor = cursor.getParent();
                }
                return false;
            } finally {
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

}
