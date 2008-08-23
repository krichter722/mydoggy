package org.noos.xing.mydoggy.mydoggyset;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Test used to reproduce an infite-loop bug where two ToolWindows keep getting the focus alternatively.
 * <p/>
 * To produce the bug, just drag and drop the Tool_2 ToolWindow to the bottom of the Tool_1.
 * The Tool_2 must not have the focus when you start draging.
 *
 * @author Jean Morissette (jean.morissette@gmail.com)
 */
public class Test {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() {
        initComponents();
//        initToolWindowManager();
    }

    protected void start() {
        frame.setVisible(true);

//        ToolWindow tw1 = toolWindowManager.getToolWindow("Tool_1");
//        tw1.setActive(true);
//
//        ToolWindow tw2 = toolWindowManager.getToolWindow("Tool_2");
//        tw2.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("Sample App...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        this.frame.getContentPane().setLayout(new BorderLayout());

        JPanel panel = (JPanel) this.frame.getContentPane();
        panel.setOpaque(false);
    }

    protected void initToolWindowManager() {
// Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        this.toolWindowManager = myDoggyToolWindowManager;

        toolWindowManager.registerToolWindow(
                "Tool_1",
                "Tool_1",
                null,
                new JPanel(),
                ToolWindowAnchor.LEFT);


        toolWindowManager.registerToolWindow(
                "Tool_2",
                "Tool_2",
                null,
                new JPanel(),
                ToolWindowAnchor.BOTTOM);


        // Made all tools available
        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, BorderLayout.CENTER);
    }

    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
    }

    public static void main(String[] args) throws IOException {
        Test test = new Test();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}