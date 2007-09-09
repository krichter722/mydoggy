package org.noos.xing.yasaf.ioc;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface Repository {

    Directory getRoot();

    Directory goTo(Object... path);

}
