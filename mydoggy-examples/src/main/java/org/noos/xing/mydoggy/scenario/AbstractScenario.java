package org.noos.xing.mydoggy.scenario;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AbstractScenario implements Scenario {

    public String getName() {
        return "";
    }

    public String getDescription() {
        return "";
    }

    public String getSource() {
        return "";
    }

    public Window launch() {
        return null;  
    }
}
