package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowManagerListener;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowCleaner implements Cleaner, ToolWindowManagerListener {
    protected ToolWindowManager manager;
    protected ToolWindow toolWindow;
    protected List<Cleaner> toolWindowCleaners;

    public ToolWindowCleaner(ToolWindowManager manager, ToolWindow toolWindow) {
        this.manager = manager;
        this.toolWindow = toolWindow;
        manager.addToolWindowManagerListener(this);
    }

    public void cleanup() {
        if (toolWindowCleaners != null)
            for (Cleaner cleaner : toolWindowCleaners) {
                cleaner.cleanup();
            }

        manager.removeToolWindowManagerListener(this);
    }

    public void toolWindowRegistered(ToolWindowManagerEvent event) {
    }

    public void toolWindowUnregistered(ToolWindowManagerEvent event) {
        if (event.getToolWindow() == toolWindow)
            cleanup();
    }

    public void toolWindowGroupAdded(ToolWindowManagerEvent event) {
    }

    public void toolWindowGroupRemoved(ToolWindowManagerEvent event) {
    }

    public void addCleaner(Cleaner cleaner) {
        if (toolWindowCleaners == null)
            toolWindowCleaners = new ArrayList<Cleaner>();
        toolWindowCleaners.add(cleaner);
    }
}
