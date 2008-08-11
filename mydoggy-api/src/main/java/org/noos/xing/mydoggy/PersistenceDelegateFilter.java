package org.noos.xing.mydoggy;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface PersistenceDelegateFilter {

    boolean storeToolWindowManagerDescriptor();

    boolean storeToolWindow(ToolWindow toolWindow);

    boolean storeToolWindowBar(ToolWindowBar toolWindowBar);

    boolean storeContentManager();

}
