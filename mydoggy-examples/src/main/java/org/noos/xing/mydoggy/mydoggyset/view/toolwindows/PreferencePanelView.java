package org.noos.xing.mydoggy.mydoggyset.view.toolwindows;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.mydoggyset.view.toolwindows.tabs.ToolWindowTabsView;
import org.noos.xing.mydoggy.mydoggyset.view.toolwindows.types.TypeDescriptorsPreferenceView;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PreferencePanelView extends ComponentView {
    protected JPanel mainPanel;

    public PreferencePanelView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        mainPanel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        mainPanel.setBorder(new TitledBorder("Preferences"));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("TypeDescriptors", new TypeDescriptorsPreferenceView(viewContext).getComponent());
        tabbedPane.add("ToolWindowTabs", new ToolWindowTabsView(viewContext).getComponent());

        mainPanel.add(tabbedPane, "0,0,FULL,FULL");

        return mainPanel;
    }


    protected void initListeners() {
        viewContext.addViewContextChangeListener(ToolWindow.class, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                ToolWindow toolWindow = (ToolWindow) evt.getNewValue();

                if (toolWindow != null)
                    mainPanel.setBorder(new TitledBorder("Preference : " + toolWindow.getId()));
                else
                    mainPanel.setBorder(new TitledBorder("Preference : "));
            }
        });
    }
}
