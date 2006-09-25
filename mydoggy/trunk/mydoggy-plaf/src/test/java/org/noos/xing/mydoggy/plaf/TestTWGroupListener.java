package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowGroupEvent;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestTWGroupListener extends TestCase {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() throws Exception {
        frame = new JFrame("test");
        toolWindowManager = new MyDoggyToolWindowManager(frame);

    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testAddToolToGroup() {

        ToolWindow tool = toolWindowManager.registerToolWindow(
                "test", "title", null, new JLabel("Test"), ToolWindowAnchor.TOP
        );
        ToolWindowGroup group = toolWindowManager.getToolWindowGroup("main");

        TestToolWindowGroupListener listener = new TestToolWindowGroupListener();
        group.addToolWindowGroupListener(listener);

        group.addToolWindow(tool);

        assertTrue(listener.isToolAdded());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowGroupEvent.ActionId.TOOL_ADDED, listener.getLastEvent().getId());
        assertEquals(group, listener.getLastEvent().getToolWindowGroup());
        assertEquals(tool, listener.getLastEvent().getToolWindow());
    }

    public void testRemoveToolToGroup() {

        ToolWindow tool = toolWindowManager.registerToolWindow(
                "test", "title", null, new JLabel("Test"), ToolWindowAnchor.TOP
        );
        ToolWindowGroup group = toolWindowManager.getToolWindowGroup("main");

        TestToolWindowGroupListener listener = new TestToolWindowGroupListener();
        group.addToolWindowGroupListener(listener);

        group.addToolWindow(tool);
        group.removeToolWindow(tool);

        assertTrue(listener.isToolRemoved());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowGroupEvent.ActionId.TOOL_REMOVED, listener.getLastEvent().getId());
        assertEquals(group, listener.getLastEvent().getToolWindowGroup());
        assertEquals(tool, listener.getLastEvent().getToolWindow());
    }

    public void testGroupShowed() {
        ToolWindow tool = toolWindowManager.registerToolWindow(
                "test", "title", null, new JLabel("Test"), ToolWindowAnchor.TOP
        );
        ToolWindowGroup group = toolWindowManager.getToolWindowGroup("main");

        TestToolWindowGroupListener listener = new TestToolWindowGroupListener();
        group.addToolWindowGroupListener(listener);

        group.addToolWindow(tool);

        group.setVisible(true);

        assertTrue(listener.isGroupShowed());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowGroupEvent.ActionId.GROUP_SHOWED, listener.getLastEvent().getId());
        assertEquals(group, listener.getLastEvent().getToolWindowGroup());
        assertNull(listener.getLastEvent().getToolWindow());

        group.setVisible(false);
    }

    public void testGroupHided() {
        ToolWindow tool = toolWindowManager.registerToolWindow(
                "test", "title", null, new JLabel("Test"), ToolWindowAnchor.TOP
        );
        ToolWindowGroup group = toolWindowManager.getToolWindowGroup("main");

        TestToolWindowGroupListener listener = new TestToolWindowGroupListener();
        group.addToolWindowGroupListener(listener);

        group.addToolWindow(tool);
        group.setVisible(true);
        group.setVisible(false);

        assertTrue(listener.isGroupHided());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowGroupEvent.ActionId.GROUP_HIDED, listener.getLastEvent().getId());
        assertEquals(group, listener.getLastEvent().getToolWindowGroup());
        assertNull(listener.getLastEvent().getToolWindow());
    }

    private class TestToolWindowGroupListener implements ToolWindowGroupListener {
        private boolean toolAdded;
        private boolean toolRemoved;
        private boolean groupShowed;
        private boolean groupHided;

        private ToolWindowGroupEvent lastEvent;


        public void groupHided(ToolWindowGroupEvent event) {
            this.groupHided = true;
            this.lastEvent = event;
        }

        public void groupShowed(ToolWindowGroupEvent event) {
            this.groupShowed = true;
            this.lastEvent = event;
        }

        public void toolAdded(ToolWindowGroupEvent event) {
            this.toolAdded = true;
            this.lastEvent = event;
        }

        public void toolRemoved(ToolWindowGroupEvent event) {
            this.toolRemoved = true;
            this.lastEvent = event;
        }

        public boolean isGroupShowed() {
            return groupShowed;
        }

        public boolean isGroupHided() {
            return groupHided;
        }

        public boolean isToolAdded() {
            return toolAdded;
        }

        public boolean isToolRemoved() {
            return toolRemoved;
        }

        public ToolWindowGroupEvent getLastEvent() {
            return lastEvent;
        }

    }

}
