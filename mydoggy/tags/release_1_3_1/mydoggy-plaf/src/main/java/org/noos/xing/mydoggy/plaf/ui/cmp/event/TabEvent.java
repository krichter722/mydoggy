package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentManager;

import java.awt.event.MouseEvent;
import java.util.EventObject;

public class TabEvent extends EventObject {
    public static enum ActionId {
        ON_CLOSE, ON_DETACH
    }

    private JTabbedContentManager contentManager;
    private ActionId actionId;
    private Content content;
    private String description;
    private MouseEvent mouseEvent;
    private int overTabIndex;

    public TabEvent(JTabbedContentManager source, ActionId actionId, Content content, MouseEvent e, String description, int overTabIndex) {
        super(source);
        this.contentManager = source;
        this.actionId = actionId;
        this.content = content;
        this.mouseEvent = e;
        this.description = description;
        this.overTabIndex = overTabIndex;
    }


    public JTabbedContentManager getContentManager() {
        return contentManager;
    }

    public ActionId getActionId() {
        return actionId;
    }

    public Content getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    public int getOverTabIndex() {
        return overTabIndex;
    }
}
