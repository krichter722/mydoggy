package org.noos.xing.yasaf.view;

import org.noos.xing.yasaf.view.ViewContextChangeListener;

import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ViewContext {

    Object get(Object key);

    <T> T get(Class<T> a);

    Object put(Object key, Object value);

    void addViewContextChangeListener(Object key, ViewContextChangeListener listener);

    void addViewContextChangeListener(ViewContextChangeListener listener);

}
