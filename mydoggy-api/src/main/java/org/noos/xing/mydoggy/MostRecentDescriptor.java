package org.noos.xing.mydoggy;

/**
 * TODO
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
 */
public interface MostRecentDescriptor extends PushAwayModeDescriptor {

    /**
     *
     * @param anchors
     * @since 1.3.0
     */
    void append(ToolWindowAnchor... anchors);

    /**
     *
     * @return
     * @since 1.3.0
     */
    ToolWindowAnchor[] getMostRecentAnchors();

}
