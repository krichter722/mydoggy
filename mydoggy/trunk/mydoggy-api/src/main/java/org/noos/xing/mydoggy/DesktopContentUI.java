package org.noos.xing.mydoggy;

import java.awt.*;

/**
 * TODO
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface DesktopContentUI {

    /**
     * Sets the content frame location.
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
     * Gets the location of the content frame in the form of a
     * point specifying the component's top-left corner.
     * The location will be relative to the system coordinate space.
     *
     * @return an instance of <code>Point</code> representing the top-left corner of the component's bounds in
     *         the system coordinate space.
     * @see #setLocation
     */
    Point getLocation();

    /**
     * Sets the content frame size.
     *
     * @param width  the new width of this component in pixels
     * @param height the new height of this component in pixels
     * @see #getSize
     */
    void setSize(int width, int height);

    /**
     * Returns the size of the content frame in the form of a
     * <code>Dimension</code> object. The <code>height</code>
     * field of the <code>Dimension</code> object contains
     * this content frame's height, and the <code>width</code>
     * field of the <code>Dimension</code> object contains
     * this content frame's width.
     *
     * @return a <code>Dimension</code> object that indicates the
     *         size of this content frame.
     * @see #setSize
     */
    Dimension getSize();

    /**
     * Returns whether the <code>DesktopContentUI</code> is currently iconified.
     *
     * @return <code>true</code> if this content frame is iconified
     * @see #setIconified(boolean)
     */
    boolean isIconified();

    /**
     * Iconifies or de-iconifies this content frame,
     *
     * @param iconified a boolean, where <code>true</code> means to iconify this content frame and
     *                  <code>false</code> means to de-iconify it.
     * @see #isIconified()
     */
    void setIconified(boolean iconified);

}
