package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface DockableDescriptor {

    enum DockableType {
        TOOL_WINDOW,
        TOOL_WINDOW_TAB,
        CONTENT,
        OTHER
    }


    ToolWindowAnchor getAnchor();

    DockableType getDockableType();

    Dockable getDockable();


    JComponent getRepresentativeAnchor();

    int getRepresentativeAnchorIndex();

    void updateRepresentativeAnchor();

    JComponent getRepresentativeAnchor(Component container);

    void resetRepresentativeAnchor();


    ResourceManager getResourceManager();

    MyDoggyToolWindowManager getManager();

    MyDoggyToolWindowBar getToolBar();

    boolean isPreviewAvailable();

    Component getPreviewComponent();

    void setAnchor(ToolWindowAnchor anchor, int index);

}
