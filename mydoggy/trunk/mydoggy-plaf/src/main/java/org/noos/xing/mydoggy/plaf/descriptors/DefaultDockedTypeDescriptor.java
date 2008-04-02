package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowActionHandler;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultDockedTypeDescriptor extends PropertyChangeEventSource implements DockedTypeDescriptor, PropertyChangeListener, InternalTypeDescriptor {
    private transient ResourceManager resourceManager;

    private ToolWindowActionHandler toolWindowActionHandler;
    private boolean popupMenuEnabled;
    private JMenu toolsMenu;
    private int dockLength;
    private boolean animating;
    private boolean autoHide;

    private boolean previewEnabled;
    private int previewDelay;
    private float previewTransparentRatio;
    private boolean hideRepresentativeButtonOnVisible;
    private boolean idVisibleOnTitleBar;

    public DefaultDockedTypeDescriptor(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.toolsMenu = new JMenu(resourceManager.getString("@@tool.toolsMenu"));
        this.popupMenuEnabled = true;
        this.dockLength = 200;
        this.toolWindowActionHandler = null;
        this.animating = true;
        this.autoHide = false;
        this.previewEnabled = true;
        this.previewDelay = 1000;
        this.previewTransparentRatio = 0.65f;
        this.hideRepresentativeButtonOnVisible = false;
        this.idVisibleOnTitleBar = true;
    }

    public DefaultDockedTypeDescriptor(ToolWindowDescriptor toolWindowDescriptor,
                                       DefaultDockedTypeDescriptor parent,
                                       ResourceManager resourceManager,
                                       int dockLength, boolean popupMenuEnabled,
                                       ToolWindowActionHandler toolWindowActionHandler, boolean animating,
                                       boolean autoHide, boolean previewEnabled, int previewDelay, float previewTransparentRatio,
                                       boolean hideRepresentativeButtonOnVisible, boolean idVisibleOnTitleBar) {
        this.resourceManager = resourceManager;
        this.toolsMenu = new JMenu(resourceManager.getString("@@tool.toolsMenu"));
        this.popupMenuEnabled = popupMenuEnabled;
        this.dockLength = dockLength;
        this.toolWindowActionHandler = toolWindowActionHandler;
        this.animating = animating;
        this.autoHide = autoHide;
        this.previewEnabled = previewEnabled;
        this.previewDelay = previewDelay;
        this.previewTransparentRatio = previewTransparentRatio;
        this.hideRepresentativeButtonOnVisible = hideRepresentativeButtonOnVisible;
        this.idVisibleOnTitleBar = idVisibleOnTitleBar;

        parent.addPropertyChangeListener(this);

        toolWindowDescriptor.getCleaner().addCleaner(this);
    }

    public void setPopupMenuEnabled(boolean enabled) {
        boolean old = this.popupMenuEnabled;
        this.popupMenuEnabled = enabled;

        firePropertyChangeEvent("popupMenuEnabled", old, enabled);
    }

    public boolean isPopupMenuEnabled() {
        return popupMenuEnabled;
    }

    public JMenu getToolsMenu() {
        return toolsMenu;
    }

    public int getDockLength() {
        return dockLength;
    }

    public void setDockLength(int dockLength) {
        int old = this.dockLength;
        this.dockLength = dockLength;

        firePropertyChangeEvent("dockLength", old, dockLength);
    }

    public ToolWindowActionHandler getToolWindowActionHandler() {
        return toolWindowActionHandler;
    }

    public void setToolWindowActionHandler(ToolWindowActionHandler toolWindowActionHandler) {
        ToolWindowActionHandler old = this.toolWindowActionHandler;
        this.toolWindowActionHandler = toolWindowActionHandler;

        firePropertyChangeEvent("toolWindowActionHandler", old, toolWindowActionHandler);
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        if (this.previewEnabled == previewEnabled)
            return;

        boolean old = this.previewEnabled;
        this.previewEnabled = previewEnabled;
        firePropertyChangeEvent("previewEnabled", old, previewEnabled);
    }

    public int getPreviewDelay() {
        return previewDelay;
    }

    public void setPreviewDelay(int previewDelay) {
        if (this.previewDelay == previewDelay)
            return;

        int old = this.previewDelay;
        this.previewDelay = previewDelay;
        firePropertyChangeEvent("previewDelay", old, previewDelay);
    }

    public float getPreviewTransparentRatio() {
        return previewTransparentRatio;
    }

    public void setHideRepresentativeButtonOnVisible(boolean hideRepresentativeButtonOnVisible) {
        if (this.hideRepresentativeButtonOnVisible == hideRepresentativeButtonOnVisible)
            return;

        boolean old = this.hideRepresentativeButtonOnVisible;
        this.hideRepresentativeButtonOnVisible = hideRepresentativeButtonOnVisible;
        firePropertyChangeEvent("hideRepresentativeButtonOnVisible", old, hideRepresentativeButtonOnVisible);
    }

    public boolean isHideRepresentativeButtonOnVisible() {
        return hideRepresentativeButtonOnVisible;
    }

    public void setIdVisibleOnTitleBar(boolean idVisibleOnTitleBar) {
        if (this.idVisibleOnTitleBar == idVisibleOnTitleBar)
            return;

        boolean old = this.idVisibleOnTitleBar;
        this.idVisibleOnTitleBar = idVisibleOnTitleBar;
        firePropertyChangeEvent("idVisibleOnTitleBar", old, idVisibleOnTitleBar);
    }

    public boolean isIdVisibleOnTitleBar() {
        return idVisibleOnTitleBar;
    }

    public void setAutoHide(boolean autoHide) {
        boolean old = this.autoHide;
        this.autoHide = autoHide;

        firePropertyChangeEvent("autoHide", old, autoHide);
    }

    public boolean isAutoHide() {
        return autoHide;
    }

    public void setPreviewTransparentRatio(float previewTransparentRatio) {
        if (this.previewTransparentRatio == previewTransparentRatio)
            return;

        float old = this.previewTransparentRatio;
        this.previewTransparentRatio = previewTransparentRatio;
        firePropertyChangeEvent("previewTransparentRatio", old, previewTransparentRatio);
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        if (this.animating == animating)
            return;

        boolean old = this.animating;
        this.animating = animating;
        firePropertyChangeEvent("animating", old, animating);
    }

    public void setEnabled(boolean enabled) {
        throw new RuntimeException("Cannot call this method. This type is always available.");
    }

    public boolean isEnabled() {
        return true;
    }

    public ToolWindowTypeDescriptor cloneMe(ToolWindowDescriptor toolWindowDescriptor) {
        return new DefaultDockedTypeDescriptor(toolWindowDescriptor,
                                               this,
                                               resourceManager,
                                               dockLength,
                                               popupMenuEnabled,
                                               toolWindowActionHandler,
                                               animating,
                                               this.autoHide, previewEnabled,
                                               previewDelay, previewTransparentRatio,
                                               hideRepresentativeButtonOnVisible,
                                               idVisibleOnTitleBar);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("popupMenuEnabled".equals(evt.getPropertyName())) {
            setPopupMenuEnabled((Boolean) evt.getNewValue());
        } else if ("dockLength".equals(evt.getPropertyName())) {
            setDockLength((Integer) evt.getNewValue());
        } else if ("animating".equals(evt.getPropertyName())) {
            setAnimating((Boolean) evt.getNewValue());
        } else if ("toolWindowActionHandler".equals(evt.getPropertyName())) {
            setToolWindowActionHandler((ToolWindowActionHandler) evt.getNewValue());
        } else if ("previewEnabled".equals(evt.getPropertyName())) {
            setPreviewEnabled((Boolean) evt.getNewValue());
        } else if ("previewDelay".equals(evt.getPropertyName())) {
            setPreviewDelay((Integer) evt.getNewValue());
        } else if ("previewTransparentRatio".equals(evt.getPropertyName())) {
            setPreviewTransparentRatio((Float) evt.getNewValue());
        } else if ("hideRepresentativeButtonOnVisible".equals(evt.getPropertyName())) {
            setHideRepresentativeButtonOnVisible((Boolean) evt.getNewValue());
        } else if ("idVisibleOnTitleBar".equals(evt.getPropertyName())) {
            setIdVisibleOnTitleBar((Boolean) evt.getNewValue());
        } else if ("autoHide".equals(evt.getPropertyName())) {
            setAutoHide((Boolean) evt.getNewValue());
        }
    }

}
