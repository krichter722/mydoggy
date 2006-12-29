package org.noos.xing.mydoggy;

import java.awt.*;

/**
 * This interface let you modify ui behaviours of a content when a <code>DesktopContentManagerUI</code> is used
 * as current <code>ContentManagerUI</code>.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.1.0
 * @see DesktopContentManagerUI
 */
public interface DesktopContentUI extends ContentUI {

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
     * @since 1.1.0
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
     * @since 1.1.0
     */
    Point getLocation();

    /**
     * Sets the content frame size.
     *
     * @param width  the new width of this component in pixels
     * @param height the new height of this component in pixels
     * @see #getSize
     * @since 1.1.0
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
     * @since 1.1.0
     */
    Dimension getSize();

    /**
     * Returns whether the <code>DesktopContentUI</code> is currently iconified.
     *
     * @return <code>true</code> if this content frame is iconified
     * @see #setIconified(boolean)
     * @since 1.1.0
     */
    boolean isIconified();

    /**
     * Iconifies or de-iconifies this content frame,
     *
     * @param iconified a boolean, where <code>true</code> means to iconify this content frame and
     *                  <code>false</code> means to de-iconify it.
     * @see #isIconified()
     * @since 1.1.0
     */
    void setIconified(boolean iconified);

}
