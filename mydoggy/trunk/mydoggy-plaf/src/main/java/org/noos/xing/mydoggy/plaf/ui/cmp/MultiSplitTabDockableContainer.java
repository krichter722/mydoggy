package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.Dockable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MultiSplitTabDockableContainer extends MultiSplitDockableContainer {

    public MultiSplitTabDockableContainer(MyDoggyToolWindowManager toolWindowManager) {
        super(toolWindowManager, JSplitPane.VERTICAL_SPLIT);
    }


    protected Container getComponentWrapper(Dockable dockable, Component component) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFocusCycleRoot(true);
        tabbedPane.addTab(
                dockable.getTitle(),
                dockable.getIcon(),
                component);

        return tabbedPane;
    }

    protected Component getWrappedComponent(Container container) {
        JTabbedPane tabbedPane = (JTabbedPane) container;
        return tabbedPane.getComponentAt(0);
    }

    protected void addToComponentWrapper(Component wrapperSource, Dockable dockable, Component content) {
        super.addToComponentWrapper(wrapperSource, dockable, content);
    }
}
