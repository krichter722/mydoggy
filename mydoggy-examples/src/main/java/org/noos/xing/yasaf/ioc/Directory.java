package org.noos.xing.yasaf.ioc;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface Directory<E> {

    Directory addDirectory(Object name);

    Directory getDirectory(Object name);

    void addResource(Object name, E resource);

    E getResource(Object name);

}
