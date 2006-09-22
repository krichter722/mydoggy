package org.noos.xing.mydoggy.plaf.collections;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Angelo De Caro
 */
public class SetEnumeration<E> implements Enumeration<E> {

    private Set<E> set;
    private Iterator<E> iterator;
    private Enumeration<E> enumeration;
    private E next;

    public SetEnumeration(Set<E> set, Enumeration<E> enumeration) {
        this.set = set;
        iterator = set.iterator();
        this.enumeration = enumeration;
    }


    public boolean hasMoreElements() {
        if (next == null) {
            if (iterator.hasNext()) {
                next = iterator.next();
            } else if (enumeration != null) {
                while (next == null && enumeration.hasMoreElements()) {
                    next = enumeration.nextElement();
                    if (set.contains(next)) {
                        next = null;
                    }
                }
            }
        }
        return next != null;
    }

    public E nextElement() {
        if (hasMoreElements()) {
            E result = next;
            next = null;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }
}
