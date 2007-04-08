package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.PushAwayMode;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Stack;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowManagerDescriptor implements ToolWindowManagerDescriptor, PropertyChangeListener {
    private PushAwayMode pushAwayMode;
    private MyDoggyToolWindowManager manager;

    private boolean checkParam = true;
    private Stack<ToolWindowAnchor> mostRecentStack;

    public MyDoggyToolWindowManagerDescriptor(MyDoggyToolWindowManager manager) {
        this.manager = manager;
        this.pushAwayMode = PushAwayMode.VERTICAL;

        initMostRecent();
    }

    public void setPushAwayMode(PushAwayMode pushAwayMode) {
        if (this.pushAwayMode == pushAwayMode && checkParam)
            return;

        this.pushAwayMode = pushAwayMode;

        // Change mode
        int leftSplitLocation = manager.getBar(LEFT).getSplitDividerLocation();
        int rightSplitLocation = manager.getBar(RIGHT).getSplitDividerLocation();
        int topSplitLocation = manager.getBar(TOP).getSplitDividerLocation();
        int bottomSplitLocation = manager.getBar(BOTTOM).getSplitDividerLocation();

        manager.getBar(LEFT).getSplitPane().setRightComponent(null);
        manager.getBar(RIGHT).getSplitPane().setLeftComponent(null);
        manager.getBar(TOP).getSplitPane().setBottomComponent(null);
        manager.getBar(BOTTOM).getSplitPane().setTopComponent(null);

        switch (pushAwayMode) {
            case HORIZONTAL:
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(RIGHT).getSplitPane().setLeftComponent(manager.getBar(TOP).getSplitPane());
                manager.getBar(TOP).getSplitPane().setBottomComponent(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(BOTTOM).getSplitPane().setResizeWeight(1);

                manager.add(manager.getBar(LEFT).getSplitPane(), "1,1,FULL,FULL");

                manager.mainSplitPane = manager.getBar(BOTTOM).getSplitPane();
                manager.mainSplitPane.setTopComponent(manager.mainContainer);

                SwingUtil.repaintNow(manager);
                manager.getBar(LEFT).setSplitDividerLocation(leftSplitLocation);
                SwingUtil.repaint(manager.getBar(LEFT).getSplitPane());
                manager.getBar(BOTTOM).setSplitDividerLocation(bottomSplitLocation);
                SwingUtil.repaint(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(RIGHT).setSplitDividerLocation(rightSplitLocation);
                SwingUtil.repaint(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(TOP).setSplitDividerLocation(topSplitLocation);
                SwingUtil.repaint(manager.getBar(TOP).getSplitPane());
                break;
            case VERTICAL:
                manager.getBar(BOTTOM).getSplitPane().setTopComponent(manager.getBar(TOP).getSplitPane());
                manager.getBar(TOP).getSplitPane().setBottomComponent(manager.getBar(LEFT).getSplitPane());
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(RIGHT).getSplitPane().setResizeWeight(1);

                manager.add(manager.getBar(BOTTOM).getSplitPane(), "1,1,FULL,FULL");

                manager.mainSplitPane = manager.getBar(RIGHT).getSplitPane();
                manager.mainSplitPane.setLeftComponent(manager.mainContainer);

                SwingUtil.repaintNow(manager);
                manager.getBar(LEFT).setSplitDividerLocation(leftSplitLocation);
                SwingUtil.repaint(manager.getBar(LEFT).getSplitPane());
                manager.getBar(BOTTOM).setSplitDividerLocation(bottomSplitLocation);
                SwingUtil.repaint(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(RIGHT).setSplitDividerLocation(rightSplitLocation);
                SwingUtil.repaint(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(TOP).setSplitDividerLocation(topSplitLocation);
                SwingUtil.repaint(manager.getBar(TOP).getSplitPane());
                break;
            case ANTICLOCKWISE:
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(BOTTOM).getSplitPane().setTopComponent(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(RIGHT).getSplitPane().setLeftComponent(manager.getBar(TOP).getSplitPane());
                manager.getBar(TOP).getSplitPane().setResizeWeight(1);

                manager.add(manager.getBar(LEFT).getSplitPane(), "1,1,FULL,FULL");

                manager.mainSplitPane = manager.getBar(TOP).getSplitPane();
                manager.mainSplitPane.setBottomComponent(manager.mainContainer);

                SwingUtil.repaintNow(manager);
                manager.getBar(LEFT).setSplitDividerLocation(leftSplitLocation);
                SwingUtil.repaint(manager.getBar(LEFT).getSplitPane());
                manager.getBar(BOTTOM).setSplitDividerLocation(bottomSplitLocation);
                SwingUtil.repaint(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(RIGHT).setSplitDividerLocation(rightSplitLocation);
                SwingUtil.repaint(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(TOP).setSplitDividerLocation(topSplitLocation);
                SwingUtil.repaint(manager.getBar(TOP).getSplitPane());
                break;
            case MOST_RECENT:
                setSplit(mostRecentStack.get(3), mostRecentStack.get(2));
                setSplit(mostRecentStack.get(2), mostRecentStack.get(1));
                setSplit(mostRecentStack.get(1), mostRecentStack.get(0));

                manager.getBar(mostRecentStack.get(0)).getSplitPane().setResizeWeight(0d);
                manager.getBar(ToolWindowAnchor.BOTTOM).getSplitPane().setResizeWeight(1d);

                manager.add(manager.getBar(mostRecentStack.get(3)).getSplitPane(), "1,1,FULL,FULL");

                manager.mainSplitPane = manager.getBar(mostRecentStack.get(0)).getSplitPane();
                setSplitCmp(mostRecentStack.get(0), manager.mainContainer);

                Map<ToolWindowAnchor, Integer> tmp = new Hashtable<ToolWindowAnchor, Integer>();
                tmp.put(LEFT, leftSplitLocation);
                tmp.put(RIGHT, rightSplitLocation);
                tmp.put(BOTTOM, bottomSplitLocation);
                tmp.put(TOP, topSplitLocation);

                // Repaint
                SwingUtil.repaintNow(manager);
                manager.getBar(mostRecentStack.get(3)).setSplitDividerLocation(tmp.get(mostRecentStack.get(3)));
                SwingUtil.repaintNow(manager.getBar(mostRecentStack.get(3)).getSplitPane());
                manager.getBar(mostRecentStack.get(2)).setSplitDividerLocation(tmp.get(mostRecentStack.get(2)));
                SwingUtil.repaintNow(manager.getBar(mostRecentStack.get(2)).getSplitPane());
                manager.getBar(mostRecentStack.get(1)).setSplitDividerLocation(tmp.get(mostRecentStack.get(1)));
                SwingUtil.repaintNow(manager.getBar(mostRecentStack.get(1)).getSplitPane());
                manager.getBar(mostRecentStack.get(0)).setSplitDividerLocation(tmp.get(mostRecentStack.get(0)));
                SwingUtil.repaintNow(manager.getBar(mostRecentStack.get(0)).getSplitPane());
                break;
        }
    }

    public PushAwayMode getPushAwayMode() {
        return pushAwayMode;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("visible".equals(evt.getPropertyName())) {
            if (((Boolean) evt.getNewValue())) {
                ToolWindowAnchor target = ((ToolWindowDescriptor) evt.getSource()).getToolWindow().getAnchor();
                if (mostRecentStack.peek() == target)
                    return;

                // remove last
                mostRecentStack.remove(0);

                // check for target in stack
                for (Iterator<ToolWindowAnchor> iterator = mostRecentStack.iterator(); iterator.hasNext();) {
                    ToolWindowAnchor toolWindowAnchor = iterator.next();
                    if (toolWindowAnchor == target)
                        iterator.remove();
                }

                // put target at the head
                mostRecentStack.push(target);

                // check size
                if (mostRecentStack.size() < 4) {
                    addAnchor(LEFT);
                    addAnchor(RIGHT);
                    addAnchor(BOTTOM);
                    addAnchor(TOP);
                }

                if (pushAwayMode == PushAwayMode.MOST_RECENT) {
                    checkParam = false;
                    try {
                        setPushAwayMode(PushAwayMode.MOST_RECENT);
                    } finally {
                        checkParam = true;
                    }
                }
            }
        }
    }


    protected void initMostRecent() {
        this.mostRecentStack = new Stack<ToolWindowAnchor>();
        mostRecentStack.push(TOP);
        mostRecentStack.push(RIGHT);
        mostRecentStack.push(BOTTOM);
        mostRecentStack.push(LEFT);

        manager.propertyChangeSupport.addPropertyChangeListener("visible", this);
    }

    protected void setSplit(ToolWindowAnchor source, ToolWindowAnchor target) {
        setSplitCmp(source, manager.getBar(target).getSplitPane());
    }

    protected void setSplitCmp(ToolWindowAnchor source, Component cmp) {
        switch(source) {
            case LEFT :
                manager.getBar(source).getSplitPane().setRightComponent(cmp);
                break;
            case RIGHT :
                manager.getBar(source).getSplitPane().setLeftComponent(cmp);
                break;
            case TOP :
                manager.getBar(source).getSplitPane().setBottomComponent(cmp);
                break;
            case BOTTOM :
                manager.getBar(source).getSplitPane().setTopComponent(cmp);
                break;
        }
    }

    protected void addAnchor(ToolWindowAnchor target) {
        boolean found = false;
        for (ToolWindowAnchor toolWindowAnchor : mostRecentStack) {
            if (toolWindowAnchor == target) {
                found = true;
                break;
            }
        }
        if (!found)
            mostRecentStack.add(0, target);
    }

}
