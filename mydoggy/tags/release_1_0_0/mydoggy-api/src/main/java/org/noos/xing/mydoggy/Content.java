package org.noos.xing.mydoggy;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.awt.*;

/**
 * A content is a wrapper of a component decorated with some properties like
 * a title, an icon, etc. The visualization of a content depends on specific
 * platform implementation. A platform implementation can use a JTabbedPane
 * or a JDesktopPane for example.
 * 
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface Content {

    /**
      * Return the content unique identifier.
      *
      * @return the content unique identifier.
      */
    Object getKey();

    /**
     * Sets the title to <code>title</code> which can be <code>null</code>.
     *
     * @param title the title to be displayed for the content.
     * @see #getTitle()
     */
    void setTitle(String title);

    /**
     * Returns the content title.
     *
     * @return the title.
     * @see #setTitle(String)
     */
    String getTitle();

    /**
     * Sets the foreground color <code>foreground</code> which can be
     * <code>null</code>, in which case the content's foreground color
     * will default to the foreground color of this <code>Content</code>.
     *
     * @param foreground the color to be displayed as the content's foreground
     * @see #getForeground
     */
    public void setForeground(Color foreground);

    /**
     * Returns the content foreground color.
     *
     * @return the <code>Color</code> of the content foreground.
     * @see #setForeground
     */
    public Color getForeground();

    /**
     * Sets the icon to <code>icon</code> which can be <code>null</code>.
     *
     * @param icon the icon to be displayed for the content.
     * @see #getIcon()
     */
    void setIcon(Icon icon);

    /**
     * Returns the content icon.
     *
     * @return the icon.
     * @see #setIcon(javax.swing.Icon)
     */
    Icon getIcon();

    /**
     * Sets the disabled icon to <code>icon</code> which can be <code>null</code>.
     *
     * @param disabledIcon the icon to be displayed in the content when disabled.
     * @see #getDisabledIcon()
     */
    void setDisabledIcon(Icon disabledIcon);

    /**
     * Returns the content disabled icon.
     *
     * @return the disabled icon.
     * @see #setDisabledIcon(javax.swing.Icon)
     */
    Icon getDisabledIcon();

    /**
     * Sets the tool tip text to <code>toolTipText</code> which
     * can be <code>null</code>.
     *
     * @param toolTipText the tool tip text to be displayed for the content.
     * @see #getToolTipText()
     */
    void setToolTipText(String toolTipText);

    /**
     * Returns the content tooltip text.
     *
     * @return a string containing the tool tip text.
     * @see #setToolTipText(String)
     */
    String getToolTipText();

    /**
     * Sets whether or not the content is enabled.
     *
     * @param enabled whether or not the content should be enabled.
     * @see #isEnabled()
     */
    void setEnabled(boolean enabled);

    /**
     * Returns whether or not the content is currently enabled.
     *
     * @return true if the content is enabled;
     *         false otherwise
     * @see #setEnabled(boolean)
     */
    boolean isEnabled();

    /**
     * Sets whether or not the content is selected.
     *
     * @param selected whether or not the content should be selected.
     * @see #isSelected()
     */
    void setSelected(boolean selected);

    /**
     * Returns whether or not the content is currently selected.
     *
     * @return true if the content is selected;
     *         false otherwise
     * @see #setSelected(boolean)
     */
    boolean isSelected();

    /**
     * Sets the component to <code>component</code>.
     *
     * @param component the component for the content
     * @see #getComponent()
     */
    void setComponent(Component component);

    /**
     * Returns the component.
     *
     * @return the component.
     * @see #setComponent(java.awt.Component)
     */
    Component getComponent();

    /**
     * Sets the popup menu to <code>popupMenu</code>.
     *
     * @param popupMenu the popup menu for the content.
     * @see #getPopupMenu()
     */
    void setPopupMenu(JPopupMenu popupMenu);

    /**
     * Returns the popup menu.
     *
     * @return the popup menu.
     * @see #setComponent(java.awt.Component)
     */
    JPopupMenu getPopupMenu();

    /**
     * This method is used to detach a content from the main window. When a content is detached
     * it is showed into a separete window.
     * @param detached true to detach the content, false to reattach the content into the main window
     */
    void setDetached(boolean detached);

    /**
     * Returns whether or not the content is currently detached.
     *
     * @return true if the content is detached;
     *         false otherwise
     * @see #setDetached(boolean)
     */
    boolean isDetached();

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
     */
    PropertyChangeListener[] getPropertyChangeListeners();

}