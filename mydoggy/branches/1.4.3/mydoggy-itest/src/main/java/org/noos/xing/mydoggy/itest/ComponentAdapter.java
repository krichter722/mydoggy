package org.noos.xing.mydoggy.itest;

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

    enum Location {
        RIGHT,
        LEFT,
        BOTTOM,
        TOP
    }

    void setDelay(int delay);

    ComponentAdapter moveTo();

    ComponentAdapter moveToCenter();

    ComponentAdapter moveTo(int x, int y);

    ComponentAdapter moveTo(Location location);

    ComponentAdapter move(Shape shape);

    ComponentAdapter press(MouseButton mouseButton);

    ComponentAdapter release();

    ComponentAdapter release(MouseButton mouseButton);

    ComponentAdapter click(MouseButton mouseButton);

    ComponentAdapter wheel(int amount);

    ComponentAdapter showTip(String message);
    
}
