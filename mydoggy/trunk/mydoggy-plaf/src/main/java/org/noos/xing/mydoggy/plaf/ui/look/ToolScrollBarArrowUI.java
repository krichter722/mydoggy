package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro
 * TODO: introduce create UI
 */
public class ToolScrollBarArrowUI extends MetalLabelUI {

    protected LineBorder labelBorder;


    public ToolScrollBarArrowUI() {
    }


    public void installUI(JComponent c) {
        super.installUI(c);

        labelBorder = new LineBorder(Color.GRAY, 1, true, 3, 3);
        c.setBorder(labelBorder);
    }

    protected void installListeners(JLabel c) {
        super.installListeners(c);
        c.addMouseListener(new ScrollToolBarArrowMouseAdapter());
    }

    public void update(Graphics g, JComponent c) {
        if (c.isOpaque())
            GraphicsUtil.fillRect(g,
                                  new Rectangle(0, 0, c.getWidth(), c.getHeight()),
                                  UIManager.getColor(MyDoggyKeySpace.TOOL_SCROLL_BAR_UI_BCK_START),
                                  UIManager.getColor(MyDoggyKeySpace.TOOL_SCROLL_BAR_UI_BCK_END),
                                  null,
                                  GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
        else {
            g.setColor(c.getParent().getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
        paint(g, c);
    }


    public class ScrollToolBarArrowMouseAdapter extends MouseAdapter {

        public void mouseEntered(MouseEvent e) {
			Component source = e.getComponent();
			if (!source.isOpaque()) {
                labelBorder.setLineColor(Color.BLACK);
                SwingUtil.repaint(source);
            }
        }

        public void mouseExited(MouseEvent e) {
			Component source = e.getComponent();
			if (!source.isOpaque()) {
                labelBorder.setLineColor(Color.GRAY);
                SwingUtil.repaint(source);
            }
        }

    }

}
