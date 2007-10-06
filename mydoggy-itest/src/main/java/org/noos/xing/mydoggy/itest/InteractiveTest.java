package org.noos.xing.mydoggy.itest;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface InteractiveTest {

    String getName();

    String getDescription();

    Container setup();

    void dispose();

    void execute(InteractiveUI interactiveUI);

}
