package org.noos.xing.mydoggy.examples.sample;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.examples.sample.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.examples.sample.model.ToolsTableModel;
import org.noos.xing.mydoggy.examples.sample.model.ToolGroupsTableModel;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SampleApp {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    private Component toolsContent;
    private Component groupEditorContent;

    private JPopupMenu toolsPopupMenu;
    private JPopupMenu groupsPopupMenu;

    protected void setUp() throws Exception {
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

        this.frame = new JFrame("Sample App...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
                System.exit(0);
            }
        });
        fileMenu.add(exit);

        JMenu contentMenu = new JMenu("Content");

        JMenuItem toolsContentItem = new JMenuItem("Tools");
        toolsContentItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ToolWindowContentManager manager = toolWindowManager.getContentManager();
                for (int i = 0, size = manager.getContentCount(); i < size; i++) {
                    if (manager.getComponentAt(i) == toolsContent)
                        return;
                }

                manager.addContent("Tools", null, toolsContent);
                manager.setPopupMenuAt(manager.getContentCount() - 1, toolsPopupMenu);
            }
        });

        JMenuItem groupEditorContentItem = new JMenuItem("Groups");
        groupEditorContentItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ToolWindowContentManager manager = toolWindowManager.getContentManager();
                for (int i = 0, size = manager.getContentCount(); i < size; i++) {
                    if (manager.getComponentAt(i) == groupEditorContent)
                        return;
                }

                manager.addContent("Group Editor", null, groupEditorContent);
                manager.setPopupMenuAt(manager.getContentCount() - 1, groupsPopupMenu);
            }
        });

        contentMenu.add(toolsContentItem);
        contentMenu.add(groupEditorContentItem);

        menuBar.add(fileMenu);
        menuBar.add(contentMenu);

        this.frame.setJMenuBar(menuBar);
    }

    protected void initToolWindowManager() {
        this.toolWindowManager = new MyDoggyToolWindowManager(frame, null, "0,0,");

        JPanel panel = new JPanel(new TableLayout(new double[][]{{20, -1, 20}, {20, -1, 20}}));
        panel.add(new JButton("2"), "1,1,FULL,FULL");

        toolWindowManager.registerToolWindow("1", "title1", null, new JButton("ciao1"), ToolWindowAnchor.LEFT);
        toolWindowManager.registerToolWindow("2", "title2", null, panel, ToolWindowAnchor.RIGHT);
        toolWindowManager.registerToolWindow("3", "title3", null, new JButton("ciao3"), ToolWindowAnchor.LEFT);
        toolWindowManager.registerToolWindow("4", "title4", null, new JButton("ciao4"), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("5", "title5", null, new JButton("ciao5"), ToolWindowAnchor.TOP);
        toolWindowManager.registerToolWindow("6", "title6", null, new JButton("ciao6"), ToolWindowAnchor.BOTTOM);
        toolWindowManager.registerToolWindow("7", "title7", null, new JButton("ciao7"), ToolWindowAnchor.TOP);

        ToolWindow toolWindow = toolWindowManager.registerToolWindow("8", "title4", null, new JButton("ciao4"), ToolWindowAnchor.RIGHT);
        toolWindow.setAvailable(true);
        toolWindow = toolWindowManager.registerToolWindow("9", "title4", null, new JButton("ciao4"), ToolWindowAnchor.RIGHT);
        toolWindow.setAvailable(true);
        toolWindow = toolWindowManager.registerToolWindow("10", "title4", null, new JButton("ciao4"), ToolWindowAnchor.RIGHT);
        toolWindow.setAvailable(true);
        toolWindow = toolWindowManager.registerToolWindow("11", "title4", null, new JButton("ciao4"), ToolWindowAnchor.RIGHT);
        toolWindow.setAvailable(true);
        toolWindow = toolWindowManager.registerToolWindow("12", "title4", null, new JButton("ciao4"), ToolWindowAnchor.RIGHT);
        toolWindow.setAvailable(true);
        toolWindow = toolWindowManager.registerToolWindow("13", "title4", null, new JButton("ciao4"), ToolWindowAnchor.RIGHT);
        toolWindow.setAvailable(true);
        toolWindow.setTitle("New Title");
        toolWindow.setIndex(1);


        ToolWindowGroup mainGroup = toolWindowManager.getToolWindowGroup("Main");
        ToolWindowGroup submainGroup = toolWindowManager.getToolWindowGroup("SubMain");

        toolWindow = toolWindowManager.getToolWindow("1");
        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        dockedTypeDescriptor.getUserDefinedMenu().add(new JMenuItem("Prova"));
        dockedTypeDescriptor.setPopupMenuEnabled(false);
        dockedTypeDescriptor.setDockLength(300);

        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setModal(true);


        toolWindow.setAvailable(true);
        mainGroup.addToolWindow(toolWindow);

        toolWindow = toolWindowManager.getToolWindow("2");
        toolWindow.setAvailable(true);
        toolWindow.setType(ToolWindowType.FLOATING_WINDOW);

        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_WINDOW);
        descriptor.setLocation(100, 100);
        descriptor.setSize(250, 250);
        submainGroup.addToolWindow(toolWindow);

        toolWindow = toolWindowManager.getToolWindow("3");
        toolWindow.setAvailable(true);
        mainGroup.addToolWindow(toolWindow);

        toolWindow = toolWindowManager.getToolWindow("4");
        toolWindow.setType(ToolWindowType.FLOATING_WINDOW);
        toolWindow.setAvailable(true);
        submainGroup.addToolWindow(toolWindow);

        toolWindow = toolWindowManager.getToolWindow("5");
        toolWindow.setType(ToolWindowType.FLOATING_WINDOW);
        toolWindow.setAvailable(true);

        toolWindow = toolWindowManager.getToolWindow("6");
        toolWindow.setAvailable(true);
        submainGroup.addToolWindow(toolWindow);

        toolWindow = toolWindowManager.getToolWindow("7");
        toolWindow.setAvailable(true);

        toolsContent = initToolsContent();
        groupEditorContent = initGroupEditorContent();

        toolWindowManager.getContentManager().addContent("Tools", null, toolsContent);
        toolWindowManager.getContentManager().setPopupMenuAt(
                toolWindowManager.getContentManager().getContentCount() - 1,
                toolsPopupMenu
        );

        // Add ToolWindowManager content pane to frame
//        this.frame.getContentPane().add(myDoggyToolWindowManager.getContentPane(), "0,0,");
    }

    protected Component initToolsContent() {
        JTable toolsTable = new JTable(new ToolsTableModel(toolWindowManager));
        toolsTable.getColumnModel().getColumn(4).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(5).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(6).setCellRenderer(new CheckBoxCellRenderer());

        toolsPopupMenu = new JPopupMenu("Tools");
        toolsPopupMenu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                toolsPopupMenu.removeAll();
                for (ToolWindow toolWindow : toolWindowManager.getToolWindows()) {
                    JMenuItem item = new JMenuItem(toolWindow.getTitle());
                    item.setActionCommand(toolWindow.getId());
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            toolWindowManager.getToolWindow(e.getActionCommand()).setActive(true);
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


    public static void main(String[] args) {
        SampleApp test = new SampleApp();
        try {
            test.setUp();
            test.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
