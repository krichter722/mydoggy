package org.noos.xing.mydoggy;

import javax.swing.*;

/**
 * This interface is used to modify all toolwindow anchor related properties.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.4.2
 * @see org.noos.xing.mydoggy.ToolWindowAnchor
 * @see org.noos.xing.mydoggy.ToolWindowManager#getToolWindowBar(ToolWindowAnchor) 
 */
public interface ToolWindowBar extends Observable {

    /**
     * Returns the toolwindow manager related to this bar.
     * 
     * @return the toolwindow manager related to this bar.
     */
    ToolWindowManager getToolWindowManager();

    /**
     * Returns the reference anchor.
     * 
     * @return the reference anchor.
     * @since 1.4.2
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     */
    ToolWindowAnchor getAnchor();

    /**
     * Sets the size of the border the separate a docked toolwindow from the contenManager for a
     * specific anchor.
     *
     * @param size   an integer giving the size of the divider in pixels
     * @see #getDividerSize()
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @since 1.4.2
     */
    void setDividerSize(int size);

    /**
     * Returns the size of the border the separate a docked toolwindow from the contenManager for the
     * specified anchor.
     *
     * @return the size of the border
     * @see #setDividerSize(int)
     * @since 1.4.2
     */
    int getDividerSize();

    /**
     * Sets the aggregate mode for tools on the this anchor.
     *
     * @param enable aggregate mode value.
     * @since 1.4.2
     */
    void setAggregateMode(boolean enable);

    /**
     * Returns aggregate mode value for this anchor.
     *
     * @return aggregate mode value.
     * @since 1.4.2
     */
    boolean isAggregateMode();

    /**
     * Sets the popup menu to <code>popupMenu</code>.
     *
     * @param popupMenu the popup menu for the bar.
     * @see #getPopupMenu()
     * @since 1.4.2
     */
    void setPopupMenu(JPopupMenu popupMenu);

    /**
     * Returns the popup menu.
     *
     * @return the popup menu.
     * @see #setPopupMenu(javax.swing.JPopupMenu)
     * @since 1.4.2
     */
    JPopupMenu getPopupMenu();

    /**
     * Sets the length in pixel of the bar.
     *
     * @param length length in pixel of the bar.
     * @since 1.4.2
     */
    void setLength(int length);

    /**
     * Returns the length in pixel of the bar.
     *
     * @return the length in pixel of the bar
     * @since 1.4.2
     */
    int getLength();

    /**
     * Returns an array of the toolwindows attached on this bar.
     *
     * @return an array of the toolwindows attached on this bar.
     * @since 1.4.2
     */
    ToolWindow[] getToolWindows();

    /**
     * TODO
     * @see #setVisible(boolean)
     */
    boolean isVisible();

    /**
     * TODO:
     * @since 1.5.0
     */
    void setVisible(boolean visible);

    /**
     * Returns the value of visible property.
     *
     * @return <code>false</code> if the last call to <code>setVisible</code> was with a false, <code>true</code> otherwise.
     * @since 1.5.0
     * @see #setToolsVisible(boolean)
     */
    boolean areToolsVisible();

    /**
     * This method is used to store and restore the toolwindows visible on this bar.
     * So when you call <code>setVisible(false)</code> the current state of the bar is stored and all docked toolwindows
     * visible on this bar are hidden.
     * When you after call <code>setVisible(true)</code> the state of the bar is restored to the previous saved state.
     *
     * @param visible <code>false</code> to store the state of the bar and hide all docked toolwindows. <code>true</code>
     * to restore the previous saved state if any.
     * @since 1.5.0
     * @see #areToolsVisible() 
     */
    void setToolsVisible(boolean visible);
}
