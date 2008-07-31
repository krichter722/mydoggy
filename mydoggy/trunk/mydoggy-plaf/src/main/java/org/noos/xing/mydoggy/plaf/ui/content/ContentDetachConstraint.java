package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Content;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentDetachConstraint {
    Content onContent;
    int index;
    AggregationPosition aggregatePosition;

    public ContentDetachConstraint(Content onContent, int index, AggregationPosition aggregatePosition) {
        this.onContent = onContent;
        this.index = index;
        this.aggregatePosition = aggregatePosition;
    }

    public Content getOnContent() {
        return onContent;
    }

    public int getIndex() {
        return index;
    }

    public AggregationPosition getAggregatePosition() {
        return aggregatePosition;
    }
}
