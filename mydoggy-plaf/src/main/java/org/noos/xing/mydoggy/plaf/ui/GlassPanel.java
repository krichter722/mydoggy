package org.noos.xing.mydoggy.plaf.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class GlassPanel extends JPanel implements ContainerListener {
    private Image draggingImage = null;
    private Image previewImage = null;

    private Point location = new Point(0, 0);
    private Point oldLocation = new Point(0, 0);

    private int width;
    private int height;
    private Rectangle visibleRect = null;

    private GlassPaneMouseAdapter adapter;

    public GlassPanel(RootPaneContainer rootPaneContainer) {
        adapter = new GlassPaneMouseAdapter(rootPaneContainer);
        setOpaque(false);
        setVisible(false);
        setLayout(null);
        addContainerListener(this);
    }

    public void setDraggingImage(BufferedImage draggingImage) {
        setDraggingImage(draggingImage, draggingImage == null ? 0 : draggingImage.getWidth());
    }

    public void setDraggingImage(BufferedImage draggingImage, int width) {
        if (draggingImage != null) {
            float ratio = (float) draggingImage.getWidth() / (float) draggingImage.getHeight();
            this.width = width;
            height = (int) (width / ratio);
        }

        this.location = null;
        this.draggingImage = draggingImage;
    }

    public Image getDraggingImage() {
        return draggingImage;
    }


    public void setPreviewImage(Image previewImage) {
        if (previewImage != null) {
            float ratio = (float) previewImage.getWidth(this) / (float) previewImage.getHeight(this);
            this.width = previewImage.getWidth(this);
            height = (int) (width / ratio);
        }

        this.location = null;
        this.previewImage = previewImage;
    }

    public Image getPreviewImage() {
        return previewImage;
    }


    public void setPoint(Point location) {
        this.oldLocation = this.location;
        this.location = location;
    }

    public Rectangle getRepaintRect() {
        if (location == null || oldLocation == null)
            return getBounds();
        
        int x = (int) (location.getX() - (width / 2d));
        int y = (int) (location.getY() - (height / 2d));

        int x2 = (int) (oldLocation.getX() - (width / 2d));
        int y2 = (int) (oldLocation.getY() - (height / 2d));

        int width = this.width;
        int height = this.height;

        return new Rectangle(x, y, width, height).union(new Rectangle(x2, y2, width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if ((draggingImage == null && previewImage == null) || !isVisible() || location == null)
            return;

        Graphics2D g2 = (Graphics2D) g.create();
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int x,y;
        if (draggingImage != null) {
            x = (int) (location.getX() - (width / 2d));
            y = (int) (location.getY() - (height / 2d));
        } else {
            x = (int) location.getX();
            y = (int) location.getY();
        }

        if (visibleRect != null) {
            g2.setClip(visibleRect);
        }

        if (visibleRect != null) {
            Area clip = new Area(visibleRect);
            g2.setClip(clip);
        }

        Image image = (draggingImage != null) ? draggingImage : previewImage;
        g2.drawImage(image, x, y, width, height, null);
    }

    public void componentAdded(ContainerEvent e) {
        setVisible(true);
        if (getComponentCount() == 1) {
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
            addMouseWheelListener(adapter);
        }
    }

    public void componentRemoved(ContainerEvent e) {
        if (getComponentCount() == 0) {
            setVisible(false);
            removeMouseListener(adapter);
            removeMouseMotionListener(adapter);
            removeMouseWheelListener(adapter);
        }
    }

    public void setVisible(boolean aFlag) {
        if (!aFlag) {
            if (getComponentCount() == 0)
                super.setVisible(aFlag);
        } else
            super.setVisible(aFlag);
    }

}