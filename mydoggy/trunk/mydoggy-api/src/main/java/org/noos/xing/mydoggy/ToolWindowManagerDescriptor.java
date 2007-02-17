package org.noos.xing.mydoggy;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @todo add javadocs...
 */
public interface ToolWindowManagerDescriptor {
    public enum PushAwayMode {LEFT, TOP}

    void setPushAwayMode(PushAwayMode pushAwayMode);

    PushAwayMode getPushAwayMode();
        
}
