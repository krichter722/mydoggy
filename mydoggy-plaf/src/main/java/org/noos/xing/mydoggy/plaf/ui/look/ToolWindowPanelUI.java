package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowPanelUI extends BasicPanelUI {

    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowPanelUI();
    }


    protected ToolWindowPanel toolWindowPanel;


    @Override
    public void installUI(JComponent c) {
        // Init fields
        this.toolWindowPanel = (ToolWindowPanel) c;

        super.installUI(c);
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        // Reset fields
        toolWindowPanel = null;
    }

    @Override
    protected void installDefaults(JPanel p) {
        super.installDefaults(p);

        installComponents();
    }


    protected void installComponents() {
        toolWindowPanel.setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL},
                                                                         {SwingUtil.getInt("ToolWindowTitleBarUI.length", 16), TableLayout.FILL}},
                                                          false));
        toolWindowPanel.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));

        JPanel componentContainer = toolWindowPanel.getComponentContainer();
        componentContainer.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        componentContainer.add(toolWindowPanel.getToolWindowDescriptor().getComponent(), "0,0,FULL,FULL");
        componentContainer.setBorder(null);

        toolWindowPanel.add(toolWindowPanel.getToolWindowTitleBar(), "0,0");
        toolWindowPanel.add(componentContainer, "0,1");

        toolWindowPanel.getToolWindowDescriptor().getToolWindow().getToolWindowTabs()[0].setSelected(true);
    }
}
