package org.noos.xing.mydoggy;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ContentUI {

	/**
	 * Returns whether this content could be close using the ui.
	 * @return <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 */
	boolean isCloseable();

	/**
	 * Sets the closeable property of this content.
	 * @param closeable <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 * @see #isCloseable()
	 */
	void setCloseable(boolean closeable);

	/**
	 * Returns whether this content could be detach using the ui.
	 * @return <code>true</code> if this content can be detached using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 */
	boolean isDetachable();

	/**
	 * Sets the detachable property of this content.
	 * @param detachable <code>true</code> if this content can be detached using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 * @see #isDetachable() ()
	 */
	void setDetachable(boolean detachable);

}
