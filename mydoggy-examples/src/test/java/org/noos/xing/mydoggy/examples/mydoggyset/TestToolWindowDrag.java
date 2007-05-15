package org.noos.xing.mydoggy.examples.mydoggyset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestToolWindowDrag extends UITestCase {
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
        ToolWindow toolWindow = toolWindowManager.registerToolWindow("ID", "Title 1", null, new JButton("H1"), ToolWindowAnchor.LEFT);
        toolWindow.setAvailable(true);

        toolWindow = toolWindowManager.registerToolWindow("ID2", "Title 2", null, new JButton("H2"), ToolWindowAnchor.BOTTOM);
        toolWindow.setAvailable(true);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
    }

    protected void tearDown() throws Exception {
        frame.dispose();
    }

    public void testDragLeftToTop() {
        moveMouseTo("toolWindow.rb.ID");
        pressMouseLeftButton();
        moveMouseTo("toolWindowManager.bar.BOTTOM", 30, 10);
        releaseMouseLeftButton();

        delay(10000);
//        assertTrue("Invalid Drag", ask("Is drag correct?"));
    }



}