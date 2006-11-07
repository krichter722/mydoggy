package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.*;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestDockedTypeDescriptor extends TestCase {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() throws Exception {
        frame = new JFrame("test");
        toolWindowManager = new MyDoggyToolWindowManager(frame);
    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testDockedTypeDescriptor() {
        assertNotNull(toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED));
    }

    public void testDockedTypeDescriptorDockLength() {
        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        descriptor.setDockLength(150);

        descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        assertEquals(150, descriptor.getDockLength());
    }

    public void testDockedTypeDescriptorPopupMenuEnabled() {
        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        descriptor.setPopupMenuEnabled(false);

        descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        assertEquals(false, descriptor.isPopupMenuEnabled());

        descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        descriptor.setPopupMenuEnabled(true);

        descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        assertEquals(true, descriptor.isPopupMenuEnabled());
    }

    public void testDockedTypeDescriptorUserDefinedMenu() {
        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        assertNotNull(descriptor.getToolsMenu());
    }


    public void testTWDockedTypeDescriptorDockLength() {
        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        descriptor.setDockLength(150);

        // Check DockLength on tool window type descriptor
        ToolWindow toolWindow = toolWindowManager.registerToolWindow("id", "title", null, new JLabel("title"), ToolWindowAnchor.LEFT);
        descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        assertEquals(150, descriptor.getDockLength());

        // Modify manager type descriptor 
        descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        descriptor.setDockLength(250);

        // Check modification
        descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        assertEquals(250, descriptor.getDockLength());
    }

    public void testTWDockedTypeDescriptorPopupMenuEnabled() {
        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        descriptor.setPopupMenuEnabled(false);

        // Check DockLength on tool window type descriptor
        ToolWindow toolWindow = toolWindowManager.registerToolWindow("id", "title", null, new JLabel("title"), ToolWindowAnchor.LEFT);
        descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        assertEquals(false, descriptor.isPopupMenuEnabled());

        // Modify manager type descriptor
        descriptor = (DockedTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.DOCKED);
        descriptor.setPopupMenuEnabled(true);

        // Check modification
        descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        assertEquals(true, descriptor.isPopupMenuEnabled());
    }

}
