package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.impl.ui.JBalloonTip;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveDragToTabTest extends MyDoggySetInteractiveTest {
    protected ToolWindowManager toolWindowManager;

    public InteractiveDragToTabTest(Container root, ToolWindowManager toolWindowManager) throws AWTException {
        super("InteractiveDragToTabTest", "InteractiveDragToTabTest", root);
        this.toolWindowManager = toolWindowManager;
    }

    public void execute() {
        toolWindowManager.getToolWindowGroup().setVisible(false);

        componentLookuper.lookup(null).moveToCenter();
        showTip("<html>Let's go to <br>show Tool 3", 1000);

        clickOnRepresentativeButton("Tool 3");
        showTip("<html>Let's go to <br>show Tool 13", 1000);
        clickOnRepresentativeButton("Tool 13");

        delay(500);

        showTip("<html>Drag Tool 13 to <br>Tool 3 as a Tab", 1000);
        drag("toolWindow.Tool 13.tab.Title 13", "toolWindow.tabContainer.Tool 3");
        drag("toolWindow.Tool 3.tab.Title 13", "toolWindowManager.bar." + ToolWindowAnchor.TOP);
        drag("toolWindow.Tool 13.tab.Title 13", "toolWindow.tabContainer.Tool 3");
        drag("toolWindow.Tool 3.tab.Title 13", "toolWindow.container.Tool 3");
    }

}