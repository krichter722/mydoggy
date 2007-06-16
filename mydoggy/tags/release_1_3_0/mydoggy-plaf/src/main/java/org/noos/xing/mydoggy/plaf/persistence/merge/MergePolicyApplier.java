package org.noos.xing.mydoggy.plaf.persistence.merge;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.persistence.PersistedToolWindow;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface MergePolicyApplier {

    void applyToolWindow(ToolWindow toolWindow, PersistedToolWindow persistedToolWindow);

}
