package org.noos.xing.mydoggy.plaf.persistence;

import org.xml.sax.Attributes;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedContent {
    private String key;
    private boolean selected;
    private boolean enabled;
    private boolean detached;

    public PersistedContent(Attributes attributes) {
        this.key = attributes.getValue("key"); 
        this.selected = Boolean.parseBoolean(attributes.getValue("selected"));
        this.enabled = Boolean.parseBoolean(attributes.getValue("enabled"));
        this.detached = Boolean.parseBoolean(attributes.getValue("detached"));
    }

    public String getKey() {
        return key;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDetached() {
        return detached;
    }
}
