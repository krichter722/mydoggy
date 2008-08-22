package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowGroupEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestTWGroupListener extends TestCase {

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

    public void testGroupShown() {
        ToolWindow tool = toolWindowManager.registerToolWindow(
                "test", "title", null, new JLabel("Test"), ToolWindowAnchor.TOP
        );
        ToolWindowGroup group = toolWindowManager.getToolWindowGroup("main");

        TestToolWindowGroupListener listener = new TestToolWindowGroupListener();
        group.addToolWindowGroupListener(listener);

        group.addToolWindow(tool);

        group.setVisible(true);

        assertTrue(listener.isGroupShown());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowGroupEvent.ActionId.GROUP_SHOWN, listener.getLastEvent().getId());
        assertEquals(group, listener.getLastEvent().getToolWindowGroup());
        assertNull(listener.getLastEvent().getToolWindow());

        group.setVisible(false);
    }

    public void testGroupHidden() {
        ToolWindow tool = toolWindowManager.registerToolWindow(
                "test", "title", null, new JLabel("Test"), ToolWindowAnchor.TOP
        );
        ToolWindowGroup group = toolWindowManager.getToolWindowGroup("main");

        TestToolWindowGroupListener listener = new TestToolWindowGroupListener();
        group.addToolWindowGroupListener(listener);

        group.addToolWindow(tool);
        group.setVisible(true);
        group.setVisible(false);

        assertTrue(listener.isGroupHidden());
        assertNotNull(listener.getLastEvent());
        assertEquals(ToolWindowGroupEvent.ActionId.GROUP_HIDDEN, listener.getLastEvent().getId());
        assertEquals(group, listener.getLastEvent().getToolWindowGroup());
        assertNull(listener.getLastEvent().getToolWindow());
    }

    private class TestToolWindowGroupListener implements ToolWindowGroupListener {
        private boolean toolAdded;
        private boolean toolRemoved;
        private boolean groupShown;
        private boolean groupHidden;

        private ToolWindowGroupEvent lastEvent;


        public void groupHidden(ToolWindowGroupEvent event) {
            this.groupHidden = true;
            this.lastEvent = event;
        }

        public void groupShown(ToolWindowGroupEvent event) {
            this.groupShown = true;
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

        public boolean isGroupShown() {
            return groupShown;
        }

        public boolean isGroupHidden() {
            return groupHidden;
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
