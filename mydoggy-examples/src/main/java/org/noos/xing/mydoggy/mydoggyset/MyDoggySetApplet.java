package org.noos.xing.mydoggy.mydoggyset;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;
import org.noos.common.Question;
import org.noos.common.context.Context;
import org.noos.common.object.ObjectCreator;
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
import org.noos.xing.mydoggy.plaf.ui.look.MyDoggyResourceManager;
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
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
public class MyDoggySetApplet extends JApplet {
    protected ToolWindowManager toolWindowManager;
    protected ViewContext myDoggySetContext;


    public void init() {
        setUp();
    }

    public void start() {
        start(null);
    }


    public void setUp() {
        initComponents();
        initToolWindowManager();
    }

    public void start(final Runnable runnable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                myDoggySetContext.put(MyDoggySet.class, null);

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
        setSize(640, 480);
        getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));

        // Init ToolWindowManager
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager(Locale.US, null);

        // Apply now all customization if necessary
        customizeToolWindowManager(myDoggyToolWindowManager);

        this.toolWindowManager = myDoggyToolWindowManager;
        this.myDoggySetContext = new MyDoggySetContext(toolWindowManager, this);
        initMenuBar();
    }

    protected void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new LoadWorkspaceAction(this, toolWindowManager));
        fileMenu.add(new StoreWorkspaceAction(this, toolWindowManager));
        fileMenu.addSeparator();
        fileMenu.add(new FrameshotAction(this));
        fileMenu.add(new FramePieceshotAction(this));
        fileMenu.add(new MagnifierAction(this));

        // Content Menu
        JMenu contentMenu = new JMenu("Content");
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

        setJMenuBar(menuBar);
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
        toolWindowManager.registerToolWindow("Tool 1", "Title 1", null, toolOnePanel/*new JButton("Hello World 1")*/, ToolWindowAnchor.LEFT);

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
//            window.getTypeDescriptor(DockedTypeDescriptor.class).setHideRepresentativeButtonOnVisible(true);
        }

        // Setup Tool 1
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Tool 1");
        toolWindow.setAutoHide(true);

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
                JOptionPane.showMessageDialog(MyDoggySetApplet.this, "Hello World!!!");
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
                return JOptionPane.showConfirmDialog(MyDoggySetApplet.this, "Are you sure?") == JOptionPane.OK_OPTION;
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
        getContentPane().add((Component) toolWindowManager, "1,1,");
    }

    protected void customizeToolWindowManager(MyDoggyToolWindowManager myDoggyToolWindowManager) {
        ResourceManager resourceManager = myDoggyToolWindowManager.getResourceManager();

        // Add customization here. See the page http://mydoggy.sourceforge.net/mydoggy-plaf/resourceManagerUsing.html
/*
        resourceManager.putProperty("dialog.owner.enabled", "false");
        resourceManager.putProperty("ContentManagerDropTarget.enabled", "true");
*/
        resourceManager.putProperty("ContentManagerUI.ContentManagerUiListener.import", "true");
        resourceManager.putProperty("drag.icon.transparency.enabled", "false");
        resourceManager.putProperty("drag.icon.useDefault", "true");

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
                                                     new MyDoggyResourceManager.ComponentUICreator() {

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
*/
/*
        myDoggyResourceManager.putInstanceCreator(TitleBarButtons.class,
                                                  new MyDoggyResourceManager.InstanceCreator() {
                                                      public Object createComponent(Object... args) {
                                                          return new MenuTitleBarButtons(
                                                                  (ToolWindowDescriptor) args[0],
                                                                  (DockedContainer) args[1]
                                                          );
                                                      }
                                                  }
        );
*/

/*
        myDoggyResourceManager.putInstanceCreator(TitleBarButtons.class,
                                                  new MyDoggyResourceManager.InstanceCreator() {
                                                      public Object createComponent(Object... args) {
                                                          return new MenuTitleBarButtons(
                                                                  (ToolWindowDescriptor) args[0],
                                                                  (DockedContainer) args[1]
                                                          );
                                                      }
                                                  }
        );
*/
        myDoggyResourceManager.putInstanceCreator(ParentOfQuestion.class, new ObjectCreator() {
            public Object create(Context context) {
                return new CustomParentOfQuestion(context.get(Component.class),
                                                  context.get(ToolWindow.class));
            }
        });
    }


    public class CustomParentOfQuestion implements Question<Component, Boolean> {
        protected Component parent;
        protected ToolWindow toolWindow;

        public CustomParentOfQuestion(Component parent, ToolWindow toolWindow) {
            this.parent = parent;
            this.toolWindow = toolWindow;
        }

        public Boolean getAnswer(Component param) {
            if (param == null)
                return false;

            Component cursor = param;
            while (cursor != null) {
                if ((cursor instanceof JXMonthView && toolWindow.isActive()) || cursor == parent)
                    return true;
                cursor = cursor.getParent();
            }
            return false;
        }

    }

}