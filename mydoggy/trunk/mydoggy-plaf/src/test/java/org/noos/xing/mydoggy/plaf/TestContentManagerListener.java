package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ContentManagerListener;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.event.ContentManagerEvent;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestContentManagerListener extends TestCase {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() throws Exception {
        frame = new JFrame("test");
        toolWindowManager = new MyDoggyToolWindowManager(frame);

    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testAddContent() {
        ContentManager contentManager = toolWindowManager.getContentManager();

        TestCMListener listener = new TestCMListener();
        contentManager.addContentManagerListener(listener);

        Content content = contentManager.addContent("key", "content", null, new JButton("Hello World!!!"));

        assertTrue(listener.isContentAdded());
        assertNotNull(listener.getLastEvent());
        assertEquals(ContentManagerEvent.ActionId.CONTENT_ADDED, listener.getLastEvent().getId());
        assertEquals(content, listener.getLastEvent().getContent());
    }

    public void testRemoveContent() {
        ContentManager contentManager = toolWindowManager.getContentManager();

        TestCMListener listener = new TestCMListener();
        contentManager.addContentManagerListener(listener);

        Content content = contentManager.addContent("key", "content", null, new JButton("Hello World!!!"));
        assertTrue(contentManager.removeContent(content));

        assertTrue(listener.isContentRemoved());
        assertNotNull(listener.getLastEvent());
        assertEquals(ContentManagerEvent.ActionId.CONTENT_REMOVED, listener.getLastEvent().getId());
        assertEquals(content, listener.getLastEvent().getContent());
    }

    private class TestCMListener implements ContentManagerListener {
        private boolean contentAdded;
        private boolean contentRemoved;

        private ContentManagerEvent lastEvent;


        public void contentAdded(ContentManagerEvent event) {
            this.contentAdded = true;
            this.lastEvent = event;
        }

        public void contentRemoved(ContentManagerEvent event) {
            this.contentRemoved = true;
            this.lastEvent = event;
        }

        public boolean contentRemoving(ContentManagerEvent event) {
            return true;
        }

        public boolean isContentAdded() {
            return contentAdded;
        }

        public boolean isContentRemoved() {
            return contentRemoved;
        }

        public ContentManagerEvent getLastEvent() {
            return lastEvent;
        }

    }

}
