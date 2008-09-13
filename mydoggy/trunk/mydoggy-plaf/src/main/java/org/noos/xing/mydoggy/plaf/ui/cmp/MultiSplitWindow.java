package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Dockable;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface MultiSplitWindow<D extends Dockable> {

    void addDockable(D dockable, Component content);

    void addDockable(D dockable, Component content,
                     D aggregationOnDockable, AggregationPosition aggregationPosition);

    void removeDockable(D dockable);

    int getNumDockables();

    D getDockable();

    boolean containsDockable(D dockable);
}
