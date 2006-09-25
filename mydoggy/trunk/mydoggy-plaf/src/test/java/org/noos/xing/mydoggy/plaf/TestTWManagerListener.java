package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestTWManagerListener extends TestCase {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() throws Exception {
        frame = new JFrame("test");
        toolWindowManager = new MyDoggyToolWindowManager(frame);

    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testRegisterToolWindow() {
        TestToolWindowManagerListener listener = new TestToolWindowManagerListener();
        toolWindowManager.addToolWindowManagerListener(listener);

        ToolWindow tool = toolWindowManager.registerToolWindow(
                "test", "title", null, new JLabel("Test"), ToolWindowAnchor.TOP
        );

        assertTrue(listener.isWindowRegistered());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowManagerEvent.ActionId.TOOL_REGISTERED, listener.getLastEvent().getActionId());
        assertEquals(tool, listener.getLastEvent().getToolWindow());
    }

    public void testUnregisterToolWindow() {
        TestToolWindowManagerListener listener = new TestToolWindowManagerListener();
        toolWindowManager.addToolWindowManagerListener(listener);
        ToolWindow tool = toolWindowManager.registerToolWindow(
                "test", "title", null, new JLabel("Test"), ToolWindowAnchor.TOP
        );

        toolWindowManager.unregisterToolWindow(tool.getId());

        assertTrue(listener.isWindowUnregistered());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowManagerEvent.ActionId.TOOL_UNREGISTERED, listener.getLastEvent().getActionId());
        assertEquals(tool, listener.getLastEvent().getToolWindow());
    }

    public void testUnregisterAllToolWindow() {
        TestToolWindowManagerListener listener = new TestToolWindowManagerListener();
        toolWindowManager.addToolWindowManagerListener(listener);
        ToolWindow tool = toolWindowManager.registerToolWindow(
                "test", "title", null, new JLabel("Test"), ToolWindowAnchor.TOP
        );

        toolWindowManager.unregisterAllToolWindow();

        assertTrue(listener.isWindowUnregistered());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowManagerEvent.ActionId.TOOL_UNREGISTERED, listener.getLastEvent().getActionId());
        assertEquals(tool, listener.getLastEvent().getToolWindow());
    }

    public void testAddToolWindowGroup() {
        TestToolWindowManagerListener listener = new TestToolWindowManagerListener();
        toolWindowManager.addToolWindowManagerListener(listener);

        ToolWindowGroup group = toolWindowManager.getToolWindowGroup("main");

        assertTrue(listener.isWindowGroupAdded());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowManagerEvent.ActionId.GROUP_ADDED, listener.getLastEvent().getActionId());
        assertEquals(group, listener.getLastEvent().getToolWindowGroup());
    }

    public void testRemoveToolWindowGroup() {
        TestToolWindowManagerListener listener = new TestToolWindowManagerListener();
        toolWindowManager.addToolWindowManagerListener(listener);

        ToolWindowGroup group = toolWindowManager.getToolWindowGroup("main");
        toolWindowManager.removeToolWindowGroup(group.getName());

        assertTrue(listener.isWindowGroupRemoved());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowManagerEvent.ActionId.GROUP_REMOVED, listener.getLastEvent().getActionId());
        assertEquals(group, listener.getLastEvent().getToolWindowGroup());
    }

    private class TestToolWindowManagerListener implements ToolWindowManagerListener {
        private boolean windowRegistered;
        private boolean windowUnregistered;
        private boolean windowGroupAdded;
        private boolean windowGroupRemoved;

        private ToolWindowManagerEvent lastEvent;

        public void toolWindowRegistered(ToolWindowManagerEvent event) {
            this.windowRegistered = true;
            this.lastEvent = event;
        }

        public void toolWindowUnregistered(ToolWindowManagerEvent event) {
            this.windowUnregistered = true;
            this.lastEvent = event;
        }

        public void toolWindowGroupAdded(ToolWindowManagerEvent event) {
            this.windowGroupAdded = true;
            this.lastEvent = event;
        }

        public void toolWindowGroupRemoved(ToolWindowManagerEvent event) {
            this.windowGroupRemoved = true;
            this.lastEvent = event;
        }


        public boolean isWindowGroupAdded() {
            return windowGroupAdded;
        }

        public boolean isWindowGroupRemoved() {
            return windowGroupRemoved;
        }

        public boolean isWindowRegistered() {
            return windowRegistered;
        }

        public boolean isWindowUnregistered() {
            return windowUnregistered;
        }

        public ToolWindowManagerEvent getLastEvent() {
            return lastEvent;
        }

    }

}
