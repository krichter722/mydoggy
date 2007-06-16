package org.noos.xing.mydoggy.plaf.ui;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ResourceBoundles {
    private static ResourceBundle resourceBundle;

    public static void initResourceBoundles(Locale locale) {
        if (locale == null)
            locale = Locale.getDefault();
		try {
			resourceBundle = ResourceBundle.getBundle("org/noos/xing/mydoggy/plaf/ui/messages/messages",
													  locale,
													  ResourceBoundles.class.getClassLoader());
		} catch (Throwable e) {
			e.printStackTrace();
			resourceBundle = new ResourceBundle() {
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
	}

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}
