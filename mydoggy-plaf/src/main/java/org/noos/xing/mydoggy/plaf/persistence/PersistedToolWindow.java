package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.xml.sax.Attributes;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedToolWindow {
    private String id;
    private boolean available;
    private boolean visible;
    private boolean active;
    private boolean autoHide;
    private boolean aggregateMode;
    private boolean maximized;
    private int index;
    private ToolWindowAnchor anchor;
    private ToolWindowType type;
    private List<String> tabs;

    public PersistedToolWindow(Attributes attributes) {
        this.id = attributes.getValue("id");
        this.available = Boolean.parseBoolean(attributes.getValue("available"));
        this.visible = Boolean.parseBoolean(attributes.getValue("visible"));
        this.active = Boolean.parseBoolean(attributes.getValue("active"));
        this.autoHide = Boolean.parseBoolean(attributes.getValue("autoHide")); 
        this.anchor = ToolWindowAnchor.valueOf(attributes.getValue("anchor"));
        this.type = ToolWindowType.valueOf(attributes.getValue("type"));
        this.aggregateMode = Boolean.parseBoolean(attributes.getValue("aggregateMode"));
        this.maximized = Boolean.parseBoolean(attributes.getValue("maximized"));
        this.index = Integer.parseInt(attributes.getValue("index"));
        this.tabs = new ArrayList<String>();
    }


    public String getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAutoHide() {
        return autoHide;
    }

    public ToolWindowAnchor getAnchor() {
        return anchor;
    }

    public ToolWindowType getType() {
        return type;
    }

    public boolean isAggregateMode() {
        return aggregateMode;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public int getIndex() {
        return index;
    }

    public List<String> getTabs() {
        return tabs;
    }

    public void addTab(Attributes attributes) {
        tabs.add(attributes.getValue("toolWindowId"));
    }
}
