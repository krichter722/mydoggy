package org.noos.xing.mydoggy;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.2.0
 */
public interface PersistenceDelegate {

    /**
     *
     * @param outputStream
     * @since 1.2.0
     */
    void save(OutputStream outputStream);

    /**
     *
     * @param inputStream
     * @since 1.2.0
     */
    void apply(InputStream inputStream);

}
