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
    protected Component parentComponent;

    public NestedManagerView(Component parentComponent, ToolWindowManager toolWindowManager) {
        this.parentComponent = parentComponent;
        this.toolWindowManager = toolWindowManager;
    }

    public Component getComponent() {
        MyDoggyToolWindowManager nestedToolManager = new MyDoggyToolWindowManager();

        // Add a tool

        ToolWindow toolWindow = nestedToolManager.registerToolWindow("Nested Tool 1",
                                                                     "Nested Tool 1",
                                                                     null,
                                                                     new JButton("Nested Hello World 1"),
                                                                     ToolWindowAnchor.RIGHT);
        toolWindow.setAvailable(true);
        toolWindow.setType(ToolWindowType.SLIDING);

        // Setup contents

        ContentManager nestedContentManager = nestedToolManager.getContentManager();

        nestedContentManager.setContentManagerUI(new MyDoggyMultiSplitContentManagerUI());

        // Add a content
        Content browseContent = nestedContentManager.addContent("Browse", "Browse", null, new JButton("BrowseButton"), null,
                                                                new MultiSplitConstraint(AggregationPosition.BOTTOM));
        browseContent.getContentUI().setCloseable(false);

        // Add another content
        Content queryContent = nestedContentManager.addContent("Query", "Query", null, new JButton("QueryButton"), null,
                                                               new MultiSplitConstraint(browseContent, 0));
        queryContent.getContentUI().setCloseable(false);

        return nestedToolManager;
    }

}