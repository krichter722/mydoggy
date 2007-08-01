package org.noos.xing.mydoggy.examples.mydoggyset.content;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.examples.mydoggyset.model.ToolsTableModel;
import org.noos.xing.mydoggy.examples.mydoggyset.ui.CheckBoxCellRenderer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import info.clearthought.layout.TableLayout;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsContentComponent extends JPanel {
    private ToolWindowManager toolWindowManager;

    public ToolsContentComponent(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        initComponents();
    }

    protected void initComponents() {
        // Tools Panel

        JPanel toolsPanel = new JPanel(new TableLayout(new double[][]{{-1},{-1}}));
        toolsPanel.setBorder(new TitledBorder("ToolWindows"));

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

        toolsPanel.setLayout(new TableLayout(new double[][]{{-1},{-1}}));
        toolsPanel.add(new JScrollPane(toolsTable), "0,0,FULL,FULL");

        // Preference Panel

        JPanel prefPanel = new JPanel(new TableLayout(new double[][]{{-1},{20}}));
        prefPanel.setBorder(new TitledBorder("Preferences"));


        // Setup main panel
        setLayout(new TableLayout(new double[][]{{-1},{-1,5,-1}}));
        add(toolsPanel, "0,0,FULL,FULL");
        add(prefPanel, "0,2,FULL,FULL");
    }

}
