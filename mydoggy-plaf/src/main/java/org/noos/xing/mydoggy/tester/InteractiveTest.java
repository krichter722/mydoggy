package org.noos.xing.mydoggy.tester;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface InteractiveTest {

    Container getRootContainer();

    void interactiveText(InteractiveUI interactiveUI);

    void dispose();

}
