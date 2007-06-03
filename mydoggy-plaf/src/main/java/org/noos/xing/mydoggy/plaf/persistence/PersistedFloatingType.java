package org.noos.xing.mydoggy.plaf.persistence;

import org.xml.sax.Attributes;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedFloatingType {
    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;
    private boolean enabled;
    private boolean modal;
    private Point location;
    private Dimension size;
    private boolean animating;

    public PersistedFloatingType(Attributes attributes) {
        this.transparentMode = Boolean.parseBoolean(attributes.getValue("transparentMode"));
        this.transparentDelay = Integer.parseInt(attributes.getValue("transparentDelay"));
        this.transparentRatio = Float.parseFloat(attributes.getValue("transparentRatio"));
        this.enabled = Boolean.parseBoolean(attributes.getValue("enabled"));
        this.modal = Boolean.parseBoolean(attributes.getValue("modal"));
        this.animating = Boolean.parseBoolean(attributes.getValue("animating"));

        if (attributes.getValue("x") != null)
            this.location = new Point(
                    Integer.parseInt(attributes.getValue("x")),
                    Integer.parseInt(attributes.getValue("y"))
            );

        if (attributes.getValue("width") != null)  
            this.size = new Dimension(
                    Integer.parseInt(attributes.getValue("width")),
                    Integer.parseInt(attributes.getValue("height"))
            );
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

    public boolean isModal() {
        return modal;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public boolean isAnimating() {
        return animating;
    }
}