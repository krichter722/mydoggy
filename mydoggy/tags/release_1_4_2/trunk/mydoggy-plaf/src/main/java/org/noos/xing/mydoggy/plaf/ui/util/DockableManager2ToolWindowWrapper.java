package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.xing.mydoggy.DockableManagerListener;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.event.DockableManagerEvent;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DockableManager2ToolWindowWrapper implements ToolWindowListener {
    protected DockableManagerListener listener;

    public DockableManager2ToolWindowWrapper(DockableManagerListener listener) {
        this.listener = listener;
    }

    public void toolWindowTabAdded(ToolWindowTabEvent event) {
        listener.dockableAdded(new DockableManagerEvent(event.getSource(),
                                                        DockableManagerEvent.ActionId.DOCKABLE_ADDED,
                                                        event.getToolWindowTab()));
    }

    public boolean toolWindowTabRemoving(ToolWindowTabEvent event) {
        return true;  
    }

    public void toolWindowTabRemoved(ToolWindowTabEvent event) {
        listener.dockableRemoved(new DockableManagerEvent(event.getSource(),
                                                          DockableManagerEvent.ActionId.DOCKABLE_REMOVED,
                                                          event.getToolWindowTab()));
    }

    public DockableManagerListener getListener() {
        return listener;
    }
}