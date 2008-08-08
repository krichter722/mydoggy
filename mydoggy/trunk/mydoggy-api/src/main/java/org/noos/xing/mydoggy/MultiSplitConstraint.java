package org.noos.xing.mydoggy;

/**
 * This class is used to give instructions to the MultiSplitContentManagerUI on how to add
 * a content to the ui.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.4.0
 */
public class MultiSplitConstraint {
    private Content onContent;
    private AggregationPosition onPosition;
    private int onIndex = -1;


    /**
     * This tells to aggregate on the <code>aggregationContent</code> 
     *
     * @param onContent aggregate on this content
     */
    public MultiSplitConstraint(Content onContent) {
        this.onContent = onContent;
    }

    /**
     * This tells to aggregate on the <code>aggregationPosition</code> position
     *
     * @param onPosition aggregate using this position.
     */
    public MultiSplitConstraint(AggregationPosition onPosition) {
        this.onPosition = onPosition;
    }

    /**
     * This tells to aggregate on the <code>aggregationContent</code> on the
     * <code>aggregationPosition</code> position
     *
     * @param onContent aggregate on this content
     * @param onPosition using this position
     */
    public MultiSplitConstraint(Content onContent, AggregationPosition onPosition) {
        this.onContent = onContent;
        this.onPosition = onPosition;
    }

    /**
     * This tells to aggregate on the <code>aggregationContent</code> on the
     * <code>aggregationIndexLocation</code> location.
     *
     * @param onContent aggregate on this content
     * @param onIndex using this location
     */
    public MultiSplitConstraint(Content onContent, int onIndex) {
        this.onContent = onContent;
        this.onIndex = onIndex;
    }

    /**
     * TODO:
     * @param onContent
     * @param onIndex
     * @param onPosition
     * @since 1.5.0
     */
    public MultiSplitConstraint(Content onContent, int onIndex, AggregationPosition onPosition) {
        this.onContent = onContent;
        this.onIndex = onIndex;
        this.onPosition = onPosition;
    }


    public Content getOnContent() {
        return onContent;
    }

    public int getOnIndex() {
        return onIndex;
    }

    public AggregationPosition getOnPosition() {
        return onPosition;
    }


    public String toString() {
        return "MultiSplitConstraint{" +
               "onContent=" + onContent +
               ", onPosition=" + onPosition +
               ", onIndex=" + onIndex +
               '}';
    }
}
