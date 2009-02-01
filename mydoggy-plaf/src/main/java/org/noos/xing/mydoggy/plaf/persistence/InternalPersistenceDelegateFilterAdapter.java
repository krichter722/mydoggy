package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowBar;

/**
 * An abstract adapter class for receiving persistence events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InternalPersistenceDelegateFilterAdapter implements InternalPersistenceDelegateFilter {

    public boolean saveSelectedContent() {
        return true;
    }

    public boolean storeToolWindowManagerDescriptor() {
        return true;
    }

    public boolean storeToolWindow(ToolWindow toolWindow) {
        return true;
    }

    public boolean storeToolWindowBar(ToolWindowBar toolWindowBar) {
        return true;
    }

    public boolean storeContentManager() {
        return true;
    }
}