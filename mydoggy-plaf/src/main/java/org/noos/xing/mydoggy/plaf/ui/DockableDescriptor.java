package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.cleaner.CleanerAggregator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface DockableDescriptor extends Cleaner {

    enum DockableType {
        TOOL_WINDOW,
        TOOL_WINDOW_TAB,
        CONTENT,
        CUSTOM
    }


    DockableType getDockableType();

    Dockable getDockable();



    void setAnchor(ToolWindowAnchor anchor, int index);

    ToolWindowAnchor getAnchor();

    void setAnchorPositionLocked(boolean anchorPositionLocked);

    boolean isAnchorPositionLocked();

    int getAnchorIndex();

    void setAvailable(boolean available);

    boolean isAvailable();

    boolean isAvailableCountable();


    JComponent getRepresentativeAnchor();

    JComponent getRepresentativeAnchor(Component parent);

    void updateRepresentativeAnchor();

    void resetRepresentativeAnchor();


    MyDoggyToolWindowManager getManager();

    MyDoggyToolWindowBar getToolBar();

    MyDoggyToolWindowBar getToolBar(ToolWindowAnchor anchor);

    CleanerAggregator getCleaner();


    boolean isDragImageAvailable();

    Component getComponentForDragImage();

}
