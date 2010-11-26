package org.noos.xing.mydoggy.scenario;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface Scenario {

    String getName();

    String getDescription();

    String getSource();

    Window launch();

}
