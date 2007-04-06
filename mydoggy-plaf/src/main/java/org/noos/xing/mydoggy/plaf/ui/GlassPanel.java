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
    private Image dragged = null;
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

    public void setImage(BufferedImage dragged) {
        setImage(dragged, dragged == null ? 0 : dragged.getWidth());
    }

    public void setImage(BufferedImage dragged, int width) {
        if (dragged != null) {
            float ratio = (float) dragged.getWidth() / (float) dragged.getHeight();
            this.width = width;
            height = (int) (width / ratio);
        }

        this.location = null;
        this.dragged = dragged;
    }

    public Image getDragged() {
        return dragged;
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
        if (dragged == null || !isVisible() || location == null)
            return;

        Graphics2D g2 = (Graphics2D) g.create();
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int x = (int) (location.getX() - (width / 2d));
        int y = (int) (location.getY() - (height / 2d));

        if (visibleRect != null) {
            g2.setClip(visibleRect);
        }

        if (visibleRect != null) {
            Area clip = new Area(visibleRect);
            g2.setClip(clip);
        }

        g2.drawImage(dragged, x, y, width, height, null);
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
