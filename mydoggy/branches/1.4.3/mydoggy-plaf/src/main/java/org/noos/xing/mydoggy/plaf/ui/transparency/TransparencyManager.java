package org.noos.xing.mydoggy.plaf.ui.transparency;

import java.awt.*;

/**
 * 
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface TransparencyManager<E extends Component> {

    /**
     * Returns <tt>true</tt> if the manager can manage the transparency, <tt>false</tt> otherwise. 
     *
     * @return <tt>true</tt> if the manager can manage the transparency, <tt>false</tt> otherwise.
     */
    boolean isServiceAvailable();

    /**
     * Sets the transparency value for the spicified component.
     *
     * @param component the component to which assign the transparency.
     * @param transparency the transparency value. The valid range is [0.0 , 1.0].
     */
    void setAlphaModeRatio(E component, float transparency);

    /**
     * Returns <tt>true</tt> if the transparency value of the <code>component</code> if higher than zero,
     * <tt>false</tt> otherwise.
     * @param component the component for which retrieve the transparency value.
     * @return <tt>true</tt> if the transparency value of the <code>component</code> if higher than zero,
     * <tt>false</tt> otherwise.
     */
    boolean isAlphaModeEnabled(E component);

}
