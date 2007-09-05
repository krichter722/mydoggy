package org.noos.xing.yasaf.plaf.view;

import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContainer;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;

import info.clearthought.layout.TableLayout;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PanelViewContainer implements ViewContainer {
    protected JPanel panel;

    public PanelViewContainer() {
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
        this.panel = new JPanel(new TableLayout(new double[][]{{-1}, {-1}}));
    }
    
}
