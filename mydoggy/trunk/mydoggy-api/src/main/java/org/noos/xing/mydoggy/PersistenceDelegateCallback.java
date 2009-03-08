package org.noos.xing.mydoggy;

/**
 * A callback interface used to listen to events fired by the PersistenceDelegate
 * during the merge.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.PersistenceDelegate
 * @see org.noos.xing.mydoggy.PersistenceDelegate#merge(java.io.InputStream, org.noos.xing.mydoggy.PersistenceDelegate.MergePolicy, PersistenceDelegateCallback)
 * @since 1.5.0
 */
public interface PersistenceDelegateCallback {

    /**
     * The interface is used to analyze the properties of a source of persistence data.
     *
     * @since 1.5.0
     */
    interface PersistenceNode {

        /**
         * Returns the node name.
         *
         * @return the node name.
         * @since 1.5.0
         */
        String getName();

        /**
         * Returns <tt>true</tt> if this node contains an attribute for the specified
         * name.
         *
         * @param name attribute name whose presence in this node is to be tested.
         * @return <tt>true</tt> if this node contains an attributed for the specified name.
         * @since 1.5.0
         */
        boolean containsAttribute(String name);

        /**
         * Returns the attribute's value.
         *
         * @param name attribute name whose value in this node is to be returned.
         * @return the attribute's value.
         * @since 1.5.0
         */
        String getAttributeValue(String name);

        /**
         * Returns the attribute's value as boolean.
         *
         * @param name name attribute whose value in this node is to be returned.
         * @param defaultValue the value to be returned if the node does not contain an attribute with the specified name.
         * @return the attribute's value as boolean.
         * @since 1.5.0
         */
        boolean getBoolean(String name, boolean defaultValue);

        /**
         * Returns the attribute's value as integer.
         *
         * @param name name attribute whose value in this node is to be returned.
         * @param defaultValue the value to be returned if the node does not contain an attribute with the specified name.
         * @return the attribute's value as integer.
         * @since 1.5.0
         */
        int getInteger(String name, int defaultValue);

        /**
         * Returns the attribute's value as float.
         *
         * @param name name attribute whose value in this node is to be returned.
         * @param defaultValue the value to be returned if the node does not contain an attribute with the specified name.
         * @return the attribute's value as float.
         * @since 1.5.0
         */
        float getFloat(String name, float defaultValue);

    }


    /**
     * This method is invoked when the PersistenceDelegate try to access a ToolWindow that is not
     * currently registered in the ToolWindowManager.
     *
     * @param toolWindowManager a reference to.
     * @param toolWindowId      the requested toolwindow id.
     * @param node              a node instance to retrieve all the properties related to the requested toolwindow.
     * @return a instance of ToolWindow eventually registered.
     * @since 1.5.0
     */
    ToolWindow toolwindowNotFound(ToolWindowManager toolWindowManager, String toolWindowId, PersistenceNode node);

    /**
     * This method is invoked when the PersistenceDelegate try to access a Content that is not
     * currently registered in the ContentManager.
     *
     * @param toolWindowManager a reference to.
     * @param contentId         the requested content id.
     * @param node              a node instance to retrieve all the properties related to the requested content.
     * @return a instance of Content eventually registered.
     * @since 1.5.0
     */
    Content contentNotFound(ToolWindowManager toolWindowManager, String contentId, PersistenceNode node);

    /**
     * This method is invoked each time a property is loaded by the persistence delegate.
     * So the user can decide to accept or refuse the attributeValue loaded from the persistence storage.  
     *
     * @param node the current source of persistence data.
     * @param attribute the attribute whose value is to be validated.
     * @param attributeValue the attribute value loaded from the persistence storage.
     * @param attributeDefaultValue the default value assigned by the system to that attribute.
     * @return the attribute value to be used.
     * @since 1.5.0
     */
    String validate(PersistenceNode node, String attribute, String attributeValue, Object attributeDefaultValue); 

}
