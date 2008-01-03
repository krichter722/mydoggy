package org.noos.xing.mydoggy.itest.impl;

import java.awt.*;
import java.util.Vector;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class UIUtil {

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
