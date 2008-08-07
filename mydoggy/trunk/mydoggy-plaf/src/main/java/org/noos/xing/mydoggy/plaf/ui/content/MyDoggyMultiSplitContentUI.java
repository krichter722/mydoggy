package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.MultiSplitConstraint;
import org.noos.xing.mydoggy.MultiSplitContentUI;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitDockableContainer;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyMultiSplitContentUI extends MyDoggyTabbedContentUI implements MultiSplitContentUI {
    protected MultiSplitDockableContainer multiSplitContainer;
    protected boolean showAlwaysTab;


    public MyDoggyMultiSplitContentUI(ContentManagerUI contentManagerUI, MultiSplitDockableContainer multiSplitContainer, Content content) {
        super(contentManagerUI, null, content);
        
        this.multiSplitContainer = multiSplitContainer;
        this.showAlwaysTab = true;
    }


    public void setConstraints(Object... constraints) {
        if (constraints.length > 0 && constraints[0] instanceof MultiSplitConstraint) {
            MultiSplitConstraint multiSplitConstraint = (MultiSplitConstraint) constraints[0];

            multiSplitContainer.setConstraints(content,
                                               content.getComponent(),
                                               multiSplitConstraint.getOnContent(),
                                               multiSplitConstraint.getOnIndex(),
                                               multiSplitConstraint.getOnPosition());
        }
    }

    public void setShowAlwaysTab(boolean showAlwaysTab) {
        if (this.showAlwaysTab == showAlwaysTab)
            return;

        boolean old = this.showAlwaysTab;
        this.showAlwaysTab = showAlwaysTab;

        firePropertyChangeEvent("showAlwaysTab", old, showAlwaysTab);
    }

    public boolean isShowAlwaysTab() {
        return showAlwaysTab;
    }
}