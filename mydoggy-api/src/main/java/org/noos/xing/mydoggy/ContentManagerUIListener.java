package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.ContentManagerUIEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving "interesting" events
 * (content removing, content detached) on the content manager ui.
 * <p/>
 * The listener object created is then registered with the
 * content manager ui using the <code>addContentManagerUIListener</code>
 * method.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ContentManagerUI
 * @see ContentManagerUI#addContentManagerUIListener(ContentManagerUIListener) 
 */
public interface ContentManagerUIListener extends EventListener {

    /**
     * Invoked before removing the content.
     *
     * @param event the fired event.
     * @return false if you want to rollback the remove action.
     * @see org.noos.xing.mydoggy.event.ContentManagerUIEvent
     */
    boolean contentUIRemoving(ContentManagerUIEvent event);

    /**
     /**
      * Invoked when a contentui has been detached from the ui container.
      *
      * @param event the fired event.
      * @see org.noos.xing.mydoggy.event.ContentManagerUIEvent
      */
    void contentUIDetached(ContentManagerUIEvent event);
}