package org.noos.xing.mydoggy.itest;


/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ComponentLookuper {

    ComponentAdapter lookup(String componentName);

    ComponentAdapter lookup(ComponentFilter componentFilter);

    ComponentAdapter lookup();
}
