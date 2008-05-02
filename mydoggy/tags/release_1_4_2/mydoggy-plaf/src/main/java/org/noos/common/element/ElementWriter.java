package org.noos.common.element;

import org.noos.common.context.Context;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ElementWriter<W> {

    void write(W w, Context context);
    
}
