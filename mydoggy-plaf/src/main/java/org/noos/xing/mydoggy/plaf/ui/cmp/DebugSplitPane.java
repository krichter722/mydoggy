package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;

import javax.swing.plaf.SplitPaneUI;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DebugSplitPane extends UIFSplitPane {
    private MyDoggyToolWindowBar toolWindowBar;

    public DebugSplitPane() {
    }

    public DebugSplitPane(int newOrientation) {
        super(newOrientation);
    }

    public DebugSplitPane(int newOrientation, boolean newContinuousLayout) {
        super(newOrientation, newContinuousLayout);
    }

    public DebugSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
        super(newOrientation, newLeftComponent, newRightComponent);
    }

    public DebugSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent) {
        super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
    }

    public void setUI(SplitPaneUI ui) {
        super.setUI(ui);
        setBorder(null);
        setContinuousLayout(true);
    }

    public void setDividerLocation(int location) {
//            if (toolWindowBar != null && toolWindowBar.aa && location == 0)
//                return;

//        String anchor = (toolWindowBar != null) ? toolWindowBar.getAnchor().toString() : "";
//        System.out.println("--dividerLocation(" + anchor + ") : " + (getHeight() - location));
//        if (location <= 5) {
//            new RuntimeException().printStackTrace();
//        }

//            if (toolWindowBar != null && toolWindowBar.getAnchor() == ToolWindowAnchor.LEFT)
//                System.out.println("--dividerLocation : " + location);
        super.setDividerLocation(location);
    }

    public void setToolWindowBar(MyDoggyToolWindowBar toolWindowBar) {
        this.toolWindowBar = toolWindowBar;
    }
}
