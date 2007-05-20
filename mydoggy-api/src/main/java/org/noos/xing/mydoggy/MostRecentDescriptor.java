package org.noos.xing.mydoggy;

/**
 * This interface is used to modify the behaviour of PushAwayMode.MOST_RECENT mode.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
 * @see PushAwayMode#MOST_RECENT
 */
public interface MostRecentDescriptor extends PushAwayModeDescriptor {

    /**
     * This methods is used to simulate activations.
     *
     * @param anchors an array of anchors. The last anchor represents the last activation. 
     * @since 1.3.0
     */
    void append(ToolWindowAnchor... anchors);

    /**
     * Returns last activated anchors. The first element represents the last activation.
     *
     * @return last activated anchors.
     * @since 1.3.0
     */
    ToolWindowAnchor[] getMostRecentAnchors();

}
