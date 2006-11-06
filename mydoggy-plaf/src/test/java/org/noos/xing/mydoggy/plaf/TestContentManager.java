package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.Content;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestContentManager extends TestCase {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() throws Exception {
        frame = new JFrame("test");
        toolWindowManager = new MyDoggyToolWindowManager(frame);

    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testContentManager() {
        assertNotNull(toolWindowManager.getContentManager());
    }

    public void testAddContentOne() {
        ContentManager contentManager = toolWindowManager.getContentManager();
        Content content = contentManager.addContent("key", "title", null, new JButton("Hello World!!!"));

        assertNotNull(content);
        assertEquals(contentManager.getContentCount(), 1);

        content = contentManager.getContent(0);

        assertEquals(content.getTitle(), "title");
        assertEquals(content.getIcon(), null);
        assertEquals(content.getToolTipText(), null);
        assertTrue(content.getComponent() instanceof JButton);

        content = contentManager.getContent("key");

        assertEquals(content.getTitle(), "title");
        assertEquals(content.getIcon(), null);
        assertEquals(content.getToolTipText(), null);
        assertTrue(content.getComponent() instanceof JButton);
    }

    public void testAddContentTwo() {
        ContentManager contentManager = toolWindowManager.getContentManager();
        Content content = contentManager.addContent("key", "title", null, new JButton("Hello World!!!"), "tip");

        assertNotNull(content);
        assertEquals(contentManager.getContentCount(), 1);

        content = contentManager.getContent(0);

        assertEquals(content.getTitle(), "title");
        assertEquals(content.getIcon(), null);
        assertEquals(content.getToolTipText(), "tip");
        assertTrue(content.getComponent() instanceof JButton);
    }

}
