package org.noos.xing.mydoggy;

/**
 * Every tool window has a type that specifies the visual behaviours of the tool.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public enum ToolWindowType {
    DOCKED,

    SLIDING,

    /**
     *
     */
    FLOATING,

    /**
     * This type differs from FLOATING type for the absence of the representative label
     * on the tool window bar.
     *
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     */
    FLOATING_WINDOW
}
