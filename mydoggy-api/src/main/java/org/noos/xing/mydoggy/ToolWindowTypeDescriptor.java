package org.noos.xing.mydoggy;

/**
 * This is a markup interface for all ToolWindowTypeDescriptor.
 * A ToolWindowTypeDescriptor is an interface to modify the behaviours of
 * a specific tool window type.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowType
 * @see org.noos.xing.mydoggy.DockedTypeDescriptor
 * @see org.noos.xing.mydoggy.FloatingTypeDescriptor
 * @since 1.0.0
 */
public interface ToolWindowTypeDescriptor {

    /**
     * TODO
     * @since 1.3.0
     * @return
     */
    boolean isAnimating();

    /**
     * TODO
     * @param animating
     * @since 1.3.0
     */
    void setAnimating(boolean animating);

}
