package org.noos.xing.yasaf.view;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ViewContextManager {

    void addViewContext(Object key, ViewContext viewContext);

    ViewContext getViewContext(Object key);

}
