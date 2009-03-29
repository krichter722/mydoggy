package org.noos.xing.mydoggy.plaf.persistence.xml.merge;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.PersistenceDelegateCallback;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.persistence.xml.SharedWindows;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class UnionMergePolicy extends ResetMergePolicy {

    public void applyToolWindow(ToolWindow toolWindow, PersistenceDelegateCallback.PersistenceNode toolNode, SharedWindows sharedWindows) {
        if (toolWindow.isVisible()) {

            if (sharedWindows.isInSharedWindow(toolWindow)) {
                Dockable refDockable = sharedWindows.getRefDockable(toolWindow);

                if (refDockable != null)
                    toolWindow.aggregateByReference((ToolWindow) refDockable, AggregationPosition.DEFAULT);
            }

            return;
        }

        super.applyToolWindow(toolWindow, toolNode, sharedWindows);
    }
    
}
