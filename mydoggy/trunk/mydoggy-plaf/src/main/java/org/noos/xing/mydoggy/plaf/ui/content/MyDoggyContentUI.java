package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyContentUI extends PropertyChangeEventSource {

    protected ContentManager contentManager;
    protected ContentManagerUI contentManagerUI;
    protected Content content;

    protected boolean closable;
    protected boolean detachable;
    protected boolean minimizable;
    protected boolean maximizable;
    protected boolean transparentMode;
    protected float transparentRatio;
    protected int transparentDelay;
    protected Rectangle detachedBounds;
    protected boolean addToTaskBar;
    protected boolean alwaysOnTop;


    public MyDoggyContentUI(ContentManager contentManager,
                            ContentManagerUI contentManagerUI,
                            Content content) {
        this.contentManager = contentManager;
        this.contentManagerUI = contentManagerUI;
        this.content = content;

        this.closable = contentManagerUI.isCloseable();
        this.detachable = contentManagerUI.isDetachable();
        this.minimizable = contentManagerUI.isMinimizable();
        this.maximizable = contentManagerUI.isMaximizable();
        this.alwaysOnTop = true; // Maybe this can be obtained from the contentManagerUI 

        this.transparentMode = true;
        this.transparentRatio = 0.7f;
        this.transparentDelay = 0;

        this.addToTaskBar = false;
    }


    public void cleanup() {
        super.cleanup();

        content = null;
        contentManager = null;
        contentManagerUI = null;
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

        firePropertyChangeEvent("closable", old, closable);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        if (this.detachable == detachable)
            return;

        boolean old = this.detachable;
        this.detachable = detachable;

        firePropertyChangeEvent("detachable", old, detachable);
    }

    public boolean isMinimizable() {
        return minimizable;
    }

    public void setMinimizable(boolean minimizable) {
        if (this.minimizable == minimizable)
            return;

        boolean old = this.minimizable;
        this.minimizable = minimizable;

        firePropertyChangeEvent("minimizable", old, minimizable);
    }

    public boolean isMaximizable() {
        return maximizable;
    }

    public void setMaximizable(boolean maximizable) {
        if (this.maximizable == maximizable)
            return;

        boolean old = this.maximizable;
        this.maximizable = maximizable;

        firePropertyChangeEvent("maximizable", old, maximizable);
    }

    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        if (this.transparentMode == transparentMode)
            return;

        boolean old = this.transparentMode;
        this.transparentMode = transparentMode;

        firePropertyChangeEvent("transparentMode", old, transparentMode);
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        if (this.transparentRatio == transparentRatio)
            return;

        float old = this.transparentRatio;
        this.transparentRatio = transparentRatio;

        firePropertyChangeEvent("transparentRatio", old, transparentRatio);
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public void setTransparentDelay(int transparentDelay) {
        if (this.transparentDelay == transparentDelay)
            return;

        int old = this.transparentDelay;
        this.transparentDelay = transparentDelay;

        firePropertyChangeEvent("transparentDelay", old, transparentDelay);
    }

    public Rectangle getDetachedBounds() {
        return detachedBounds;
    }

    public void setDetachedBounds(Rectangle detachedBounds) {
        if ((this.detachedBounds != null && this.detachedBounds.equals(detachedBounds)) || detachedBounds == null)
            return;

        this.detachedBounds = detachedBounds;
        firePropertyChangeEvent("detachedBounds", null, detachedBounds);
    }

    public void setAddToTaskBarWhenDetached(boolean addToTaskBar) {
        if (this.addToTaskBar == addToTaskBar)
            return;

        boolean old = this.addToTaskBar;
        this.addToTaskBar = addToTaskBar;

        firePropertyChangeEvent("addToTaskBar", old, addToTaskBar);
    }

    public boolean isAddToTaskBarWhenDetached() {
        return addToTaskBar;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        if (this.alwaysOnTop == alwaysOnTop)
            return;

        boolean old = this.alwaysOnTop;
        this.alwaysOnTop = alwaysOnTop;

        firePropertyChangeEvent("alwaysOnTop", old, alwaysOnTop);
    }

    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }
}
