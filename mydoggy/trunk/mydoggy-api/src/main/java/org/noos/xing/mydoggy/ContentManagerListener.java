package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.ContentManagerEvent;

import java.util.EventListener;


/**
 * The listener interface for receiving "interesting" events
 * (content added, content removed) on the content manager.
 * <p/>
 * The listener object created is then registered with the
 * content manager using the <code>addContentManagerListener</code>
 * method.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ContentManager
 * @see ContentManager#addContentManagerListener(ContentManagerListener)
 * @since 1.1.0
 */
public interface ContentManagerListener extends EventListener {

    /**
     * Invoked when a content has been added to the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.ContentManagerEvent
     * @since 1.1.0
     */
    void contentAdded(ContentManagerEvent event);

    /**
     * Invoked when a content has been removed to the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.ContentManagerEvent
     * @since 1.1.0
     */
    void contentRemoved(ContentManagerEvent event);

    /**
     * Invoked when a content has became selected or deselected depending on the ActionId of the event.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.ContentManagerEvent
     * @since 1.3.1
     */
    void contentSelected(ContentManagerEvent event);
    
}
