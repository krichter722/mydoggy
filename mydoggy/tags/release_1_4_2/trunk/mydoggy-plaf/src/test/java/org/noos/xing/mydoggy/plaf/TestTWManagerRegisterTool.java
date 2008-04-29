package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestTWManagerRegisterTool extends TestCase {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() throws Exception {
        frame = new JFrame("test");
        toolWindowManager = new MyDoggyToolWindowManager();
        frame.add((Component) toolWindowManager);
    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testRegisterToolWindow() {

        ToolWindow toolWindow = toolWindowManager.registerToolWindow(
                "id", "title", null, new JLabel("label"), ToolWindowAnchor.LEFT
        );

        assertNotNull(toolWindow);
        assertEquals("id", toolWindow.getId());
        assertEquals("title", toolWindow.getTitle());
        assertNull(toolWindow.getIcon());
        assertEquals(ToolWindowAnchor.LEFT, toolWindow.getAnchor());

        assertEquals(1, toolWindowManager.getToolWindows().length);
        assertEquals(toolWindow, toolWindowManager.getToolWindow("id"));
    }

    /**
     * We try to register a tool window with a null id.
     */
    public void testRegisterToolWindowUnuseCaseOne() {
        try {
            ToolWindow toolWindow = toolWindowManager.registerToolWindow(
                    null, "title", null, new JLabel("label"), ToolWindowAnchor.LEFT
            );
            fail("Try body must throws a RuntimeException");
        } catch (Exception e) {
        }
    }

    /**
     * We try to register a tool window with a null compoennt.
     */
    public void testRegisterToolWindowUnuseCaseTwo() {
        try {
            ToolWindow toolWindow = toolWindowManager.registerToolWindow(
                    "id", "title", null, null, ToolWindowAnchor.LEFT
            );
            fail("Try body must throws a RuntimeException");
        } catch (Exception e) {
        }
    }

}
