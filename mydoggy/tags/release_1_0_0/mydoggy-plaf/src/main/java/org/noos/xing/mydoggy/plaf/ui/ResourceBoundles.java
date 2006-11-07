package org.noos.xing.mydoggy.plaf.ui;

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

        resourceBundle = ResourceBundle.getBundle("org/noos/xing/mydoggy/plaf/ui/messages/messages", locale);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}
