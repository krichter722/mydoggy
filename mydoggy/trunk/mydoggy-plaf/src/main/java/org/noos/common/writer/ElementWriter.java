package org.noos.common.writer;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ElementWriter<W> {

    void write(W w, Object... args);
    
}
