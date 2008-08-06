package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Dockable;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface MultiDockableOwner {

    Dockable getDockableAt(Point point);

    int getDockableIndex();
    
}
