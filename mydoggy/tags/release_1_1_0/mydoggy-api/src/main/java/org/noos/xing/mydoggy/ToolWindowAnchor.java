package org.noos.xing.mydoggy;

/**
 * Every tool window has an anchor that specifies where the tool is attached.
 * The user can specifies also an index relative to the other tools attached
 * to the same anchor, like a list.
 * Every anchor rapresents a tool window bar where for every tool a representative
 * button is positioned.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public enum ToolWindowAnchor {
    TOP,

    LEFT,

    BOTTOM,

    RIGHT;

    /**
     * The index position relative to the other tools attached to same anchor.
     * The -1 value means last position.
     */
    private int index = -1;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
