package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.ToolWindowManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FrameshotAction extends AbstractAction {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    public FrameshotAction(JFrame frame, ToolWindowManager toolWindowManager) {
        super("Frameshot");
        this.frame = frame;
        this.toolWindowManager = toolWindowManager;
    }

    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Robot robot = new Robot();
                    BufferedImage image = robot.createScreenCapture(frame.getBounds());
                    ImageIO.write(image, "png", new File("screenshot.png"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}