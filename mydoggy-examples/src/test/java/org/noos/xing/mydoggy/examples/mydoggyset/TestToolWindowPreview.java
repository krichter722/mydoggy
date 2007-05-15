package org.noos.xing.mydoggy.examples.mydoggyset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestToolWindowPreview extends UITestCase {
    private JFrame frame;
    private ToolWindow toolWindow;

    protected void setUp() throws Exception {
        setRootContainer(frame = new JFrame("TestToolWindowPreview"));
        frame.setSize(640, 480);
        frame.setLocation(150,150);

        MyDoggyToolWindowManager toolWindowManager = new MyDoggyToolWindowManager(frame);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new TableLayout(new double[][]{{-1},{-1}}));
        contentPane.add(toolWindowManager, "0,0,FULL,FULL");

        // Register ToolWindow
        toolWindow = toolWindowManager.registerToolWindow("ID", "Title", null, new JButton("H"), ToolWindowAnchor.BOTTOM);
        toolWindow.setAvailable(true);

        frame.setVisible(true);
    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testPreviewEnabled() {
        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        descriptor.setPreviewEnabled(true);
        descriptor.setPreviewDelay(1000);

        moveMouseTo("toolWindow.rb.ID");
        delay(1100);

//        assertTrue("Preview not visible", ask("Is preview visible?"));

        moveMouseTo("toolWindowManager.mainContainer");

        delay(1000);

//        assertTrue("Preview is still visible", ask("Is preview not visible?"));
    }

/*
    public void testPreviewDisabled() {
        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        descriptor.setPreviewEnabled(false);

        moveMouseTo("toolWindow.rb.ID");
        delay(1100);

        assertFalse("Preview is visible.", ask("Is preview visible?"));

        moveMouseTo("toolWindowManager.mainContainer");

        delay(1000);

        assertFalse("Preview is visible.", ask("Is preview visible?"));
    }
*/
}
