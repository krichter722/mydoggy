package org.noos.xing.mydoggy;

/**
 * TODO
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.4.0
 */
public class MultiSplitConstraint {
    private Content content;
    private int aggregationIndexLocation;
    private AggregationPosition aggregationPosition;

    public MultiSplitConstraint(Content content) {
        this.content = content;
    }

    public MultiSplitConstraint(Content content, AggregationPosition aggregationPosition) {
        this.content = content;
        this.aggregationPosition = aggregationPosition;
    }

    public MultiSplitConstraint(Content content, int aggregationIndexLocation) {
        this.content = content;
        this.aggregationIndexLocation = aggregationIndexLocation;
    }

    public MultiSplitConstraint(Content content, int aggregationIndexLocation, AggregationPosition aggregationPosition) {
        this.content = content;
        this.aggregationIndexLocation = aggregationIndexLocation;
        this.aggregationPosition = aggregationPosition;
    }

    public Content getContent() {
        return content;
    }

    public int getAggregationIndexLocation() {
        return aggregationIndexLocation;
    }

    public AggregationPosition getAggregationPosition() {
        return aggregationPosition;
    }
}
