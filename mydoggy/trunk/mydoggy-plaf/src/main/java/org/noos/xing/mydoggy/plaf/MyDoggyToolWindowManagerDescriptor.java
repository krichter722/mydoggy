package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import static org.noos.xing.mydoggy.ToolWindowAnchor.LEFT;
import static org.noos.xing.mydoggy.ToolWindowAnchor.RIGHT;
import static org.noos.xing.mydoggy.ToolWindowAnchor.TOP;
import static org.noos.xing.mydoggy.ToolWindowAnchor.BOTTOM;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowManagerDescriptor implements ToolWindowManagerDescriptor {
    private PushAwayMode pushAwayMode;
    private MyDoggyToolWindowManager manager;

    public MyDoggyToolWindowManagerDescriptor(MyDoggyToolWindowManager manager) {
        this.manager = manager;
        this.pushAwayMode = PushAwayMode.TOP;
    }

    public void setPushAwayMode(PushAwayMode pushAwayMode) {
        if (this.pushAwayMode == pushAwayMode)
            return;

        this.pushAwayMode = pushAwayMode;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        manager.getPersistenceDelegate().save(outputStream);

        // TODO: is there something better???
        manager.getToolWindowGroup().setVisible(false);

        manager.getBar(LEFT).getSplitPane().setLeftComponent(null);
        manager.getBar(LEFT).getSplitPane().setRightComponent(null);

        manager.getBar(RIGHT).getSplitPane().setLeftComponent(null);
        manager.getBar(RIGHT).getSplitPane().setRightComponent(null);

        manager.getBar(TOP).getSplitPane().setLeftComponent(null);
        manager.getBar(TOP).getSplitPane().setRightComponent(null);

        manager.getBar(BOTTOM).getSplitPane().setLeftComponent(null);
        manager.getBar(BOTTOM).getSplitPane().setRightComponent(null);
        
        switch (pushAwayMode) {
            case LEFT:
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(RIGHT).getSplitPane().setLeftComponent(manager.getBar(TOP).getSplitPane());
                manager.getBar(TOP).getSplitPane().setBottomComponent(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(BOTTOM).getSplitPane().setResizeWeight(1);

                manager.add(manager.getBar(LEFT).getSplitPane(), "1,1,FULL,FULL");

                manager.mainSplitPane = manager.getBar(BOTTOM).getSplitPane();
                manager.mainSplitPane.setTopComponent(manager.mainContainer);
                break;
            case TOP:
                manager.getBar(BOTTOM).getSplitPane().setTopComponent(manager.getBar(TOP).getSplitPane());
                manager.getBar(TOP).getSplitPane().setBottomComponent(manager.getBar(LEFT).getSplitPane());
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(RIGHT).getSplitPane().setResizeWeight(1);

                manager.add(manager.getBar(BOTTOM).getSplitPane(), "1,1,FULL,FULL");

                manager.mainSplitPane = manager.getBar(RIGHT).getSplitPane();
                manager.mainSplitPane.setLeftComponent(manager.mainContainer);
                break;
        }

        manager.getPersistenceDelegate().apply(new ByteArrayInputStream(outputStream.toByteArray()));
                
        SwingUtil.repaint(manager);
    }

    public PushAwayMode getPushAwayMode() {
        return pushAwayMode;
    }
}
