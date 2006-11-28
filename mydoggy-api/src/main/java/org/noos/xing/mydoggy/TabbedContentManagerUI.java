package org.noos.xing.mydoggy;

/**
 * TODO
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.1.0
 */
public interface TabbedContentManagerUI extends ContentManagerUI {

    /**
     * Sets whether or not the ui must show a tab for all contents also
     * when there is just one content.
     *
     * @param show whether or not the ui must show a tab for all contents.
     * @see #isShowAlwaysTab()
     * @since 1.1.0
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
     * @since 1.1.0
     */
    boolean isShowAlwaysTab();

    /**
     *
     * @param closeable
     * @since 1.1.0
     */
    void setCloseable(boolean closeable);

    /**
     *
     * @param detacchable
     * @since 1.1.0
     */
    void setDetachable(boolean detacchable);

    /**
     *
     * @param content
     * @return
     * @since 1.1.0
     */
    TabbedContentUI getTabbedContentUI(Content content);
    
}
