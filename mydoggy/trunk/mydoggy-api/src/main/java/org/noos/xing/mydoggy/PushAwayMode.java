package org.noos.xing.mydoggy;

/**
 * This enumeration specifies the "push away" modes for tools with specific anchor.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.2.0
 */
public enum PushAwayMode {

    /**
     * Using this mode, left/right tools push away top/bottom tools.
     */
    HORIZONTAL,

    /**
     * Using this mode, top/bottom tools push away left/right tools.
     */
    VERTICAL,

    /**
     * Using this mode, left tool pushs away bottom tool, bottom tool pushs away right tool,
     * right tool pushs away top tool.
     */
    ANTICLOCKWISE,

    MOST_RECENT
}
