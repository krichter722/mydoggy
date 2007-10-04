package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.InteractiveMouse;
import org.noos.xing.mydoggy.itest.InteractiveUI;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveDragTest extends AbstractInteractiveTest {

    public InteractiveDragTest(Container masterContainer) {
        super(masterContainer);
    }

    public String getDescription() {
        return "TODO";
    }

    public void interactiveTest(InteractiveUI interactiveUI) {
        InteractiveMouse mouse = interactiveUI.getInteractiveMouse();

        mouse.moveTo("toolWindow.rb.Tool 1", 500)
                .press(InteractiveMouse.Type.LEFT)
                .moveTo("toolWindowManager.bar.RIGHT", 10, 80, 500)
                .release(500)
                .moveTo("toolWindow.rb.Tool 1")
                .press(InteractiveMouse.Type.LEFT)
                .moveTo("toolWindowManager.bar.LEFT", 10, 80, 500)
                .release();
    }

}