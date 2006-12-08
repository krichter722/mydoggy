package org.noos.xing.mydoggy;

/**
 * TODO
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.1.0
 */
public interface TabbedContentUI {

    /**
     *
     * @return
     * @since 1.1.0
     */
    boolean isCloseable();

    /**
     *
     * @param closeable
     * @since 1.1.0
     */
    void setCloseable(boolean closeable);

    /**
     *
     * @return
     * @since 1.1.0
     */
    boolean isDetachable();

    /**
     *
     * @param detachable
     * @since 1.1.0
     */
    void setDetachable(boolean detachable);

}
