package org.noos.xing.mydoggy.plaf.ui;

import org.progx.collage.dnd.DragAndDropLock;

import javax.swing.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.Area;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class GlassPanel extends JPanel implements ContainerListener {
    private final int ANIMATION_DELAY = 500;

    private Image dragged = null;
    private Point location = new Point(0, 0);
    private Point oldLocation = new Point(0, 0);

    private int width;
    private int height;
    private Rectangle visibleRect = null;

    private float zoom = 1.0f;
    private float alpha = 0.7f;

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
        int x = (int) (location.getX() - (width * zoom / 2));
        int y = (int) (location.getY() - (height * zoom / 2));

        int x2 = (int) (oldLocation.getX() - (width * zoom / 2));
        int y2 = (int) (oldLocation.getY() - (height * zoom / 2));

        int width = (int) (this.width * zoom);
        int height = (int) (this.height * zoom);

        return new Rectangle(x, y, width, height).union(new Rectangle(x2, y2, width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (dragged == null || !isVisible())
            return;

        Graphics2D g2 = (Graphics2D) g.create();
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int x = (int) (location.getX() - (width * zoom / 2));
        int y = (int) (location.getY() - (height * zoom / 2));

        if (visibleRect != null) {
            g2.setClip(visibleRect);
        }

        if (visibleRect != null) {
            Area clip = new Area(visibleRect);
            g2.setClip(clip);
        }

        g2.drawImage(dragged, x, y, (int) (width * zoom), (int) (height * zoom), null);
    }

    public void startAnimation(Rectangle visibleRect) {
        this.visibleRect = visibleRect;
        new Timer(1000 / 30, new FadeOutAnimation()).start();
    }

    private class FadeOutAnimation implements ActionListener {
        private long start;

        FadeOutAnimation() {
            this.start = System.currentTimeMillis();
            oldLocation = location;
        }

        public void actionPerformed(ActionEvent e) {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > ANIMATION_DELAY) {
                ((Timer) e.getSource()).stop();
                setVisible(false);
                zoom = 1.0f;
                alpha = 0.6f;
                visibleRect = null;
                dragged = null;
                DragAndDropLock.setLocked(false);
            } else {
                alpha = 0.6f - (0.6f * (float) elapsed / (float) ANIMATION_DELAY);
                zoom = 1.0f + 3.0f * ((float) elapsed / (float) ANIMATION_DELAY);
            }
            repaint(getRepaintRect());
        }
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
