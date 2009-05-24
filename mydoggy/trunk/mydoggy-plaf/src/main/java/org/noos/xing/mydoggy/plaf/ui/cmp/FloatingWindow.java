package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.ToolWindow;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface FloatingWindow extends MultiSplitWindow<ToolWindow> {

    Window getWindow();


    String getName();

    void setName(String name);

    void setModal(boolean modal);

    void setAlwaysOnTop(boolean alwaysOnTop);

    void setUndecorated(boolean decorated);

    
    Container getContentPane();

    void setContentPane(Container container);


    void setVisible(boolean visible);

    boolean isVisible();

    void dispose();

    boolean isFocused();


    int getWidth();

    int getHeight();

    int getX();

    int getY();

    void setBounds(int x, int y, int width, int height);

    void setBounds(Rectangle lastBounds);

    Rectangle getBounds();

    void setSize(int width, int height);

    void setSize(Dimension size);

    void setLocation(Point location);

    void setResizable(boolean resizable);

    void importFrom(FloatingWindow oldWindow);

}
