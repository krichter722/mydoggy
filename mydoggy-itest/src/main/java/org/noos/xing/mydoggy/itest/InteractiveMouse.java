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

    void moveTo(int x, int y);

    void moveTo(String componentName);

    void moveTo(String componentName, int offsetX, int offsetY);

    void press(Type type);

    void release();

    void release(Type type);

    void click(Type type);

    void wheel(int amount);

}
