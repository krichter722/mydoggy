package org.noos.xing.mydoggy;

/**
 * This interface rapresents a ui that use a container to create a multiple-document interface
 * or a virtual desktop.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.1.0
 */
public interface DesktopContentManagerUI extends ContentManagerUI {

    /**
	 * Returns the ui part to which this manager maps the specified <code>content<code>.
     * @param content content whose associated ui part is to be returned.
     * @return the ui part to which this manager maps the specified <code>content<code>. 
     * @since 1.1.0
     */
    DesktopContentUI getDesktopContentUI(Content content);

}
