package org.noos.xing.mydoggy;

/**
 * This class is used to give instructions to the MultiSplitContentManagerUI on how to add
 * a content to the ui.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.4.0
 */
public class MultiSplitConstraint {
    private Content aggregationContent;
    private AggregationPosition aggregationPosition;
    private int aggregationIndexLocation = -1;

    /**
     * This tells to aggregate on the <code>aggregationContent</code> 
     *
     * @param aggregationContent aggregate on this content
     */
    public MultiSplitConstraint(Content aggregationContent) {
        this.aggregationContent = aggregationContent;
    }

    /**
     * This tells to aggregate on the <code>aggregationPosition</code> position
     *
     * @param aggregationPosition aggregate using this position.
     */
    public MultiSplitConstraint(AggregationPosition aggregationPosition) {
        this.aggregationPosition = aggregationPosition;
    }

    /**
     * This tells to aggregate on the <code>aggregationContent</code> on the
     * <code>aggregationPosition</code> position
     *
     * @param aggregationContent aggregate on this content
     * @param aggregationPosition using this position
     */
    public MultiSplitConstraint(Content aggregationContent, AggregationPosition aggregationPosition) {
        this.aggregationContent = aggregationContent;
        this.aggregationPosition = aggregationPosition;
    }

    /**
     * This tells to aggregate on the <code>aggregationContent</code> on the
     * <code>aggregationIndexLocation</code> location.
     *
     * @param aggregationContent aggregate on this content
     * @param aggregationIndexLocation using this location
     */
    public MultiSplitConstraint(Content aggregationContent, int aggregationIndexLocation) {
        this.aggregationContent = aggregationContent;
        this.aggregationIndexLocation = aggregationIndexLocation;
    }


    public Content getAggregationContent() {
        return aggregationContent;
    }

    public int getAggregationIndexLocation() {
        return aggregationIndexLocation;
    }

    public AggregationPosition getAggregationPosition() {
        return aggregationPosition;
    }

    public String toString() {
        return "MultiSplitConstraint{" +
               "aggregationContent=" + aggregationContent +
               ", aggregationPosition=" + aggregationPosition +
               ", aggregationIndexLocation=" + aggregationIndexLocation +
               '}';
    }
}
