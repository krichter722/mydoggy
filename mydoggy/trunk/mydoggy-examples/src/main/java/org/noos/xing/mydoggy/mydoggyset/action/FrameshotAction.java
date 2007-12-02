package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.ToolWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FrameshotAction extends AbstractAction {
    protected JFrame frame;
    protected File dir;
    protected File dirThumb;

    public FrameshotAction(JFrame frame) {
        super("Frameshot");
        this.frame = frame;
        this.dir = new File("shots");
        dir.mkdirs();
        this.dirThumb = new File(dir, "thumb");
        dirThumb.mkdirs();

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(new Character('f'), InputEvent.CTRL_MASK));

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent(KeyEvent e) {
                switch (e.getID()) {
                    case KeyEvent.KEY_TYPED:
                        if (e.isControlDown() && e.getKeyChar() == '\006') {
                            FrameshotAction.this.actionPerformed(null);
                        }
                        break;
                }

                return false;
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);

                    // Take the frameshow
                    Robot robot = new Robot();
                    BufferedImage image = robot.createScreenCapture(frame.getBounds());

                    // Store
                    final String finalName = JOptionPane.showInputDialog("Input frameshot name : ");
                    ImageIO.write(image, "png", new File(dir, finalName + ".png"));

                    Image scaledImage = image.getScaledInstance(150, 113, Image.SCALE_SMOOTH);

                    BufferedImage scaled = new BufferedImage(150, 113, BufferedImage.TYPE_INT_RGB);
                    scaled.getGraphics().drawImage(scaledImage, 0, 0, null);

                    ImageIO.write(scaled, "png", new File(dirThumb, finalName + ".png"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

}
