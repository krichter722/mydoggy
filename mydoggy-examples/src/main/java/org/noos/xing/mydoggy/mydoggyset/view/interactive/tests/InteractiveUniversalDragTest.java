package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveUniversalDragTest extends MyDoggySetInteractiveTest {

    public InteractiveUniversalDragTest(ToolWindowManager toolWindowManager, Container root) throws AWTException {
        super(toolWindowManager, root);
    }

    public String getName() {
        return "The Universal Drag System...";
    }

    public String getDescription() {
        return "<html>" +
                "<h2>Test Description</h2>" +
                "<p>" +
                "This test shows how to drag a toolwindow everywhere you want..." +
                "</p>" +
                "</html>";
    }

    public void execute() {
        restoreWorkspace();

        componentLookuper.lookup().moveToCenter();

        showTip("<html>Let's go to <br>show Tool 3", 1000);
        clickOnRepresentativeButton("Tool 3");

        showTip("<html>Let's go to <br>show Tool 13", 1000);
        clickOnRepresentativeButton("Tool 13");

        showTip("<html>Drag Tool 13 to <br>Tool 3 as a Tab", 1000);
        drag("toolWindow.Tool 13.tab.Title 13", "toolWindow.container.Tool 3");

        showTip("<html>Drag Tab Tool 13 to <br> the Top bar", 1000);
        drag("toolWindow.Tool 3.tab.Title 13", "toolWindowManager.bar." + ToolWindowAnchor.TOP);

        showTip("<html>Drag Tool 13 to <br> Tool 3 as a Tab", 1000);
        drag("toolWindow.Tool 13.tab.Title 13", "toolWindow.container.Tool 3", AggregationPosition.LEFT);

        moveOn("toolWindow.rb.Tool 12", "Drag The Representative Anchor on Tool 13");
        drag("toolWindow.rb.Tool 12", "toolWindow.container.Tool 13", AggregationPosition.BOTTOM);
    }

}