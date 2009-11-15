package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.ToolWindow;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface FloatingLiveWindow extends MultiSplitWindow<ToolWindow> {
    
    void resetLayout();

    void setLayout();


    void mount();

    boolean unmount();


    boolean isVisible();

    boolean isValid();


    void setSize(int width, int height);

    void setSize(Dimension size);

    void setLocation(int x, int y);

    void setLocation(Point location);

    int getX();

    int getY();

    int getWidth();

    int getHeight();

    void setBounds(Rectangle bounds);

    Rectangle getBounds();

    void updateUI();

}
