package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Dockable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DockablePanel extends JPanel {

    public DockablePanel(Dockable dockable, Component component) {
        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        setFocusCycleRoot(true);
        setFocusable(false);
        add(component, "0,0,FULL,FULL");
        putClientProperty(Dockable.class, dockable);
    }

    public Dockable getDockable() {
        return (Dockable) getClientProperty(Dockable.class);
    }

    public Component getComponent() {
        return getComponent(0);
    }

    public void setComponent(Component component) {
        removeAll();
        add(component, "0,0,FULL,FULL");
    }


}
