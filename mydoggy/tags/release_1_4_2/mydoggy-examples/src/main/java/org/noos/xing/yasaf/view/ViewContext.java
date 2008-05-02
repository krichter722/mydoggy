package org.noos.xing.yasaf.view;

import java.io.Serializable;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ViewContext extends Serializable {

    Object get(Object key);

    <T> T get(Class<T> key);

    Object put(Object key, Object value);

    void addViewContextChangeListener(Object key, ViewContextChangeListener listener);

    void addViewContextChangeListener(ViewContextChangeListener listener);

}
