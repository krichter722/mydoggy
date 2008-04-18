package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.DesktopContentUI;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DesktopContentFrame extends JInternalFrame implements DesktopContentUI, Cleaner {
    protected boolean detachable;
    protected boolean transparentMode;
    protected float transparentRatio;
    protected int transparentDelay;
    protected Content content;
    protected Rectangle detachedBounds;
    protected boolean addToTaskBar;
    protected boolean minimizable;


    public DesktopContentFrame(ContentManagerUI contentManagerUI, Content content, String title) {
        super(title, true, true, true, true);
        this.content = content;

        this.closable = contentManagerUI.isCloseable();
        this.detachable = contentManagerUI.isDetachable();
        this.minimizable = contentManagerUI.isMinimizable();

        this.transparentMode = true;
        this.transparentRatio = 0.8f;
        this.transparentDelay = 1000;
    }


    public boolean isIconified() {
        return super.isIcon();
    }

    public void setIconified(boolean iconified) {
        try {
            setIcon(iconified);
        } catch (PropertyVetoException ignore) {
            ignore.printStackTrace();
        }
    }

    public Content getContent() {
        return content;
    }

    public boolean isCloseable() {
        return closable;
    }

    public void setCloseable(boolean closable) {
        if (this.closable == closable)
            return;

        boolean old = this.closable;
        this.closable = closable;

        firePropertyChange("closable", old, closable);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        if (this.detachable == detachable)
            return;

        boolean old = this.detachable;
        this.detachable = detachable;

        firePropertyChange("detachable", old, detachable);
    }

    public boolean isMinimizable() {
        return minimizable;
    }

    public void setMinimizable(boolean minimizable) {
        if (this.minimizable == minimizable)
            return;

        boolean old = this.minimizable;
        this.minimizable = minimizable;

        firePropertyChange("minimizable", old, minimizable);
    }

    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        if (this.transparentMode == transparentMode)
            return;

        boolean old = this.transparentMode;
        this.transparentMode = transparentMode;

        firePropertyChange("transparentMode", old, transparentMode);
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        if (this.transparentRatio == transparentRatio)
            return;

        float old = this.transparentRatio;
        this.transparentRatio = transparentRatio;

        firePropertyChange("transparentRatio", old, transparentRatio);
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public void setTransparentDelay(int transparentDelay) {
        if (this.transparentDelay == transparentDelay)
            return;

        int old = this.transparentDelay;
        this.transparentDelay = transparentDelay;

        firePropertyChange("transparentDelay", old, transparentDelay);
    }

    public void setConstraints(Object... constraints) {
        if (constraints.length > 0) {
            if (constraints[0] instanceof Point) {
                Point location = (Point) constraints[0];

                setBounds(location.x, location.y, 320, 200);
            } else if (constraints[0] instanceof Rectangle) {
                setBounds((Rectangle) constraints[0]);
            }
        }
    }

    public Rectangle getDetachedBounds() {
        return detachedBounds;
    }

    public void setDetachedBounds(Rectangle detachedBounds) {
        if ((this.detachedBounds != null && this.detachedBounds.equals(detachedBounds)) || detachedBounds == null)
            return;

        this.detachedBounds = detachedBounds;
        firePropertyChange("detachedBounds", null, detachedBounds);
    }

    public void setAddToTaskBarWhenDetached(boolean addToTaskBar) {
        if (this.addToTaskBar == addToTaskBar)
            return;

        boolean old = this.addToTaskBar;
        this.addToTaskBar = addToTaskBar;

        firePropertyChange("addToTaskBar", old, addToTaskBar);
    }

    public boolean isAddToTaskBarWhenDetached() {
        return addToTaskBar;
    }

    public void cleanup() {
        content = null;
    }
}
