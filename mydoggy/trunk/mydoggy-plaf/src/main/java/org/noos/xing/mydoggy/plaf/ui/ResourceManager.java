package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This interface is used to customize not only icons, colors and internationalization but also
 * ui components creation with relative customization and transparency manager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.1
 */
public interface ResourceManager {

    /**
     * Returns an instance of class <code>clazz</code> using passed <code>args</code>.
     *
     * @param clazz the class of the instance to be returned.
     * @param args the arguments needed to create the instance.
     * @return an instance of class <code>clazz</code> using passed <code>args</code>.
     * @since 1.3.2
     */
    <T> T createInstance(Class<T> clazz, Object... args);

    /**
     * Create the component using the rule specified by the key param.
     *
     * @param key     key whose associated rule is used to create the component.
     * @param manager the toolwindow manager
     * @param args    any arguments used to create the component.
     * @return the component created using the rule specified by the key param.
     * @since 1.3.1
     */
    Component createComponent(String key, ToolWindowManager manager, Object... args);

    /**
     * Create the component ui using the rule specified by the key param.
     *
     * @param key     key whose associated rule is used to create the component.
     * @param manager the toolwindow manager
     * @param args    any arguments used to create the component ui.
     * @return the component ui created using the rule specified by the key param.
     * @since 1.3.1
     */
    ComponentUI createComponentUI(String key, ToolWindowManager manager, Object... args);

    /**
     * Apply the customization using the rule specified by the key param.
     *
     * @param key       key whose associated rule is used to customize the component.
     * @param component the component to be customized.
     * @param args      any arguments used to customize the component.
     * @return the component customized.
     * @since 1.3.1
     */
    Component applyCustomization(String key, Component component, Object... args);

    /**
     * Returns the icon to which this manager maps the specified id.  Returns
     * <tt>null</tt> if the manager contains no mapping for this id.
     *
     * @param id id whose associated icon is to be returned.
     * @return the icon to which this manager maps the specified id, or
     *         <tt>null</tt> if the manager contains no mapping for this id.
     * @since 1.3.1
     */
    Icon getIcon(String id);

    /**
     * Associates the specified icon with the specified id. If the manager previously
     * contained a mapping for this id, the old icon is replaced by the specified icon.
     *
     * @param id   id with which the specified icon is to be associated.
     * @param icon icon to be associated with the specified id.
     * @return previous icon associated with specified id, or <tt>null</tt>
     *         if there was no mapping for id.
     * @since 1.3.1
     */
    Icon putIcon(String id, Icon icon);

    /**
     * Returns the color to which this manager maps the specified id.  Returns
     * <tt>null</tt> if the manager contains no mapping for this id.
     *
     * @param id id whose associated color is to be returned.
     * @return the color to which this manager maps the specified id, or
     *         <tt>null</tt> if the manager contains no mapping for this id.
     * @since 1.3.1
     */
    Color getColor(String id);

    /**
     * Associates the specified color with the specified id. If the manager previously
     * contained a mapping for this id, the old color is replaced by the specified color.
     *
     * @param id    id with which the specified color is to be associated.
     * @param color color to be associated with the specified id.
     * @return previous color associated with specified id, or <tt>null</tt>
     *         if there was no mapping for id.
     * @since 1.3.1
     */
    Color putColor(String id, Color color);

    /**
     * Returns the transparency manager for the windows used to manage transparency of toolwindows
     * of FLOATING or FLOATING_FREE type.
     *
     * @return the transparency manager for the windows.
     * @since 1.3.1
     */
    TransparencyManager<Window> getTransparencyManager();

    /**
     * Sets the transparency manager for the windows used to manage transparency of toolwindows
     * of FLOATING or FLOATING_FREE type.
     *
     * @param transparencyManager the transparency manager for the windows.
     * @since 1.3.1
     */
    void setTransparencyManager(TransparencyManager<Window> transparencyManager);

    /**
     * Sets the locale of this manager used for the internationalization of the relative toolwindow manager.
     *
     * @param locale the locale to become this manager's locale
     * @since 1.3.1
     */
    void setLocale(Locale locale);

    /**
     * Sets the bundle used for the internationalization of special strings like the the toolwindow id.
     *
     * @param locale      the locale for which a resource bundle is desired
     * @param bundle      the base name of the resource bundle, a fully qualified class name
     * @param classLoader the class loader from which to load the resource bundle
     * @see #getUserResourceBundle()
     * @since 1.3.1
     */
    void setUserBundle(Locale locale, String bundle, ClassLoader classLoader);

    /**
     * Sets the bundle used for the internationalization of special strings like the the toolwindow id.
     *
     * @param userBundle the ResourceBundle
     * @since 1.3.1
     */
    void setUserBundle(ResourceBundle userBundle);

    /**
     * Returns the resource bundle used for the internationalization of mydoggy.
     *
     * @return the resource bundle used for the internationalization of mydoggy.
     * @since 1.3.1
     */
    ResourceBundle getResourceBundle();

    /**
     * Returns the resource bundle used for the internationalization of special strings like the the toolwindow id.
     *
     * @return the resource bundle used for the internationalization of special strings like the the toolwindow id.
     * @since 1.3.1
     */
    ResourceBundle getUserResourceBundle();

    /**
     * This method is equivalent to <code>getResourceBundle().getString(key)</code>
     *
     * @param key the key for the desired string
     * @return the string for the given key
     * @since 1.3.1
     */
    String getString(String key);

    /**
     * This method is equivalent to <code>getUserResourceBundle().getString(key)</code>
     *
     * @param key the key for the desired string
     * @return the string for the given key
     * @since 1.3.1
     */
    String getUserString(String key);

}