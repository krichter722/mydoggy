package org.noos.xing.mydoggy.mydoggyset.view.contents;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.mydoggyset.view.contents.model.ContentsTableModel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
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
import java.awt.event.ActionEvent;

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
        contentsTable.getSelectionModel().addListSelectionListener(
                new ContextPutTableListSelectionListener(viewContext, Content.class, contentsTable, -1)
        );

        JCheckBox booleanEditor = new JCheckBox();
        booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 6; i++) {
            contentsTable.getColumnModel().getColumn(i).setCellRenderer(new CheckBoxCellRenderer());
            contentsTable.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(booleanEditor));
        }

        ToolBarContentPanel toolBarContentPanel = new ToolBarContentPanel(new JScrollPane(contentsTable));
        toolBarContentPanel.getToolBar().add(new RemoveAllContentAction(viewContext.get(ToolWindowManager.class)));
        toolBarContentPanel.getToolBar().add(new ViewContextAction("Remove", null, viewContext,
                                                                   "remove", Content.class));

        main.add(toolBarContentPanel, "0,0,FULL,FULL");

        return main;
    }

    protected void initListeners() {
        viewContext.addViewContextChangeListener("remove", new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                ViewContext viewContext = evt.getViewContext();
                viewContext.get(ToolWindowManager.class).getContentManager().removeContent(
                        viewContext.get(Content.class)
                );
            }
        });
    }

    protected class RemoveAllContentAction extends AbstractAction {
        protected ToolWindowManager toolWindowManager;

        public RemoveAllContentAction(ToolWindowManager toolWindowManager) {
            super("Remove All");
            this.toolWindowManager = toolWindowManager;
        }

        public void actionPerformed(ActionEvent e) {
            toolWindowManager.getContentManager().removeAllContents();
        }
    }

}
