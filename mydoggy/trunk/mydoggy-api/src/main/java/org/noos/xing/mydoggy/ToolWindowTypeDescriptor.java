package org.noos.xing.mydoggy;

/**
 * This is a markup interface for all ToolWindowTypeDescriptor.
 * A ToolWindowTypeDescriptor is an interface to modify the behaviours of
 * a specific tool window type.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowType
 * @see org.noos.xing.mydoggy.DockedTypeDescriptor
 * @see org.noos.xing.mydoggy.FloatingTypeDescriptor
 * @since 1.0.0
 */
public interface ToolWindowTypeDescriptor extends Observable {

    /**
     * Returns the type whom this descriptor is for. 
     *
     * @return the type whom this descriptor is for.
     * @since 1.5.0
     */
    ToolWindowType getType();

    /**
     * Enables or disables animations, depending on the value of the
     * parameter <code>animating</code>.
     *
     * @param animating If <code>true</code>, animations are
     *                  enabled; otherwise animations are disabled.
     * @since 1.3.0
     */
    void setAnimating(boolean animating);

    /**
     * Returns whether the animations are enabled.
     *
     * @return true is the animations are enabled, false otherwise.
     * @since 1.3.0
     */
    boolean isAnimating();

    /**
     * Sets if the toolwindow id has to be shown on the title bar when the toolwindow is docked (that is type == DOCKED).
     *
     * @param idVisibleOnTitleBar <code>true</code> if the toolwindow id has to be shown on the title bar.
     * tool has type ToolWindowType.DOCKED.<br><code>false</code> otherwise.
     * @see #isIdVisibleOnTitleBar()
     * @since 1.4.0
     */
    void setIdVisibleOnTitleBar(boolean idVisibleOnTitleBar);

    /**
     * Returns the "idVisibleOnTitleBar" property value.
     * @return <code>true</code> if the toolwindow id has to be shown on the title bar.
     * tool has type ToolWindowType.DOCKED.<br>
     *         <code>false</code> otherwise.
     * @see #setIdVisibleOnTitleBar(boolean)
     * @since 1.4.0
     */
    boolean isIdVisibleOnTitleBar();

    /**
     * This method is used to set the autoHide property for the tool.
     *
     * @param autoHide <code>true</code> to hide the tool when the tool losts focus;
     *                 <code>false</code> to make inactive the tool when the tool losts focus.
     * @see #isAutoHide() ()
     * @since 1.4.2
     */
    void setAutoHide(boolean autoHide);

    /**
     * Returns the autoHide property value of the tool.
     *
     * @return autoHide property value.
     * @see #setAutoHide(boolean)
     * @since 1.4.2
     */
    boolean isAutoHide();

    /**
     * Enables or disables this mode, depending on the value of the
     * parameter <code>enabled</code>. An enabled mode can used by user.
     *
     * @param  enabled   If <code>true</code>, this mode is
     *         enabled; otherwise this mode is disabled
     * @see #isEnabled
     * @since 1.4.2
     */
    void setEnabled(boolean enabled);

    /**
     * Determines whether this mode is enabled.
     * 
     * @return <code>true</code> if the mode is enabled,
     *          <code>false</code> otherwise
     * @see #setEnabled
     * @since 1.4.2
     */
    boolean isEnabled();

    /**
     * Sets if mydoggy must hide the representative button when the tool become visible and
     * the tool has type ToolWindowType.DOCKED.
     *
     * @param hideRepresentativeButtonOnVisible
     *         <code>true</code> if mydoggy must hide the representative button when the tool become visible and the
     *         tool has type ToolWindowType.DOCKED.<br>
     *         <code>false</code> otherwise.
     * @since 1.5.0
     */
    void setHideRepresentativeButtonOnVisible(boolean hideRepresentativeButtonOnVisible);

    /**
     * Returns the "hideRepresentativeButtonOnVisible" property value.
     *
     * @return <code>true</code> if mydoggy must hide the representative button when the tool become visible and the
     *         tool has type ToolWindowType.DOCKED.<br>
     *         <code>false</code> otherwise.
     * @since 1.5.0
     */
    boolean isHideRepresentativeButtonOnVisible();

    /**
     * TODO:...
     * @param id
     * @return
     * @since 1.5.0
     */
    ToolWindowAction getToolWindowAction(String id);

    void addToolWindowAction(ToolWindowAction toolWindowAction);

    void addToolWindowAction(ToolWindowAction toolWindowAction, int index);

    ToolWindowAction[] getToolWindowActions();

    void removeToolWindowAction(String id);


}
