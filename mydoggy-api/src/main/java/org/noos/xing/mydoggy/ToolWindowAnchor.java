package org.noos.xing.mydoggy;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public enum ToolWindowAnchor {
    TOP,

    LEFT,

    BOTTOM,

    RIGHT;

    private int index = -1;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
