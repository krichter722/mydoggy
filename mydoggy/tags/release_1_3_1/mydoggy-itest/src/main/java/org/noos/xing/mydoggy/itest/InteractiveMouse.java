package org.noos.xing.mydoggy.itest;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface InteractiveMouse {

    enum Type {
        LEFT,
        RIGHT,
        CENTER
    }

    InteractiveMouse moveTo(int x, int y);

    InteractiveMouse moveTo(String componentName);

    InteractiveMouse moveTo(String componentName, int delay);

    InteractiveMouse moveTo(String componentName, int offsetX, int offsetY);

    InteractiveMouse moveTo(String componentName, int offsetX, int offsetY, int delay);

    InteractiveMouse press(Type type);

    InteractiveMouse release();

    InteractiveMouse release(int delay);

    InteractiveMouse release(Type type);

    InteractiveMouse click(Type type);

    InteractiveMouse click(Type type, int delay);

    InteractiveMouse click(String componentName, Type type);

    InteractiveMouse click(String componentName, Type type, int delay);

    InteractiveMouse wheel(int amount);

}
