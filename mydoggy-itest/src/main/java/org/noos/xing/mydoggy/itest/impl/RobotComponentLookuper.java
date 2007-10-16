package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.ComponentAdapter;
import org.noos.xing.mydoggy.itest.ComponentFilter;
import org.noos.xing.mydoggy.itest.ComponentLookuper;
import org.noos.xing.mydoggy.plaf.ui.cmp.JModalWindow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RobotComponentLookuper implements ComponentLookuper {
    protected java.util.List<Container> roots;
    protected Robot robot;

    public RobotComponentLookuper(Robot robot, Container rootContainer) {
        this.roots = new ArrayList<Container>();
        this.roots.add(rootContainer);
        this.robot = robot;
    }

    public ComponentAdapter lookup() {
        return new RobotComponentAdapter(robot, roots.get(0));
    }

    public ComponentAdapter lookup(String componentName) {
        return lookup(new NamedComponentFilter(componentName));
    }

    public ComponentAdapter lookup(ComponentFilter componentFilter) {
        ComponentAdapter result = lookupInternal(componentFilter);
        if (result == null) {
            for (Frame frame : JFrame.getFrames()) {
                if (!roots.contains(frame))
                    roots.add(frame);
            }

            for (Window window : JModalWindow.getModalWindows()) {
                if (!roots.contains(window))
                    roots.add(window);
            }

            result = lookupInternal(componentFilter);
        }
        if (result != null)
            return result;
        throw new IllegalStateException("Cannot find any components...");
    }



    protected ComponentAdapter lookupInternal(ComponentFilter componentFilter) {
        if (componentFilter == null)
            return new RobotComponentAdapter(robot, roots.get(0));
        for (Container root : roots) {
            Component filteredComponent = findComponentByName(root, componentFilter);
            if (filteredComponent != null)
                return createComponentAdapter(filteredComponent);
        }
        return null; 
    }
    protected Component findComponentByName(Container root, ComponentFilter componentFilter) {
        if (root == null || componentFilter == null)
            return null;

        if (componentFilter.filter(root))
            return root;

        for (Component component : root.getComponents()) {
//            System.out.println(component.getName());
            if (componentFilter.filter(root))
                return component;

            if (component instanceof Container) {
                Component result = findComponentByName((Container) component, componentFilter);
                if (result != null)
                    return result;
            }
        }

        return null;
    }

    protected ComponentAdapter createComponentAdapter(Component component) {
        return new RobotComponentAdapter(robot, component);
    }

}
