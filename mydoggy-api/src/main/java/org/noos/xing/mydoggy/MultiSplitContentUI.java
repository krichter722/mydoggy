package org.noos.xing.mydoggy;

/**
 * This interface let you modify ui behaviours of a content when a <code>MultiSplitContentManagerUI</code> is used
 * as current <code>ContentManagerUI</code>.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.TabbedContentManagerUI
 * @since 1.1.0
 */
public interface MultiSplitContentUI extends TabbedContentUI {

    /**
     * Sets whether or not the ui must show a tab for all contents also
     * when there is just one content.
     *
     * @param showAlwaysTab <tt>true</tt> if the ui must show a tab for all contents also
     * when there is just one content, <tt>false</tt> otherwise
     * @see #isShowAlwaysTab()
     * @since 1.4.1
     */
    void setShowAlwaysTab(boolean showAlwaysTab);

    /**
     * Returns whether or not the ui must show a tab for all contents also
     * when there is just one content.
     * Default value is false.
     *
     * @return true if the the ui must show a tab for all contents;
     *         false otherwise
     * @see #setShowAlwaysTab(boolean)
     * @since 1.4.1
     */
    boolean isShowAlwaysTab();


}