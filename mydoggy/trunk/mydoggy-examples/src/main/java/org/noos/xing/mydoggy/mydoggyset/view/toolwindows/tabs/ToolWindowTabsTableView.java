package org.noos.xing.mydoggy.mydoggyset.view.toolwindows.tabs;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.mydoggyset.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.mydoggyset.view.toolwindows.model.ToolWindowTabsTableModel;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.component.ToolBarContentPanel;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.listener.ContextPutTableListSelectionListener;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabsTableView extends ComponentView {
    protected ToolWindowTabsTableModel toolWindowTabsTableModel;


    public ToolWindowTabsTableView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        final JTable toolsTable = new JTable(toolWindowTabsTableModel = new ToolWindowTabsTableModel());

        toolsTable.getTableHeader().setReorderingAllowed(false);
        toolsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toolsTable.getSelectionModel().addListSelectionListener(
                new ContextPutTableListSelectionListener(viewContext, ToolWindowTab.class, toolsTable, -1)
        );

        // Flashing columns...
        JCheckBox booleanEditor = new JCheckBox();
        booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);
        toolsTable.getColumnModel().getColumn(2).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(booleanEditor));



        ToolBarContentPanel toolBarContentPanel = new ToolBarContentPanel(new JScrollPane(toolsTable));
        toolBarContentPanel.setBorder(new TitledBorder("ToolWindowTabs"));
        toolBarContentPanel.getToolBar().add(new ViewContextAction("Remove All",
                                                                   viewContext,
                                                                   "removeAll"));

        return toolBarContentPanel;
    }

    protected void initListeners() {
        viewContext.addViewContextChangeListener(ToolWindow.class, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                toolWindowTabsTableModel.setToolWindow((ToolWindow) evt.getNewValue());
            }
        });

//      TODO  viewContext.addViewContextChangeListener("removeAll", new ViewContextChangeListener() {
//            public void contextChange(ViewContextChangeEvent evt) {
//                evt.getViewContext().get(ToolWindowManager.class).unregisterAllToolWindow();
//            }
//        });
    }
}