package org.noos.xing.mydoggy.itest.impl;

import java.awt.*;
import java.util.Vector;

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

    public static Vector<Window> getTopContainers(String name) {
        Vector<Window> containers = new Vector<Window>();

        Frame frames[] = Frame.getFrames();
        for (Frame frame : frames) {
            Window[] windows = frame.getOwnedWindows();

            for (Window window : windows) {
                if (window.getName() != null && window.getName().equals(name))
                    containers.add(window);
            }

            if (!containers.contains(frame)) {
                containers.add(frame);
            }
        }
        return containers;
    }
}
