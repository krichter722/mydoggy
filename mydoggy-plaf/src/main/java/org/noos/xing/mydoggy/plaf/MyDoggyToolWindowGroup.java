package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowGroupEvent;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowGroup implements ToolWindowGroup {
    private ToolWindowManager manager;

    private String name;
    private List<ToolWindow> tools;
    private EventListenerList listenerList;

    MyDoggyToolWindowGroup(ToolWindowManager manager, String name) {
        this.manager = manager;
        this.name = name;
        this.tools = new ArrayList<ToolWindow>();
        this.listenerList = new EventListenerList();
    }


    public String getName() {
        return name;
    }


    public void addToolWindow(ToolWindow toolWindow) {
        if (toolWindow == null)
            throw new NullPointerException("ToolWindow cannot be null.");

        if (!tools.contains(toolWindow)) {
            tools.add(toolWindow);
            fireAddedTool(toolWindow);
        } else
            throw new IllegalArgumentException("This group already containes passed tool window. [tool id : " + toolWindow.getId() + ", group : " + name + "]");
    }

    public boolean removeToolWindow(ToolWindow toolWindow) {
        if (toolWindow == null)
            throw new NullPointerException("ToolWindow cannot be null.");

        boolean removed = tools.remove(toolWindow);
        if (removed)
            fireRemovedTool(toolWindow);
        return removed;
    }

    public ToolWindow[] getToolsWindow() {
        return tools.toArray(new ToolWindow[0]);
    }

    public boolean containesToolWindow(ToolWindow toolWindow) {
        return tools.contains(toolWindow);
    }


    public void setVisible(final boolean visible) {
        if (manager.containsGroup(name)) {

            synchronized (MyDoggyToolWindowManager.sync) {

                boolean doAction = false;
                for (ToolWindow tool : manager.getToolWindowGroup(name).getToolsWindow()) {
                    if (tool.isVisible() != visible) {
                        doAction = true;
                        break;
                    }
                }
                
                if (doAction) {
                    for (ToolWindow tool : manager.getToolWindows()) {
                        tool.setVisible(false);
                    }

                    MyDoggyToolWindowManager.currentGroup = this;
                    try {
                        for (ToolWindow tool : MyDoggyToolWindowManager.currentGroup.getToolsWindow()) {
                            if (tool.getType() == ToolWindowType.SLIDING)
                                tool.setType(ToolWindowType.DOCKED);

                            tool.setVisible(visible);
                        }
                    } finally {
                        MyDoggyToolWindowManager.currentGroup = null;
                    }

                    if (visible)
                        fireGroupShowed();
                    else
                        fireGroupHided();
                }

                if (visible && tools.size() > 0)
                    tools.get(0).setActive(true);
            }
        }
    }


    public void addToolWindowGroupListener(ToolWindowGroupListener listener) {
        if (listener == null)
            return;

        listenerList.add(ToolWindowGroupListener.class, listener);
    }

    public void removeToolWindowGroupListener(ToolWindowGroupListener listener) {
        if (listener == null)
            return;

        listenerList.remove(ToolWindowGroupListener.class, listener);
    }

    public ToolWindowGroupListener[] getToolWindowGroupListeners() {
        return listenerList.getListeners(ToolWindowGroupListener.class);
    }


    public String toString() {
        return "MyDoggyToolWindowGroup{" +
               "name='" + name + '\'' +
               ", tools=" + tools +
               '}';
    }

    protected void fireGroupShowed() {
        ToolWindowGroupEvent event = new ToolWindowGroupEvent(manager, ToolWindowGroupEvent.ActionId.GROUP_SHOWED, this);
        for (ToolWindowGroupListener listener : listenerList.getListeners(ToolWindowGroupListener.class)) {
            listener.groupShowed(event);
        }
    }

    protected void fireGroupHided() {
        ToolWindowGroupEvent event = new ToolWindowGroupEvent(manager, ToolWindowGroupEvent.ActionId.GROUP_HIDED, this);
        for (ToolWindowGroupListener listener : listenerList.getListeners(ToolWindowGroupListener.class)) {
            listener.groupHided(event);
        }
    }

    protected void fireAddedTool(ToolWindow toolWindow) {
        ToolWindowGroupEvent event = new ToolWindowGroupEvent(manager, ToolWindowGroupEvent.ActionId.TOOL_ADDED, this, toolWindow);
        for (ToolWindowGroupListener listener : listenerList.getListeners(ToolWindowGroupListener.class)) {
            listener.toolAdded(event);
        }
    }

    protected void fireRemovedTool(ToolWindow toolWindow) {
        ToolWindowGroupEvent event = new ToolWindowGroupEvent(manager, ToolWindowGroupEvent.ActionId.TOOL_REMOVED, this, toolWindow);
        for (ToolWindowGroupListener listener : listenerList.getListeners(ToolWindowGroupListener.class)) {
            listener.toolRemoved(event);
        }
    }

}
