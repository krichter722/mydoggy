package org.noos.xing.mydoggy.mydoggyset.context;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.mydoggyset.MyDoggySet;
import org.noos.xing.mydoggy.mydoggyset.action.AddContentAction;
import org.noos.xing.mydoggy.mydoggyset.view.bars.ToolWindowBarsView;
import org.noos.xing.mydoggy.mydoggyset.view.contents.ContentsView;
import org.noos.xing.mydoggy.mydoggyset.view.customize.CustomizeView;
import org.noos.xing.mydoggy.mydoggyset.view.group.GroupsView;
import org.noos.xing.mydoggy.mydoggyset.view.manager.ManagerView;
import org.noos.xing.mydoggy.mydoggyset.view.toolwindows.ToolWindowsView;
import org.noos.xing.mydoggy.mydoggyset.view.welcome.WelcomeContentView;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggySetContext extends MapViewContext {

    public enum ActionKey {
        NEST_TOOLMANAGER,
    }

    protected Component toolsContentComponent;
    protected Component groupEditorContentComponent;
    protected Component contentsContentComponent;
    protected Component managerContentComponent;
    protected Component welcomeContentComponent;
    protected Component customizeContentComponent;
    protected Component toolBarsContentComponent;

    public MyDoggySetContext(ToolWindowManager toolWindowManager, final Component parentComponent) {
        addViewContextChangeListener(MyDoggySet.class, new AddContentAction(toolWindowManager,
                                                                            "Welcome", "Welcome", null,
                                                                            welcomeContentComponent = new WelcomeContentView(this).getComponent(),
                                                                            "Welcome", (int) 'W'));
        addViewContextChangeListener(ToolWindowManager.class, new AddContentAction(toolWindowManager,
                                                                                   "Manager", "Manager", null,
                                                                                   managerContentComponent = new ManagerView(toolWindowManager).getComponent(),
                                                                                   "Manager", (int) 'M'));
        addViewContextChangeListener(ToolWindow.class, new AddContentAction(toolWindowManager,
                                                                            "Tools", "Tools", null,
                                                                            toolsContentComponent = new ToolWindowsView(toolWindowManager).getComponent(),
                                                                            "ToolWindows", (int) 'T'));
        addViewContextChangeListener(ToolWindowGroup.class, new AddContentAction(toolWindowManager,
                                                                                 "Groups", "Group Editor", null,
                                                                                 groupEditorContentComponent = new GroupsView(parentComponent, toolWindowManager).getComponent(),
                                                                                 "Groups", (int) 'G'));
        addViewContextChangeListener(Content.class, new AddContentAction(toolWindowManager,
                                                                         "Contents", "Contents", null,
                                                                         contentsContentComponent = new ContentsView(toolWindowManager).getComponent(),
                                                                         "Contents", (int) 'C'));
        addViewContextChangeListener(ResourceManager.class, new AddContentAction(toolWindowManager,
                                                                                 "Customize", "Customize", null,
                                                                                 customizeContentComponent = new CustomizeView(parentComponent, toolWindowManager).getComponent(),
                                                                                 "Customize", (int) 'u'));
        addViewContextChangeListener(ToolWindowBar.class, new AddContentAction(toolWindowManager,
                                                                                      "Tool Bars", "Tool Bars", null,
                                                                                      toolBarsContentComponent = new ToolWindowBarsView(parentComponent, toolWindowManager).getComponent(),
                                                                                      "Tool Bars", (int) 'B'));

        addViewContextChangeListener(UIManager.class, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                try {
                    UIManager.setLookAndFeel((String) evt.getNewValue());

                    SwingUtilities.updateComponentTreeUI(parentComponent);

                    SwingUtilities.updateComponentTreeUI(groupEditorContentComponent);
                    SwingUtilities.updateComponentTreeUI(toolsContentComponent);
                    SwingUtilities.updateComponentTreeUI(contentsContentComponent);
                    SwingUtilities.updateComponentTreeUI(managerContentComponent);
                    SwingUtilities.updateComponentTreeUI(welcomeContentComponent);
                    SwingUtilities.updateComponentTreeUI(customizeContentComponent);
                    SwingUtilities.updateComponentTreeUI(toolBarsContentComponent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
