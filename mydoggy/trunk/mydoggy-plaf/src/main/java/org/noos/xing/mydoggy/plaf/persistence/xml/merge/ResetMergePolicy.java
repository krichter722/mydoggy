package org.noos.xing.mydoggy.plaf.persistence.xml.merge;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.w3c.dom.Element;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ResetMergePolicy implements MergePolicyApplier {

    public void applyToolWindow(ToolWindow toolWindow, Element toolElement) {
        boolean visible = Boolean.parseBoolean(toolElement.getAttribute("visible"));

        if (toolWindow.getType() == ToolWindowType.FLOATING || 
            toolWindow.getType() == ToolWindowType.FLOATING_FREE ||
            toolWindow.getType() == ToolWindowType.SLIDING) {

            toolWindow.setVisible(visible);

        } else {
            if (visible)
                toolWindow.aggregate();
            else
                toolWindow.setVisible(false);
        }
    }

}