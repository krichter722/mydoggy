package org.noos.xing.mydoggy;

import javax.swing.*;
import java.awt.*;

/**
 * A content is a wrapper of a component decorated with some properties like
 * a title, an icon, etc. The visualization of a content depends on specific
 * platform implementation. A platform implementation can use a JTabbedPane
 * or a JDesktopPane for example.
 * A PropertyChangeEvent is fired for the following properties:
 * <ul>
 * <li>this content's title ("title")</li>
 * <li>this content's foreground ("foreground")</li>
 * <li>this content's component ("component")</li>
 * <li>this content's selected status ("selected")</li>
 * <li>this content's enable status ("enabled")</li>
 * <li>this content's icon ("icon")</li>
 * <li>this content's disabledIcon ("disabledIcon")</li>
 * <li>this content's popupMenu ("popupMenu")</li>
 * <li>this content's detached ("detached")</li>
 * <li>this content's toolTipText ("toolTipTexttoolTipText")</li>
 * </ul>
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.0.0
 */
public interface Content extends Dockable<ContentManager> {

    /**
     * Sets the foreground color <code>foreground</code> which can be
     * <code>null</code>, in which case the content's foreground color
     * will default to the foreground color of this <code>Content</code>.
     *
     * @param foreground the color to be displayed as the content's foreground
     * @see #getForeground
     * @since 1.0.0
     */
    void setForeground(Color foreground);

    /**
     * Returns the content foreground color.
     *
     * @return the <code>Color</code> of the content foreground.
     * @see #setForeground
     * @since 1.0.0
     */
    Color getForeground();

    /**
     * Sets the disabled icon to <code>icon</code> which can be <code>null</code>.
     *
     * @param disabledIcon the icon to be displayed in the content when disabled.
     * @see #getDisabledIcon()
     * @since 1.0.0
     */
    void setDisabledIcon(Icon disabledIcon);

    /**
     * Returns the content disabled icon.
     *
     * @return the disabled icon.
     * @see #setDisabledIcon(javax.swing.Icon)
     * @since 1.0.0
     */
    Icon getDisabledIcon();

    /**
     * Sets the tool tip text to <code>toolTipText</code> which
     * can be <code>null</code>.
     *
     * @param toolTipText the tool tip text to be displayed for the content.
     * @see #getToolTipText()
     * @since 1.0.0
     */
    void setToolTipText(String toolTipText);

    /**
     * Returns the content tooltip text.
     *
     * @return a string containing the tool tip text.
     * @see #setToolTipText(String)
     * @since 1.0.0
     */
    String getToolTipText();

    /**
     * Sets whether or not the content is enabled.
     *
     * @param enabled whether or not the content should be enabled.
     * @see #isEnabled()
     * @since 1.0.0
     */
    void setEnabled(boolean enabled);

    /**
     * Returns whether or not the content is currently enabled.
     *
     * @return true if the content is enabled;
     *         false otherwise
     * @see #setEnabled(boolean)
     * @since 1.0.0
     */
    boolean isEnabled();

    /**
     * Sets the component to <code>component</code>.
     *
     * @param component the component for the content
     * @see #getComponent()
     * @since 1.0.0
     */
    void setComponent(Component component);

    /**
     * Sets the popup menu to <code>popupMenu</code>.
     *
     * @param popupMenu the popup menu for the content.
     * @see #getPopupMenu()
     * @since 1.0.0
     */
    void setPopupMenu(JPopupMenu popupMenu);

    /**
     * Returns the popup menu.
     *
     * @return the popup menu.
     * @see #setPopupMenu(javax.swing.JPopupMenu)
     * @since 1.0.0
     */
    JPopupMenu getPopupMenu();

    /**
     * Sets the keyboard mnemonic for accessing this content.
     * The mnemonic is the key which when combined with the look and feel's
     * mouseless modifier (usually Alt) will activate this content by selecting it.
     * <p/>
     * A mnemonic must correspond to a single key on the keyboard
     * and should be specified using one of the <code>VK_XXX</code>
     * keycodes defined in <code>java.awt.event.KeyEvent</code>.
     * Mnemonics are case-insensitive, therefore a key event
     * with the corresponding keycode would cause the button to be
     * activated whether or not the Shift modifier was pressed.
     *
     * @param mnemonic the key code which represents the mnemonic
     * @see #getMnemonic()
     * @since 1.3.1
     */
    void setMnemonic(int mnemonic);

    /**
     * Returns the keyboard mnemonic for accessing this content.
     *
     * @return the key code which represents the mnemonic;
     *         -1 if a mnemonic is not specified for this content.
     * @since 1.3.1
     */
    int getMnemonic();

    /**
     * Returns the content ui for this content based on the current installed ContentManagerUI.
     *
     * @return the content ui instance for this content.
     * @since 1.4.0
     */
    ContentUI getContentUI();

    /**
     * Returns the dockable that this tab is accomodating,  <code>null</code> if no dockable is accomodated.
     *
     * @return the dockable that this tab is accomodating,  <code>null</code> if no dockable is accomodated.
     * @see ContentManager#addContent(Dockable)
     * @since 1.4.0
     */
    Dockable getDockableDelegator();

    /**
     * Detach this content aggregating it on the passed content using the passed position.
     *
     * @param onContent the content used as relative position. It must be already visible.
     * @param onIndex the relative index used if onPosition == DEFAULT.
     * @param onPosition the position used to detach the content.
     * @throws IllegalArgumentException if onContent is null.
     * @since 1.5.0
     */
    void detach(Content onContent, int onIndex, AggregationPosition onPosition);

    /**
     * Detach this content aggregating it on the passed content using the passed position.
     *
     * @param onContent the content used as relative position. It must be already visible.
     * @param onPosition the position used to detach the content.
     * @throws IllegalArgumentException if onContent is null.
     * @since 1.5.0
     */
    void detach(Content onContent, AggregationPosition onPosition);

    /**
     * Detach this content aggregating it using the passed position into the content window that contains the refContent.
     *
     * @param refContent the refContent used to identify a content window.
     * @param onPosition the position used to detach the content.
     */
    void detachByReference(Content refContent, AggregationPosition onPosition);

    /**
     * Reattach a detached content using tha passed constraints.
     *
     * @param constraints the constraints used to reattach the content.
     */
    void reattach(Object... constraints);
}
