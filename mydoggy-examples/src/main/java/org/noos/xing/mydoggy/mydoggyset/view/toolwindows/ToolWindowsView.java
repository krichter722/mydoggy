package org.noos.xing.mydoggy.mydoggyset.view.toolwindows;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.view.toolwindows.tools.ToolWindowsTableView;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowsView implements View {
    protected ToolWindowManager toolWindowManager;

    public ToolWindowsView(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }

    public Component getComponent() {
        ViewContext viewContext = new MapViewContext();
        viewContext.put(ToolWindowManager.class, toolWindowManager);

        JPanel panel = new JPanel();
        panel.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {150, 5, -1}}));

        panel.add(new ToolWindowsTableView(viewContext).getComponent(), "0,0,FULL,FULL");
        panel.add(new PreferencePanelView(viewContext).getComponent(), "0,2,FULL,FULL");

        return panel;
    }

}
