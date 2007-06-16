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
     * Enables or disables animations, depending on the value of the
     * parameter <code>animating</code>.
     *
     * @param animating If <code>true</code>, animations are
     *                  enabled; otherwise animations are disabled.
     * @since 1.3.0
     */
    void setAnimating(boolean animating);

    /**
     * Returns whether the animations are enabled.
     *
     * @return true is the animations are enabled, false otherwise.
     * @since 1.3.0
     */
    boolean isAnimating();

}
