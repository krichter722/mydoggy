package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ContentManagerEnabledAction extends AbstractAction {
    private ToolWindowManager toolWindowManager;

    public ContentManagerEnabledAction(ToolWindowManager toolWindowManager) {
        super("Disable ContentManager");
        this.toolWindowManager = toolWindowManager;
    }

    public void actionPerformed(ActionEvent e) {
        ContentManager contentManager = toolWindowManager.getContentManager();
        contentManager.setEnabled(!contentManager.isEnabled());

        if (contentManager.isEnabled())
            putValue(AbstractAction.NAME, "Disable ContentManager");
        else
            putValue(AbstractAction.NAME, "Enable ContentManager");
    }
}
