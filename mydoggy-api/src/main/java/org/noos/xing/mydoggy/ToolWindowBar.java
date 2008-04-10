package org.noos.xing.mydoggy;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.4.2
 */
public interface ToolWindowBar extends Observable {

    /**
     * TODO
     * @return
     * @since 1.4.2
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

}
