package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestTWManagerBasic extends TestCase {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() throws Exception {
        frame = new JFrame("test");
        toolWindowManager = new MyDoggyToolWindowManager(frame);

    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testGetInstance() {
        assertNotNull(toolWindowManager);
    }

    public void testToolWindowMethods() {
        // getActiveToolWindowId
        assertNull(toolWindowManager.getActiveToolWindowId());

        // getToolWindows
        assertNotNull(toolWindowManager.getToolWindows());
        assertEquals(0, toolWindowManager.getToolWindows().length);

        // getToolWindowGroups
        assertNotNull(toolWindowManager.getToolWindowGroups());
        assertEquals(0, toolWindowManager.getToolWindowGroups().length);

        // getToolsByAnchor(ToolWindowAnchor.BOTTOM)
        assertNotNull(toolWindowManager.getToolsByAnchor(ToolWindowAnchor.BOTTOM));
        assertEquals(0, toolWindowManager.getToolsByAnchor(ToolWindowAnchor.BOTTOM).length);

        // getToolsByAnchor(ToolWindowAnchor.TOP)
        assertNotNull(toolWindowManager.getToolsByAnchor(ToolWindowAnchor.TOP));
        assertEquals(0, toolWindowManager.getToolsByAnchor(ToolWindowAnchor.TOP).length);

        // getToolsByAnchor(ToolWindowAnchor.LEFT)
        assertNotNull(toolWindowManager.getToolsByAnchor(ToolWindowAnchor.LEFT));
        assertEquals(0, toolWindowManager.getToolsByAnchor(ToolWindowAnchor.LEFT).length);

        // getToolsByAnchor(ToolWindowAnchor.RIGHT)
        assertNotNull(toolWindowManager.getToolsByAnchor(ToolWindowAnchor.RIGHT));
        assertEquals(0, toolWindowManager.getToolsByAnchor(ToolWindowAnchor.RIGHT).length);

        // getToolWindow(int)
        assertNull(toolWindowManager.getToolWindow(0));
        assertNull(toolWindowManager.getToolWindow(-1));
        assertNull(toolWindowManager.getToolWindow(10));

        // getToolWindow(String)
        assertNull(toolWindowManager.getToolWindow("id"));
        assertNull(toolWindowManager.getToolWindow(null));
    }

    public void testToolWindowGroupMethods() {
        // getToolWindowGroups
        assertNotNull(toolWindowManager.getToolWindowGroups());
        assertEquals(0, toolWindowManager.getToolWindowGroups().length);

        // getToolWindowGroup(String
        assertNotNull(toolWindowManager.getToolWindowGroup("group"));
    }

    public void testToolWindowTypeDescriptor() {
        assertNotNull(toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING));
        assertNotNull(toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING_WINDOW));
        assertNotNull(toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED));
    }
}
