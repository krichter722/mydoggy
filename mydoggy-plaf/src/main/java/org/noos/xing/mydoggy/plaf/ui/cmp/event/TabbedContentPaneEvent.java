package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentPane;

import java.awt.event.MouseEvent;
import java.util.EventObject;

public class TabbedContentPaneEvent extends EventObject {
    public static enum ActionId {
        ON_CLOSE,
        ON_DETACH,
        ON_MAXIMIZE
    }

    private JTabbedContentPane tabbedContentPane;
    private ActionId actionId;
    private Content content;
    private String description;
    private MouseEvent mouseEvent;

    public TabbedContentPaneEvent(JTabbedContentPane source, ActionId actionId, Content content, MouseEvent e, String description) {
        super(source);
        this.tabbedContentPane = source;
        this.actionId = actionId;
        this.content = content;
        this.mouseEvent = e;
        this.description = description;
    }


    public JTabbedContentPane getTabbedContentPane() {
        return tabbedContentPane;
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

}