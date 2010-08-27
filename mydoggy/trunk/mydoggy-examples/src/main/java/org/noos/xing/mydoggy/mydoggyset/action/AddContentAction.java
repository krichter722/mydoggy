package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AddContentAction implements ActionListener, ViewContextChangeListener {
    private ToolWindowManager toolWindowManager;
    private String contentId;
    private String title;
    private Icon icon;
    private Component component;
    private String tooltip;
    private int mnemonic;

    public AddContentAction(ToolWindowManager toolWindowManager, String contentId, String title, Icon icon, Component component, String tooltip, int mnemonic) {
        this.toolWindowManager = toolWindowManager;
        this.contentId = contentId;
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.tooltip = tooltip;
        this.mnemonic = mnemonic;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        actionPerformed(null);
    }

    public void actionPerformed(ActionEvent e) {
        ContentManager contentManager = toolWindowManager.getContentManager();
        Content content = contentManager.getContent(contentId);
        if (content == null) {
            content = contentManager.addContent(contentId,
                                                        title,
                                                        icon,
                                                        component,
                                                        tooltip);
            content.getContentUI().setAlwaysOnTop(false);
            if (mnemonic != -1)
                content.setMnemonic(mnemonic);
            content.setSelected(true);
        } else
            content.setSelected(true);
    }

}
