package org.noos.xing.yasaf.view;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ViewContainer {

    Container getContainer();

    void plugView(View view);
}
