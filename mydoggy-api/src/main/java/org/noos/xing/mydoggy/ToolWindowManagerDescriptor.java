package org.noos.xing.mydoggy;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowManagerDescriptor {
    public enum PushAwayMode {LEFT, TOP}

    void setPushAwayMode(PushAwayMode pushAwayMode);

    PushAwayMode getPushAwayMode();
        
}
