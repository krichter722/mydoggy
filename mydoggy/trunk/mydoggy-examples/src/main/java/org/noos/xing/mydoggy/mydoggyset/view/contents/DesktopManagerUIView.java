package org.noos.xing.mydoggy.mydoggyset.view.contents;

import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.DesktopContentManagerUI;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.MultiSplitContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyDesktopContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DesktopManagerUIView extends ComponentView implements ViewContextChangeListener {
    protected ToolWindowManager toolWindowManager;
    protected DesktopContentManagerUI desktopContentManagerUI;

    public DesktopManagerUIView(ViewContext viewContext) {
        super(viewContext);
        this.toolWindowManager = viewContext.get(ToolWindowManager.class);

        ContentManagerUI contentManagerUI = toolWindowManager.getContentManager().getContentManagerUI();
        if (contentManagerUI instanceof DesktopContentManagerUI)
            this.desktopContentManagerUI = (DesktopContentManagerUI) contentManagerUI;
        else
            this.desktopContentManagerUI = new MyDoggyDesktopContentManagerUI();
    }

    protected Component initComponent() {
        JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1, 50, -1},{-1, 20, -1}}));
        panel.add(new JLabel("No Pref"), "1,1,FULL,FULL");

        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ContentManagerUI.class.equals(evt.getProperty())) {
            if (evt.getNewValue().equals(DesktopContentManagerUI.class)) {
                toolWindowManager.getContentManager().setContentManagerUI(desktopContentManagerUI);
                
                viewContext.put(ContentManagerUI.class, this);
            }
        }
    }


}