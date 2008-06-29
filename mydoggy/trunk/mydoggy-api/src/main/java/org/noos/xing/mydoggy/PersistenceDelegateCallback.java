package org.noos.xing.mydoggy;

/**
 * TODO
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.5.0
 */
public interface PersistenceDelegateCallback {

    /**
     *
     * @param toolWindowManager
     * @param contentId
     * @return
     * @since 1.5.0
     */
    Content contentNotFound(ToolWindowManager toolWindowManager, String contentId);
    
}
