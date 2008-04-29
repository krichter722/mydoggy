package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindowType;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface TitleBarButtons extends ComponentWrapper {

    Component getFocusable();

    void setType(ToolWindowType type);

}
