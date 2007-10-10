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

        clickOn("toolWindow.rb.Tool 3");
        JBalloonTip balloonTip = new JBalloonTip(((JFrame) root));
        balloonTip.setText("Hello World");
        balloonTip.show(150, 150);
        clickOn("toolWindow.rb.Tool 13");

        delay(500);

        drag("toolWindow.Tool 13.tab.Title 13", "toolWindow.tabContainer.Tool 3");
        drag("toolWindow.Tool 3.tab.Title 13", "toolWindowManager.bar." + ToolWindowAnchor.TOP);
        drag("toolWindow.Tool 13.tab.Title 13", "toolWindow.tabContainer.Tool 3");
        drag("toolWindow.Tool 3.tab.Title 13", "toolWindow.container.Tool 3");
    }

}