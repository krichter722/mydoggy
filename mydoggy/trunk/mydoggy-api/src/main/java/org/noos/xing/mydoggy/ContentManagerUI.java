package org.noos.xing.mydoggy;

/**
 * This is a markup interface for all ContentManagerUI.
 * A ContentManagerUI is an interface to modify the ui behaviours of
 * a content manager. For example this is used to modify the way a content
 * is showed.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see TabbedContentManagerUI
 * @see DesktopContentManagerUI
 */
public interface ContentManagerUI {

	/**
	 *
	 * @param closeable
	 * @since 1.1.0
	 */
	void setCloseable(boolean closeable);

	/**
	 * @param detachable
	 * @since 1.1.0
	 */
	void setDetachable(boolean detachable);

}
