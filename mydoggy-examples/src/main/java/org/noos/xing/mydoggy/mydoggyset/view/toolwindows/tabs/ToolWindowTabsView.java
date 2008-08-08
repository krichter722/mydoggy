package org.noos.xing.mydoggy.mydoggyset.view.toolwindows.tabs;

import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabsView extends ComponentView {

    public ToolWindowTabsView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));

        panel.add(new ToolWindowTabsTableView(viewContext).getComponent(), "0,0,FULL,FULL");

        return panel;
    }

}