package org.noos.common.element;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @todo introduce this
 */
public interface ElementParserContext {

    void put(Object key, Object value);

    boolean remove(Object key);

    Object get(Object key);

    <T> T get(Class<T> key);

}
