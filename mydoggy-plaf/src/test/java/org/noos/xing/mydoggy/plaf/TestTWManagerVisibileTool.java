package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestTWManagerVisibileTool extends TestCase {

    private JFrame frame;
	private ToolWindowManager toolWindowManager;

    protected void setUp() throws Exception {
        frame = new JFrame("test");
        final MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager(frame);
        this.toolWindowManager = myDoggyToolWindowManager;
        myDoggyToolWindowManager.setMainContent(new JTree());

        registerToolWindow();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setSize(640, 480);
                frame.setLocation(100, 100);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.getContentPane().setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
                frame.getContentPane().add(myDoggyToolWindowManager, "0,0,");

                frame.setVisible(true);
            }
        });
    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    private void registerToolWindow() {
		Component toolContent = new JLabel("label");
		ToolWindow toolWindow = toolWindowManager.registerToolWindow(
                "id", "title", null, toolContent, ToolWindowAnchor.LEFT
        );

        assertNotNull(toolWindow);
        assertEquals("id", toolWindow.getId());
        assertEquals("title", toolWindow.getTitle());
        assertNull(toolWindow.getIcon());
        assertEquals(ToolWindowAnchor.LEFT, toolWindow.getAnchor());

        assertEquals(1, toolWindowManager.getToolWindows().length);
        assertEquals(toolWindow, toolWindowManager.getToolWindow("id"));
    }

    public void testSetVisibileTrueDocked() {
        ToolWindow window = toolWindowManager.getToolWindow("id");

        assertFalse(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(true);

        assertTrue(window.isAvailable());
        assertTrue(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(false);
    }

    public void testSetVisibileFalseDocked() {
        ToolWindow window = toolWindowManager.getToolWindow("id");

        assertFalse(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(true);

        window.setVisible(false);

        assertTrue(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());
    }

    public void testSetVisibileTrueSliding() {
        ToolWindow window = toolWindowManager.getToolWindow("id");
        window.setType(ToolWindowType.SLIDING);

        assertFalse(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(true);

        assertTrue(window.isAvailable());
        assertTrue(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(false);
    }

    public void testSetVisibileFalseSliding() {
        ToolWindow window = toolWindowManager.getToolWindow("id");
        window.setType(ToolWindowType.SLIDING);

        assertFalse(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(true);

        window.setVisible(false);

        assertTrue(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());
    }

    public void testSetVisibileTrueFloating() {
        ToolWindow window = toolWindowManager.getToolWindow("id");
        window.setType(ToolWindowType.FLOATING);

        assertFalse(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(true);

        assertTrue(window.isAvailable());
        assertTrue(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(false);
    }

    public void testSetVisibileFalseFloating() {
        ToolWindow window = toolWindowManager.getToolWindow("id");
        window.setType(ToolWindowType.FLOATING);

        assertFalse(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(true);

        window.setVisible(false);

        assertTrue(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());
    }

    public void testSetVisibileTrueFloatingWindow() {
        ToolWindow window = toolWindowManager.getToolWindow("id");
        window.setType(ToolWindowType.FLOATING_FREE);

        assertFalse(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(true);

        assertTrue(window.isAvailable());
        assertTrue(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(false);
    }

    public void testSetVisibileFalseFloatingWindow() {
        ToolWindow window = toolWindowManager.getToolWindow("id");
        window.setType(ToolWindowType.FLOATING_FREE);

        assertFalse(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());

        window.setVisible(true);

        window.setVisible(false);

        assertTrue(window.isAvailable());
        assertFalse(window.isVisible());
        assertFalse(window.isActive());

    }

}
