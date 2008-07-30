package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.plaf.ui.cmp.TabbedContentPane;

import java.util.EventObject;

public class TabbedContentPaneEvent extends EventObject {
    public static enum ActionId {
        ON_CLOSE,
        ON_DETACH,
    }

    private TabbedContentPane tabbedContentPane;
    private ActionId actionId;
    private Content content;

    public TabbedContentPaneEvent(TabbedContentPane source, ActionId actionId, Content content) {
        super(source);
        this.tabbedContentPane = source;
        this.actionId = actionId;
        this.content = content;
    }


    public TabbedContentPane getTabbedContentPane() {
        return tabbedContentPane;
    }

    public ActionId getActionId() {
        return actionId;
    }

    public Content getContent() {
        return content;
    }

}