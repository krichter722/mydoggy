package org.noos.xing.mydoggy.examples.mydoggyset.content;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.util.Colors;
import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;

import javax.swing.*;
import java.awt.*;

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
        JPanel panel = new JPanel(new TableLayout(new double[][]{{-1, 100, 5, 160, 3, 100, 5, 160, -1}, 
                                                                 {-1, 70, 10, 70, 10, 70, -1}}));
        panel.setBackground(Colors.blu);

        JButton manager = new JButton("Manager");
        manager.setOpaque(false);
        manager.setContentAreaFilled(false);
        manager.setFocusPainted(false);
        manager.setBorder(new LineBorder(Color.WHITE));

        JLabel managerLabel = new JLabel("<html>Content to setup all manager </br> properties </html>");
        managerLabel.setForeground(Color.WHITE);

        JButton tools = new JButton("tools");
        JButton contents = new JButton("contents");
        JButton groups = new JButton("groups");
        JButton itests = new JButton("itests");

        panel.add(manager, "1,1,FULL,FULL");
        panel.add(managerLabel, "3,1,FULL,FULL");

        panel.add(tools, "1,3,FULL,FULL");
        panel.add(contents, "1,5,FULL,FULL");

        panel.add(groups, "5,1,FULL,FULL");
        panel.add(itests, "5,3,FULL,FULL");

        // Setup main panel
        setLayout(new TableLayout(new double[][]{{3, -1, 3},{3, 93,5,-1,3}}));
        add(new JLabel(new ImageIcon(
                this.getClass().getClassLoader().getResource("org/noos/xing/mydoggy/examples/mydoggyset/images/banner.jpg")
        )), "1,1,FULL,FULL");
        add(panel, "1,3,FULL,FULL");
    }

    protected void render(JPanel panel, Icon buttonIcon, String labelText) {
        JButton manager = new JButton(buttonIcon);
        manager.setOpaque(false);
        manager.setContentAreaFilled(false);
        manager.setFocusPainted(false);
        manager.setBorder(new LineBorder(Color.WHITE));

        JLabel managerLabel = new JLabel(labelText);
        managerLabel.setForeground(Color.WHITE);
    }


}