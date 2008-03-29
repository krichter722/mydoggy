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
    protected List<Cleaner> cleaners;

    public ToolWindowCleaner(ToolWindowManager manager, ToolWindow toolWindow) {
        this.manager = manager;
        this.toolWindow = toolWindow;
    }

    public void cleanup() {
        if (cleaners != null)
            for (Cleaner cleaner : cleaners) {
                cleaner.cleanup();
            }

        manager.removeToolWindowManagerListener(this);
    }

    public void toolWindowRegistered(ToolWindowManagerEvent event) {
    }

    public void toolWindowUnregistered(ToolWindowManagerEvent event) {
        cleanup();
    }

    public void toolWindowGroupAdded(ToolWindowManagerEvent event) {
    }

    public void toolWindowGroupRemoved(ToolWindowManagerEvent event) {
    }

    public void addCleaner(Cleaner cleaner) {
        if (cleaners == null)
            cleaners = new ArrayList<Cleaner>();
        cleaners.add(cleaner);
    }
}
