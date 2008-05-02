package org.noos.common.element;

import org.noos.common.context.Context;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ElementParser<E> {

    boolean parse(E e, Context context);
    
}
