package org.noos.xing.mydoggy;


/**
 * This interface represents a ui that use a component that lets the user switch between
 * a group of components by clicking on a tab with a given title and/or icon (i.e. JTabbedPane). 
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.1.0
 */
public interface TabbedContentManagerUI extends ContentManagerUI<TabbedContentUI>, Observable {

    /**
     * The tab placement enum used in positioning the component that show tabs.
     * @since 1.1.0
     */
    enum TabPlacement {
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    /**
     * The tab layout enum used in laying out the tabs when all the tabs will not fit within a single line.
     * @since 1.1.0
     */
    enum TabLayout {
        /**
         * The tab layout for wrapping tabs in multiple lines when all tabs will not fit within a single line.
         * @since 1.1.0
         */
        WRAP,

        /**
         * The tab layout for providing a subset of available tabs when all the tabs will not fit within a single line.
         * @since 1.1.0
         */
        SCROLL
    }


    /**
     * Sets the tab placement for this manager.
     *
     * @param tabPlacement the placement for the tabs relative to the content
     * @see org.noos.xing.mydoggy.TabbedContentManagerUI.TabPlacement
     * @since 1.1.0
     */
    void setTabPlacement(TabPlacement tabPlacement);

    /**
     * Returns the placement of the tabs for this manager.
     *
     * @return the placement of the tabs for this manager.
     * @see #setTabPlacement
     * @see org.noos.xing.mydoggy.TabbedContentManagerUI.TabPlacement
     * @since 1.1.0
     */
    TabPlacement getTabPlacement();

    /**
     * Sets the layout type which the manager will use in laying out the tabs
     * when all the tabs will not fit within a single line.
     *
     * @param tabLayout the layoyt type used to layout the tabs
     * @see #getTabLayout()
     * @see org.noos.xing.mydoggy.TabbedContentManagerUI.TabLayout
     * @since 1.1.0
     */
    void setTabLayout(TabLayout tabLayout);

    /**
     * Returns the layout type used by the manager to layout the tabs when all the
     * tabs will not fit within a single line.
     *
     * @return layout type used by the manager to layout the tabs.
     * @see #setTabLayout(org.noos.xing.mydoggy.TabbedContentManagerUI.TabLayout)
     * @see org.noos.xing.mydoggy.TabbedContentManagerUI.TabLayout
     * @since 1.1.0
     */
    TabLayout getTabLayout();

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
}
