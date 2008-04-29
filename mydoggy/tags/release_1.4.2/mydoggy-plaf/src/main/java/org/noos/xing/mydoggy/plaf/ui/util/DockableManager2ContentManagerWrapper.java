package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.xing.mydoggy.ContentManagerListener;
import org.noos.xing.mydoggy.DockableManagerListener;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.event.DockableManagerEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DockableManager2ContentManagerWrapper implements ContentManagerListener {
    protected DockableManagerListener listener;

    public DockableManager2ContentManagerWrapper(DockableManagerListener listener) {
        this.listener = listener;
    }

    public void contentAdded(ContentManagerEvent event) {
        listener.dockableAdded(new DockableManagerEvent(event.getSource(),
                                                        DockableManagerEvent.ActionId.DOCKABLE_ADDED,
                                                        event.getContent()));
    }

    public void contentRemoved(ContentManagerEvent event) {
        listener.dockableRemoved(new DockableManagerEvent(event.getSource(),
                                                          DockableManagerEvent.ActionId.DOCKABLE_REMOVED,
                                                          event.getContent()));
    }

    public void contentSelected(ContentManagerEvent event) {
    }

    public DockableManagerListener getListener() {
        return listener;
    }
}