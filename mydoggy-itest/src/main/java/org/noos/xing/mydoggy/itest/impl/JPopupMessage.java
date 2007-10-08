package org.noos.xing.mydoggy.itest.impl;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JPopupMessage extends JLabel {
    protected JLayeredPane layeredPane;

    public JPopupMessage(JFrame frame) {
        this.layeredPane = frame.getLayeredPane();
    }

    public void showMessage(String message, int x, int y) {
        setText(message);
        setLocation(x, y);
        setSize(100,100);

        layeredPane.setLayer(this, 5);
        layeredPane.add(this);
        layeredPane.revalidate();
        layeredPane.setVisible(true);
    }

    public void clear() {
        layeredPane.remove(this);
        layeredPane.revalidate();
    }

}
