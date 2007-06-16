package org.noos.xing.mydoggy.plaf.persistence.merge;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.persistence.PersistedToolWindow;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class UnionMergePolicy implements MergePolicyApplier{

    public void applyToolWindow(ToolWindow toolWindow, PersistedToolWindow persistedToolWindow) {
        if (toolWindow.isVisible())
            return;

        if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
            toolWindow.setVisible(persistedToolWindow.isVisible());
        } else {
            if (persistedToolWindow.isVisible())
                toolWindow.aggregate();
            else
                toolWindow.setVisible(false);
        }

    }
    
}
