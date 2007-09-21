package org.noos.xing.mydoggy.mydoggyset.context;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.mydoggyset.MyDoggySet;
import org.noos.xing.mydoggy.mydoggyset.action.AddContentAction;
import org.noos.xing.mydoggy.mydoggyset.view.contents.ContentsView;
import org.noos.xing.mydoggy.mydoggyset.view.group.GroupEditorContentComponent;
import org.noos.xing.mydoggy.mydoggyset.view.interactive.InteractiveTestView;
import org.noos.xing.mydoggy.mydoggyset.view.manager.ManagerView;
import org.noos.xing.mydoggy.mydoggyset.view.toolwindows.ToolWindowsView;
import org.noos.xing.mydoggy.mydoggyset.view.wellcome.WelcomeContentComponent;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentContext extends MapViewContext {

    private Component toolsContentComponent;
    private Component groupEditorContentComponent;
    private Component contentsContentComponent;
    private Component managerContentComponent;
    private Component wellcomeContentComponent;
    private Component interactiveTestContentComponent;

    public ContentContext(ToolWindowManager toolWindowManager, JFrame frame) {
        addViewContextChangeListener(MyDoggySet.class, new AddContentAction(toolWindowManager,
                                                                            "Wellcome", "Wellcome", null,
                                                                            wellcomeContentComponent = new WelcomeContentComponent(this).getComponent(),
                                                                            "Wellcome", (int) 'W'));
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
                                                                                 groupEditorContentComponent = new GroupEditorContentComponent(toolWindowManager),
                                                                                 "Groups", (int) 'G'));
        addViewContextChangeListener(Content.class, new AddContentAction(toolWindowManager,
                                                                         "Contents", "Contents", null,
                                                                         contentsContentComponent = new ContentsView(toolWindowManager).getComponent(),
                                                                         "Contents", (int) 'C'));
        addViewContextChangeListener(InteractiveTest.class, new AddContentAction(toolWindowManager,
                                                                                 "ITests", "Interactive Tests", null,
                                                                                 interactiveTestContentComponent = new InteractiveTestView(frame, toolWindowManager).getComponent(),
                                                                                 "Interactive Tests", (int) 'I'));
        addViewContextChangeListener(UIManager.class, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                SwingUtilities.updateComponentTreeUI(groupEditorContentComponent);
                SwingUtilities.updateComponentTreeUI(toolsContentComponent);
                SwingUtilities.updateComponentTreeUI(contentsContentComponent);
                SwingUtilities.updateComponentTreeUI(managerContentComponent);
                SwingUtilities.updateComponentTreeUI(wellcomeContentComponent);
                SwingUtilities.updateComponentTreeUI(interactiveTestContentComponent);
            }
        });
    }

}
