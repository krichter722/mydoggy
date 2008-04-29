package org.noos.xing.yasaf.plaf.view;

import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContainer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PanelViewContainer implements ViewContainer {
    protected Container panel;

    public PanelViewContainer() {
        initComponents();
    }

    public PanelViewContainer(Container panel) {
        this.panel = panel;
        initComponents();
    }

    public Container getContainer() {
        return panel;
    }

    public void plugView(View view) {
        panel.removeAll();
        panel.add(view.getComponent(), "0,0,FULL,FULL");

        SwingUtil.repaint(panel);
    }

    protected void initComponents() {
        if (this.panel != null)
            this.panel.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        else
            this.panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
    }
    
}
