package org.noos.common.element;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ElementParser<E> {

    boolean parse(E e, Object... args);
    
}
