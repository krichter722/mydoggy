package org.noos.xing.mydoggy;

/**
 * TODO
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface TabbedContentManagerUI extends ContentManagerUI {

    /**
     * Sets whether or not the ui must show a tab for all contents also
     * when there is just one content.
     *
     * @param show whether or not the ui must show a tab for all contents.
     * @see #isShowAlwaysTab()
     */
    void setShowAlwaysTab(boolean show);

    /**
     * Returns whether or not the ui must show a tab for all contents also
     * when there is just one content.
     * Default value is false.
     *
     * @return true if the the ui must show a tab for all contents;
     *         false otherwise
     * @see #setShowAlwaysTab(boolean)
     */
    boolean isShowAlwaysTab();
    
}
