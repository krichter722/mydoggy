package org.noos.xing.mydoggy.mydoggyset.view.contents;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.mydoggyset.view.contents.model.ContentsTableModel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentTableView extends ComponentView {

    public ContentTableView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        JPanel main = new JPanel(new ExtendedTableLayout(new double[][]{{-1},{-1}}));
        main.setBorder(new TitledBorder("Contents"));

        JTable contentsTable = new JTable(new ContentsTableModel(viewContext.get(ToolWindowManager.class)));
        contentsTable.getTableHeader().setReorderingAllowed(false);

        JCheckBox booleanEditor = new JCheckBox();
        booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 4; i++) {
            contentsTable.getColumnModel().getColumn(i).setCellRenderer(new CheckBoxCellRenderer());
            contentsTable.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(booleanEditor));
        }

        main.add(new JScrollPane(contentsTable), "0,0,FULL,FULL");

        return main;
    }
}
