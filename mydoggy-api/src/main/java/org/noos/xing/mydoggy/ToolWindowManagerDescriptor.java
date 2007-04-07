package org.noos.xing.mydoggy;

/**
 * This interface is used to modify the behaviours of the ToolWindowManager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.2.0
 */
public interface ToolWindowManagerDescriptor {

    /**
     * Sets the push away mode to <code>pushAwayMode</code>
     * @param pushAwayMode the new push away mode
     * @see org.noos.xing.mydoggy.PushAwayMode
     * @since 1.2.0
     */
    void setPushAwayMode(PushAwayMode pushAwayMode);

    /**
     * Returns the current push away mode.
     * @return the current push away mode. 
     * @since 1.2.0
     */
    PushAwayMode getPushAwayMode();
        
}
