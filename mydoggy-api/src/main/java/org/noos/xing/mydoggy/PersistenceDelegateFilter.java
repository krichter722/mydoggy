package org.noos.xing.mydoggy;

/**
 * TODO:...
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.5.0
 */
public interface PersistenceDelegateFilter {

    boolean storeToolWindowManagerDescriptor();

    boolean storeToolWindow(ToolWindow toolWindow);

    boolean storeToolWindowBar(ToolWindowBar toolWindowBar);

    boolean storeContentManager();

}
