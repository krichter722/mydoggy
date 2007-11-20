package org.noos.xing.mydoggy.itest;

import java.awt.geom.Ellipse2D;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ComponentAdapter {

    enum MouseButton {
        LEFT,
        RIGHT,
        CENTER
    }

    void setDelay(int delay);

    ComponentAdapter moveTo();

    ComponentAdapter moveToCenter();

    ComponentAdapter moveTo(int x, int y);

    ComponentAdapter move(Shape shape);

    ComponentAdapter press(MouseButton mouseButton);

    ComponentAdapter release();

    ComponentAdapter release(MouseButton mouseButton);

    ComponentAdapter click(MouseButton mouseButton);

    ComponentAdapter wheel(int amount);

    ComponentAdapter showTip(String message);
    
}
