package org.noos.xing.mydoggy.itest;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface InteractiveUI {

    InteractiveMouse getInteractiveMouse();

    InteractiveAssertor getInteractiveAssertor();


    void delay(int millis);

    void importRoot(String rootName);

    Container[] getRoots();
}
