package org.noos.xing.mydoggy.examples.mydoggyset.action;

import org.noos.xing.mydoggy.Content;
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
    private int mnemonic;

    public AddContentAction(ToolWindowManager toolWindowManager, String contentId, String title, Icon icon, Component component, String tooltip, int mnemonic) {
        super(contentId);
        this.toolWindowManager = toolWindowManager;
        this.contentId = contentId;
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.tooltip = tooltip;
        this.mnemonic = mnemonic;
    }

    public void actionPerformed(ActionEvent e) {
        ContentManager contentManager = toolWindowManager.getContentManager();
        if (contentManager.getContent(contentId) == null) {
            Content content = contentManager.addContent(contentId,
                                      title,
                                      icon,
                                      component,
                                      tooltip);
            if (mnemonic != -1)
                content.setMnemonic(mnemonic);
        }
    }
}
