package org.noos.xing.mydoggy.examples.mydoggyset.action;

import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AddContentAction extends AbstractAction {
    private ToolWindowManager toolWindowManager;
    private String contentId;
    private String title;
    private Icon icon;
    private Component component;
    private String tooltip;

    public AddContentAction(ToolWindowManager toolWindowManager, String contentId, String title, Icon icon, Component component, String tooltip) {
        super(contentId);
        this.toolWindowManager = toolWindowManager;
        this.contentId = contentId;
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.tooltip = tooltip;
    }

    public void actionPerformed(ActionEvent e) {
        ContentManager contentManager = toolWindowManager.getContentManager();
        if (contentManager.getContent(contentId) == null)
            contentManager.addContent(contentId,
                                      title,
                                      icon,
                                      component,
                                      tooltip);
    }
}
