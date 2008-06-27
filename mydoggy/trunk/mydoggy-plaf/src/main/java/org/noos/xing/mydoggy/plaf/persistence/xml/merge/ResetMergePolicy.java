package org.noos.xing.mydoggy.plaf.persistence.xml.merge;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.w3c.dom.Element;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ResetMergePolicy implements MergePolicyApplier {

    public void applyToolWindow(final ToolWindow toolWindow, Element toolElement) {
        boolean visible = Boolean.parseBoolean(toolElement.getAttribute("visible"));

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
            } else
                toolWindow.setVisible(visible);

        } else {
            if (visible)
                toolWindow.aggregate();
            else
                toolWindow.setVisible(false);
        }
    }

}