package org.noos.xing.mydoggy;

/**
 * This interface is used to modify the behaviours of the ToolWindowManager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.2.0
 */
public interface ToolWindowManagerDescriptor {
    public enum PushAwayMode {
        HORIZONTAL,
        VERTICAL
    }

    /**
     *
     * @param pushAwayMode
     * @since 1.2.0
     */
    void setPushAwayMode(PushAwayMode pushAwayMode);

    /**
     *
     * @return
     * @since 1.2.0
     */
    PushAwayMode getPushAwayMode();
        
}
