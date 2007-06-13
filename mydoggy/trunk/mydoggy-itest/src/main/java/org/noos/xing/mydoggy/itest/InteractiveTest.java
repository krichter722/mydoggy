package org.noos.xing.mydoggy.itest;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface InteractiveTest {

    Container setup();

    void dispose();

    void interactiveTest(InteractiveUI interactiveUI);

}
