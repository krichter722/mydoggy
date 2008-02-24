package org.noos.common.writer;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ElementWriterProvider<W, I> {

    ElementWriter<W> getElementWriter(I id);
    
}
