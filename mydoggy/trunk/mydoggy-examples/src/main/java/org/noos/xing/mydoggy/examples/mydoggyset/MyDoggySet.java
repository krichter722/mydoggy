package org.noos.xing.mydoggy.examples.mydoggyset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.examples.mydoggyset.model.ContentsTableModel;
import org.noos.xing.mydoggy.examples.mydoggyset.model.ToolGroupsTableModel;
import org.noos.xing.mydoggy.examples.mydoggyset.model.ToolsTableModel;
import org.noos.xing.mydoggy.examples.mydoggyset.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.desktop.MyDoggyDesktopContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.FileInputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggySet {
    // Possible Look & Feels
    private String currentLookAndFeel;

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    private Component toolsContent;
    private Component groupEditorContent;
    private Component contentsContent;

    private JPopupMenu toolsPopupMenu;
    private JPopupMenu groupsPopupMenu;

    private JMenu lafMenu;

    private ContentManagerUI defaultManagerUI;

    protected void setUp() {
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
    }


    protected void initComponents() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        this.frame = new JFrame("MyDoggy-Set...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));

        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem load = new JMenuItem("Load Workspace");
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    FileInputStream inputStream = new FileInputStream("workspace.xml");
                    toolWindowManager.getPersistenceDelegate().apply(inputStream);
                    inputStream.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        fileMenu.add(load);

        JMenuItem store = new JMenuItem("Store Workspace");
        store.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    FileOutputStream output = new FileOutputStream("workspace.xml");
                    toolWindowManager.getPersistenceDelegate().save(output);
                    output.close();
                } catch (Exception e1) {
                    e1.printStackTrace(); 
                }
            }
        });
        fileMenu.add(store);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        fileMenu.addSeparator();
        fileMenu.add(exit);

        // Content Menu
        JMenu contentMenu = new JMenu("Content");

        JMenuItem toolsContentItem = new JMenuItem("Tools");
        toolsContentItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContentManager contentManager = toolWindowManager.getContentManager();
                if (contentManager.getContent("Tools") == null)
                    contentManager.addContent("Tools", "Tools", null, toolsContent, "ToolWindows").setPopupMenu(toolsPopupMenu);
            }
        });

        JMenuItem groupEditorContentItem = new JMenuItem("Groups");
        groupEditorContentItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContentManager contentManager = toolWindowManager.getContentManager();

                if (contentManager.getContent("Group Editor") == null)
                    contentManager.addContent("Group Editor", "Group Editor", null, groupEditorContent, "ToolWindowGroup").setPopupMenu(groupsPopupMenu);
            }
        });

        JMenuItem contentsContentItem = new JMenuItem("Contents");
        contentsContentItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContentManager contentManager = toolWindowManager.getContentManager();

                if (contentManager.getContent("Contents") == null)
                    contentManager.addContent("Contents", "Contents", null, contentsContent, "Contents Editor");
            }
        });

        contentMenu.add(toolsContentItem);
        contentMenu.add(groupEditorContentItem);
        contentMenu.add(contentsContentItem);

        // Content Manager UI

        JMenu contentManagerMenu = new JMenu("Content Manager UI");

        final JMenuItem tabUIItem = new JMenuItem("TabbedContentManagerUI");
        final JMenuItem desktopUIItem = new JMenuItem("DesktopContentManagerUI");

        tabUIItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolWindowManager.getContentManager().setContentManagerUI(defaultManagerUI);
                tabUIItem.setEnabled(false);
                desktopUIItem.setEnabled(true);
            }
        });

        desktopUIItem.addActionListener(new ActionListener() {
            final MyDoggyDesktopContentManagerUI desktopContentManagerUI = new MyDoggyDesktopContentManagerUI();

            public void actionPerformed(ActionEvent e) {
                toolWindowManager.getContentManager().setContentManagerUI(desktopContentManagerUI);
                tabUIItem.setEnabled(true);
                desktopUIItem.setEnabled(false);                
            }
        });

        contentManagerMenu.add(tabUIItem);
        contentManagerMenu.add(desktopUIItem);

        // L&F Menu
        lafMenu = new JMenu("Looks");

        String currentLaF = UIManager.getLookAndFeel().getName();

        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo aLafInfo : lafInfo) {
            JMenuItem mi = createLafMenuItem(lafMenu, aLafInfo.getName(), aLafInfo.getClassName());
            if (currentLaF.equals(aLafInfo.getName())) {
                mi.setSelected(true);
                currentLookAndFeel = aLafInfo.getClassName();
            }
        }

        // File Menu
        JMenu pushAwayMenu = new JMenu("PushAway Mode");

        final JMenuItem paHorizontal = new JMenuItem("Horizontal");
        final JMenuItem paVertical = new JMenuItem("Vertical");
        final JMenuItem paAnticlockwise = new JMenuItem("Anticlockwise");

        paHorizontal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolWindowManager.getToolWindowManagerDescriptor().setPushAwayMode(PushAwayMode.HORIZONTAL);
                paHorizontal.setEnabled(false);
                paVertical.setEnabled(true);
                paAnticlockwise.setEnabled(true);
            }
        });

        paVertical.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolWindowManager.getToolWindowManagerDescriptor().setPushAwayMode(PushAwayMode.VERTICAL);
                paVertical.setEnabled(false);
                paHorizontal.setEnabled(true);
                paAnticlockwise.setEnabled(true);
            }
        });

        paAnticlockwise.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolWindowManager.getToolWindowManagerDescriptor().setPushAwayMode(PushAwayMode.ANTICLOCKWISE);
                paAnticlockwise.setEnabled(false);
                paHorizontal.setEnabled(true);
                paVertical.setEnabled(true);
            }
        });

        paVertical.setEnabled(false);

        pushAwayMenu.add(paHorizontal);
        pushAwayMenu.add(paVertical);
        pushAwayMenu.add(paAnticlockwise);


        menuBar.add(fileMenu);
        menuBar.add(contentMenu);
        menuBar.add(contentManagerMenu);
        menuBar.add(lafMenu);
        menuBar.add(pushAwayMenu);

        this.frame.setJMenuBar(menuBar);
    }

    protected void initToolWindowManager() {
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager(frame);
        this.toolWindowManager = myDoggyToolWindowManager;

        FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        typeDescriptor.setTransparentDelay(0);


        JPanel panel = new JPanel(new TableLayout(new double[][]{{20, -1, 20}, {20, -1, 20}}));
        panel.add(new JButton("Hello World 2"), "1,1,FULL,FULL");

		toolWindowManager.registerToolWindow("1", "Title 1", null, new JButton("Hello World 1"), ToolWindowAnchor.LEFT);
        toolWindowManager.registerToolWindow("2", "Title 2", null, panel, ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("3", "Title 3",
                                             SwingUtil.loadIcon("org/noos/xing/mydoggy/examples/mydoggyset/icons/save.png"),
                                             new JButton("Hello World 3"), ToolWindowAnchor.LEFT);
        toolWindowManager.registerToolWindow("4", "Title 4", null, new JButton("Hello World 4"), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("5", "Title 5", null, new JButton("Hello World 5"), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("6", "Title 6", null, new JButton("Hello World 6"), ToolWindowAnchor.BOTTOM);
        toolWindowManager.registerToolWindow("7", "Title 7", null, new JButton("Hello World 7"), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("8", "Title 8", null, new JButton("Hello World 8"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("9", "Title 9", null, new JButton("Hello World 9"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("10", "Title 10", null, new JButton("Hello World 10"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("11", "Title 11", null, new JButton("Hello World 11"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("12", "Title 12", null, new JButton("Hello World 12"), ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("13", "Title 13", null, new JButton("Hello World 13"), ToolWindowAnchor.RIGHT);

        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);

        // Create two groups
        ToolWindowGroup mainGroup = toolWindowManager.getToolWindowGroup("Main");
        ToolWindowGroup submainGroup = toolWindowManager.getToolWindowGroup("SubMain");

        // Set TypeDescriptor properties for tool window 1
        ToolWindow toolWindow = toolWindowManager.getToolWindow("1");

        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        dockedTypeDescriptor.setPopupMenuEnabled(false);
        dockedTypeDescriptor.setDockLength(300);

        mainGroup.addToolWindow(toolWindow);

        // Set properties for tool window 2
        toolWindow = toolWindowManager.getToolWindow("2");
        dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        dockedTypeDescriptor.getToolsMenu().add(new JMenuItem("Prova"));

        toolWindow.setType(ToolWindowType.FLOATING_FREE);

        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_FREE);
        descriptor.setLocation(100, 100);
        descriptor.setSize(250, 250);

        submainGroup.addToolWindow(toolWindow);

        toolWindow = toolWindowManager.getToolWindow("3");
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

        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor ) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setEnabled(false);

        mainGroup.addToolWindow(toolWindow);

        // Set properties for tool window 4
        toolWindow = toolWindowManager.getToolWindow("4");
        toolWindow.setType(ToolWindowType.FLOATING_FREE);
        submainGroup.addToolWindow(toolWindow);

        // Set properties for tool window 5
        toolWindow = toolWindowManager.getToolWindow("5");
        toolWindow.setType(ToolWindowType.FLOATING_FREE);

        submainGroup.addToolWindow(toolWindowManager.getToolWindow("6"));

        // Set properties for tool window 7
        toolWindow = toolWindowManager.getToolWindow("7");
        toolWindow.setType(ToolWindowType.FLOATING);

        floatingTypeDescriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setModal(true);

        toolsContent = initToolsContent();
        groupEditorContent = initGroupEditorContent();
        contentsContent = initContentsContent();

        defaultManagerUI = toolWindowManager.getContentManager().getContentManagerUI();
        TabbedContentManagerUI tabbedContentManagerUI = (TabbedContentManagerUI) defaultManagerUI;
        tabbedContentManagerUI.setShowAlwaysTab(false);
        tabbedContentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.BOTTOM);
        tabbedContentManagerUI.setTabLayout(TabbedContentManagerUI.TabLayout.WRAP);
//        tabbedContentManagerUI.setCloseable(false);
//        tabbedContentManagerUI.setDetachable(false);

        toolWindowManager.getContentManager().addContent("Tools", "Tools", null, toolsContent, "ToolWindows")
                .setPopupMenu(toolsPopupMenu);
        assert toolWindowManager.getContentManager().getContentManagerUI().getContentUI(
                toolWindowManager.getContentManager().getContent(0)
        ) != null;

//        toolWindowManager.getToolWindowManagerDescriptor().setPushAwayMode(ToolWindowManagerDescriptor.PushAwayMode.HORIZONTAL);

        // Add MyDoggyToolWindowManager to frame
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");
    }

    protected Component initToolsContent() {
        JTable toolsTable = new JTable(new ToolsTableModel(toolWindowManager));
        toolsTable.getTableHeader().setReorderingAllowed(false);

        // Type column
        JComboBox types = new JComboBox(new Object[]{ToolWindowType.DOCKED,
                                                     ToolWindowType.SLIDING, ToolWindowType.FLOATING, ToolWindowType.FLOATING_FREE});
        toolsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(types));

        // Anchor column
        JComboBox anchors = new JComboBox(new Object[]{ToolWindowAnchor.LEFT,
                                                       ToolWindowAnchor.RIGHT, ToolWindowAnchor.BOTTOM, ToolWindowAnchor.TOP});
        toolsTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(anchors));

        // Available, Visible, Active columns
        JCheckBox booleanEditor = new JCheckBox();
        booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);
        toolsTable.getColumnModel().getColumn(4).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(booleanEditor));

        toolsTable.getColumnModel().getColumn(5).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(booleanEditor));

        toolsTable.getColumnModel().getColumn(6).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(booleanEditor));

        // Index column
        TableColumn indexColumn = toolsTable.getColumnModel().getColumn(7);
        indexColumn.setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Integer index = (Integer) value;
                if (index == -1)
                    setText("No Index");
                return c;
            }
        });
        JComboBox indexs = new JComboBox(new Object[]{-1, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        indexs.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Integer toolIndex = (Integer) value;
                if (toolIndex == -1)
                    setText("No Index");
                return c;
            }
        });
        indexColumn.setCellEditor(new DefaultCellEditor(indexs));

        toolsPopupMenu = new JPopupMenu("Tools");
        toolsPopupMenu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                toolsPopupMenu.removeAll();
                for (ToolWindow toolWindow : toolWindowManager.getToolWindows()) {
                    JMenuItem item = new JMenuItem(toolWindow.getTitle());
                    item.setActionCommand(toolWindow.getId());

                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            toolWindowManager.getToolWindow(e.getActionCommand())
                                    .setActive(true);
                        }
                    });

                    toolsPopupMenu.add(item);
                }
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        return new JScrollPane(toolsTable);
    }

    protected Component initGroupEditorContent() {
        JPanel toolGroupsPanel = new JPanel(new TableLayout(new double[][]{{-1, 5, 150, 5}, {5, 25, 5, 25, 5, -1}}));
        toolGroupsPanel.setBorder(new TitledBorder("Groups"));

        final JTable toolGroupsTable = new JTable(new ToolGroupsTableModel(toolWindowManager));
        toolGroupsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane toolGroupsTableScroll = new JScrollPane(toolGroupsTable);


        JButton showGroup = new JButton("Show Group");
        showGroup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (toolGroupsTable.getSelectedRow() != -1) {
                    String name = (String) toolGroupsTable.getModel().getValueAt(
                            toolGroupsTable.getSelectedRow(), 0
                    );
                    toolWindowManager.getToolWindowGroup(name).setVisible(true);
                }
            }
        });

        JButton hideGroup = new JButton("Hide Group");
        hideGroup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (toolGroupsTable.getSelectedRow() != -1) {
                    String name = (String) toolGroupsTable.getModel().getValueAt(
                            toolGroupsTable.getSelectedRow(), 0
                    );
                    toolWindowManager.getToolWindowGroup(name).setVisible(false);
                }
            }
        });

        toolGroupsPanel.add(toolGroupsTableScroll, "0,0,0,5,FULL,FULL");
        toolGroupsPanel.add(showGroup, "2,1,c,c");
        toolGroupsPanel.add(hideGroup, "2,3,c,c");

        groupsPopupMenu = new JPopupMenu("Groups");
		groupsPopupMenu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                groupsPopupMenu.removeAll();
                for (ToolWindowGroup toolWindowGroup : toolWindowManager.getToolWindowGroups()) {
                    JMenuItem item = new JMenuItem(toolWindowGroup.getName());
                    item.setActionCommand(toolWindowGroup.getName());
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            toolWindowManager.getToolWindowGroup(e.getActionCommand()).setVisible(true);
                        }
                    });
                    groupsPopupMenu.add(item);
                }
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        return toolGroupsPanel;
    }

    protected Component initContentsContent() {
        JTable contentsTable = new JTable(new ContentsTableModel(toolWindowManager));
        contentsTable.getTableHeader().setReorderingAllowed(false);

        JCheckBox booleanEditor = new JCheckBox();
        booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 4; i++) {
            contentsTable.getColumnModel().getColumn(i).setCellRenderer(new CheckBoxCellRenderer());
            contentsTable.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(booleanEditor));
        }

        return new JScrollPane(contentsTable);
    }


    protected JMenuItem createLafMenuItem(JMenu menu, String label, String laf) {
        JMenuItem mi = menu.add(new JRadioButtonMenuItem(label));
        mi.setActionCommand(laf);
        mi.addActionListener(new ChangeLookAndFeelAction(this));
        mi.setEnabled(isAvailableLookAndFeel(laf));
        return mi;
    }

    protected boolean isAvailableLookAndFeel(String laf) {
        try {
            Class lnfClass = Class.forName(laf);
            LookAndFeel newLAF = (LookAndFeel) (lnfClass.newInstance());
            return newLAF.isSupportedLookAndFeel();
        } catch (Exception e) {
            return false;
        }
    }

    protected void setLookAndFeel(String laf) {
        if (!currentLookAndFeel.equals(laf)) {
            currentLookAndFeel = laf;

            updateLookAndFeel();

            for (int i = 0; i < lafMenu.getItemCount(); i++) {
                JMenuItem item = lafMenu.getItem(i);
                item.setSelected(item.getActionCommand().equals(laf));
            }
        }
    }

    protected void updateLookAndFeel() {
        try {
            UIManager.setLookAndFeel(currentLookAndFeel);

            SwingUtilities.updateComponentTreeUI(frame);

            SwingUtilities.updateComponentTreeUI(groupEditorContent);
            SwingUtilities.updateComponentTreeUI(groupsPopupMenu);

            SwingUtilities.updateComponentTreeUI(toolsContent);
            SwingUtilities.updateComponentTreeUI(toolsPopupMenu);

            SwingUtilities.updateComponentTreeUI(contentsContent);
        } catch (Exception ex) {
			ex.printStackTrace();
		}
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


    static class ChangeLookAndFeelAction extends AbstractAction {
        final MyDoggySet myDoggySet;

        protected ChangeLookAndFeelAction(MyDoggySet myDoggySet) {
            super("ChangeTheme");
            this.myDoggySet = myDoggySet;
        }

        public void actionPerformed(ActionEvent e) {
            myDoggySet.setLookAndFeel(e.getActionCommand());
        }
    }

}
