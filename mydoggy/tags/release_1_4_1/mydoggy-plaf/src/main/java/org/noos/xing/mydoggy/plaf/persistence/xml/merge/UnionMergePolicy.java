package org.noos.xing.mydoggy.plaf.persistence.xml.merge;

import org.noos.xing.mydoggy.ToolWindow;
import org.w3c.dom.Element;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class UnionMergePolicy extends ResetMergePolicy {

    public void applyToolWindow(ToolWindow toolWindow, Element toolElement) {
        if (toolWindow.isVisible())
            return;

        super.applyToolWindow(toolWindow, toolElement);
    }
    
}
