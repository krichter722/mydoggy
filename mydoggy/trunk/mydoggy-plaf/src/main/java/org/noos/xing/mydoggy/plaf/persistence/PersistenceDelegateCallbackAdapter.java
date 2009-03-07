package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.PersistenceDelegateCallback;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;

/**
 * An abstract adapter class for receiving persistence events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * 
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistenceDelegateCallbackAdapter implements PersistenceDelegateCallback {

    public ToolWindow toolwindowNotFound(ToolWindowManager toolWindowManager, String toolWindowId, PersistenceNode node) {
        return null;
    }

    public Content contentNotFound(ToolWindowManager toolWindowManager, String contentId, PersistenceNode node) {
        return null;
    }

    public String validate(PersistenceNode node, String attribute, String attributeValue, Object attributeDefaultValue) {
        return attributeValue;
    }

}
