package org.noos.xing.mydoggy;

/**
 * A callback interface used to listen to events fired by the PersistenceDelegate
 * during the merge.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.5.0
 * @see org.noos.xing.mydoggy.PersistenceDelegate
 * @see org.noos.xing.mydoggy.PersistenceDelegate#merge(java.io.InputStream, org.noos.xing.mydoggy.PersistenceDelegate.MergePolicy, PersistenceDelegateCallback)
 */
public interface PersistenceDelegateCallback {

    /**
     * TODO: add javadoc
     * @since 1.5.0
     */
    interface PersistenceNode {

        /**
         *
         * @param name
         * @return
         * @since 1.5.0
         */
        boolean containsAttribute(String name);

        /**
         *
         * @param name
         * @return
         * @since 1.5.0
         */
        String getAttributeValue(String name);

        /**
         *
         * @param name
         * @param defaultValue
         * @return
         * @since 1.5.0
         */
        boolean getBoolean(String name, boolean defaultValue);

        /**
         *
         * @param name
         * @param defaultValue
         * @return
         * @since 1.5.0
         */
        int getInteger(String name, int defaultValue);

        /**
         *
         * @param name
         * @param defaultValue
         * @return
         * @since 1.5.0
         */
        float getFloat(String name, float defaultValue);

    }


    /**
     * This method is invoked when the PersistenceDelegate try to access a ToolWindow that is not
     * currently registered in the ToolWindowManager.
     *
     * @param toolWindowManager a reference to.
     * @param toolWindowId the requested toolwindow id.
     * @param node  TODO
     * @return a instance of ToolWindow eventually registered.
     * @since 1.5.0
     */
    ToolWindow toolwindowNotFound(ToolWindowManager toolWindowManager, String toolWindowId, PersistenceNode node);

    /**
     * This method is invoked when the PersistenceDelegate try to access a Content that is not
     * currently registered in the ContentManager.
     *
     * @param toolWindowManager a reference to.
     * @param contentId the requested content id.
     * @param node
     * @return a instance of Content eventually registered.
     * @since 1.5.0
     */
    Content contentNotFound(ToolWindowManager toolWindowManager, String contentId, PersistenceNode node);

}
