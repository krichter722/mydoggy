package org.noos.xing.mydoggy.plaf.ui;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ResourceBundleManager {
    private static final ResourceBundleManager INSTANCE = new ResourceBundleManager();

    public static ResourceBundleManager getInstance() {
        return INSTANCE;
    }

    private ResourceBundle resourceBundle;
    private ResourceBundle userResourceBundle;

    public void init(Locale locale) {
        this.resourceBundle = initResourceBundle(locale,
                                            "org/noos/xing/mydoggy/plaf/ui/messages/messages",
                                            ResourceBundleManager.class.getClassLoader());
	}

    public void initUserBundle(Locale locale, String bundle, ClassLoader classLoader) {
        this.userResourceBundle = initResourceBundle(locale, bundle, classLoader);

    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public ResourceBundle getUserResourceBundle() {
        return userResourceBundle;
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }

    public String getUserString(String key) {
        return (userResourceBundle != null) ? userResourceBundle.getString(key) : key;
    }

    protected ResourceBundle initResourceBundle(Locale locale, String bundle, ClassLoader classLoader) {
        ResourceBundle result;
        if (locale == null)
            locale = Locale.getDefault();

		try {
            if (classLoader == null)
                result = ResourceBundle.getBundle(bundle, locale);
            else
                result = ResourceBundle.getBundle(bundle, locale, classLoader);
		} catch (Throwable e) {
			e.printStackTrace();
            
            result = new ResourceBundle() {
				protected Object handleGetObject(String key) {
					return key;
				}

				public Enumeration<String> getKeys() {
					return new Enumeration<String>() {
						public boolean hasMoreElements() {
							return false;
						}

						public String nextElement() {
							return null;
						}
					};
				}
			};
		}
        return result;
    }

}
