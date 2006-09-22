package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro
 */
public class ScrollToolBarArrowlUI extends MetalLabelUI {

    static final Color start = new Color(255, 212, 151);
    static final Color end = new Color(255, 244, 204);

    protected JLabel label;
    protected LineBorder labelBorder;

    public void installUI(JComponent c) {
        super.installUI(c);
        label = (JLabel) c;
        labelBorder = new LineBorder(Color.GRAY, 1, true, 3, 3);
        label.setBorder(labelBorder);
    }

    protected void installListeners(JLabel c) {
        super.installListeners(c);

        BarLabelMouseAdapter adapter = new BarLabelMouseAdapter();
        c.addMouseListener(adapter);
    }


    public void update(Graphics g, JComponent c) {
        if (c.isOpaque())
            GraphicsUtil.fillRect(g, new Rectangle(0, 0, c.getWidth(), c.getHeight()),
                                  start, end, null, GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
        else {
            g.setColor(c.getParent().getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
        paint(g, c);
    }

    class BarLabelMouseAdapter extends MouseAdapter {

        public void mouseEntered(MouseEvent e) {
            if (!label.isOpaque()) {
                labelBorder.setLineColor(Color.BLACK);
                SwingUtil.repaint(label);
            }
        }

        public void mouseExited(MouseEvent e) {
            if (!label.isOpaque()) {
                labelBorder.setLineColor(Color.GRAY);
                SwingUtil.repaint(label);
            }
        }

    }

}
