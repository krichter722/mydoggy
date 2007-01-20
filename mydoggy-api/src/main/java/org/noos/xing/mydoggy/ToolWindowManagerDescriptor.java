package org.noos.xing.mydoggy;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowManagerDescriptor {
    public enum Dock {LEFT, TOP}

    void setDock(Dock dock);

    Dock getDock();
        
}
