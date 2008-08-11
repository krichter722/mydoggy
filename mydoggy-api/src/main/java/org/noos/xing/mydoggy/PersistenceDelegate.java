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
        /**
         * All settings suggested by the stream will be applied.
         */
        RESET,

        /**
         * if a toolwindow is already visible but the stream suggests to hide it then no action will be taken.
         * The toolwindow will remain visible
         */
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
     * TODO: 
     * @param outputStream
     * @param filter
     * @since 1.5.0
     */
    void save(OutputStream outputStream, PersistenceDelegateFilter filter);

    /**
     * Reads the settings for the ToolWindows, already registered into the manager, from the input
     * stream. By dafault the MergePolicy.RESET policy is used.
     * 
     * @param inputStream the input stream.
     * @exception RuntimeException  if an error occurred when reading from the
     *               input stream.
     * @since 1.2.0
     * @see #save(java.io.OutputStream)
     * @see org.noos.xing.mydoggy.PersistenceDelegate.MergePolicy#RESET
     */
    void apply(InputStream inputStream);

    /**
     * Reads the settings for the ToolWindows, already registered into the manager, from the input
     * stream applying the passed merge policy.
     *
     * @param inputStream the input stream.
     * @param mergePolicy the merget policy used to apply settings.
     * @exception RuntimeException  if an error occurred when reading from the
     *               input stream.
     * @see org.noos.xing.mydoggy.PersistenceDelegate.MergePolicy
     * @since 1.3.0
     */
    void merge(InputStream inputStream, MergePolicy mergePolicy);

    /**
     * Reads the settings for the ToolWindows, already registered into the manager, from the input
     * stream applying the passed merge policy and using the passed callback.
     *
     * @param inputStream the input stream.
     * @param mergePolicy the merget policy used to apply settings.
     * @param callback a callback object instance. It can be null.
     * @exception RuntimeException  if an error occurred when reading from the
     *               input stream.
     * @since 1.5.0
     * @see org.noos.xing.mydoggy.PersistenceDelegateCallback
     */
    void merge(InputStream inputStream, MergePolicy mergePolicy, PersistenceDelegateCallback callback);


}
