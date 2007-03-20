package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.*;

import java.util.EventObject;

/**
 * An event which indicates that an action occurred in the content manager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentManagerUIEvent extends EventObject {

    /**
     * Action Identifier Enum.
     */
    public enum ActionId {
        CONTENTUI_REMOVING,        // When a content is added.
        CONTENTUI_DETACHED         // When a content is removed.
    }

    /**
     * Indicates the action identifier.
     *
     * @see org.noos.xing.mydoggy.event.ContentManagerEvent.ActionId
     */
    private final ContentManagerUIEvent.ActionId actionId;

    /**
     * Indicates the content on which the action has occured.
     */
    private final ContentUI content;

    /**
     * Constructs a <code>ContentManagerEvent</code> object with the
     * specified source content manager, actionId, content.
     * Creating an invalid event (such as by using ActionId.ADD_CONTENT with a null content)
     * results in unspecified behavior.
     * <p/>
     * This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     *
     * @param source     the content manager where the action has occured.
     * @param actionId   the action identifier
     * @param content    the content subject of the action.
     * @see org.noos.xing.mydoggy.ContentManager
     * @see org.noos.xing.mydoggy.event.ContentManagerEvent.ActionId
     * @see org.noos.xing.mydoggy.Content
     */
    public ContentManagerUIEvent(ContentManagerUI source, ContentManagerUIEvent.ActionId actionId, ContentUI content) {
        super(source);
        this.actionId = actionId;
        this.content = content;
    }


    /**
     * Returns the action identifier.
     *
     * @return the action identifier.
     */
    public ActionId getId() {
        return actionId;
    }

    /**
     * Returns the content subject of the action.
     *
     * @return the content.
     */
    public ContentUI getContentUI() {
        return content;
    }

}