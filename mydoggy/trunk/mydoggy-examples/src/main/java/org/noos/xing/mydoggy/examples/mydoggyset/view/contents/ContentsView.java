package org.noos.xing.mydoggy.examples.mydoggyset.view.contents;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.plaf.view.MapViewContext;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentsView implements View {
    protected ToolWindowManager toolWindowManager;

    public ContentsView(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }

    public Component getComponent() {
        ViewContext viewContext = new MapViewContext();
        viewContext.put(ToolWindowManager.class, toolWindowManager);

        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(new double[][]{{-1}, {-1, 5, -1}}));
        panel.add(new PreferencePanelView(viewContext).getComponent(),
                  "0,0,FULL,FULL");
        panel.add(new ContentTableView(viewContext).getComponent(),
                  "0,2,FULL,FULL");

        return panel;
    }

}