package org.noos.xing.mydoggy.plaf.persistence.xml.merge;

import org.noos.xing.mydoggy.ToolWindow;
import org.w3c.dom.Element;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface MergePolicyApplier {

    void applyToolWindow(ToolWindow toolWindow, Element toolElement);

}
