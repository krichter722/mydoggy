package org.noos.xing.mydoggy.plaf.persistence.xml.merge;

import org.noos.xing.mydoggy.PersistenceDelegateCallback;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.persistence.xml.SharedWindows;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class UnionMergePolicy extends ResetMergePolicy {

    public void applyToolWindow(ToolWindow toolWindow, PersistenceDelegateCallback.PersistenceNode toolNode, SharedWindows sharedWindows) {
        if (toolWindow.isVisible())
            return;

        super.applyToolWindow(toolWindow, toolNode, sharedWindows);
    }
    
}
