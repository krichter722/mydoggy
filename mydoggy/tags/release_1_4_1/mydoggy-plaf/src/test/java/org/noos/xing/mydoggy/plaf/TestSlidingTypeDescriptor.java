package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.SlidingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestSlidingTypeDescriptor extends TestCase {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() throws Exception {
        frame = new JFrame("test");
        toolWindowManager = new MyDoggyToolWindowManager(frame);
    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testSlidingTypeDescriptor() {
        assertNotNull(toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING));
    }

    public void testSlidingTypeDescriptorTransparentDelay() {
        SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING);
        descriptor.setTransparentDelay(1000);

        descriptor = (SlidingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING);
        assertEquals(1000, descriptor.getTransparentDelay());
    }

    public void testSlidingTypeDescriptorTransparentMode() {
        SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING);
        descriptor.setTransparentMode(false);

        descriptor = (SlidingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING);
        assertEquals(false, descriptor.isTransparentMode());

        descriptor = (SlidingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING);
        descriptor.setTransparentMode(true);

        descriptor = (SlidingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING);
        assertEquals(true, descriptor.isTransparentMode());
    }

    public void testSlidingTypeDescriptorTransparentRatio() {
        SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING);
        descriptor.setTransparentRatio(0.5f);

        descriptor = (SlidingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING);
        assertEquals(0.5f, descriptor.getTransparentRatio());
    }

    public void testSlidingTypeDescriptorTransparentRatioUnuseCase() {
        SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.SLIDING);
        try {
            descriptor.setTransparentRatio(1.5f);
            fail("Must fail with 1.5f");
        } catch (Exception e) {}

        try {
            descriptor.setTransparentRatio(-0.1f);
            fail("Must fail with -0.1f");
        } catch (Exception e) {}
    }

}
