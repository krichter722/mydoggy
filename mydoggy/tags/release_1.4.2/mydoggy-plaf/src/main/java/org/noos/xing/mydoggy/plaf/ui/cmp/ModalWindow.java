package org.noos.xing.mydoggy.plaf.ui.cmp;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ModalWindow {

    Window getWindow();

    void setModal(boolean modal);

    Container getContentPane();

    void setSize(int width, int height);

    void setSize(Dimension size);

    void setLocation(Point location);

    void setBounds(Rectangle lastBounds);

    Rectangle getBounds();

    void setVisible(boolean visible);

    boolean isVisible();

    boolean isFocused();

    void setName(String name);

    void setContentPane(Container container);

    int getWidth();

    int getHeight();

    int getX();

    int getY();

    void setBounds(int x, int y, int width, int height);
}
