package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.actions.PlafToolWindowAction;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class DefaultToolWindowTypeDescriptor extends PropertyChangeEventSource implements ToolWindowTypeDescriptor,
                                                                                                   PropertyChangeListener {
    protected ToolWindowDescriptor toolWindowDescriptor;

    protected boolean enabled;
    protected boolean animating;
    protected boolean autoHide;
    protected boolean idVisibleOnTitleBar;
    protected boolean titleBarButtonsVisible;
    protected boolean titleBarVisible;
    protected boolean hideRepresentativeButtonOnVisible;

    protected Map<String, ToolWindowAction> toolWindowActionMap;


    protected DefaultToolWindowTypeDescriptor() {
        this.enabled = true;
        this.animating = true;
        this.autoHide = false;
        this.idVisibleOnTitleBar = true;
        this.hideRepresentativeButtonOnVisible = false;
        this.titleBarButtonsVisible = true;
        this.titleBarVisible = true;

        this.toolWindowActionMap = new HashMap<String, ToolWindowAction>();
    }

    public DefaultToolWindowTypeDescriptor(ToolWindowDescriptor toolWindowDescriptor,
                                           DefaultToolWindowTypeDescriptor parent,
                                           boolean enabled,
                                           boolean animating,
                                           boolean autoHide,
                                           boolean idVisibleOnTitleBar,
                                           boolean hideRepresentativeButtonOnVisible,
                                           boolean titleBarButtonsVisible,
                                           boolean titleBarVisible) {
        super(toolWindowDescriptor.getManager().getFirePublicEvent());
        
        this.toolWindowDescriptor = toolWindowDescriptor;
        this.animating = animating;
        this.autoHide = autoHide;
        this.enabled = enabled;
        this.idVisibleOnTitleBar = idVisibleOnTitleBar;
        this.hideRepresentativeButtonOnVisible = hideRepresentativeButtonOnVisible;
        this.titleBarButtonsVisible = titleBarButtonsVisible;
        this.titleBarVisible = titleBarVisible;

        this.toolWindowActionMap = new HashMap<String, ToolWindowAction>();

        parent.addPropertyChangeListener(this);

        toolWindowDescriptor.getCleaner().addCleaner(this);
    }


    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled)
            return;

        boolean old = this.enabled;
        this.enabled = enabled;

        firePropertyChangeEvent("enabled", old, enabled);
    }

    public boolean isEnabled() {
        return enabled;
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

    public void setAutoHide(boolean autoHide) {
        boolean old = this.autoHide;
        this.autoHide = autoHide;

        firePropertyChangeEvent("autoHide", old, autoHide);
    }

    public boolean isAutoHide() {
        return autoHide;
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

    public void setTitleBarButtonsVisible(boolean titleBarButtonsVisible) {
        if (this.titleBarButtonsVisible == titleBarButtonsVisible)
            return;

        boolean old = this.titleBarButtonsVisible;
        this.titleBarButtonsVisible = titleBarButtonsVisible;
        firePropertyChangeEvent("titleBarButtonsVisible", old, titleBarButtonsVisible);
    }

    public boolean isTitleBarButtonsVisible() {
        return titleBarButtonsVisible;
    }

    public void setTitleBarVisible(boolean titleBarVisible) {
        if (this.titleBarVisible == titleBarVisible)
            return;

        boolean old = this.titleBarVisible;
        this.titleBarVisible = titleBarVisible;
        firePropertyChangeEvent("titleBarVisible", old, titleBarVisible);
    }

    public boolean isTitleBarVisible() {
        return titleBarVisible;
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

    public ToolWindowAction getToolWindowAction(String id) {
        return toolWindowActionMap.get(id);
    }

    public void addToolWindowAction(ToolWindowAction toolWindowAction) {
        addToolWindowAction(toolWindowAction, -1);
    }

    public void addToolWindowAction(ToolWindowAction toolWindowAction, int index) {
        if (toolWindowAction == null)
            throw new IllegalArgumentException("The action cannot be null.");

//        if (getToolWindowAction(toolWindowAction.getId()) != null)
//            throw new IllegalArgumentException("The action is already registered.");

        ToolWindowAction oldToolWindowAction = toolWindowActionMap.get(toolWindowAction.getId());
        if (oldToolWindowAction != null) {
            if (!toolWindowDescriptor.containsToolWindowAction(this, oldToolWindowAction.getId()))
                oldToolWindowAction.setToolWindow(null);
        }

        if (!toolWindowDescriptor.containsToolWindowAction(this, toolWindowAction))
            toolWindowAction.setToolWindow(toolWindowDescriptor.getToolWindow());

        if (toolWindowAction.getValue("constraint") == null)
            toolWindowAction.putValue("constraint", index);
        toolWindowActionMap.put(toolWindowAction.getId(), toolWindowAction);

        firePropertyChangeEvent("toolWindowAction", oldToolWindowAction, toolWindowAction, new Object[]{this});
    }

    public ToolWindowAction[] getToolWindowActions() {
        return toolWindowActionMap.values().toArray(new ToolWindowAction[0]);
    }

    public void removeToolWindowAction(String id) {
        if (toolWindowActionMap.get(id) instanceof PlafToolWindowAction)
            throw new IllegalArgumentException("Cannot remove a system action.");

        ToolWindowAction toolWindowAction = toolWindowActionMap.remove(id);
        if (toolWindowAction != null) {
            if (!toolWindowDescriptor.containsToolWindowAction(this, toolWindowAction.getId()))
                toolWindowAction.setToolWindow(null);
        }

        firePropertyChangeEvent("toolWindowAction", toolWindowAction, null, new Object[]{this});
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if ("idVisibleOnTitleBar".equals(evt.getPropertyName())) {
            setIdVisibleOnTitleBar((Boolean) evt.getNewValue());
        } else if ("autoHide".equals(evt.getPropertyName())) {
            setAutoHide((Boolean) evt.getNewValue());
        } else if ("enabled".equals(evt.getPropertyName())) {
            setEnabled((Boolean) evt.getNewValue());
        } else if ("animating".equals(evt.getPropertyName())) {
            setAnimating((Boolean) evt.getNewValue());
        } else if ("hideRepresentativeButtonOnVisible".equals(evt.getPropertyName())) {
            setHideRepresentativeButtonOnVisible((Boolean) evt.getNewValue());
        } else if ("titleBarButtonsVisible".equals(evt.getPropertyName())) {
            setTitleBarButtonsVisible((Boolean) evt.getNewValue());
        } else if ("titleBarVisible".equals(evt.getPropertyName())) {
            setTitleBarVisible((Boolean) evt.getNewValue());
        }
    }


}
