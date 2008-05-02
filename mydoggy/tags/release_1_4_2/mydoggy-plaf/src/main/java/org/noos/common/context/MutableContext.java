package org.noos.common.context;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface MutableContext extends Context {

    void put(Object key, Object value);

    Object remove(Object key);
    
}
