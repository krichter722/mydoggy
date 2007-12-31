package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.MultiSplitConstraint;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitDockableContainer;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyMultiSplitContentUI extends MyDoggyTabbedContentUI {
    protected MultiSplitDockableContainer multiSplitContainer;

    public MyDoggyMultiSplitContentUI(MultiSplitDockableContainer multiSplitContainer, Content content) {
        super(content);
        this.multiSplitContainer = multiSplitContainer;
    }

    public void setConstraints(Object... constraints) {
        if (constraints.length > 0 && constraints[0] instanceof MultiSplitConstraint) {
            MultiSplitConstraint multiSplitConstraint = (MultiSplitConstraint) constraints[0];

            multiSplitContainer.removeDockable(content);
            multiSplitContainer.addDockable(content,
                                            content.getComponent(),
                                            multiSplitConstraint.getContent(),
                                            multiSplitConstraint.getAggregationIndexLocation(),
                                            multiSplitConstraint.getAggregationPosition());
        }
    }
}