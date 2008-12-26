package org.noos.xing.mydoggy.mydoggyset.view.group;

import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.mydoggyset.view.group.model.ToolGroupsTableModel;
import org.noos.xing.mydoggy.mydoggyset.view.group.model.ToolsComboBoxModel;
import org.noos.xing.mydoggy.mydoggyset.view.group.model.ToolsInGroupTableModel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.component.ToolBarContentPanel;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.plaf.view.listener.ContextPutItemListener;
import org.noos.xing.yasaf.plaf.view.listener.ContextPutTableListSelectionListener;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GroupsView implements View {
    protected ToolWindowManager toolWindowManager;
    protected Component parentComponent;

    public GroupsView(Component parentComponent, ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        this.parentComponent = parentComponent;
    }

    public Component getComponent() {
        ViewContext viewContext = new MapViewContext();
        viewContext.put(ToolWindowManager.class, toolWindowManager);
        viewContext.put("windowAncestor", parentComponent);

        JPanel panel = new JPanel();
        panel.setLayout(new ExtendedTableLayout(new double[][]{{3, -1, 3, 200, 3}, {3, -1, 3}}));
        panel.add(new GroupsTableView(viewContext).getComponent(), "1,1,FULL,FULL");
        panel.add(new ToolsInGroupTableView(viewContext).getComponent(), "3,1,FULL,FULL");

        return panel;
    }

    public class GroupsTableView extends ComponentView {
        protected JTable groupsTable;

        public GroupsTableView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            groupsTable = new JTable(new ToolGroupsTableModel(viewContext));

            groupsTable.getSelectionModel().addListSelectionListener(
                    new ContextPutTableListSelectionListener(viewContext, ToolWindowGroup.class, groupsTable, -1)
            );

            JCheckBox booleanEditor = new JCheckBox();
            booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);

            groupsTable.getColumnModel().getColumn(1).setCellRenderer(new CheckBoxCellRenderer());
            groupsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(booleanEditor));

            ToolBarContentPanel panel = new ToolBarContentPanel(new JScrollPane(groupsTable));
            panel.setBorder(new TitledBorder("Groups"));

            panel.getToolBar().add(new ViewContextAction("Add", viewContext, GroupKeySpace.ADD_GROUP));
            panel.getToolBar().add(new ViewContextAction("Remove", null, viewContext, GroupKeySpace.REMOVE_GROUP,
                                                         ToolWindowGroup.class));
            panel.getToolBar().add(new ViewContextAction("Show", null, viewContext, GroupKeySpace.SHOW_GROUP,
                                                         ToolWindowGroup.class));
            panel.getToolBar().add(new ViewContextAction("Hide", null, viewContext, GroupKeySpace.HIDE_GROUP,
                                                         ToolWindowGroup.class));

            return panel;
        }

        protected void initListeners() {
            viewContext.addViewContextChangeListener(GroupKeySpace.ADD_GROUP, new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    String groupName = JOptionPane.showInputDialog((Component) viewContext.get("windowAncestor"), "Group Name");
                    if (groupName != null)
                        viewContext.get(ToolWindowManager.class).getToolWindowGroup(groupName);
                }
            });
            viewContext.addViewContextChangeListener(GroupKeySpace.REMOVE_GROUP, new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    evt.getViewContext().get(ToolWindowManager.class).removeToolWindowGroup(
                            evt.getViewContext().get(ToolWindowGroup.class)
                    );
                }
            });
            viewContext.addViewContextChangeListener(GroupKeySpace.SHOW_GROUP, new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    ToolWindowGroup group = viewContext.get(ToolWindowGroup.class);
                    if (group != null)
                        group.setVisible(true);
                }
            });
            viewContext.addViewContextChangeListener(GroupKeySpace.HIDE_GROUP, new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    ToolWindowGroup group = viewContext.get(ToolWindowGroup.class);
                    if (group != null)
                        group.setVisible(false);
                }
            });
        }
    }

    public class ToolsInGroupTableView extends ComponentView {

        public ToolsInGroupTableView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            JTable toolsInGroupTable = new JTable(new ToolsInGroupTableModel(viewContext));
            toolsInGroupTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            toolsInGroupTable.getSelectionModel().addListSelectionListener(
                    new ContextPutTableListSelectionListener(viewContext, GroupKeySpace.TOOL_IN_GROUP_ID, toolsInGroupTable, 0)
            );

            ToolBarContentPanel panel = new ToolBarContentPanel(new JScrollPane(toolsInGroupTable));
            panel.setBorder(new TitledBorder("Tools In"));

            JComboBox tools = new JComboBox(new ToolsComboBoxModel(viewContext));
            tools.addItemListener(new ContextPutItemListener(viewContext, GroupKeySpace.TOOL_ID));

            panel.getToolBar().add(tools);
            panel.getToolBar().add(new ViewContextAction("Add", null, viewContext, GroupKeySpace.ADD_TOOL,
                                                         ToolWindowGroup.class));
            panel.getToolBar().add(new ViewContextAction("Remove", null, viewContext, GroupKeySpace.REMOVE_TOOL,
                                                         GroupKeySpace.TOOL_IN_GROUP_ID));

            return panel;
        }
    }
}