package org.noos.xing.mydoggy.plaf.ui.translucent;

import javax.swing.*;
import java.awt.*;


/**
 * @author Angelo De Caro
 */
public class TranslucentPanel extends JPanel implements TranslucentComponent {
    private float alpha = 1.0f;
    private boolean oldOpaque;

    public TranslucentPanel() {
    }

    public TranslucentPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public TranslucentPanel(LayoutManager layout) {
        super(layout);
    }

    public TranslucentPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }


    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Composite oldComp = g2d.getComposite();

        float alpha = getAlphaModeEnabled();

        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComp);

        super.paint(g2d);

        g2d.setComposite(oldComp);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Composite oldComp = g2d.getComposite();

        float alpha = getAlphaModeEnabled();

        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComp);

        super.paintComponent(g2d); 

        g2d.setComposite(oldComp);
    }

    public void setAlphaModeRatio(float alpha) {
        if (this.alpha != alpha) {
            assert alpha >= 0 && alpha <= 1.0;
            float oldAlpha = this.alpha;
            this.alpha = alpha;
            if (alpha > 0f && alpha < 1f) {
                if (oldAlpha == 1) {
                    //it used to be 1, but now is not. Save the oldOpaque
                    oldOpaque = isOpaque();
                    setOpaque(false);
                }
                javax.swing.RepaintManager manager = javax.swing.RepaintManager.currentManager(this);
                if (!(manager instanceof TranslucentRepaintManager))
                    javax.swing.RepaintManager.setCurrentManager(new TranslucentRepaintManager());
            } else if (alpha == 1) {
                //restore the oldOpaque if it was true (since opaque is false now)
                if (oldOpaque) {
                    setOpaque(true);
                }
            }
            firePropertyChange("alpha", oldAlpha, alpha);
            repaint();
        }
    }

    public float getAlphaModeEnabled() {
        return alpha;
    }

}
