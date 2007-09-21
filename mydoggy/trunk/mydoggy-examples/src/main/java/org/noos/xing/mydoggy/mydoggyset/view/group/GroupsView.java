package org.noos.xing.mydoggy.mydoggyset.view.group;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;
import org.noos.xing.mydoggy.mydoggyset.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.mydoggyset.view.group.model.ToolGroupsTableModel;
import org.noos.xing.mydoggy.mydoggyset.view.group.model.ToolsComboBoxModel;
import org.noos.xing.mydoggy.mydoggyset.view.group.model.ToolsInGroupTableModel;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.component.ToolBarContentPanel;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class GroupsView implements View {
    protected ToolWindowManager toolWindowManager;
    protected JFrame frame;

    public GroupsView(JFrame frame, ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        this.frame = frame;
    }

    public Component getComponent() {
        ViewContext viewContext = new MapViewContext();
        viewContext.put(ToolWindowManager.class, toolWindowManager);
        viewContext.put(JFrame.class, frame);

        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(new double[][]{{3, -1, 3, 200, 3}, {3, -1, 3}}));
        panel.add(new GroupsTableView(viewContext).getComponent(), "1,1,FULL,FULL");
        panel.add(new ToolsInGroupTableView(viewContext).getComponent(), "3,1,FULL,FULL");

        viewContext.put(ToolWindowManagerDescriptor.class, toolWindowManager.getToolWindowManagerDescriptor());

        return panel;
    }

    public class GroupsTableView extends ComponentView {
        protected JTable groupsTable;

        public GroupsTableView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            groupsTable = new JTable(new ToolGroupsTableModel(viewContext));
            groupsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JCheckBox booleanEditor = new JCheckBox();
            booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);

            groupsTable.getColumnModel().getColumn(2).setCellRenderer(new CheckBoxCellRenderer());
            groupsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(booleanEditor));

            ToolBarContentPanel panel = new ToolBarContentPanel(new JScrollPane(groupsTable));
            panel.getToolBar().add(new ViewContextAction("Add", viewContext, "addGroup"));
            panel.getToolBar().add(new ViewContextAction("Show", viewContext, "showGroup"));
            panel.getToolBar().add(new ViewContextAction("Hide", viewContext, "hideGroup"));
            panel.setBorder(new TitledBorder("Groups"));
            return panel;
        }

        protected void initListeners() {
            viewContext.addViewContextChangeListener("addGroup", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    String groupName = JOptionPane.showInputDialog(viewContext.get(JFrame.class), "Group Name");
                    viewContext.get(ToolWindowManager.class).getToolWindowGroup(groupName);
                    viewContext.put("refreshGroups", null);
                }
            });
            viewContext.addViewContextChangeListener("showGroup", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    ToolWindowGroup group = viewContext.get(ToolWindowGroup.class);
                    if (group != null)
                        group.setVisible(true);
                }
            });
            viewContext.addViewContextChangeListener("hideGroup", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    ToolWindowGroup group = viewContext.get(ToolWindowGroup.class);
                    if (group != null)
                        group.setVisible(false);
                }
            });
            groupsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (groupsTable.getSelectedRow() != -1) {
                        ToolWindowGroup group = (ToolWindowGroup) groupsTable.getValueAt(groupsTable.getSelectedRow(), -1);
                        viewContext.put(ToolWindowGroup.class, group);
                    }
                }
            });
        }
    }

    public class ToolsInGroupTableView extends ComponentView {

        public ToolsInGroupTableView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            final JTable toolsInGroupTable = new JTable(new ToolsInGroupTableModel(viewContext));
            toolsInGroupTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            toolsInGroupTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (toolsInGroupTable.getSelectedRow() != -1) {
                        viewContext.put("toolInGroupId", 
                                        toolsInGroupTable.getValueAt(toolsInGroupTable.getSelectedRow(), 0));
                    }
                }
            });

            ToolBarContentPanel panel = new ToolBarContentPanel(new JScrollPane(toolsInGroupTable));

            JComboBox tools = new JComboBox(new ToolsComboBoxModel(viewContext));
            tools.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    viewContext.put("toolId", e.getItem());
                }
            });

            panel.getToolBar().add(tools);
            panel.getToolBar().add(new ViewContextAction("Add", viewContext, "addTool"));
            panel.getToolBar().add(new ViewContextAction("Remove", viewContext, "removeTool"));
            panel.setBorder(new TitledBorder("Tools In"));

            return panel;
        }
    }
}
