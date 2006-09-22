package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.boundle.DefaultResourceBoundle;
import org.noos.xing.mydoggy.plaf.boundle.ResourceBoundle;

import java.util.Locale;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ResourceBoundles {
    private static ResourceBoundle resourceBoundle;

    public static void initResourceBoundles(Locale locale) {
        if (locale == null)
            locale = Locale.getDefault();

        DefaultResourceBoundle defaultResourceBoundle = new DefaultResourceBoundle("ToolWindowManager");
        defaultResourceBoundle.addResource("org/noos/xing/mydoggy/plaf/ui/messages/messages",
                                           locale,
                                           null);
        resourceBoundle = defaultResourceBoundle;
    }

    public static ResourceBoundle getResourceBoundle() {
        return resourceBoundle;
    }
}
