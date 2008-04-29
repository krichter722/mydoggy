package org.noos.common.context;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface Context {

    Object get(Object key);

    <T> T get(Class<T> key);
    
}
