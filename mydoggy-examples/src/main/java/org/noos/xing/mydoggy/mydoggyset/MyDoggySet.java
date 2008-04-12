package org.noos.xing.mydoggy.mydoggyset;

import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;
import org.noos.common.Question;
import org.noos.common.context.Context;
import org.noos.common.object.ObjectCreator;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowManagerDescriptor.Corner.*;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.mydoggyset.action.*;
import org.noos.xing.mydoggy.mydoggyset.context.MyDoggySetContext;
import org.noos.xing.mydoggy.mydoggyset.ui.LookAndFeelMenuItem;
import org.noos.xing.mydoggy.mydoggyset.ui.MonitorPanel;
import org.noos.xing.mydoggy.mydoggyset.ui.RuntimeMemoryMonitorSource;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.CustomDockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
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
        initToolWindowManager();
    }

    public void start(final Runnable runnable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                myDoggySetContext.put(MyDoggySet.class, null);
                SwingUtil.centrePositionOnScreen(frame);
                frame.setVisible(true);
                memoryMonitorDescriptor.setAvailable(true);

                if (runnable != null) {
                    Thread t = new Thread(runnable);
                    t.start();
                }
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
        this.frame = new JFrame("MyDoggy-Set 1.4.2 ...");
        this.frame.setSize(640, 480);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));

        // Init ToolWindowManager
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager(frame, Locale.US, null);

        // Apply now all customization if necessary
        customizeToolWindowManager(myDoggyToolWindowManager);

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

    protected void initToolWindowManager() {
        // Setup type descriptor templates...
        FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        typeDescriptor.setTransparentDelay(0);

        // Register tools
        JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{20, -1, 20}, {20, -1, 20}}));
        panel.add(new JButton("Hello World 2"), "1,1,FULL,FULL");

        // JXDatePicker panel
        final JLabel label = new JLabel();
        label.setText("Choose Date by selecting below.");

        final JXDatePicker datePicker = new JXDatePicker(System.currentTimeMillis());
        datePicker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText(datePicker.getDate().toString());
            }
        });

        JPanel toolOnePanel = new JPanel();
        toolOnePanel.add(label, BorderLayout.NORTH);
        toolOnePanel.add(datePicker, BorderLayout.CENTER);
        toolWindowManager.registerToolWindow("Tool 1", "Title 1", null, toolOnePanel, ToolWindowAnchor.LEFT);
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

        JPanel form1 = new JPanel();
        form1.setFocusCycleRoot(true);
        form1.add(new JTextField(10));

        toolWindowManager.registerToolWindow("Tool 10", "Title 10", null, form1/*new JButton("Hello World 10")*/, ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 11", "Title 11", null, new JButton("Hello World 11"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 12", "Title 12", null, new JButton("Hello World 12"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("Tool 13", "Title 13", null, new JButton("Hello World 13"), ToolWindowAnchor.RIGHT);

        // Make all available
        for (ToolWindow window : toolWindowManager.getToolWindows()) {
            window.setAvailable(true);
        }

        // Setup Tool 1
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Tool 1");
        toolWindow.setAutoHide(true);

/*
        toolWindow.getTypeDescriptor(SlidingTypeDescriptor.class).setEnabled(false);
        toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class).setEnabled(false);
        toolWindow.getTypeDescriptor(FloatingLiveTypeDescriptor.class).setEnabled(false);
*/

        DockedTypeDescriptor dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
//        dockedTypeDescriptor.setPopupMenuEnabled(false);
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

        // Setup ContentManager
        toolWindowManager.getContentManager().addContentManagerListener(new ContentManagerListener() {
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

//        contentManagerUI.setMinimizable(false);

        // Setup Corner Components
        ToolWindowManagerDescriptor managerDescriptor = toolWindowManager.getToolWindowManagerDescriptor();

        JButton nwButton = new JButton(SwingUtil.loadIcon("org/noos/xing/mydoggy/mydoggyset/icons/plus.png"));
        nwButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ResourceManager resourceManager = ((MyDoggyToolWindowManager) toolWindowManager).getResourceManager();
                resourceManager.putProperty(
                                MyDoggyKeySpace.TOOL_WINDOW_HORIZONTAL_BAR_LENGTH,
                                String.valueOf(resourceManager.getInt(MyDoggyKeySpace.TOOL_WINDOW_HORIZONTAL_BAR_LENGTH, 23) + 1)
                        );
            }
        });

        JButton swButton = new JButton(SwingUtil.loadIcon("org/noos/xing/mydoggy/mydoggyset/icons/minus.png"));
        swButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ResourceManager resourceManager = ((MyDoggyToolWindowManager) toolWindowManager).getResourceManager();
                resourceManager.putProperty(
                                MyDoggyKeySpace.TOOL_WINDOW_HORIZONTAL_BAR_LENGTH,
                                String.valueOf(resourceManager.getInt(MyDoggyKeySpace.TOOL_WINDOW_HORIZONTAL_BAR_LENGTH, 23) - 1)
                        );
            }
        });

        JButton neButton = new JButton(SwingUtil.loadIcon("org/noos/xing/mydoggy/mydoggyset/icons/plus.png"));
        neButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ResourceManager resourceManager = ((MyDoggyToolWindowManager) toolWindowManager).getResourceManager();
                resourceManager.putProperty(
                                MyDoggyKeySpace.TOOL_WINDOW_VERTICAL_BAR_LENGTH,
                                String.valueOf(resourceManager.getInt(MyDoggyKeySpace.TOOL_WINDOW_VERTICAL_BAR_LENGTH, 23) + 1)
                        );
            }
        });

        JButton seButton = new JButton(SwingUtil.loadIcon("org/noos/xing/mydoggy/mydoggyset/icons/minus.png"));
        seButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ResourceManager resourceManager = ((MyDoggyToolWindowManager) toolWindowManager).getResourceManager();
                resourceManager.putProperty(
                                MyDoggyKeySpace.TOOL_WINDOW_VERTICAL_BAR_LENGTH,
                                String.valueOf(resourceManager.getInt(MyDoggyKeySpace.TOOL_WINDOW_VERTICAL_BAR_LENGTH, 23) - 1)
                        );
            }
        });

        managerDescriptor.setCornerComponent(NORD_WEST, nwButton);
        managerDescriptor.setCornerComponent(SOUTH_WEST, swButton);
        managerDescriptor.setCornerComponent(NORD_EAST, neButton);
        managerDescriptor.setCornerComponent(SOUTH_EAST, seButton);

        // Add MyDoggyToolWindowManager to frame
        this.frame.getContentPane().add((Component) toolWindowManager, "1,1,");
    }

    protected void customizeToolWindowManager(MyDoggyToolWindowManager myDoggyToolWindowManager) {
        ResourceManager resourceManager = myDoggyToolWindowManager.getResourceManager();

        // Add customization here. See the page http://mydoggy.sourceforge.net/mydoggy-plaf/resourceManagerUsing.html
/*
        resourceManager.putProperty("dialog.owner.enabled", "false");
*/
        resourceManager.putProperty("ContentManagerDropTarget.enabled", "true");
        resourceManager.putProperty("ContentManagerUI.ContentManagerUiListener.import", "true");
/*
        resourceManager.putProperty("drag.icon.transparency.enabled", "false");
        resourceManager.putProperty("drag.icon.useDefault", "true");
*/

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
        resourceManager.putColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START, Color.RED);
        resourceManager.putColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END, Color.ORANGE);
*/

/*
        resourceManager.putColor(MyDoggyKeySpace.RAB_FOREGROUND, Color.BLUE);
*/

/*
        myDoggyResourceManager.putComponentUICreator(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR_UI,
                                                     new ObjectCreator<ComponentUI>() {
                                                         public ComponentUI create(Context context) {
                                                             return new ToolWindowTitleBarUI(context.get(ToolWindowDescriptor.class) ,
                                                                                             context.get(ToolWindowContainer.class)) {
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

        myDoggyResourceManager.putInstanceCreator(TitleBarButtons.class,
                                                  new ObjectCreator() {
                                                      public Object create(Context context) {
                                                          return new MenuTitleBarButtons(
                                                                  context.get(ToolWindowDescriptor.class),
                                                                  context.get(ToolWindowContainer.class)
                                                          );
                                                      }
                                                  });
*/

        myDoggyResourceManager.putInstanceCreator(ParentOfQuestion.class, new ObjectCreator() {
            public Object create(Context context) {
                return new CustomParentOfQuestion(context.get(Component.class),
                                                  context.get(ToolWindow.class));
            }
        });


        memoryMonitorDescriptor = new MemoryMonitorDockableDescriptor(myDoggyToolWindowManager, ToolWindowAnchor.BOTTOM);
    }

    protected void dispose() {
        frame.setVisible(false);
        frame.dispose();
    }


    public static void main(String[] args) {
        MyDoggySet test = new MyDoggySet();
        try {
            test.setUp();
//            test.toolWindowManager.getContentManager().setEnabled(false);

//            test.start(new MultiSplitRandomConstraints(test));
//            test.start(new TabbedRandomConstraints(test));
            test.start(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class CustomParentOfQuestion implements Question {
        protected Component parent;
        protected ToolWindow toolWindow;

        public CustomParentOfQuestion(Component parent, ToolWindow toolWindow) {
            this.parent = parent;
            this.toolWindow = toolWindow;
        }

        public boolean is(Object... params) {
            if (params.length == 0)
                return false;

            Component component = (Component) params[0];
            if (component == null)
                return false;

            Component cursor = component;
            while (cursor != null) {
                if ((cursor instanceof JXMonthView && toolWindow.isActive()) || cursor == parent) 
                    return true;
                cursor = cursor.getParent();
            }
            return false;
        }
    }

    public static class MemoryMonitorDockableDescriptor extends CustomDockableDescriptor {

        public MemoryMonitorDockableDescriptor(MyDoggyToolWindowManager manager, ToolWindowAnchor anchor) {
            super(manager, anchor);
        }

        public void updateRepresentativeAnchor() {
        }

        public JComponent getRepresentativeAnchor(Component parent) {
            if (representativeAnchor == null)
                representativeAnchor = new MemoryMonitorPanel(anchor);
            return representativeAnchor;
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
                        setLayout(new TableLayout(new double[][]{{-1},{120, 1, 17}}));
                        add(memoryUsage, "0,0,FULL,FULL");
                        add(gc, "0,2,FULL,FULL");
                        break;
                    case RIGHT:
                        memoryUsage.setOrientation(SwingConstants.VERTICAL);
                        setLayout(new TableLayout(new double[][]{{-1},{17, 1, 120}}));
                        add(gc, "0,0,FULL,FULL");
                        add(memoryUsage, "0,2,FULL,FULL");
                        break;
                }

                registerDragGesture(memoryUsage);
                registerDragGesture(gc);
                registerDragGesture(this);
            }

            public void setSleepTime(int sleepTime) {
                this.sleepTime = sleepTime;
            }

        }

    }


    public static class MultiSplitRandomConstraints implements Runnable {
        MyDoggySet myDoggySet;

        public MultiSplitRandomConstraints(MyDoggySet myDoggySet) {
            this.myDoggySet = myDoggySet;
        }

        public void run() {
            try {
                Random random = new Random();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(ToolWindowManager.class, null);
                    }
                });
                Thread.sleep(2000);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(ToolWindow.class, null);
                    }
                });
                Thread.sleep(2000);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(Content.class, null);
                    }
                });

                Thread.sleep(2000);
                for (int i = 0; i < 200; i++) {
                    int index = random.nextInt(4);
                    Content content = null;
                    switch (index) {
                        case 0:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Welcome");
                            break;
                        case 1:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Manager");
                            break;
                        case 2:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Tools");
                            break;
                        case 3:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Contents");
                            break;
                    }


                    index = random.nextInt(2);
                    Content contentOn = null;
                    switch (index) {
                        case 0:
                            index = random.nextInt(4);
                            switch (index) {
                                case 0:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Welcome");
                                    break;
                                case 1:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Manager");
                                    break;
                                case 2:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Tools");
                                    break;
                                case 3:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Contents");
                                    break;
                            }
                            if (contentOn == content)
                                contentOn = null;
                            break;

                    }

                    index = random.nextInt(2);
                    AggregationPosition aggregationPosition = null;
                    switch (index) {
                        case 0:
                            index = random.nextInt(5);
                            switch (index) {
                                case 0:
                                    aggregationPosition = AggregationPosition.BOTTOM;
                                    break;
                                case 1:
                                    aggregationPosition = AggregationPosition.TOP;
                                    break;
                                case 2:
                                    aggregationPosition = AggregationPosition.LEFT;
                                    break;
                                case 3:
                                    aggregationPosition = AggregationPosition.RIGHT;
                                    break;
                                case 4:
                                    aggregationPosition = AggregationPosition.DEFAULT;
                                    break;
                            }
                            if (contentOn == content)
                                contentOn = null;
                            break;

                    }

                    MultiSplitConstraint constraint = new MultiSplitConstraint(
                            contentOn, aggregationPosition
                    );

                    StringBuffer sb = new StringBuffer();
                    sb.append("apply(\"").append(content.getId()).append("\",");
                    if (contentOn != null)
                        sb.append("\"").append(contentOn.getId()).append("\",");
                    if (aggregationPosition != null)
                        sb.append(aggregationPosition);
                    sb.append(");");

                    System.out.println(sb);

                    content.getContentUI().setConstraints(constraint);

                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class TabbedRandomConstraints implements Runnable {
        MyDoggySet myDoggySet;

        public TabbedRandomConstraints(MyDoggySet myDoggySet) {
            this.myDoggySet = myDoggySet;
        }

        public void run() {
            try {
                Random random = new Random();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(ToolWindowManager.class, null);
                    }
                });
                Thread.sleep(2000);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(ToolWindow.class, null);
                    }
                });
                Thread.sleep(2000);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myDoggySet.getMyDoggySetContext().put(Content.class, null);
                    }
                });

                Thread.sleep(2000);
                for (int i = 0; i < 200; i++) {
                    int index = random.nextInt(4);
                    Content content = null;
                    switch (index) {
                        case 0:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Welcome");
                            break;
                        case 1:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Manager");
                            break;
                        case 2:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Tools");
                            break;
                        case 3:
                            content = myDoggySet.getToolWindowManager().getContentManager().getContent("Contents");
                            break;
                    }


                    index = random.nextInt(2);
                    Content contentOn = null;
                    switch (index) {
                        case 0:
                            index = random.nextInt(4);
                            switch (index) {
                                case 0:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Welcome");
                                    break;
                                case 1:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Manager");
                                    break;
                                case 2:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Tools");
                                    break;
                                case 3:
                                    contentOn = myDoggySet.getToolWindowManager().getContentManager().getContent("Contents");
                                    break;
                            }
                            if (contentOn == content)
                                contentOn = null;
                            break;

                    }

                    index = random.nextInt(4);
                    
                    content.getContentUI().setConstraints(index);

                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
