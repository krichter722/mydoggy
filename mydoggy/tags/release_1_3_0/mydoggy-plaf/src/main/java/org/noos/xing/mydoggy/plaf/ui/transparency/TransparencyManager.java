package org.noos.xing.mydoggy.plaf.ui.transparency;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface TransparencyManager<E extends Component> {

    boolean isServiceAvailable();

    void setAlphaModeRatio(E component, float transparency);

    boolean isAlphaModeEnabled(E component);

}
