package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.yasaf.view.ViewContext;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveDebugTest extends MyDoggySetInteractiveTest {
    protected ViewContext mydoggySetContext;

    public InteractiveDebugTest(ViewContext mydoggySetContext, Container root, ToolWindowManager toolWindowManager) throws AWTException {
        super(toolWindowManager, root);
        this.mydoggySetContext = mydoggySetContext;
    }

    public String getName() {
        return "Debug Test...";
    }

    public String getDescription() {
        return "<html>" +
                "<h2>Test Description</h2>" +
                "<p>" +
                "Debug Test..." +
                "</p>" +
                "</html>";
    }

    public void execute() {
        restoreWorkspace();

        mydoggySetContext.put(ToolWindowManager.class, null);
    }

}