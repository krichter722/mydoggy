package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.xing.mydoggy.DockableManagerListener;
import org.noos.xing.mydoggy.ToolWindowManagerListener;
import org.noos.xing.mydoggy.event.DockableManagerEvent;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DockableManager2ToolWindowManagerWrapper implements ToolWindowManagerListener {
    protected DockableManagerListener listener;

    public DockableManager2ToolWindowManagerWrapper(DockableManagerListener listener) {
        this.listener = listener;
    }

    public void toolWindowRegistered(ToolWindowManagerEvent event) {
        listener.dockableAdded(new DockableManagerEvent(event.getSource(),
                                               DockableManagerEvent.ActionId.DOCKABLE_ADDED,
                                               event.getToolWindow()));
    }

    public void toolWindowUnregistered(ToolWindowManagerEvent event) {
        listener.dockableRemoved(new DockableManagerEvent(event.getSource(),
                                               DockableManagerEvent.ActionId.DOCKABLE_REMOVED, 
                                               event.getToolWindow()));
    }

    public void toolWindowGroupAdded(ToolWindowManagerEvent event) {
    }

    public void toolWindowGroupRemoved(ToolWindowManagerEvent event) {
    }

    public DockableManagerListener getListener() {
        return listener;
    }
}
