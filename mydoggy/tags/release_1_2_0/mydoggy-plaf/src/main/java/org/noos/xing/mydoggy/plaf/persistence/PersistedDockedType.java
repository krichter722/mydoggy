package org.noos.xing.mydoggy.plaf.persistence;

import org.xml.sax.Attributes;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedDockedType {
    private boolean popupMenuEnabled;
    private int dockLength;

    public PersistedDockedType(Attributes attributes) {
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
