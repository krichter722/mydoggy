package org.noos.xing.mydoggy.mydoggyset.content;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.context.AddContentContext;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.Colors;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class WellcomeContentComponent extends JPanel {

    public WellcomeContentComponent() {
        initComponents();
    }

    protected void initComponents() {

        // Setup wellcome panel...
        JPanel panel = new JPanel(new TableLayout(new double[][]{{-1, 100, 5, 160, 3, 100, 5, 160, -1}, 
                                                                 {-1, 70, 10, 70, 10, 70, -1}}));
        panel.setBackground(Colors.blu);

        panel.add(renderButton("Manager", ToolWindowManager.class),
                  "1,1,FULL,FULL");
        panel.add(renderLabel("<html>Edit ToolWindowManager </br> properties </html>"),
                  "3,1,FULL,FULL");

        panel.add(renderButton("Tools", ToolWindow.class),
                  "1,3,FULL,FULL");
        panel.add(renderLabel("<html>Edit ToolWindows </br> properties</html>"), 
                  "3,3,FULL,FULL");

        panel.add(renderButton("Contents", Content.class),
                  "1,5,FULL,FULL");
        panel.add(renderLabel("<html>Edit Contents </br> properties</html>"), 
                  "3,5,FULL,FULL");

        panel.add(renderButton("Groups", ToolWindowGroup.class),
                  "5,1,FULL,FULL");
        panel.add(renderLabel("<html>Edit Groups </br> properties</html>"),
                  "7,1,FULL,FULL");

        panel.add(renderButton("ITests", InteractiveTest.class),
                  "5,3,FULL,FULL");
        panel.add(renderLabel("<html>Run Interactive </br> Tests</html>"), 
                  "7,3,FULL,FULL");

        // Setup main panel
        setLayout(new TableLayout(new double[][]{{3, -1, 3},{3, 93,5,-1,3}}));
        add(new JLabel(new ImageIcon(
                this.getClass().getClassLoader().getResource("org/noos/xing/mydoggy/examples/mydoggyset/images/banner.jpg")
        )), "1,1,FULL,FULL");
        add(panel, "1,3,FULL,FULL");
    }

    protected JButton renderButton(String text, Object inContextKey) {
        JButton button = new JButton(text);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(Color.WHITE));
        button.addActionListener(new ViewContextAction(AddContentContext.class, inContextKey));

        return button;
    }

    protected JLabel renderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);

        return label;
    }

}