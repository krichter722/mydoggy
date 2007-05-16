package org.noos.xing.mydoggy.tester;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface InteractiveUI {

    void moveMouseTo(String componentName);

    void moveMouseTo(String componentName, int offsetX, int offsetY);

    void pressMouseLeftButton();

    void releaseMouseLeftButton();

    boolean ask(String message);

    void delay(int millis);

    void setRootContainer(Container rootContainer);

    void moveMouse(Point to);

    void assertTrue(String s, boolean b);

    void mouseLeftClick();

    Frame getFrame(String name);
}
