package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveRepresentativeDraggingTest extends MyDoggySetInteractiveTest {

    public InteractiveRepresentativeDraggingTest(Container root, ToolWindowManager toolWindowManager) throws AWTException {
        super("Representavie Button Dragging",
              "InteractiveDragTest", toolWindowManager, root);
    }

    public void execute() {
        restoreWorkspace();

        moveToAnchor("toolWindow.rb.Tool 1", ToolWindowAnchor.BOTTOM);
        showTip("Move down", 2000);
        moveToAnchor("toolWindow.rb.Tool 1", ToolWindowAnchor.BOTTOM);
    }

}