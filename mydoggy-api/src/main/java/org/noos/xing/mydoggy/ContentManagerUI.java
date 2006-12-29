package org.noos.xing.mydoggy;

/**
 * A ContentManagerUI is an interface to modify the ui behaviours of
 * a content manager. For example this is used to modify the way a content
 * is showed. 
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see TabbedContentManagerUI
 * @see DesktopContentManagerUI
 */
public interface ContentManagerUI<C extends ContentUI> {

	/**
	 * Sets the closeable property of all contents registered to content manager.
	 * @param closeable <code>true</code> if all contents can be closed using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 */
	void setCloseable(boolean closeable);

	/**
	 * Sets the detachable property of all contents registered to content manager..
	 * @param detachable <code>true</code> if all contents can be detached using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 */
	void setDetachable(boolean detachable);

	/**
	 * Returns the ui part to which this manager maps the specified <code>content<code>.
	 * @param content content whose associated ui part is to be returned.
	 * @return the ui part to which this manager maps the specified <code>content<code>.
	 * @since 1.1.0
	 */
	C getContentUI(Content content);

}
