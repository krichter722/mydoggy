package org.noos.xing.mydoggy.plaf.ui.content;

import java.awt.*;
import java.awt.event.MouseEvent;

public class TabbedEvent extends Event {

    private JTabbedContentManager contentManager;
    private String description;
    private MouseEvent e;
    private int overTabIndex;

    public TabbedEvent(JTabbedContentManager contentManager, MouseEvent e, String description, int overTabIndex) {
        super(null, 0, null);
        this.contentManager = contentManager;
        this.e = e;
        this.description = description;
        this.overTabIndex = overTabIndex;
    }


    public JTabbedContentManager getContentManager() {
        return contentManager;
    }

    public String getDescription() {
        return description;
    }

    public MouseEvent getMouseEvent() {
        return e;
    }

    public int getOverTabIndex() {
        return overTabIndex;
    }
}
