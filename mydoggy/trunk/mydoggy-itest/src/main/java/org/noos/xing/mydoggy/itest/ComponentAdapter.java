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

    ComponentAdapter moveTo();

    ComponentAdapter moveToCenter();

    ComponentAdapter moveToCenter(int delay);

    ComponentAdapter moveTo(int x, int y);

    ComponentAdapter move(Shape shape);

    ComponentAdapter press(MouseButton mouseButton);

    ComponentAdapter press(MouseButton mouseButton, int delay);

    ComponentAdapter release();

    ComponentAdapter release(int delay);

    ComponentAdapter release(MouseButton mouseButton, int delay);

    ComponentAdapter click(MouseButton mouseButton);

    ComponentAdapter click(MouseButton mouseButton, int delay);

    ComponentAdapter wheel(int amount);

    ComponentAdapter wheel(int amount, int delay);

    ComponentAdapter showTip(String message, int delay);
}