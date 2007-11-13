package org.noos.xing.mydoggy;

/**
 * TODO
 * 
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.4.0
 */
public class MultiSplitConstraint {
    private Content content;
    private AggregationPosition aggregationPosition;

    public MultiSplitConstraint(Content content, AggregationPosition aggregationPosition) {
        this.content = content;
        this.aggregationPosition = aggregationPosition;
    }

    public Content getContent() {
        return content;
    }

    public AggregationPosition getAggregationPosition() {
        return aggregationPosition;
    }
}
