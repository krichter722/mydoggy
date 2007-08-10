package org.noos.xing.mydoggy.examples.mydoggyset.content;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.util.Colors;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class WellcomeContentComponent extends JPanel {
    private ToolWindowManager toolWindowManager;

    public WellcomeContentComponent(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        initComponents();
    }

    protected void initComponents() {

        // Setup wellcome panel...
        JPanel panel = new JPanel(new TableLayout(new double[][]{{-1},{-1}}));
        panel.setBackground(Colors.blu);

        // Setup main panel
        setLayout(new TableLayout(new double[][]{{-1},{93,5,-1}}));
        add(new JLabel(new ImageIcon(
                this.getClass().getClassLoader().getResource("org/noos/xing/mydoggy/examples/mydoggyset/images/banner.jpg")
        )), "0,0,FULL,FULL");
        add(panel, "0,2,FULL,FULL");
    }


}