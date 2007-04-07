package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.PushAwayMode;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowManagerDescriptor implements ToolWindowManagerDescriptor {
    private PushAwayMode pushAwayMode;
    private MyDoggyToolWindowManager manager;

    public MyDoggyToolWindowManagerDescriptor(MyDoggyToolWindowManager manager) {
        this.manager = manager;
        this.pushAwayMode = PushAwayMode.VERTICAL;
    }

    public void setPushAwayMode(PushAwayMode pushAwayMode) {
        if (this.pushAwayMode == pushAwayMode)
            return;

        this.pushAwayMode = pushAwayMode;

        // Store workspace
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manager.getPersistenceDelegate().save(outputStream);

        // Hi all tools
        manager.getToolWindowGroup().setVisible(false);

        // Change mode
        manager.getBar(LEFT).getSplitPane().setLeftComponent(null);
        manager.getBar(LEFT).getSplitPane().setRightComponent(null);

        manager.getBar(RIGHT).getSplitPane().setLeftComponent(null);
        manager.getBar(RIGHT).getSplitPane().setRightComponent(null);

        manager.getBar(TOP).getSplitPane().setLeftComponent(null);
        manager.getBar(TOP).getSplitPane().setRightComponent(null);

        manager.getBar(BOTTOM).getSplitPane().setLeftComponent(null);
        manager.getBar(BOTTOM).getSplitPane().setRightComponent(null);
        
//        SwingUtil.repaint(manager);

        switch (pushAwayMode) {
            case HORIZONTAL:
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(RIGHT).getSplitPane().setLeftComponent(manager.getBar(TOP).getSplitPane());
                manager.getBar(TOP).getSplitPane().setBottomComponent(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(BOTTOM).getSplitPane().setResizeWeight(1);

                manager.add(manager.getBar(LEFT).getSplitPane(), "1,1,FULL,FULL");

                manager.mainSplitPane = manager.getBar(BOTTOM).getSplitPane();
                manager.mainSplitPane.setTopComponent(manager.mainContainer);
                break;
            case VERTICAL:
                manager.getBar(BOTTOM).getSplitPane().setTopComponent(manager.getBar(TOP).getSplitPane());
                manager.getBar(TOP).getSplitPane().setBottomComponent(manager.getBar(LEFT).getSplitPane());
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(RIGHT).getSplitPane().setResizeWeight(1);

                manager.add(manager.getBar(BOTTOM).getSplitPane(), "1,1,FULL,FULL");

                manager.mainSplitPane = manager.getBar(RIGHT).getSplitPane();
                manager.mainSplitPane.setLeftComponent(manager.mainContainer);
                break;
            case ANTICLOCKWISE:
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(BOTTOM).getSplitPane().setTopComponent(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(RIGHT).getSplitPane().setLeftComponent(manager.getBar(TOP).getSplitPane());
                manager.getBar(TOP).getSplitPane().setResizeWeight(1);

                manager.add(manager.getBar(LEFT).getSplitPane(), "1,1,FULL,FULL");

                manager.mainSplitPane = manager.getBar(TOP).getSplitPane();
                manager.mainSplitPane.setBottomComponent(manager.mainContainer);

                break;
        }

        // Reload workspace
        manager.getPersistenceDelegate().apply(new ByteArrayInputStream(outputStream.toByteArray()));

        SwingUtil.repaintNow(manager);
    }

    public PushAwayMode getPushAwayMode() {
        return pushAwayMode;
    }
}
