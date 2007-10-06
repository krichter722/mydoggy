package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowAnchor;
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

    public void execute(InteractiveUI interactiveUI) {
        moveToAnchor(interactiveUI, "toolWindow.rb.Tool 1", ToolWindowAnchor.RIGHT);
        moveToAnchor(interactiveUI, "toolWindow.rb.Tool 1", ToolWindowAnchor.LEFT);
    }

}