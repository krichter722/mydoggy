package org.noos.xing.mydoggy.mydoggyset.view.customize;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.view.customize.model.ColorsTableModel;
import org.noos.xing.mydoggy.mydoggyset.view.customize.model.IconsTableModel;
import org.noos.xing.mydoggy.mydoggyset.view.customize.ui.ColorCellRenderer;
import org.noos.xing.mydoggy.mydoggyset.view.customize.ui.ColorChooseCellEditor;
import org.noos.xing.mydoggy.mydoggyset.view.customize.ui.IconCellRenderer;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.component.TitlePanel;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class CustomizeView implements View {
    protected ToolWindowManager toolWindowManager;
    protected Component parentComponent;

    public CustomizeView(Component parentComponent, ToolWindowManager toolWindowManager) {
        this.parentComponent = parentComponent;
        this.toolWindowManager = toolWindowManager;
    }

    public Component getComponent() {
        ViewContext viewContext = new MapViewContext();
        viewContext.put(ToolWindowManager.class, toolWindowManager);
        viewContext.put("windowAncestor", parentComponent);

        JPanel panel = new JPanel();
        panel.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1, 3, -1}}));
        panel.add(new CustomizeColorsView(viewContext).getComponent(), "0,0,FULL,FULL");
        panel.add(new CustomizeIconsView(viewContext).getComponent(), "0,2,FULL,FULL");

        return panel;
    }

    protected class CustomizeColorsView extends ComponentView {

        public CustomizeColorsView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            JTable colorsTable = new JTable(new ColorsTableModel(((MyDoggyToolWindowManager) viewContext.get(ToolWindowManager.class)).getResourceManager()));
            colorsTable.getTableHeader().setReorderingAllowed(false);

            TableColumn indexColumn = colorsTable.getColumnModel().getColumn(1);
            indexColumn.setCellRenderer(new ColorCellRenderer());
            indexColumn.setCellEditor(new ColorChooseCellEditor());

            return new TitlePanel("Colors", new JScrollPane(colorsTable));
        }


    }

    protected class CustomizeIconsView extends ComponentView {

        public CustomizeIconsView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            JTable iconsTable = new JTable(new IconsTableModel(((MyDoggyToolWindowManager) viewContext.get(ToolWindowManager.class)).getResourceManager()));
            iconsTable.getTableHeader().setReorderingAllowed(false);

            TableColumn indexColumn = iconsTable.getColumnModel().getColumn(1);
            indexColumn.setCellRenderer(new IconCellRenderer());
//            indexColumn.setCellEditor(new ColorChooseCellEditor());

            return new TitlePanel("Icons", new JScrollPane(iconsTable));
        }


    }
}