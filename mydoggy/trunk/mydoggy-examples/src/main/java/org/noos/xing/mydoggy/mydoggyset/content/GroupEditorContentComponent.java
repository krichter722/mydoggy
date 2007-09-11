package org.noos.xing.mydoggy.mydoggyset.content;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.model.ToolGroupsTableModel;
import org.noos.xing.mydoggy.mydoggyset.ui.CheckBoxCellRenderer;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class GroupEditorContentComponent extends JPanel {
    private ToolWindowManager toolWindowManager;

    public GroupEditorContentComponent(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        initComponents();
    }

    protected void initComponents() {
        setLayout(new TableLayout(new double[][]{{-1, 5, 150, 5}, {5, 25, 5, 25, 5, -1}}));
        setBorder(new TitledBorder("Groups"));

        final JTable toolGroupsTable = new JTable(new ToolGroupsTableModel(toolWindowManager));
        toolGroupsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane toolGroupsTableScroll = new JScrollPane(toolGroupsTable);

        JCheckBox booleanEditor = new JCheckBox();
        booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);
        toolGroupsTable.getColumnModel().getColumn(2).setCellRenderer(new CheckBoxCellRenderer());
        toolGroupsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(booleanEditor));

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

        add(toolGroupsTableScroll, "0,0,0,5,FULL,FULL");
        add(showGroup, "2,1,c,c");
        add(hideGroup, "2,3,c,c");
    }
}
