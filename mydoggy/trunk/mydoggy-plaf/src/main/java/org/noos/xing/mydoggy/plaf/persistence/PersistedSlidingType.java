package org.noos.xing.mydoggy.plaf.persistence;

import org.xml.sax.Attributes;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedSlidingType {
    private boolean popupMenuEnabled;
    private int dockLength;

    public PersistedSlidingType(Attributes attributes) {
        this.popupMenuEnabled = Boolean.parseBoolean(attributes.getValue("popupMenuEnabled"));
        this.dockLength = Integer.parseInt(attributes.getValue("dockLength"));
    }

    public boolean isPopupMenuEnabled() {
        return popupMenuEnabled;
    }
            
    public int getDockLength() {
        return dockLength;
    }
}
