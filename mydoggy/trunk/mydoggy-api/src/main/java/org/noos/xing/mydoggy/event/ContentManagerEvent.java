package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.*;

import java.util.EventObject;

/**
 * An event which indicates that an action occurred in the content manager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.0.0
 */
public class ContentManagerEvent extends EventObject {
    private static final long serialVersionUID = -4432400424463237878L;

    /**
     * Action Identifier Enum.
     */
    public enum ActionId {
        CONTENT_ADDED,        // When a content is added.
        CONTENT_REMOVED       // When a content is removed.
    }

    /**
     * Indicates the action identifier.
     *
     * @see ContentManagerEvent.ActionId
     */
    private final ActionId actionId;

    /**
     * Indicates the content on which the action has occured.
     */
    private final Content content;

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
     * @see ContentManagerEvent.ActionId
     * @see org.noos.xing.mydoggy.Content
     */
    public ContentManagerEvent(ContentManager source, ContentManagerEvent.ActionId actionId, Content content) {
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
    public Content getContent() {
        return content;
    }

}
