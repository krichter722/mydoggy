package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.PersistenceDelegateFilter;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowBar;

/**
 * An abstract adapter class for receiving persistence events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InternalPersistenceDelegateFilterWrapper implements InternalPersistenceDelegateFilter {
    protected PersistenceDelegateFilter persistenceDelegateFilter;


    public InternalPersistenceDelegateFilterWrapper(PersistenceDelegateFilter persistenceDelegateFilter) {
        this.persistenceDelegateFilter = persistenceDelegateFilter;
    }


    public boolean saveSelectedContent() {
        return true;
    }

    public boolean storeToolWindowManagerDescriptor() {
        return persistenceDelegateFilter.storeToolWindowManagerDescriptor();
    }

    public boolean storeToolWindow(ToolWindow toolWindow) {
        return persistenceDelegateFilter.storeToolWindow(toolWindow);
    }

    public boolean storeToolWindowBar(ToolWindowBar toolWindowBar) {
        return persistenceDelegateFilter.storeToolWindowBar(toolWindowBar);
    }

    public boolean storeContentManager() {
        return persistenceDelegateFilter.storeContentManager();
    }
}