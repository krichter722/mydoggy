package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentUI;
import org.noos.xing.mydoggy.plaf.ui.cmp.TabbedContentPane;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class MyDoggyTabbedContentUI extends MyDoggyContentUI implements TabbedContentUI {
    protected TabbedContentPane tabbedContentPane;


    public MyDoggyTabbedContentUI(ContentManagerUI contentManagerUI,
                                  TabbedContentPane tabbedContentPane,
                                  Content content) {
        super(null, contentManagerUI, content);

        this.tabbedContentPane = tabbedContentPane;
    }


    public void setConstraints(Object... constraints) {
        if (constraints.length > 0 && constraints[0] instanceof Integer) 
            tabbedContentPane.setIndex(content, (Integer) constraints[0]);                           
    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.tabbedContentPane = null;
    }
}
