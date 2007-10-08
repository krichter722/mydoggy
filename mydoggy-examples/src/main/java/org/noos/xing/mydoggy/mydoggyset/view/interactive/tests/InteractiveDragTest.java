package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowAnchor;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveDragTest extends MyDoggySetInteractiveTest {

    public InteractiveDragTest(Container root) throws AWTException {
        super("InteractiveDragTest", "InteractiveDragTest", root);
    }

    public void execute() {
        moveToAnchor("toolWindow.rb.Tool 1", ToolWindowAnchor.RIGHT);
        moveToAnchor("toolWindow.rb.Tool 1", ToolWindowAnchor.LEFT);
    }

}