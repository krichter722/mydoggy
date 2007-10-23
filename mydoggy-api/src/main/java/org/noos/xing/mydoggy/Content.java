package org.noos.xing.mydoggy;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.awt.*;
import java.io.Serializable;

/**
 * A content is a wrapper of a component decorated with some properties like
 * a title, an icon, etc. The visualization of a content depends on specific
 * platform implementation. A platform implementation can use a JTabbedPane
 * or a JDesktopPane for example.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.0.0
 */
public interface Content {

    /**
      * Return the content unique identifier.
      *
      * @return the content unique identifier.
     * @since 1.0.0
      */
    Object getKey();

    /**
     * Sets the title to <code>title</code> which can be <code>null</code>.
     *
     * @param title the title to be displayed for the content.
     * @see #getTitle()
     * @since 1.0.0
     */
    void setTitle(String title);

    /**
     * Returns the content title.
     *
     * @return the title.
     * @see #setTitle(String)
     * @since 1.0.0
     */
    String getTitle();

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
     * Sets the icon to <code>icon</code> which can be <code>null</code>.
     *
     * @param icon the icon to be displayed for the content.
     * @see #getIcon()
     * @since 1.0.0
     */
    void setIcon(Icon icon);

    /**
     * Returns the content icon.
     *
     * @return the icon.
     * @see #setIcon(javax.swing.Icon)
     * @since 1.0.0
     */
    Icon getIcon();

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
     * Sets whether or not the content is selected.
     *
     * @param selected whether or not the content should be selected.
     * @see #isSelected()
     * @since 1.0.0
     */
    void setSelected(boolean selected);

    /**
     * Returns whether or not the content is currently selected.
     *
     * @return true if the content is selected;
     *         false otherwise
     * @see #setSelected(boolean)
     * @since 1.0.0
     */
    boolean isSelected();

    /**
     * Sets the component to <code>component</code>.
     *
     * @param component the component for the content
     * @see #getComponent()
     * @since 1.0.0
     */
    void setComponent(Component component);

    /**
     * Returns the component.
     *
     * @return the component.
     * @see #setComponent(java.awt.Component)
     * @since 1.0.0
     */
    Component getComponent();

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
     * @see #setComponent(java.awt.Component)
     * @since 1.0.0
     */
    JPopupMenu getPopupMenu();

    /**
     * This method is used to detach a content from the main window. When a content is detached
     * it is showed into a separete window.
     * @param detached true to detach the content, false to reattach the content into the main window
     * @since 1.0.0
     */
    void setDetached(boolean detached);

    /**
     * Returns whether or not the content is currently detached.
     *
     * @return true if the content is detached;
     *         false otherwise
     * @see #setDetached(boolean)
     * @since 1.0.0
     */
    boolean isDetached();

    /**
     * Sets the keyboard mnemonic for accessing this content.
     * The mnemonic is the key which when combined with the look and feel's
     * mouseless modifier (usually Alt) will activate this content by selecting it.
     * <p>
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
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of this class, including the
     * following:
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
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be added
     * @see #getPropertyChangeListeners()
     * @see #removePropertyChangeListener
     * @since 1.0.0
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a PropertyChangeListener from the listener list. This method
     * should be used to remove PropertyChangeListeners that were registered
     * for all bound properties of this class.
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be removed
     * @see #addPropertyChangeListener
     * @see #getPropertyChangeListeners
     * @since 1.0.0
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Returns an array of all the property change listeners
     * registered on this content.
     *
     * @return all of this content's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @since 1.0.0
     */
    PropertyChangeListener[] getPropertyChangeListeners();

    ToolWindow getToolWindow();
}
