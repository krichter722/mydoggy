package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultDockedTypeDescriptor extends PropertyChangeEventSource implements DockedTypeDescriptor, PropertyChangeListener, InternalTypeDescriptor {
    protected ToolWindowDescriptor toolWindowDescriptor;
    protected ToolWindowActionHandler toolWindowActionHandler;
    protected boolean popupMenuEnabled;
    protected JMenu toolsMenu;
    protected int dockLength;
    protected boolean animating;
    protected boolean autoHide;

    protected boolean previewEnabled;
    protected int previewDelay;
    protected float previewTransparentRatio;
    protected boolean hideRepresentativeButtonOnVisible;
    protected boolean idVisibleOnTitleBar;
    protected int minimumDockLength;

    protected Set<ToolWindowAnchor> lockingAnchors;
    protected Map<String, ToolWindowAction> toolWindowActionMap;


    public DefaultDockedTypeDescriptor() {
        this.toolsMenu = new JMenu(SwingUtil.getString("@@tool.toolsMenu"));
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
        this.minimumDockLength = 100;
        this.lockingAnchors = new HashSet<ToolWindowAnchor>();
        this.toolWindowActionMap = new HashMap<String, ToolWindowAction>();
    }

    public DefaultDockedTypeDescriptor(ToolWindowDescriptor toolWindowDescriptor,
                                       DefaultDockedTypeDescriptor parent,
                                       int dockLength, boolean popupMenuEnabled,
                                       ToolWindowActionHandler toolWindowActionHandler, boolean animating,
                                       boolean autoHide, boolean previewEnabled, int previewDelay, float previewTransparentRatio,
                                       boolean hideRepresentativeButtonOnVisible,
                                       boolean idVisibleOnTitleBar,
                                       int minimumDockLength,
                                       ToolWindowAnchor[] lockingAnchors) {
        this.toolWindowDescriptor = toolWindowDescriptor;
        this.toolsMenu = new JMenu(SwingUtil.getString("@@tool.toolsMenu"));
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
        this.minimumDockLength = minimumDockLength;
        this.lockingAnchors = new HashSet<ToolWindowAnchor>();
        this.lockingAnchors.addAll(Arrays.asList(lockingAnchors));
        this.toolWindowActionMap = new HashMap<String, ToolWindowAction>();
        initActions();

        parent.addPropertyChangeListener(this);

        toolWindowDescriptor.getCleaner().addCleaner(this);
    }

    
    public void setPopupMenuEnabled(boolean enabled) {
        if (this.popupMenuEnabled == enabled)
            return;

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
        if (this.dockLength == dockLength || dockLength <= 0)
            return;

        if (dockLength < minimumDockLength)
            setMinimumDockLength(dockLength);

        int old = this.dockLength;
        this.dockLength = dockLength;

        firePropertyChangeEvent("dockLength", old, dockLength);
    }

    public void setMinimumDockLength(int minimumDockLength) {
        if (this.minimumDockLength == minimumDockLength)
            return;

        int old = this.minimumDockLength;
        this.minimumDockLength = minimumDockLength;
        
        firePropertyChangeEvent("minimumDockLength", old, minimumDockLength);
    }

    public int getMinimumDockLength() {
        return minimumDockLength;
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

    public void addLockingAnchor(ToolWindowAnchor anchor) {
        lockingAnchors.add(anchor);
    }

    public void removeLockingAnchor(ToolWindowAnchor anchor) {
        lockingAnchors.remove(anchor);
    }

    public void removeAllLockingAnchor() {
        lockingAnchors.clear();
    }

    public ToolWindowAnchor[] getLockingAnchors() {
        return lockingAnchors.toArray(new ToolWindowAnchor[lockingAnchors.size()]);
    }

    public boolean containsLockingAnchor(ToolWindowAnchor anchor) {
        return lockingAnchors.contains(anchor);
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

    public ToolWindowType getType() {
        return ToolWindowType.DOCKED;
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

    public ToolWindowAction getToolWindowAction(String id) {
        return toolWindowActionMap.get(id);
    }

    public void addToolWindowAction(ToolWindowAction toolWindowAction) {
        toolWindowAction.setToolWindow(toolWindowDescriptor.getToolWindow());
        toolWindowActionMap.put(toolWindowAction.getId(), toolWindowAction);
    }

    public ToolWindowTypeDescriptor cloneMe(ToolWindowDescriptor toolWindowDescriptor) {
        return new DefaultDockedTypeDescriptor(toolWindowDescriptor,
                                               this,
                                               dockLength,
                                               popupMenuEnabled,
                                               toolWindowActionHandler,
                                               animating,
                                               this.autoHide, previewEnabled,
                                               previewDelay, previewTransparentRatio,
                                               hideRepresentativeButtonOnVisible,
                                               idVisibleOnTitleBar,
                                               minimumDockLength,
                                               getLockingAnchors());
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


    protected void initActions() {
/*
        ToolWindowAction toolWindowAction = new DockToolWindowAction();
        toolWindowAction.setToolWindow(toolWindowDescriptor.getToolWindow());
        toolWindowActionMap.put(ToolWindowAction.DOCK_ACTION_ID, toolWindowAction);

        toolWindowAction = new HideToolWindowAction();
        toolWindowAction.setToolWindow(toolWindowDescriptor.getToolWindow());
        toolWindowActionMap.put(ToolWindowAction.HIDE_ACTION_ID, toolWindowAction);
*/
    }

}
