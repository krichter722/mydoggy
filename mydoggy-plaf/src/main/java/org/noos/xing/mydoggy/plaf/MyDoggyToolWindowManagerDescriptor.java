package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowManagerDescriptor extends PropertyChangeEventSource implements ToolWindowManagerDescriptor, PropertyChangeListener, MostRecentDescriptor {
    protected PushAwayMode pushAwayMode;
    protected MyDoggyToolWindowManager manager;
    protected boolean numberingEnabled;
    public boolean checkParam;
    protected boolean previewEnabled;
    protected boolean showUnavailableTools;
    protected Stack<ToolWindowAnchor> mostRecentStack;


    public MyDoggyToolWindowManagerDescriptor(MyDoggyToolWindowManager manager) {
        this.manager = manager;
        this.pushAwayMode = PushAwayMode.VERTICAL;
        this.numberingEnabled = this.previewEnabled = true;
        this.showUnavailableTools = false;
        this.checkParam = false;

        initMostRecent();
    }

    public void setPushAwayMode(PushAwayMode pushAwayMode) {
        if (this.pushAwayMode == pushAwayMode && checkParam)
            return;

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

                manager.getBar(LEFT).getSplitPane().setResizeWeight(0d);
                manager.getBar(BOTTOM).getSplitPane().setResizeWeight(1d);
                manager.getBar(TOP).getSplitPane().setResizeWeight(0d);
                manager.getBar(RIGHT).getSplitPane().setResizeWeight(1d);

                manager.add(manager.getBar(LEFT).getSplitPane(), "1,1,FULL,FULL");

                manager.setMainSplitPane(BOTTOM);

                SwingUtil.repaintNow(manager);
                manager.getBar(LEFT).setSplitDividerLocation(leftSplitLocation);
                SwingUtil.repaintNow(manager.getBar(LEFT).getSplitPane());
                manager.getBar(RIGHT).setSplitDividerLocation(rightSplitLocation);
                SwingUtil.repaintNow(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(TOP).setSplitDividerLocation(topSplitLocation);
                SwingUtil.repaintNow(manager.getBar(TOP).getSplitPane());
                manager.getBar(BOTTOM).setSplitDividerLocation(bottomSplitLocation);
                SwingUtil.repaintNow(manager.getBar(BOTTOM).getSplitPane());
                break;
            case VERTICAL:
                manager.getBar(BOTTOM).getSplitPane().setTopComponent(manager.getBar(TOP).getSplitPane());
                manager.getBar(TOP).getSplitPane().setBottomComponent(manager.getBar(LEFT).getSplitPane());
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(RIGHT).getSplitPane());

                manager.getBar(LEFT).getSplitPane().setResizeWeight(0d);
                manager.getBar(BOTTOM).getSplitPane().setResizeWeight(1d);
                manager.getBar(TOP).getSplitPane().setResizeWeight(0d);
                manager.getBar(RIGHT).getSplitPane().setResizeWeight(1d);

                manager.add(manager.getBar(BOTTOM).getSplitPane(), "1,1,FULL,FULL");

                manager.setMainSplitPane(RIGHT);

                SwingUtil.repaintNow(manager);
                manager.getBar(BOTTOM).setSplitDividerLocation(bottomSplitLocation);
                SwingUtil.repaintNow(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(TOP).setSplitDividerLocation(topSplitLocation);
                SwingUtil.repaintNow(manager.getBar(TOP).getSplitPane());
                manager.getBar(LEFT).setSplitDividerLocation(leftSplitLocation);
                SwingUtil.repaintNow(manager.getBar(LEFT).getSplitPane());
                manager.getBar(RIGHT).setSplitDividerLocation(rightSplitLocation);
                SwingUtil.repaintNow(manager.getBar(RIGHT).getSplitPane());
                break;
            case ANTICLOCKWISE:
                manager.getBar(LEFT).getSplitPane().setRightComponent(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(BOTTOM).getSplitPane().setTopComponent(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(RIGHT).getSplitPane().setLeftComponent(manager.getBar(TOP).getSplitPane());

                manager.getBar(RIGHT).getSplitPane().setResizeWeight(1d);
                manager.getBar(LEFT).getSplitPane().setResizeWeight(0d);
                manager.getBar(BOTTOM).getSplitPane().setResizeWeight(1d);
                manager.getBar(TOP).getSplitPane().setResizeWeight(0d);

                manager.add(manager.getBar(LEFT).getSplitPane(), "1,1,FULL,FULL");

                manager.setMainSplitPane(TOP);

                SwingUtil.repaintNow(manager);
                manager.getBar(LEFT).setSplitDividerLocation(leftSplitLocation);
                SwingUtil.repaintNow(manager.getBar(LEFT).getSplitPane());
                manager.getBar(BOTTOM).setSplitDividerLocation(bottomSplitLocation);
                SwingUtil.repaintNow(manager.getBar(BOTTOM).getSplitPane());
                manager.getBar(RIGHT).setSplitDividerLocation(rightSplitLocation);
                SwingUtil.repaintNow(manager.getBar(RIGHT).getSplitPane());
                manager.getBar(TOP).setSplitDividerLocation(topSplitLocation);
                SwingUtil.repaintNow(manager.getBar(TOP).getSplitPane());
                break;
            case MOST_RECENT:
                setSplit(mostRecentStack.get(3), mostRecentStack.get(2));
                setSplit(mostRecentStack.get(2), mostRecentStack.get(1));
                setSplit(mostRecentStack.get(1), mostRecentStack.get(0));

                manager.getBar(RIGHT).getSplitPane().setResizeWeight(1d);
                manager.getBar(LEFT).getSplitPane().setResizeWeight(0d);
                manager.getBar(BOTTOM).getSplitPane().setResizeWeight(1d);
                manager.getBar(TOP).getSplitPane().setResizeWeight(0d);

                manager.add(manager.getBar(mostRecentStack.get(3)).getSplitPane(), "1,1,FULL,FULL");

                manager.setMainSplitPane(mostRecentStack.get(0));

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

        // fire changing
        PushAwayMode old = this.pushAwayMode;
        this.pushAwayMode = pushAwayMode;
        firePropertyChangeEvent("pushAwayMode", old, this.pushAwayMode);
    }

    public PushAwayMode getPushAwayMode() {
        return pushAwayMode;
    }

    public PushAwayModeDescriptor getPushAwayModeDescriptor(PushAwayMode pushAwayMode) {
        switch (pushAwayMode) {
            case MOST_RECENT:
                return this;
            default:
                throw new IllegalArgumentException("There isn't any manager for mode. [mode : " + pushAwayMode + "]");
        }
    }

    public void setCornerComponent(Corner corner, Component component) {
        manager.setCornerComponent(corner, component);
    }

    public void setNumberingEnabled(boolean numberingEnabled) {
        if (this.numberingEnabled == numberingEnabled)
            return;

        boolean old = this.numberingEnabled;
        this.numberingEnabled = numberingEnabled;

        firePropertyChangeEvent("numberingEnabled", old, numberingEnabled);
    }

    public boolean isNumberingEnabled() {
        return numberingEnabled;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        if (this.previewEnabled == previewEnabled)
            return;

        boolean old = this.previewEnabled;
        this.previewEnabled = previewEnabled;

        firePropertyChangeEvent("previewEnabled", old, previewEnabled);
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public void setShowUnavailableTools(boolean showUnavailableTools) {
        if (this.showUnavailableTools == showUnavailableTools)
            return;

        boolean old = this.showUnavailableTools;
        this.showUnavailableTools = showUnavailableTools;

        firePropertyChangeEvent("showUnavailableTools", old, showUnavailableTools);
    }

    public boolean isShowUnavailableTools() {
        return showUnavailableTools;
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if ("visible".equals(evt.getPropertyName())) {
            if (((Boolean) evt.getNewValue())) {
                ToolWindowAnchor target = ((ToolWindowDescriptor) evt.getSource()).getToolWindow().getAnchor();
                addMostRecentAnchor(target);

                if (pushAwayMode == PushAwayMode.MOST_RECENT)
                    forceChangePushAwayMode(PushAwayMode.MOST_RECENT);
            }
        }
    }


    public void append(ToolWindowAnchor... anchors) {
        if (anchors == null)
            throw new NullPointerException("anchors cannot be null.");

        for (ToolWindowAnchor anchor : anchors) {
            addMostRecentAnchor(anchor);
        }

        if (pushAwayMode == PushAwayMode.MOST_RECENT)
            forceChangePushAwayMode(PushAwayMode.MOST_RECENT);
    }

    public ToolWindowAnchor[] getMostRecentAnchors() {
        return mostRecentStack.toArray(new ToolWindowAnchor[mostRecentStack.size()]);
    }


    protected void initMostRecent() {
        this.mostRecentStack = new Stack<ToolWindowAnchor>();
        mostRecentStack.push(TOP);
        mostRecentStack.push(RIGHT);
        mostRecentStack.push(BOTTOM);
        mostRecentStack.push(LEFT);

        manager.addInternalPropertyChangeListener("visible", this);
    }

    protected void addMostRecentAnchor(ToolWindowAnchor target) {
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
            mrCheckForAnchor(LEFT);
            mrCheckForAnchor(RIGHT);
            mrCheckForAnchor(BOTTOM);
            mrCheckForAnchor(TOP);
        }
    }

    protected void mrCheckForAnchor(ToolWindowAnchor target) {
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

    protected void setSplit(ToolWindowAnchor source, ToolWindowAnchor target) {
        setSplitComponent(source, manager.getBar(target).getSplitPane());
    }

    protected void setSplitComponent(ToolWindowAnchor source, Component cmp) {
        switch (source) {
            case LEFT:
                manager.getBar(source).getSplitPane().setRightComponent(cmp);
                break;
            case RIGHT:
                manager.getBar(source).getSplitPane().setLeftComponent(cmp);
                break;
            case TOP:
                manager.getBar(source).getSplitPane().setBottomComponent(cmp);
                break;
            case BOTTOM:
                manager.getBar(source).getSplitPane().setTopComponent(cmp);
                break;
        }
    }

    protected void forceChangePushAwayMode(PushAwayMode pushAwayMode) {
        checkParam = false;
        try {
            setPushAwayMode(pushAwayMode);
        } finally {
            checkParam = true;
        }
    }


}
