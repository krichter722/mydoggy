package org.noos.xing.mydoggy;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface provides useful methods to save and load the tool window manager's workspace.
 * When you request to save the workspace, all settings of ToolWindows with relative descriptors are saved.
 * To obtain an instance of PersistenceDelegate you have to invoke the method <code>getPersistenceDelegate</code>
 * of <code>ToolWindowManager</code> interface. So you can obtain a persistence delegate specific to a
 * particular tool window manager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.2.0
 * @see org.noos.xing.mydoggy.ToolWindowManager
 */
public interface PersistenceDelegate {

    enum MergePolicy {
        RESET,
        UNION
    }

    /**
     * Writes all settings of ToolWindows with relative descriptors to the output stream in a format suitable
     * for loading using the {@link #apply(java.io.InputStream)} method.
     * <p>
     * After the entries have been written, the output stream is flushed.  The
     * output stream remains open after this method returns.
     * <p>
     * Look at the specific implementation for the data format.
     * 
     * @param outputStream an output stream
     * @throws RuntimeException if an error occurred when writing to the ouput stream.
     * @since 1.2.0
     * @see #apply(java.io.InputStream)
     */
    void save(OutputStream outputStream);

    /**
     * Reads the settings for the ToolWindows, already registered into the manager, from the input
     * stream.
     * 
     * @param inputStream the input stream.
     * @exception RuntimeException  if an error occurred when reading from the
     *               input stream.
     * @since 1.2.0
     * @see #save(java.io.OutputStream) 
     */
    void apply(InputStream inputStream);

    /**
     * TODO: javadocs
     * 
     * @param inputStream
     * @param mergePolicy
     * @since 1.3.0
     */
    void merge(InputStream inputStream, MergePolicy mergePolicy);
}
