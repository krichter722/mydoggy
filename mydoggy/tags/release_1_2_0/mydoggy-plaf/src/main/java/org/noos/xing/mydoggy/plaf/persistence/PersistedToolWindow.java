package org.noos.xing.mydoggy.plaf.persistence;

import org.xml.sax.Attributes;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedToolWindow {
    private String id;
    private boolean available;
    private boolean visible;
    private boolean active;
    private boolean autoHide;
    private ToolWindowAnchor anchor;
    private ToolWindowType type;

    public PersistedToolWindow(Attributes attributes) {
        this.id = attributes.getValue("id");
        this.available = Boolean.parseBoolean(attributes.getValue("available"));
        this.visible = Boolean.parseBoolean(attributes.getValue("visible"));
        this.active = Boolean.parseBoolean(attributes.getValue("active"));
        this.autoHide = Boolean.parseBoolean(attributes.getValue("autoHide")); 
        this.anchor = ToolWindowAnchor.valueOf(attributes.getValue("anchor"));
        this.type = ToolWindowType.valueOf(attributes.getValue("type"));
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
}
