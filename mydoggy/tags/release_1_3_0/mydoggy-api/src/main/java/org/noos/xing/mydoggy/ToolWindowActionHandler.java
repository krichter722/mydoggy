package org.noos.xing.mydoggy;

/**
 * This interface let you intercept toolwindow ui events. You can change, for example,
 * the behaviour of the hide button.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.2.0
 * @see org.noos.xing.mydoggy.DockedTypeDescriptor#setToolWindowActionHandler(ToolWindowActionHandler)
 */
public interface ToolWindowActionHandler {

    /**
     * This method is called when the user click on the hide button. If no handled is setted on
     * DockedTypeDescriptor, the default behaviour is to invoke the method <code>setVisible(false)</code>
     * on the toolwindow. Using this interface you can change that behaviour.
     *
     * @param toolWindow the toolwindow that owns the hide button.
     * @see org.noos.xing.mydoggy.DockedTypeDescriptor#setToolWindowActionHandler(ToolWindowActionHandler)
     * @since 1.2.0
     */
    void onHideButtonClick(ToolWindow toolWindow);
    
}
