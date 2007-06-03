package org.noos.xing.mydoggy.plaf.persistence;

import org.xml.sax.Attributes;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedSlidingType {
    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;
    private boolean enabled;
    private boolean animating;

    public PersistedSlidingType(Attributes attributes) {
        this.transparentMode = Boolean.parseBoolean(attributes.getValue("transparentMode"));
        this.transparentDelay = Integer.parseInt(attributes.getValue("transparentDelay"));
        this.transparentRatio = Float.parseFloat(attributes.getValue("transparentRatio"));
        this.enabled = Boolean.parseBoolean(attributes.getValue("enabled"));
        this.animating = Boolean.parseBoolean(attributes.getValue("animating"));
    }


    public boolean isTransparentMode() {
        return transparentMode;
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAnimating() {
        return animating;
    }
}
