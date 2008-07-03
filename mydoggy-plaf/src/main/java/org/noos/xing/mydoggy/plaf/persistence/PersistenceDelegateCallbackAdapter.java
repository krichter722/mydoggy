package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.PersistenceDelegateCallback;
import org.noos.xing.mydoggy.ToolWindowManager;

/**
 * TODO:
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistenceDelegateCallbackAdapter implements PersistenceDelegateCallback {
    
    public Content contentNotFound(ToolWindowManager toolWindowManager, String contentId) {
        return null;
    }

}
