package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.PersistenceDelegateFilter;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowBar;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistenceDelegateFilterAdapter implements PersistenceDelegateFilter {

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
