package org.noos.xing.mydoggy;

/**
 * This interface is used to filter some content to be stored by the PersistenceDelegate.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.5.0
 * @see org.noos.xing.mydoggy.PersistenceDelegate
 * @see org.noos.xing.mydoggy.PersistenceDelegate#save(java.io.OutputStream, PersistenceDelegateFilter)
 */
public interface PersistenceDelegateFilter {

    /**
     * Returns <code>true</code> if the toolwindow manager descriptor must be saved, <code>true</code> otherwise.
     *
     * @return <code>true</code> if the toolwindow manager descriptor must be saved, <code>true</code> otherwise.
     * @since 1.5.0
     */
    boolean storeToolWindowManagerDescriptor();

    /**
     * Returns <code>true</code> if the passed toolwindow must be saved, <code>true</code> otherwise.
     *
     * @return <code>true</code> if the passed toolwindow must be saved, <code>true</code> otherwise.
     * @since 1.5.0
     */
    boolean storeToolWindow(ToolWindow toolWindow);

    /**
     * Returns <code>true</code> if the passed toolwindowbar must be saved, <code>true</code> otherwise.
     *
     * @return <code>true</code> if the passed toolwindowbar must be saved, <code>true</code> otherwise.
     * @since 1.5.0
     */
    boolean storeToolWindowBar(ToolWindowBar toolWindowBar);

    /**
     * Returns <code>true</code> if the content manager must be saved, <code>true</code> otherwise.
     *
     * @return <code>true</code> if the content manager must be saved, <code>true</code> otherwise.
     * @since 1.5.0
     */
    boolean storeContentManager();

}
