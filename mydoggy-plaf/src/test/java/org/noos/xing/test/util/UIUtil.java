package org.noos.xing.test.util;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class UIUtil {

    public static Component findComponentByName(Container root, String name) {
        if (root == null || name == null)
            return null;

        if (root.getName() != null && root.getName().equals(name))
            return root;

        for (Component component : root.getComponents()) {
            if (component.getName() != null && component.getName().equals(name))
                return component;

            if (component instanceof Container) {
                Component result = findComponentByName((Container) component, name);
                if (result != null)
                    return result;
            }
        }

        return null;
    }

}
