package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestFloatingTypeDescriptor extends TestCase {

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

    public void testFloatingTypeDescriptor() {
        assertNotNull(toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING));
    }

    public void testFloatingTypeDescriptorLocation() {
        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        descriptor.setLocation(10,20);

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        assertEquals(10, descriptor.getLocation().x);
        assertEquals(20, descriptor.getLocation().y);
    }

    public void testFloatingTypeDescriptorModal() {
        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        descriptor.setModal(true);

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        assertEquals(true, descriptor.isModal());

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        descriptor.setModal(false);

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        assertEquals(false, descriptor.isModal());
    }

    public void testFloatingTypeDescriptorSize() {
        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        descriptor.setSize(100,200);

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        assertEquals(100, descriptor.getSize().width);
        assertEquals(200, descriptor.getSize().height);
    }

    public void testFloatingTypeDescriptorTransparentDelay() {
        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        descriptor.setTransparentDelay(1000);

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        assertEquals(1000, descriptor.getTransparentDelay());
    }

    public void testFloatingTypeDescriptorTransparentMode() {
        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        descriptor.setTransparentMode(false);

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        assertEquals(false, descriptor.isTransparentMode());

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        descriptor.setTransparentMode(true);

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        assertEquals(true, descriptor.isTransparentMode());
    }

    public void testFloatingTypeDescriptorTransparentRatio() {
        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        descriptor.setTransparentRatio(0.5f);

        descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
        assertEquals(0.5f, descriptor.getTransparentRatio());
    }

    public void testFloatingTypeDescriptorTransparentRatioUnuseCase() {
        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindowManager.getTypeDescriptorTemplate(ToolWindowType.FLOATING);
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
