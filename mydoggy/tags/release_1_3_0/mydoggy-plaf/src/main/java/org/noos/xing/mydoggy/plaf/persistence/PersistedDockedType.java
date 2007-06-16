package org.noos.xing.mydoggy.plaf.persistence;

import org.xml.sax.Attributes;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedDockedType {
    private boolean popupMenuEnabled;
    private int dockLength;
    private boolean animating;
    private boolean previewEnabled;
    private int previewDelay;
    private float previewTransparentRatio;

    public PersistedDockedType(Attributes attributes) {
        this.popupMenuEnabled = Boolean.parseBoolean(attributes.getValue("popupMenuEnabled"));
        this.dockLength = Integer.parseInt(attributes.getValue("dockLength"));
        this.animating = Boolean.parseBoolean(attributes.getValue("animating"));
        this.previewEnabled = Boolean.parseBoolean(attributes.getValue("previewEnabled"));
        this.previewDelay = Integer.parseInt(attributes.getValue("previewDelay"));
        this.previewTransparentRatio = Float.parseFloat(attributes.getValue("previewTransparentRatio"));
    }

    public boolean isPopupMenuEnabled() {
        return popupMenuEnabled;
    }

    public int getDockLength() {
        return dockLength;
    }

    public boolean isAnimating() {
        return animating;
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public int getPreviewDelay() {
        return previewDelay;
    }

    public float getPreviewTransparentRatio() {
        return previewTransparentRatio;
    }
}
