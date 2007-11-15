package org.noos.xing.mydoggy.mydoggyset.view.toolwindows;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.mydoggyset.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.mydoggyset.view.toolwindows.model.ToolsTableModel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.listener.ContextPutListSelectionListener;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTableView extends ComponentView {

    public ToolWindowTableView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        ToolWindowManager toolWindowManager = viewContext.get(ToolWindowManager.class);

        JPanel toolsPanel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        toolsPanel.setBorder(new TitledBorder("ToolWindows"));

        final JTable toolsTable = new JTable(new ToolsTableModel(toolWindowManager));
        toolsTable.getTableHeader().setReorderingAllowed(false);
        toolsTable.getSelectionModel().addListSelectionListener(
                new ContextPutListSelectionListener(viewContext, ToolWindow.class, toolsTable, -1)
        );


        // Type column
        JComboBox types = new JComboBox(new Object[]{ToolWindowType.DOCKED,
                                                     ToolWindowType.SLIDING,
                                                     ToolWindowType.FLOATING,
                                                     ToolWindowType.FLOATING_FREE,
                                                     ToolWindowType.FLOATING_LIVE});
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

        toolsTable.getColumnModel().getColumn(8).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(booleanEditor));

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

        toolsPanel.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        toolsPanel.add(new JScrollPane(toolsTable), "0,0,FULL,FULL");

        return toolsPanel;
    }
}
