package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Dockable;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface MultiDockableOwner extends DockableOwner {

    Dockable getDockable(int index);

    int getDockableIndex(Point point);

    void setPointerVisible(boolean visible);

    boolean containsDockable(Dockable dockable);

    void setComponent(Dockable dockable, Component component);
}
