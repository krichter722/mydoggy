package org.noos.xing.yasaf.plaf.component;

import info.clearthought.layout.TableLayout;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolBarContentPanel extends JPanel {
    protected JToolBar toolBar;

    public ToolBarContentPanel(Component content) {
        setLayout(new TableLayout(new double[][]{{-1},{25,3,-1}}));
        add(toolBar = new JToolBar(), "0,0,r,FULL");
        toolBar.setBorderPainted(false);
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);
        
        add(content, "0,2");
    }

    public JToolBar getToolBar() {
        return toolBar;
    }
}
