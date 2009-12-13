package org.noos.xing.mydoggy.plaf.persistence.xml.merge;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.persistence.xml.SharedWindows;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ResetMergePolicy implements MergePolicyApplier {

    public void applyToolWindow(final ToolWindow toolWindow, PersistenceDelegateCallback.PersistenceNode toolNode, SharedWindows sharedWindows) {
        boolean visible = toolNode.getBoolean("visible", toolWindow.isVisible());

        if (toolWindow.getType() == ToolWindowType.FLOATING || 
            toolWindow.getType() == ToolWindowType.FLOATING_FREE ||
            toolWindow.getType() == ToolWindowType.FLOATING_LIVE ||
            toolWindow.getType() == ToolWindowType.SLIDING) {

            if (toolWindow.getType() == ToolWindowType.SLIDING && visible) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        toolWindow.setVisible(true);
                    }
                });
            } else {
                if (visible) {
                    if (sharedWindows.isInSharedWindow(toolWindow)) {
                        Dockable refDockable = sharedWindows.getRefDockable(toolWindow);

                        if (refDockable != null)
                            toolWindow.aggregateByReference((ToolWindow) refDockable, AggregationPosition.DEFAULT);
                        else
                            toolWindow.setVisible(visible);
                    } else
                        toolWindow.setVisible(visible);
                } else
                    toolWindow.setVisible(visible);
            }
        } else {
            if (visible) {
                if (toolNode.getBoolean("autoHide", false))
                    toolWindow.setVisible(true);
                else
                    toolWindow.aggregate();
            } else
                toolWindow.setVisible(false);
        }
    }

}