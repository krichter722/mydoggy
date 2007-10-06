package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.ComponentFilter;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class NamedComponentFilter implements ComponentFilter {
    private String name;

    public NamedComponentFilter(String name) {
        this.name = name;
    }

    public boolean filter(Component component) {
        return component.getName()  != null && component.getName().equals(name);
    }
}
