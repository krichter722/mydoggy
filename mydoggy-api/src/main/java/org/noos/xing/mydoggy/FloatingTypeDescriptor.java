package org.noos.xing.mydoggy;

import java.awt.*;

/**
 * This interface is used to modify the behaviours of FLOATING type.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowType
 */
public interface FloatingTypeDescriptor extends ToolWindowTypeDescriptor {

    /**
     * Sets the window location. This location is used the first time the window becomes visible.
     * The top-left corner of the new location
     * is specified by the <code>x</code> and <code>y</code>
     * parameters in the system coordinate space.
     *
     * @param x the <i>x</i>-coordinate of the new location's 
     *          top-left corner in the system's coordinate space
     * @param y the <i>y</i>-coordinate of the new location's
     *          top-left corner in the system's coordinate space
     * @see #getLocation
     */
    void setLocation(int x, int y);

    /**
     * Gets the location of the window in the form of a
     * point specifying the component's top-left corner.
     * The location will be relative to the system coordinate space.
     *
     * @return an instance of <code>Point</code> representing the top-left corner of the component's bounds in
     * the system coordinate space.
     * @see #setLocation
     */
    Point getLocation();

    /**
     * Sets the window size. This size is used the first time the window becomes visible.
     *
     * @param width the new width of this component in pixels
     * @param height the new height of this component in pixels
     * @see #getSize
     */
    void setSize(int width, int height);

    /**
     * Returns the size of the window in the form of a
     * <code>Dimension</code> object. The <code>height</code>
     * field of the <code>Dimension</code> object contains
     * this window's height, and the <code>width</code>
     * field of the <code>Dimension</code> object contains
     * this window's width.
     *
     * @return a <code>Dimension</code> object that indicates the
     *          size of this window.
     * @see #setSize
     */
    Dimension getSize();

    void setModal(boolean modal);

    boolean isModal();


    void setTransparentMode(boolean transparentMode);

    boolean isTransparentMode();

    void setTransparentRatio(float ratio);

    float getTransparentRatio();

    int getTransparentDelay();

    void setTransparentDelay(int delay);

}
