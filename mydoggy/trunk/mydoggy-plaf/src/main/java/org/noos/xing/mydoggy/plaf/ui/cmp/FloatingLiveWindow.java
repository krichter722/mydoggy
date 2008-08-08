package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.ToolWindow;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface FloatingLiveWindow {
    
    void resetLayout();

    void setLayout();

    void addDockable(ToolWindow toolWindow,
                            Component content);

    void addDockable(ToolWindow toolWindow,
                                                    Component content,
                                                    ToolWindow aggregationOnDockable,
                                                    AggregationPosition aggregationPosition);

    void removeDockable(ToolWindow toolWindow);

    void mount();

    void unmount();


    boolean isVisible();

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

}
