package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.InteractiveUI;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveDragToTabTest extends AbstractInteractiveTest {
    protected ToolWindowManager toolWindowManager;

    public InteractiveDragToTabTest(ToolWindowManager toolWindowManager, Container masterContainer) {
        super(masterContainer);
        this.toolWindowManager = toolWindowManager;
    }

    public String getDescription() {
        return "InteractiveDragToTabTest";
    }

    public void execute(InteractiveUI interactiveUI) {
        toolWindowManager.getToolWindowGroup().setVisible(false);

        clickOn(interactiveUI, "toolWindow.rb.Tool 3");
        clickOn(interactiveUI, "toolWindow.rb.Tool 13");

        interactiveUI.delay(500);

        drag(interactiveUI, "toolWindow.Tool 13.tab.Title 13", "toolWindow.tabContainer.Tool 3");
        drag(interactiveUI, "toolWindow.Tool 3.tab.Title 13", "toolWindowManager.bar." + ToolWindowAnchor.TOP);
        drag(interactiveUI, "toolWindow.Tool 13.tab.Title 13", "toolWindow.tabContainer.Tool 3");
        drag(interactiveUI, "toolWindow.Tool 3.tab.Title 13", "toolWindow.container.Tool 3");
    }


}