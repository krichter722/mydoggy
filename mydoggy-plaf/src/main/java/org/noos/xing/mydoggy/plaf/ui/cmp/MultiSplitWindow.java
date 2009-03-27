package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Dockable;

import java.awt.*;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface MultiSplitWindow<D extends Dockable> {

    void addDockable(D dockable, Component content);

    void addDockable(D dockable, Component content,
                     D aggregationOnDockable, AggregationPosition aggregationPosition);

    void removeDockable(D dockable);

    int getDockableCount();

    D getDockable();

    List<D> getDockables();

    boolean containsDockable(D dockable);

    
    Object getMultiSplitLayout();

    void setMultiSplitLayout(Object model);

}
