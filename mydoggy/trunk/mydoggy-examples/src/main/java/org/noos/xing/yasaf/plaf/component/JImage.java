package org.noos.xing.yasaf.plaf.component;

import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JImage extends JComponent {
    protected BufferedImage original;
    protected BufferedImage currentScaled;
    protected Dimension lastSize;

    public JImage() {
    }

    public JImage(String url) {
        try {
            original = ImageIO.read(this.getClass().getClassLoader().getResource(url));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void paintComponent(Graphics g) {
        Graphics scratchGraphics = (g == null) ? null : g.create();
        try {
            if (original != null) {
                if (lastSize != null) {
                    lastSize = getSize();
                    currentScaled = GraphicsUtil.scale(original, lastSize.width, lastSize.height);
                } else {
                    Dimension currentDim = getSize();
                    if (!currentDim.equals(lastSize)) {
                        lastSize = currentDim;
                        currentScaled = GraphicsUtil.scale(original, lastSize.width, lastSize.height);
                    }
                }
                if (currentScaled !=  null)
                    g.drawImage(currentScaled, 0,0, this);
            }
        } finally {
            scratchGraphics.dispose();
        }
    }

    public void setImage(BufferedImage image) {
        this.original = image;
        SwingUtil.repaint(this);
    }

    public BufferedImage getImage() {
        return original;
    }
}
