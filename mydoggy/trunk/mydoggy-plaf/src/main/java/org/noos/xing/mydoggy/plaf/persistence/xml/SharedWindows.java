package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.DockableManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitWindow;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SharedWindows {
    protected DockableManager dockableManager;
    protected List<SharedWindowEntry> sharedWindows;


    public SharedWindows(DockableManager dockableManager) {
        this.dockableManager = dockableManager;

        this.sharedWindows = new ArrayList<SharedWindowEntry>();
    }


    public void addSharedWindow(MultiSplitLayout.Node node, String... ids) {
        sharedWindows.add(new SharedWindowEntry(ids, node));
    }

    public String[] getSharedWindow(String id) {
        for (SharedWindowEntry sharedWindowEntry : sharedWindows) {
            for (String dockableId : sharedWindowEntry.ids) {
                if (id.equals(dockableId))
                    return sharedWindowEntry.ids;
            }
        }
        return null;
    }

    public boolean isInSharedWindow(Dockable dockable) {
        String id = dockable.getId();
        for (SharedWindowEntry sharedWindowEntry : sharedWindows) {
            for (String dockableId : sharedWindowEntry.ids) {
                if (id.equals(dockableId))
                    return true;
            }
        }
        return false;
    }

    public Dockable getRefDockable(Dockable dockable) {
        for (String id : getSharedWindow(dockable.getId())) {
            if (!id.equals(dockable.getId())) {
                Dockable ref = dockableManager.getDockableById(id);
                if (ref.isVisible()) {
                    return ref;
                }
            }
        }
        return null;
    }

    public void applyLayouts() {
        for (final SharedWindowEntry sharedWindowEntry : sharedWindows) {
            // Get the window for the entry
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            MultiSplitWindow multiSplitWindow = SwingUtil.getParent(dockableManager.getDockableById(sharedWindowEntry.ids[0]).getComponent(), MultiSplitWindow.class);
                            if (multiSplitWindow != null)
                                multiSplitWindow.setMultiSplitLayout(sharedWindowEntry.node);
                        }
                    });
                }
            });
        }
    }


    class SharedWindowEntry {
        String[] ids;
        MultiSplitLayout.Node node;

        SharedWindowEntry(String[] ids, MultiSplitLayout.Node node) {
            this.ids = ids;
            this.node = node;
        }
    }
}
