package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * TODO: create ui...
 */
public class CornerPanel extends JPanel {

    public CornerPanel(ToolWindowManagerDescriptor.Corner corner) {
        putClientProperty(ToolWindowManagerDescriptor.Corner.class, corner);

        setLayout(new TableLayout(new double[][]{{-1}, {-1}}));

        setFocusable(false);
    }

    public Component getComponent() {
        return (getComponentCount() == 0) ? null : getComponent(0);
    }

    public void setComponent(Component component) {
        removeAll();
        add(component, "0,0,FULL,FULL");
    }

    public void resetComponent() {
        removeAll();
    }

}
