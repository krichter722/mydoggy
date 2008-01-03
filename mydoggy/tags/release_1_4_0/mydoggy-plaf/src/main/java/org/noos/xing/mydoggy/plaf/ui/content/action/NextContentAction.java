package org.noos.xing.mydoggy.plaf.ui.content.action;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class NextContentAction extends AbstractAction {
    private ToolWindowManager toolWindowManager;

    public NextContentAction(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }

    public void actionPerformed(ActionEvent e) {
        Content content = toolWindowManager.getContentManager().getNextContent();
        if (content != null)
            content.setSelected(true);
    }
}
