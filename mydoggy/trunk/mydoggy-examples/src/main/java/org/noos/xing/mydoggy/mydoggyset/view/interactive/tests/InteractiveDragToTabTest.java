package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.InteractiveMouse;
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

    public void interactiveTest(InteractiveUI interactiveUI) {
        toolWindowManager.getToolWindowGroup().setVisible(false);

        InteractiveMouse mouse = interactiveUI.getInteractiveMouse();

        showTool(interactiveUI, "Tool 3");
        showTool(interactiveUI, "Tool 13");

        interactiveUI.delay(500);

        mouse.moveTo("toolWindow.Tool 13.tab.Title 13", 500);
        mouse.press(InteractiveMouse.Type.LEFT);
        interactiveUI.delay(500);
        mouse.moveTo("toolWindow.tabContainer.Tool 3", 500);
        mouse.release(500);

        mouse.moveTo("toolWindow.Tool 3.tab.Title 13", 500);
        mouse.press(InteractiveMouse.Type.LEFT);
        mouse.moveTo("toolWindowManager.bar." + ToolWindowAnchor.TOP, 300, 15, 500);
        mouse.release(500);

        mouse.moveTo("toolWindow.Tool 13.tab.Title 13", 500);
        mouse.press(InteractiveMouse.Type.LEFT);
        interactiveUI.delay(500);
        mouse.moveTo("toolWindow.tabContainer.Tool 3", 500);
        mouse.release(500);

        mouse.moveTo("toolWindow.Tool 3.tab.Title 13", 500);
        mouse.press(InteractiveMouse.Type.LEFT);
        interactiveUI.delay(500);
        mouse.moveTo("toolWindow.container.Tool 3", 100,150, 500);
        mouse.release(500);

    }

}