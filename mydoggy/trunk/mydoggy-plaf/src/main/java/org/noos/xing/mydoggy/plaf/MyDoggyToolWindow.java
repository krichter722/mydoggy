package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.util.DockableManager2ToolWindowWrapper;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Angelo De Caro
 */
public class MyDoggyToolWindow extends PropertyChangeEventSource implements ToolWindow {
    static final Object LOCK = new ToolWindowLock();

    static class ToolWindowLock {
    }

    enum Property {
        visible,
    }

    protected int index;
    protected String id;
    protected ToolWindowAnchor anchor;
    protected ToolWindowType type;

    protected boolean autoHide;
    protected boolean available;
    protected boolean visible;
    protected boolean active;
    protected boolean flash;
    protected boolean maximized;
    protected boolean aggregateEnabled;
    protected boolean lockedOnAnchor;
    protected boolean hideOnZeroTabs;

    protected java.util.List<ToolWindowTab> toolWindowTabs;
    protected Map<Object, ToolWindowTab> aliases;
    protected ToolWindowTab rootTab;

    protected ToolWindowDescriptor descriptor;

    protected EventListenerList toolWindowListeners;

    protected int availablePosition;

    protected AggregationPosition lastAggregationPosition;
    protected ToolWindowType lastDetachedType;


    public MyDoggyToolWindow(MyDoggyToolWindowManager manager,
                             String id, int index,
                             ToolWindowAnchor anchor, ToolWindowType type,
                             String title, Icon icon, Component component) {
        super(manager.getFirePublicEvent());

        this.toolWindowTabs = new ArrayList<ToolWindowTab>();
        this.id = id;
        this.index = index;
        this.anchor = anchor;
        this.type = type;
        this.available = this.active = this.visible = this.maximized = this.aggregateEnabled = false;
        this.hideOnZeroTabs = true;

        this.descriptor = (ToolWindowDescriptor) manager.createDescriptor(this);

        rootTab = addTabInternal(title, null, component, null, true);
        rootTab.setIcon(icon);

        setTitle(title);
        setIcon(icon);
    }


    public ToolWindowTab[] getDockables() {
        return getToolWindowTabs();
    }

    public ToolWindowTab getDockableById(String id) {
        throw new RuntimeException("Cannot call this method!!!");
    }

    public void addAlias(ToolWindowTab toolWindowTab, Object alias) {
        if (aliases == null)
            this.aliases = new HashMap<Object, ToolWindowTab>();
        aliases.put(alias, toolWindowTab);
    }

    public Object[] getAliases(ToolWindowTab toolWindowTab) {
        if (aliases == null)
            return new Object[0];

        java.util.List<Object> result = new ArrayList<Object>();
        for (Map.Entry<Object, ToolWindowTab> entry : aliases.entrySet()) {
            if (entry.getValue() == toolWindowTab)
                result.add(entry.getKey());
        }
        return result.toArray();
    }

    public ToolWindowTab removeAlias(Object alias) {
        return aliases.remove(alias);
    }

    public void addDockableManagerListener(DockableManagerListener listener) {
        addToolWindowListener(new DockableManager2ToolWindowWrapper(listener));
    }

    public void removeDockableManagerListener(DockableManagerListener listener) {
        for (ToolWindowListener managerListener : getToolWindowListeners()) {
            if (managerListener instanceof DockableManager2ToolWindowWrapper) {
                if (((DockableManager2ToolWindowWrapper) managerListener).getListener() == listener) {
                    removeToolWindowListener(managerListener);
                }
            }
        }
    }

    public DockableManagerListener[] getDockableManagerListeners() {
        java.util.List<DockableManagerListener> listeners = new ArrayList<DockableManagerListener>();
        for (ToolWindowListener managerListener : getToolWindowListeners()) {
            if (managerListener instanceof DockableManager2ToolWindowWrapper) {
                listeners.add(((DockableManager2ToolWindowWrapper) managerListener).getListener());
            }
        }
        return listeners.toArray(new DockableManagerListener[listeners.size()]);
    }


    public ToolWindowManager getDockableManager() {
        return descriptor.getManager();
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public Component getComponent() {
        if (toolWindowTabs.size() == 0)
            return null;
        if (toolWindowTabs.size() == 1)
            return toolWindowTabs.get(0).getComponent();
        else {
            for (ToolWindowTab toolWindowTab : toolWindowTabs) {
                if (toolWindowTab.isSelected())
                    return toolWindowTab.getComponent();
            }
            return toolWindowTabs.get(0).getComponent();
        }
    }

    public void setIndex(int index) {
        if (index != -1 && index <= 0 && index > 9)
            throw new IllegalArgumentException("Invalid index. Valid index range is [-1, 1-9]. [tool : " + getId() + ", index : " + index + "]");

        synchronized (getLock()) {
            if (index == this.index)
                return;

            int old = this.index;
            this.index = index;

            firePropertyChangeEvent("index", old, index);
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        if (this.available == available)
            return;

        setAvailableInternal(available, false);
        descriptor.getRepresentativeAnchorDescriptor().setVisible(available);
    }

    public boolean isVisible() {
        if (type == ToolWindowType.EXTERN) {
            Dockable delegator = descriptor.getManager().getDockableWrapper(this);
            if (delegator == null)
                return visible;

            if (delegator instanceof ToolWindowTab) {
                ToolWindowTab toolWindowTab = (ToolWindowTab) delegator;
                return toolWindowTab.isSelected() && toolWindowTab.getOwner().isVisible();
            } else
                return delegator.isSelected();
        }

        return visible;
    }

    public void aggregate() {
        if (!isVisible()) {
            switch (anchor) {
                case LEFT:
                case RIGHT:
                    aggregate(AggregationPosition.BOTTOM);
                    break;
                case TOP:
                case BOTTOM:
                    aggregate(AggregationPosition.RIGHT);
                    break;
            }
        }
    }

    public void aggregate(AggregationPosition onPosition) {
        aggregate(null, onPosition);
    }

    public void aggregateByReference(ToolWindow refToolWindow, AggregationPosition onPosition) {
        aggregateInternal(null, onPosition, refToolWindow);
    }

    public void aggregate(ToolWindow onToolWindow, AggregationPosition onPosition) {
        aggregateInternal(onToolWindow, onPosition, null);
    }

    public void setAggregateMode(boolean aggregateEnabled) {
        if (this.aggregateEnabled == aggregateEnabled)
            return;

        synchronized (getLock()) {
            boolean old = this.aggregateEnabled;
            this.aggregateEnabled = aggregateEnabled;

            firePropertyChangeEvent("aggregateEnabled", old, this.aggregateEnabled);
        }
    }

    public boolean isAggregateMode() {
        return aggregateEnabled;
    }

    public boolean isFlashing() {
        return flash;
    }

    public void setDetached(boolean detached) {
        if (detached) {
            ToolWindowType targetType = null;
            try {
                targetType = ToolWindowType.valueOf(UIManager.getString(MyDoggyKeySpace.TOOL_WINDOW_DETACH_TYPE));
            } catch (IllegalArgumentException e) {
                targetType = ToolWindowType.FLOATING;
            }

            if (getType() != targetType)
                lastDetachedType = getType();

            setType(targetType);
        } else {
            setType(lastDetachedType);
        }
    }

    public boolean isDetached() {
        return type != ToolWindowType.DOCKED && type != ToolWindowType.SLIDING;
    }

    public void setSelected(boolean selected) {
        setActive(selected);
    }

    public boolean isSelected() {
        return isActive();
    }

    public void setFlashing(boolean flash) {
        if (flash && isActive())
            return;

        if (!this.flash && !flash) {
            // Verify a tab...
            for (ToolWindowTab toolWindowTab : toolWindowTabs) {
                toolWindowTab.setFlashing(false);
            }            
        } else {
            if (this.flash == flash)
                return;

            synchronized (getLock()) {

                boolean old = this.flash;
                this.flash = flash;

                firePropertyChangeEvent("flash", old, flash);
            }
        }
    }

    public void setFlashing(int duration) {
        if (isVisible() && isActive())
            return;

        if (this.flash)
            return;

        synchronized (getLock()) {
            this.flash = true;

            firePropertyChangeEvent("flash", false, true, duration);
        }
    }

    public void setVisible(boolean visible) {
        setVisibleInternal(visible, false, null, null, null);
    }

    public boolean isActive() {
        if (type == ToolWindowType.EXTERN) {
            Dockable delegator = descriptor.getManager().getDockableWrapper(this);
            if (delegator == null)
                return active;

            if (delegator instanceof ToolWindowTab) {
                ToolWindowTab toolWindowTab = (ToolWindowTab) delegator;
                return toolWindowTab.isSelected() && toolWindowTab.getOwner().isActive();
            } else
                return delegator.isSelected();
        }

        return active;
    }

    public void setActive(boolean active) {
        if (this.active == active && publicEvent)
            return;

        synchronized (getLock()) {
            if (active) {
                setFlashing(false);
                setAvailable(active);
                setVisible(active);
            }

            boolean old = this.active;
            if (!publicEvent)
                old = false;
            this.active = active;

            firePropertyChangeEvent("active", old, active);
            firePropertyChangeEvent("selected", old, active);
        }
    }

    public ToolWindowAnchor getAnchor() {
        return anchor;
    }

    public int getAnchorIndex() {
        return descriptor.getAnchorIndex();
    }

    public void setAnchor(ToolWindowAnchor anchor) {
        setAnchor(anchor, -2);
    }

    public void setAnchor(ToolWindowAnchor anchor, int index) {
        synchronized (getLock()) {
            if (this.anchor == anchor &&
                (index == getDescriptor().getAnchorIndex() || index == -2))
                return;

            if (isLockedOnAnchor() && !descriptor.getRepresentativeAnchorDescriptor().containsLockingAnchor(anchor))
                return;

            if (isMaximized())
                setMaximized(false);

            if (isAvailable() &&
                (getType() == ToolWindowType.DOCKED || getType() == ToolWindowType.SLIDING)) {
                boolean tempVisible = isVisible();
                boolean tempActive = isActive();


                if (this.anchor == anchor || !isAvailable()) {
                    this.anchor = anchor;

                    fireAnchorEvent(null, anchor, index);
                } else {
                    publicEvent = false;

                    ToolWindowAnchor oldAnchor;
                    try {
                        setAvailableInternal(false, true);

                        oldAnchor = this.anchor;
                        this.anchor = anchor;

                        availablePosition = index;
                        setAvailableInternal(true, true);
                        if (tempActive) {
                            setActive(true);
                        } else if (tempVisible)
                            setVisible(true);
                    } finally {
                        publicEvent = true;
                    }
                    fireAnchorEvent(oldAnchor, anchor, index);
                }

            } else {
                ToolWindowAnchor oldAnchor = this.anchor;
                this.anchor = anchor;

                if (oldAnchor == anchor) {
                    if (index != -2)
                        fireAnchorEvent(null, anchor, index);
                } else
                    fireAnchorEvent(oldAnchor, anchor, index);
            }
        }
    }

    public boolean isAutoHide() {
        if (type == ToolWindowType.EXTERN)
            return getTypeDescriptor(DockedTypeDescriptor.class).isAutoHide();

        return getTypeDescriptor(type).isAutoHide();
    }

    public void setAutoHide(boolean autoHide) {
        if (type == ToolWindowType.EXTERN)
            return;

        boolean old = isAutoHide();
        getTypeDescriptor(type).setAutoHide(autoHide);

        firePropertyChangeEvent("autoHide", old, autoHide);
    }

    public void setLockedOnAnchor(boolean lockedOnAnchor) {
        if (this.lockedOnAnchor == lockedOnAnchor)
            return;

        boolean old = this.lockedOnAnchor;
        this.lockedOnAnchor = lockedOnAnchor;

        firePropertyChangeEvent("lockedOnAnchor", old, lockedOnAnchor);
    }

    public boolean isLockedOnAnchor() {
        return lockedOnAnchor;
    }

    public ToolWindowType getType() {
        return type;
    }

    public void setType(ToolWindowType type) {
        if (type == ToolWindowType.EXTERN)
            throw new IllegalArgumentException("Invalid type. [type :" + type + "]");

        if (this.type == ToolWindowType.EXTERN)
            descriptor.getManager().removeIfDockableDelegator(this);

        boolean forceAvailable = false;
        if (this.type == ToolWindowType.EXTERN && type != ToolWindowType.FLOATING_FREE)
            forceAvailable = true;

        setTypeInternal(type);

        if (forceAvailable) {
            available = false;
            setAvailable(true);
        }
    }

    public Icon getIcon() {
        return (rootTab != null) ? rootTab.getIcon() : null;
    }

    public void setIcon(Icon icon) {
        if (rootTab == null)
            throw new IllegalArgumentException("Cannot call this method. There is no tab.");

        synchronized (getLock()) {
            if (toolWindowTabs.size() == 0)
                return;

            if (getIcon() == icon)
                return;

            Icon old = this.getIcon();
            rootTab.setIcon(icon);

            firePropertyChangeEvent("icon", old, icon);
        }
    }

    public String getTitle() {
        return (rootTab != null) ? rootTab.getTitle() : null;
    }

    public void setTitle(String title) {
        if (rootTab == null)
            throw new IllegalArgumentException("Cannot call this method. There is no tab.");

        synchronized (getLock()) {
            if (toolWindowTabs.size() == 0)
                return;

            if (title != null && title.equals(getTitle()))
                return;

            String old = this.getTitle();
            rootTab.setTitle(title);

            firePropertyChangeEvent("title", old, title);
        }
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMinimized(boolean minimized) {
        setVisible(minimized);
    }

    public boolean isMinimized() {
        return !isVisible();
    }

    public void ensureVisible() {
        if (isVisible()) {
            DockedTypeDescriptor descriptor = getTypeDescriptor(DockedTypeDescriptor.class);
            if (descriptor.getDockLength() < descriptor.getMinimumDockLength())
                descriptor.setDockLength(descriptor.getMinimumDockLength());
        }
    }

    public void setMaximized(boolean maximized) {
        if (this.maximized == maximized || !isVisible())
            return;

        synchronized (getLock()) {
            firePlafPropertyChangeEvent("maximizedBefore", this.maximized, maximized);

            boolean old = this.maximized;
            this.maximized = maximized;

            firePropertyChangeEvent("maximized", old, maximized);
        }
    }

    public boolean isHideOnZeroTabs() {
        return hideOnZeroTabs;
    }

    public void setHideOnZeroTabs(boolean hideOnZeroTabs) {
        if (this.hideOnZeroTabs == hideOnZeroTabs)
            return;

        boolean old = this.hideOnZeroTabs;
        this.hideOnZeroTabs = hideOnZeroTabs;
        firePropertyChangeEvent("hideOnZeroTabs", old, hideOnZeroTabs);
    }

    public ToolWindowTab addToolWindowTab(String title, Component component) {
        return addTabInternal(title, null, component, null, false);
    }

    public ToolWindowTab addToolWindowTab(Dockable dockable) {
        synchronized (getLock()) {
            ToolWindowTab result;

            if (dockable instanceof ToolWindow) {
                descriptor.getManager().removeIfDockableDelegator(dockable);

                ToolWindow delegator = (ToolWindow) dockable;
                for (ToolWindowTab toolWindowTab : toolWindowTabs) {
                    if (toolWindowTab.getDockableDelegator() == dockable)
                        return toolWindowTab;
                }

                ((MyDoggyToolWindow) dockable).setTypeInternal(ToolWindowType.EXTERN);
                result = addTabInternal(delegator.getTitle(),
                                        delegator.getIcon(),
                                        delegator.getComponent(),
                                        delegator,
                                        false);

                for (ToolWindowTab tab : delegator.getToolWindowTabs()) {
                    if (!tab.getTitle().equals(delegator.getTitle()))
                        addTabInternal(tab);
                }
            } else
                throw new IllegalArgumentException("Dockable not yet supported,");

            return result;
        }
    }

    public boolean removeToolWindowTab(ToolWindowTab toolWindowTab) {
        if (toolWindowTab == null)
            throw new IllegalArgumentException("ToolWindowTab cannot be null.");
        if (!(toolWindowTab instanceof MyDoggyToolWindowTab))
            throw new IllegalArgumentException("Invalid ToolWindowTab instance.");

        // Deactive the tab
        toolWindowTab.setFlashing(false);
        toolWindowTab.setMaximized(false);

        // Remove the tab
        boolean result = toolWindowTabs.remove(toolWindowTab);
        if (result) {
            try {
                fireToolWindowTabEvent(new ToolWindowTabEvent(this,
                                                              ToolWindowTabEvent.ActionId.TAB_REMOVED,
                                                              this,
                                                              toolWindowTab));
            } finally {
                // Clean tab
                ((MyDoggyToolWindowTab) toolWindowTab).cleanup();
            }

        }

        // Check for a delegator...
        if (toolWindowTab.getDockableDelegator() != null) {
            Dockable dockable = toolWindowTab.getDockableDelegator();

            if (dockable instanceof ToolWindow) {
                // Restore the toolwindow
                ToolWindow toolWindow = (ToolWindow) dockable;
                toolWindow.setType(ToolWindowType.DOCKED);

                for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                    for (ToolWindowTab fromTab : getToolWindowTabs()) {
                        if (fromTab.getDockableDelegator() == tab) {
                            removeToolWindowTab(fromTab);
                        }
                    }
                }
            }
        }

        // Finilize
        if (rootTab == toolWindowTab) {
            if (toolWindowTabs.size() > 0)
                rootTab = toolWindowTabs.get(0);
            else
                rootTab = null;
        }

        if (toolWindowTabs.size() == 0 && isHideOnZeroTabs())
            setAvailable(false);

        return result;
    }

    public ToolWindowTab[] getToolWindowTabs() {
        return toolWindowTabs.toArray(new ToolWindowTab[toolWindowTabs.size()]);
    }

    public void addToolWindowAction(ToolWindowAction toolWindowAction) {
        descriptor.addCommonToolWindowAction(toolWindowAction);
    }

    public void removeToolWindowAction(String id) {
        descriptor.removeCommonToolWindowAction(id);
    }

    public void addToolWindowListener(ToolWindowListener listener) {
        if (toolWindowListeners == null)
            toolWindowListeners = new EventListenerList();

        toolWindowListeners.add(ToolWindowListener.class, listener);
    }

    public void removeToolWindowListener(ToolWindowListener listener) {
        if (toolWindowListeners == null)
            return;

        toolWindowListeners.remove(ToolWindowListener.class, listener);
    }

    public ToolWindowListener[] getToolWindowListeners() {
        if (toolWindowListeners == null)
            return new ToolWindowListener[0];

        return toolWindowListeners.getListeners(ToolWindowListener.class);
    }

    public ToolWindowTypeDescriptor getTypeDescriptor(ToolWindowType type) {
        return descriptor.getTypeDescriptor(type);
    }

    public <T extends ToolWindowTypeDescriptor> T getTypeDescriptor(Class<T> descriptorClass) {
        if (descriptorClass.isAssignableFrom(DockedTypeDescriptor.class))
            return (T) descriptor.getTypeDescriptor(ToolWindowType.DOCKED);
        else if (descriptorClass.isAssignableFrom(SlidingTypeDescriptor.class))
            return (T) descriptor.getTypeDescriptor(ToolWindowType.SLIDING);
        else if (descriptorClass.isAssignableFrom(FloatingLiveTypeDescriptor.class))
            return (T) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);
        else if (descriptorClass.isAssignableFrom(FloatingTypeDescriptor.class))
            return (T) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
        else
            throw new IllegalArgumentException("Cannot recognize the class type. [class : " + descriptorClass + "]");
    }

    public RepresentativeAnchorDescriptor getRepresentativeAnchorDescriptor() {
        return descriptor.getRepresentativeAnchorDescriptor();
    }

    public String toString() {
        return "MyDoggyToolWindow{" +
               "id='" + id + '\'' +
               ", index=" + index +
               ", available=" + available +
               ", visible=" + visible +
               ", active=" + active +
               ", anchor=" + anchor +
               ", type=" + type +
               ", title='" + getTitle() + '\'' +
               ", autoHide=" + autoHide +
               ", flashing=" + flash +
               ", maximized=" + maximized +
               '}';
    }

    public void cleanup() {
        super.cleanup();

        // Remove listeners...
        for (ToolWindowListener listener : getToolWindowListeners()) {
            removeToolWindowListener(listener);
        }
    }

    public final Object getLock() {
        return LOCK;
    }

    public ToolWindowDescriptor getDescriptor() {
        return descriptor;
    }


    protected void aggregateInternal(ToolWindow onToolWindow, AggregationPosition aggregationPosition, ToolWindow aggregateReferenceTool) {
        try {
            if (onToolWindow != null) {
                if ((descriptor.getManager().getContentManager().isEnabled() &&
                     onToolWindow.getAnchor() != anchor &&
                     onToolWindow.getType() != ToolWindowType.FLOATING_LIVE &&
                     onToolWindow.getType() != ToolWindowType.FLOATING &&
                     onToolWindow.getType() != ToolWindowType.FLOATING_FREE) || !onToolWindow.isVisible())
                
                    return;
            }

            if (isAutoHide())
                setAutoHide(false);

            descriptor.getManager().setShowingGroup();
            if (!isVisible()) {
                if ((onToolWindow != null && onToolWindow.getType() == ToolWindowType.FLOATING_LIVE) ||
                    (aggregateReferenceTool != null && aggregateReferenceTool.getType() == ToolWindowType.FLOATING_LIVE)) {

                    setType(ToolWindowType.FLOATING_LIVE);
                } else if ((onToolWindow != null && onToolWindow.getType() == ToolWindowType.FLOATING) ||
                           (aggregateReferenceTool != null && aggregateReferenceTool.getType() == ToolWindowType.FLOATING)) {

                    setType(ToolWindowType.FLOATING);
                } else if ((onToolWindow != null && onToolWindow.getType() == ToolWindowType.FLOATING_FREE) ||
                           (aggregateReferenceTool != null && aggregateReferenceTool.getType() == ToolWindowType.FLOATING_FREE)) {

                    setType(ToolWindowType.FLOATING);
                } else {
                    if (getType() != ToolWindowType.DOCKED)
                        setType(ToolWindowType.DOCKED);
                }

                setVisibleInternal(true, true, onToolWindow, aggregationPosition, aggregateReferenceTool);
            } else {
                publicEvent = false;
                try {
                    setVisible(false);
                } finally {
                    publicEvent = true;
                }

                if ((onToolWindow != null && onToolWindow.getType() == ToolWindowType.FLOATING_LIVE) ||
                    (aggregateReferenceTool != null && aggregateReferenceTool.getType() == ToolWindowType.FLOATING_LIVE)) {
                    setType(ToolWindowType.FLOATING_LIVE);
                } else if ((onToolWindow != null && onToolWindow.getType() == ToolWindowType.FLOATING) ||
                           (aggregateReferenceTool != null && aggregateReferenceTool.getType() == ToolWindowType.FLOATING)) {
                    setType(ToolWindowType.FLOATING);
                } else if ((onToolWindow != null && onToolWindow.getType() == ToolWindowType.FLOATING_FREE) ||
                           (aggregateReferenceTool != null && aggregateReferenceTool.getType() == ToolWindowType.FLOATING_FREE)) {
                    setType(ToolWindowType.FLOATING);
                } else {
                    if (getType() != ToolWindowType.DOCKED)
                        setType(ToolWindowType.DOCKED);
                }
/*
                if (getType() != ToolWindowType.DOCKED)
                    setType(ToolWindowType.DOCKED);
*/

                publicEvent = false;
                try {
                    setVisibleInternal(true, true, onToolWindow, aggregationPosition, aggregateReferenceTool);
                } finally {
                    publicEvent = true;
                }

                // Maybe we shourld fire an event to signal aggregation change...
            }
            lastAggregationPosition = aggregationPosition;
        } finally {
            descriptor.getManager().resetShowingGroup();
        }

    }

    protected void setAvailableInternal(boolean available, boolean moveAction) {
        if (this.available == available)
            return;

        if (available && toolWindowTabs.size() == 0)
            throw new IllegalStateException("Cannot make available the tool. No tabs available.");

        synchronized (getLock()) {
            if (!available) {
                if (isActive() && publicEvent)
                    setActive(false);
                if (isVisible())
                    setVisible(false);
            }

            boolean old = this.available;
            this.available = available;


            firePropertyChangeEvent("available", old, available,
                                    new Object[]{availablePosition, moveAction});
        }
    }

    protected void setVisibleInternal(boolean visible,
                                      boolean aggregate,
                                      ToolWindow aggregationOnTool,
                                      AggregationPosition aggregationPosition,
                                      ToolWindow aggregateReferenceTool) {
        if ((aggregateEnabled || descriptor.getManager().getToolWindowBar(anchor).isAggregateMode()) &&
            visible &&
            !aggregate &&
            getType() == ToolWindowType.DOCKED)
            aggregate();

        if (getType() == ToolWindowType.EXTERN) {
            // Call setVisible on tool that own this tool as tab...
            for (ToolWindow tool : descriptor.getManager().getToolWindows()) {
                for (ToolWindowTab tab : tool.getToolWindowTabs()) {
                    if (tab.getDockableDelegator() == this) {
                        tool.setVisible(visible);
                        if (visible)
                            tab.setSelected(true);
                        return;
                    }
                }
            }
            for (Content content : descriptor.getManager().getContentManager().getContents()) {
                if (content.getDockableDelegator() == this) {
                    content.setSelected(true);
                    return;
                }
            }
        }

        if (this.visible == visible)
            return;

        synchronized (getLock()) {
            if (!visible && isMaximized())
                setMaximized(false);

            if (visible)
                setAvailable(visible);
            else if (active && publicEvent)
                setActive(false);

            boolean old = this.visible;
            this.visible = visible;

            if (visible) {
                ToolWindowTab selectedTab = null;
                ToolWindowTab nonSelectedTab = null;
                for (final ToolWindowTab tab : getToolWindowTabs()) {
                    if (tab.isSelected())
                        selectedTab = tab;
                    else if (nonSelectedTab == null)
                        nonSelectedTab = tab;
                }
                if (selectedTab == null) {
                    if (nonSelectedTab != null) {
                        final ToolWindowTab tab = nonSelectedTab;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                tab.setSelected(true);
                            }
                        });
                    } else
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                toolWindowTabs.get(0).setSelected(true);
                            }
                        });
                }
            }

            if (aggregate) {
                firePropertyChangeEvent("visible", old, visible, new Object[]{aggregate, aggregationPosition, aggregationOnTool, aggregateReferenceTool});
            } else
                firePropertyChangeEvent("visible", old, visible);
        }
    }

    protected ToolWindowTab addTabInternal(String title, Icon icon, Component component, ToolWindow toolWindow, boolean root) {
        ToolWindowTab tab = new MyDoggyToolWindowTab(this, root, title, icon, component, toolWindow);
        toolWindowTabs.add(tab);

        fireToolWindowTabEvent(new ToolWindowTabEvent(this, ToolWindowTabEvent.ActionId.TAB_ADDED, this, tab));

        if (toolWindowTabs.size() == 1)
            rootTab = tab;

        return tab;

    }

    protected void addTabInternal(ToolWindowTab tab) {
        ToolWindowTab newTab = new MyDoggyToolWindowTab(this,
                                                        false,
                                                        tab.getTitle(),
                                                        tab.getIcon(),
                                                        tab.getComponent(),
                                                        tab);
        toolWindowTabs.add(newTab);

        fireToolWindowTabEvent(new ToolWindowTabEvent(this, ToolWindowTabEvent.ActionId.TAB_ADDED, this, newTab));
    }

    protected void setTypeInternal(ToolWindowType type) {
        synchronized (getLock()) {
            if (this.type == type)
                return;

            switch (type) {
                case SLIDING:
                    if (!(getTypeDescriptor(ToolWindowType.SLIDING)).isEnabled())
                        return;
                    break;
                case FLOATING:
                case FLOATING_FREE:
                    if (!(getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled())
                        return;
                    break;
            }

            if (isMaximized())
                setMaximized(false);

            boolean tempVisible = isVisible();
            boolean tempActive = isActive();

            publicEvent = false;

            ToolWindowType oldType;
            try {
                setVisible(false);
                if (tempActive)
                    active = false;

                publicEvent = true;

                oldType = this.type;
                this.type = type;

                if (type != ToolWindowType.EXTERN) {
                    if (tempActive && tempVisible) {
                        setActive(true);
                    } else if (tempVisible)
                        setVisible(true);
                }
            } finally {
                publicEvent = true;
            }

            firePropertyChangeEvent("type", oldType, type);
        }
    }


    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
//        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, property, oldValue, newValue);
        PropertyChangeEvent publicEvent = new PropertyChangeEvent(this, property, oldValue, newValue);

        firePropertyChangeEvent(publicEvent, publicEvent);
    }

    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue, Object userObject) {
//        PropertyChangeEvent event = new UserPropertyChangeEvent(descriptor, property, oldValue, newValue, userObject);
        PropertyChangeEvent publicEvent = new UserPropertyChangeEvent(this, property, oldValue, newValue, userObject);

        firePropertyChangeEvent(publicEvent, publicEvent);
    }

    protected void fireAnchorEvent(ToolWindowAnchor oldValue, ToolWindowAnchor newValue, Object userObject) {
        firePropertyChangeEvent("anchor", oldValue, newValue, userObject);
    }

    protected PropertyChangeListener pclListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if ("flash".equals(evt.getPropertyName()))
                firePlafPropertyChangeEvent(evt);
        }
    };


    protected void fireToolWindowTabEvent(ToolWindowTabEvent event) {
        switch (event.getActionId()) {
            case TAB_ADDED:
                event.getToolWindowTab().addPropertyChangeListener(pclListener);
                break;
            case TAB_REMOVED:
                event.getToolWindowTab().removePropertyChangeListener(pclListener);
                break;
        }


        if (toolWindowListeners == null)
            return;

        ToolWindowListener[] listeners = toolWindowListeners.getListeners(ToolWindowListener.class);
        for (ToolWindowListener listener : listeners) {
            switch (event.getActionId()) {
                case TAB_ADDED:
                    listener.toolWindowTabAdded(event);
                    break;
                case TAB_REMOVED:
                    listener.toolWindowTabRemoved(event);
                    break;
            }
        }
    }

}
