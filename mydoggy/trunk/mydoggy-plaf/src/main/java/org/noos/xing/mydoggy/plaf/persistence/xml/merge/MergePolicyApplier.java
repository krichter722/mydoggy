package org.noos.xing.mydoggy.plaf.persistence.xml.merge;

import org.noos.xing.mydoggy.PersistenceDelegateCallback;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.persistence.xml.SharedWindows;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface MergePolicyApplier {

    void applyToolWindow(ToolWindow toolWindow, PersistenceDelegateCallback.PersistenceNode toolNode, SharedWindows sharedWindows);

}
