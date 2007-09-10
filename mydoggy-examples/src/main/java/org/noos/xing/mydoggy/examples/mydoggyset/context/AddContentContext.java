package org.noos.xing.mydoggy.examples.mydoggyset.context;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.examples.mydoggyset.MyDoggySet;
import org.noos.xing.mydoggy.examples.mydoggyset.action.AddContentAction;
import org.noos.xing.mydoggy.examples.mydoggyset.content.GroupEditorContentComponent;
import org.noos.xing.mydoggy.examples.mydoggyset.content.InteractiveTestContentComponent;
import org.noos.xing.mydoggy.examples.mydoggyset.content.ManagerContentComponent;
import org.noos.xing.mydoggy.examples.mydoggyset.content.WellcomeContentComponent;
import org.noos.xing.mydoggy.examples.mydoggyset.view.contents.ContentsView;
import org.noos.xing.mydoggy.examples.mydoggyset.view.toolwindows.ToolWindowsView;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AddContentContext extends MapViewContext {
    private ToolWindowManager toolWindowManager;
    private JFrame frame;

    private Component toolsContentComponent;
    private Component groupEditorContentComponent;
    private Component contentsContentComponent;
    private Component managerContentComponent;
    private Component wellcomeContentComponent;
    private Component interactiveTestContentComponent;

    public AddContentContext(ToolWindowManager toolWindowManager, JFrame frame) {
        this.toolWindowManager = toolWindowManager;
        this.frame = frame;

        addViewContextChangeListener(MyDoggySet.class, new AddContentAction(toolWindowManager,
                                                                            "Wellcome", "Wellcome", null,
                                                                            wellcomeContentComponent = new WellcomeContentComponent(),
                                                                            "Wellcome", (int) 'W'));
        addViewContextChangeListener(ToolWindowManager.class, new AddContentAction(toolWindowManager,
                                                                                   "Manager", "Manager", null,
                                                                                   managerContentComponent = new ManagerContentComponent(toolWindowManager),
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
                                                                                 interactiveTestContentComponent = new InteractiveTestContentComponent(frame, toolWindowManager),
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
