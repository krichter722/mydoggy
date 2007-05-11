package org.noos.xing.mydoggy;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowTab {

    String getTitle();

    void setTitle(String title);

    Component getComponent();

    void setComponent(Component component);

}
