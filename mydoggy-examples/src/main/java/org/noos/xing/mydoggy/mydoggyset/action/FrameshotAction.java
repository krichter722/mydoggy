package org.noos.xing.mydoggy.mydoggyset.action;

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
    protected JFrame frame;
    protected File dir;
    protected File dirThumb;
    protected int index = 0;

    public FrameshotAction(JFrame frame) {
        super("Frameshot");
        this.frame = frame;
        this.dir = new File("shots");
        dir.mkdirs();
        this.dirThumb = new File(dir, "thumb");
        dirThumb.mkdirs();
    }

    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Robot robot = new Robot();
                    BufferedImage image = robot.createScreenCapture(frame.getBounds());
                    ImageIO.write(image, "png", new File(dir, "screenshot_" + index + ".png"));

                    Image scaledImage = image.getScaledInstance(150, 113, Image.SCALE_SMOOTH);

                    BufferedImage scaled = new BufferedImage(150, 113, BufferedImage.TYPE_INT_RGB);
                    scaled.getGraphics().drawImage(scaledImage, 0, 0, null);
                    
                    ImageIO.write(scaled, "png", new File(dirThumb, "screenshot_" + index + ".png"));
                    index++;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

}
