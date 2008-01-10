package org.noos.xing.mydoggy.mydoggyset.view.nested;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;
import org.noos.xing.yasaf.view.View;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class NestedManagerView implements View {
    protected ToolWindowManager toolWindowManager;
    protected JFrame frame;

    public NestedManagerView(JFrame frame, ToolWindowManager toolWindowManager) {
        this.frame = frame;
        this.toolWindowManager = toolWindowManager;
    }

    public Component getComponent() {
        MyDoggyToolWindowManager nestedToolManager = new MyDoggyToolWindowManager(frame);

        // Add a tool
        nestedToolManager.registerToolWindow("Nested Tool 1",
                                             "Nested Tool 1",
                                             null,
                                             new JButton("Nested Hello World 1"),
                                             ToolWindowAnchor.LEFT).setAvailable(true);

        // Setup contents

        ContentManager subContentManager = nestedToolManager.getContentManager();

        subContentManager.setContentManagerUI(new MyDoggyMultiSplitContentManagerUI());

        Content browseContent = subContentManager.addContent("Browse", "Browse", null, new JButton("BrowseButton"), null,
                                                             new MultiSplitConstraint(AggregationPosition.BOTTOM));
        TabbedContentUI contentBrowse = (TabbedContentUI) browseContent.getContentUI();
        contentBrowse.setCloseable(false);

        Content queryContent = subContentManager.addContent("Query", "Query", null, new JButton("QueryButton"), null,
                                                            new MultiSplitConstraint(browseContent, 0));
        TabbedContentUI contentQuery = (TabbedContentUI) queryContent.getContentUI();
        contentQuery.setCloseable(false);

        return nestedToolManager;
    }

}