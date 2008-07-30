package org.noos.common;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface Question<P, R> {

    R getAnswer(P param);
    
}
