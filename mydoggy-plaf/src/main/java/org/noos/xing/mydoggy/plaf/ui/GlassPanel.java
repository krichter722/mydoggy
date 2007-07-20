package org.noos.xing.mydoggy.plaf.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.geom.Area;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class GlassPanel extends JPanel implements ContainerListener {
    private RootPaneContainer rootPaneContainer;
    private Component oldGlassPanel;

    private Image draggingImage = null;

    private Point location = new Point(0, 0);
    private Point oldLocation = new Point(0, 0);

    private int width;
    private int height;
    private Rectangle visibleRect = null;

    public GlassPanel(RootPaneContainer rootPaneContainer) {
        this.rootPaneContainer = rootPaneContainer;

        setOpaque(false);
        setVisible(false);
        setLayout(null);
        addContainerListener(this);
    }

    public void setDraggingImage(Image draggingImage) {
        setDraggingImage(draggingImage, draggingImage == null ? 0 : draggingImage.getWidth(this));
    }

    public void setDraggingImage(Image draggingImage, int width) {
        if (draggingImage != null) {
            float ratio = (float) draggingImage.getWidth(this) / (float) draggingImage.getHeight(this);
            this.width = width;
            height = (int) (width / ratio);
        }

        this.draggingImage = draggingImage;
//        this.location = null;
    }

    public Image getDraggingImage() {
        return draggingImage;
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
        Graphics2D g2 = (Graphics2D) g;

        // For debug puprose
//        g2.setColor(Color.RED);
//        g2.fillOval(10,10,10,10);

        if (draggingImage == null || !isVisible() || location == null)
            return;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x,y;
        if (draggingImage != null) {
            x = (int) (location.getX() - (width / 2d));
            y = (int) (location.getY() - (height / 2d));
        } else {
            x = (int) location.getX();
            y = (int) location.getY();
        }

        if (visibleRect != null)
            g2.setClip(new Area(visibleRect));

        g2.drawImage(draggingImage, x, y, width, height, null);
    }

    public void componentAdded(ContainerEvent e) {
        setVisible(true);
    }

    public void componentRemoved(ContainerEvent e) {
        if (getComponentCount() == 0)
            setVisible(false);
    }

    public void setVisible(boolean aFlag) {
        if (!aFlag) {
            if (getComponentCount() == 0) {
                super.setVisible(aFlag);
                unmount();
            }
        } else {
            mount();
            super.setVisible(aFlag);
        }
    }

    protected GlassPanel mount() {
        if (rootPaneContainer.getGlassPane() == this)
            return this;

        oldGlassPanel = rootPaneContainer.getGlassPane();
        rootPaneContainer.setGlassPane(this);
        return this;
    }

    protected void unmount() {
        if (oldGlassPanel != null)
            rootPaneContainer.setGlassPane(oldGlassPanel);
    }

}
