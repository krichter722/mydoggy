package org.noos.xing.mydoggy.mydoggyset.view.welcome;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.Colors;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.component.JImage;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.PanelUI;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class WelcomeContentView extends ComponentView {

    public WelcomeContentView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        // Setup welcome panel...
        JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1, 100, 5, 160, 3, 100, 5, 160, -1},
                                                                         {-1, 5, 60, 5, 60, 5, 60, 5, 60, 5, -1}})) {
            public void setUI(PanelUI ui) {
                if (getUI() == null)
                    super.setUI(ui);
            }
        };

        panel.setBackground(Colors.blu);

        panel.add(renderButton("Manager", ToolWindowManager.class),
                  "1,2,FULL,FULL");
        panel.add(renderLabel("<html>Edit <br>ToolWindowManager <br> Properties </html>"),
                  "3,2,FULL,FULL");

        panel.add(renderButton("Tools", ToolWindow.class),
                  "1,4,FULL,FULL");
        panel.add(renderLabel("<html>Edit <br>ToolWindow <br> Properties</html>"),
                  "3,4,FULL,FULL");

        panel.add(renderButton("Contents", Content.class),
                  "1,6,FULL,FULL");
        panel.add(renderLabel("<html>Edit <br>Content <br> Properties</html>"),
                  "3,6,FULL,FULL");


        panel.add(renderButton("Groups", ToolWindowGroup.class),
                  "5,2,FULL,FULL");
        panel.add(renderLabel("<html>Edit <br>Group <br> Properties</html>"),
                  "7,2,FULL,FULL");

        panel.add(renderButton("Customize", ResourceManager.class),
                  "5,4,FULL,FULL");
        panel.add(renderLabel("<html>Customize <br>Colors and Icons</html>"),
                  "7,4,FULL,FULL");

        // Setup main panel
        JPanel main = new JPanel(new ExtendedTableLayout(new double[][]{{3, -1, 3}, {3, 93, 5, -1, 3}}));
        main.add(new JImage("org/noos/xing/mydoggy/mydoggyset/images/banner.jpg"), "1,1,FULL,FULL");
        main.add(panel, "1,3,FULL,FULL");

        return new JScrollPane(main);
    }

    protected JButton renderButton(String text, Object inContextKey) {
        JButton button = new JButton(text) {
            public void setUI(ButtonUI ui) {
                if (getUI() == null)
                    super.setUI(ui);
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(Color.WHITE));
        button.addActionListener(new ViewContextAction(viewContext, inContextKey));

        return button;
    }

    protected JLabel renderLabel(String text) {
        JLabel label = new JLabel(text) {
            public void setUI(LabelUI ui) {
                if (getUI() == null)
                    super.setUI(ui);
            }
        };
        label.setForeground(Color.WHITE);
        return label;
    }

}