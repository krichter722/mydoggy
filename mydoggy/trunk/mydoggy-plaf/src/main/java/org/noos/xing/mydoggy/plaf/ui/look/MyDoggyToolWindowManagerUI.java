package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyContent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowGroup;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.DockableDropPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitDockableContainer;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowManagerUI extends BasicPanelUI {
    public final Object sync = new Object();    //  TODO: is public the right visibility?

    protected boolean firePublic = true;


    protected MyDoggyToolWindowManager toolWindowManager;
    protected TableLayout contentPaneLayout;

    protected JSplitPane mainSplitPane;
    protected JPanel mainContainer;
    protected MultiSplitDockableContainer toolDockableContainer;

    protected Object activeToolWindowId;

    protected ToolWindowGroup showingGroup;

    protected Component lastFocusOwner = null;

    // Support for content manager disabling
    protected Component oldMainContent = null;
    protected boolean dockableMainContentMode = false;


    void syncPanel(ToolWindowAnchor anchor) {
        boolean revalidate = false;

        MyDoggyToolWindowBar toolWindowBar = toolWindowManager.getBar(anchor);

        if (anchor == LEFT) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShown() && contentPaneLayout.getColumn(0) != 0) {
                contentPaneLayout.setColumn(0, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShown()) && contentPaneLayout.getColumn(0) == 0) {
                contentPaneLayout.setColumn(0, toolWindowManager.getBar(LEFT).getLength());
                revalidate = true;
            }
        } else if (anchor == RIGHT) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShown() && contentPaneLayout.getColumn(2) != 0) {
                contentPaneLayout.setColumn(2, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShown()) && contentPaneLayout.getColumn(2) == 0) {
                contentPaneLayout.setColumn(2, toolWindowManager.getBar(RIGHT).getLength());
                revalidate = true;
            }
        } else if (anchor == TOP) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShown() && contentPaneLayout.getRow(0) != 0) {
                contentPaneLayout.setRow(0, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShown()) && contentPaneLayout.getRow(0) == 0) {
                contentPaneLayout.setRow(0, toolWindowManager.getBar(TOP).getLength());
                revalidate = true;
            }
        } else if (anchor == BOTTOM) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShown() && contentPaneLayout.getRow(2) != 0) {
                contentPaneLayout.setRow(2, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShown()) && contentPaneLayout.getRow(2) == 0) {
                contentPaneLayout.setRow(2, toolWindowManager.getBar(BOTTOM).getLength());
                revalidate = true;
            }
        }

        if (revalidate)
            SwingUtil.repaint(toolWindowManager);
    }

    public void ensureContentVisible(Content content) {
        // Check if any toolwindow is maximized and restore it
        for (ToolWindowDescriptor t : toolWindowManager.getToolWindowDescriptors()) {
            if (t.getToolWindow().isMaximized() && !t.isFloatingWindow() && !t.getToolWindow().isDetached()) {
                t.getToolWindow().setMaximized(false);
            }
        }

        // Is content area large enough?
        if (mainContainer.getWidth() < 50 || mainContainer.getHeight() < 50) {
            // If not, reset split panes to equal divisions
            mainSplitPane.setDividerLocation(0.5);
            for (MyDoggyToolWindowBar bar : toolWindowManager.getBars()) {
                bar.getSplitPane().setDividerLocation(0.5);
            }
        }
    }

    public void setMainContent(Component content) {
        if (content == null)
            resetMainContent();
        else {
            if (dockableMainContentMode) {
                oldMainContent = content;
            } else {
                mainContainer.setOpaque(false);
                mainContainer.removeAll();
                mainContainer.add(content, "0,0,FULL,FULL");

                mainSplitPane.invalidate();
                mainSplitPane.validate();

                SwingUtil.repaint(mainSplitPane);
            }
        }
    }

    public void resetMainContent() {
        if (dockableMainContentMode) {
            oldMainContent = null;
        } else {
            mainContainer.removeAll();
            SwingUtil.repaint(mainSplitPane);
            mainContainer.setOpaque(true);
        }
    }

    public Container getMainContainer() {
        return mainContainer;
    }

    public Component getMainContent() {
        return (mainContainer.getComponentCount() == 0) ? null : mainContainer.getComponent(0);
    }

    public void setDockableMainContentMode(boolean enable) {
        if (enable) {
            toolDockableContainer = new MultiSplitDockableContainer(toolWindowManager, JSplitPane.VERTICAL_SPLIT);
            toolDockableContainer.setStoreLayout(false);

            DockableDropPanel dockableDropPanel = new ToolDockableDropPanel();
//            dockableDropPanel.setDropTarget(new ToolWindowCommonMultiSplitDropTarget(dockableDropPanel, MyDoggyToolWindowManager.this));
            dockableDropPanel.setComponent(toolDockableContainer);

            oldMainContent = getMainContent();
            setMainContent(dockableDropPanel);
            dockableMainContentMode = true;
        } else {
            dockableMainContentMode = false;
            setMainContent(oldMainContent);
        }
    }

    public Rectangle getBoundsToScreen(Rectangle bounds, Component ref) {
        Point location = bounds.getLocation();
        SwingUtilities.convertPointToScreen(location, ref);
        bounds.setLocation(location);
        bounds.y += getJMenuBarExtraHeight();

        return bounds;

    }

    public int getJMenuBarExtraHeight() {
        JMenuBar jMenuBar = toolWindowManager.getRootPane().getJMenuBar();

        if (jMenuBar != null && jMenuBar.isVisible())
            return jMenuBar.getHeight();
        return 0;
    }



    public class AvailablePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            DockableDescriptor descriptor = (DockableDescriptor) evt.getSource();
            ToolWindowAnchor target = descriptor.getAnchor();

            // Notify specific bar
            toolWindowManager.getBar(target).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(target);
        }
    }

    public class ShowUnavailableToolsPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            for (MyDoggyToolWindowBar bar : toolWindowManager.getBars())
                bar.propertyChange(evt);

            // Syncronize bars panel
            syncPanel(LEFT);
            syncPanel(RIGHT);
            syncPanel(TOP);
            syncPanel(BOTTOM);
        }
    }

    public class VisiblePropertyChangeListener implements PropertyChangeListener {
        boolean showingGroupValueAdj = false;

        public void propertyChange(PropertyChangeEvent evt) {
            // Request by email: MyDoggy and IntelliJ IDEA GUI Editor
            SwingUtil.revalidate(toolWindowManager);

            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            // Fire "visible.before" to all bars
            PropertyChangeEvent event = new PropertyChangeEvent(evt.getSource(), "visible.before",
                                                                evt.getOldValue(), evt.getNewValue());
            for (MyDoggyToolWindowBar bar : toolWindowManager.getBars())
                bar.propertyChange(event);

            // Fire "visible" to specific bar
            toolWindowManager.getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(descriptor.getToolWindow().getAnchor());

            // Support for implicit group...
            synchronized (sync) {
                if ((showingGroup == null || showingGroup == toolWindowManager.getToolWindowGroup()) && Boolean.TRUE.equals(evt.getNewValue()) && !showingGroupValueAdj) {
                    showingGroupValueAdj = true;
                    try {
                        for (ToolWindowGroup group : toolWindowManager.getToolWindowGroups()) {
                            if (group.isImplicit() && group.containesToolWindow(descriptor.getToolWindow())) {
                                for (ToolWindow tool : group.getToolsWindow()) {
                                    if (tool != descriptor.getToolWindow())
                                        tool.aggregate();
                                }
                                break;
                            }
                        }
                    } finally {
                        showingGroupValueAdj = false;
                    }
                }
            }
        }
    }

    public class ActivePropertyChangeListener implements PropertyChangeListener {

        public synchronized void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            // Fire "active.before" for all bars
            PropertyChangeEvent event = new PropertyChangeEvent(evt.getSource(), "active.before",
                                                                evt.getOldValue(), evt.getNewValue());
            for (MyDoggyToolWindowBar bar : toolWindowManager.getBars())
                bar.propertyChange(event);

            // Fire "active" for specific bar
            toolWindowManager.getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);

            if (Boolean.FALSE.equals(evt.getNewValue())) {
                activeToolWindowId = null;

                if (lastFocusOwner != null) {
                    boolean shouldRequest = true;

                    for (MyDoggyToolWindowBar bar : toolWindowManager.getBars()) {
                        if (bar.isValueAdjusting() &&
                            toolWindowManager.getBar(descriptor.getToolWindow().getAnchor()) == bar) {
                            shouldRequest = false;
                            break;
                        }
                    }

                    if (shouldRequest)
                        SwingUtil.requestFocus(lastFocusOwner);
                }
            } else
                activeToolWindowId = descriptor.getToolWindow().getId();
        }
    }

    public class AnchorPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            ToolWindowAnchor oldAnchor = (ToolWindowAnchor) evt.getOldValue();
            ToolWindowAnchor newAnchor = (ToolWindowAnchor) evt.getNewValue();
            boolean force = false;
            if (oldAnchor == null) {
                oldAnchor = newAnchor;
                force = true;
            }

            ToolWindowType toolType = descriptor.getToolWindow().getType();
            if (toolType == ToolWindowType.FLOATING ||
                toolType == ToolWindowType.FLOATING_FREE ||
                toolType == ToolWindowType.FLOATING_LIVE ||
                force ||
                !descriptor.getToolWindow().isAvailable()) {

                PropertyChangeEvent avEvent = new UserPropertyChangeEvent(evt.getSource(), "available", true, false, new Object[]{-1, true});
                toolWindowManager.getBar(oldAnchor).propertyChange(avEvent);
                syncPanel(oldAnchor);

                assert evt instanceof UserPropertyChangeEvent;
                avEvent = new UserPropertyChangeEvent(evt.getSource(), "available", false, true,
                                                      new Object[]{((UserPropertyChangeEvent) evt).getUserObject(), true});
                toolWindowManager.getBar(newAnchor).propertyChange(avEvent);
                syncPanel(newAnchor);
            }

//            for (ToolWindowDescriptor tool : tools.values())
//                tool.getToolWindowContainer().propertyChange(evt);

            syncPanel(oldAnchor);
            syncPanel(newAnchor);

            if (force) {
                // Force reordering of aggregated tools.
//                toolWindowManager.getBar(newAnchor).propertyChange(
//                        new PropertyChangeEvent(evt.getSource(), "visible.reordering", false, true)
//                );
            }
        }

    }

    public class TypePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();

            toolWindowDescriptor.getToolBar().propertyChange(evt);

            syncPanel(toolWindowDescriptor.getToolWindow().getAnchor());
        }
    }

    public class IndexChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            ToolWindow modifiedTool = descriptor.getToolWindow();

            int newIndex = (Integer) evt.getNewValue();

            if (newIndex > 0) {
                for (ToolWindow toolWindow : toolWindowManager.getToolWindows()) {
                    if (toolWindow != modifiedTool && toolWindow.getIndex() == newIndex) {
                        toolWindow.setIndex(-1);
                        break;
                    }
                }
            }

            toolWindowManager.getBar(modifiedTool.getAnchor()).propertyChange(evt);
        }
    }

    public class IconChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            descriptor.getToolBar().propertyChange(evt);
        }

    }

    public class UpdateUIChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
/* TODO: change this...
            contentManager.updateUI();

            for (ToolWindowDescriptor descriptor : toolWindowManager.getToolWindowDescriptors()) {
                descriptor.updateUI();
            }
*/
        }
    }

    public class MaximizedChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
//            toolWindowDescriptor.getToolWindowContainer().propertyChange(evt);

            // Notify specific bar
            toolWindowManager.getBar(toolWindowDescriptor.getToolWindow().getAnchor()).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(toolWindowDescriptor.getToolWindow().getAnchor());
        }
    }

    public class ResourceManagerListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            SwingUtil.repaint(toolWindowManager);
        }
    }

    public class BarLengthListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowBar bar = (ToolWindowBar) evt.getSource();
            switch (bar.getAnchor()) {
                case LEFT:
                    if (contentPaneLayout.getColumn(0) != 0)
                        contentPaneLayout.setColumn(0, bar.getLength());
                    break;
                case RIGHT:
                    if (contentPaneLayout.getColumn(2) != 0)
                        contentPaneLayout.setColumn(2, bar.getLength());
                    break;
                case TOP:
                    if (contentPaneLayout.getRow(0) != 0)
                        contentPaneLayout.setRow(0, bar.getLength());
                    break;
                case BOTTOM:
                    if (contentPaneLayout.getRow(2) != 0)
                        contentPaneLayout.setRow(2, bar.getLength());
                    break;
            }
            SwingUtil.repaint(toolWindowManager);
        }
    }

    public class FocusOwnerChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Component newFocusOwner = (Component) evt.getNewValue();
            if (newFocusOwner != null && SwingUtilities.isDescendingFrom(newFocusOwner, mainContainer))
                lastFocusOwner = newFocusOwner;
        }
    }

    public class ContentMananagerEnabledChangeListener implements PropertyChangeListener {

        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getSource() instanceof ContentManager) {
                //TODO (+) support request from Elvis...there are focus problem...                

                final MyDoggyToolWindowGroup group = new MyDoggyToolWindowGroup(toolWindowManager, "temp", true);
                for (ToolWindow toolWindow : toolWindowManager.getToolWindows()) {
                    if (toolWindow.isVisible() && toolWindow.getType() == ToolWindowType.DOCKED)
                        group.addToolWindow(toolWindow);
                }

                if (group.getToolsWindow().length > 0) {

/*
                    PropertyChangeListener listener = new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
                            System.out.println("ERORRE :" + SwingUtil.toString(evt));
                        }
                    };
*/
                    try {
//                        group.getToolsWindow()[0].addPropertyChangeListener(listener);

                        firePublic = false;

                        group.setVisible(false);
                        setDockableMainContentMode(!(Boolean) evt.getNewValue());
                        group.setVisible(true);
                    } finally {
                        firePublic = true;
//                        group.getToolsWindow()[0].removePropertyChangeListener(listener);
                    }
                } else
                    setDockableMainContentMode(!(Boolean) evt.getNewValue());
            }
        }

    }

    public class InternalContentMananagerListener implements ContentManagerListener,
                                                             PropertyChangeListener {

        public void contentAdded(ContentManagerEvent event) {
            MyDoggyContent content = (MyDoggyContent) event.getContent();
            ensureContentVisible(content);
            content.addPlafPropertyChangeListener("ensureVisible", this);
        }

        public void contentRemoved(ContentManagerEvent event) {
            MyDoggyContent content = (MyDoggyContent) event.getContent();
            content.removePlafPropertyChangeListener(this);
        }

        public void contentSelected(ContentManagerEvent event) {
        }

        public void propertyChange(PropertyChangeEvent evt) {
            ensureContentVisible((Content) evt.getNewValue());
        }
    }


    public class ToolDockableDropPanel extends DockableDropPanel {

        public ToolDockableDropPanel() {
            super("toolWindow.container.", ToolWindow.class, Content.class);
        }

        public boolean dragStart(Transferable transferable, int action) {
            try {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                    if (System.identityHashCode(toolWindowManager) == (Integer) transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_MANAGER)) {
                        if (action == DnDConstants.ACTION_MOVE &&
                            (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF) ||
                             transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF) ||
                             transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)))

                            return super.dragStart(transferable, action);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean drop(Transferable transferable) {
            if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF)) {
                try {
                    ToolWindow toolWindow = toolWindowManager.getToolWindow(
                            transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_ID_DF)
                    );

                    if (toolWindow != null) {
                        // Move tool to another anchor

                        // Chech if it was a tab
                        if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)) {
                            // Remove from tab
                            ToolWindowTab tab = (ToolWindowTab) toolWindowManager.getDockable(
                                    transferable.getTransferData(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)
                            );
                            tab.getOwner().removeToolWindowTab(tab);
                            toolWindow = (ToolWindow) tab.getDockableDelegator();
                        }

                        ToolWindow onToolWindow = (ToolWindow) getOnDockable();
                        int anchorIndex = (onToolWindow != null) ? onToolWindow.getAnchorIndex() : -1;

                        if (toolWindow == onToolWindow)
                            return false;

                        boolean oldAggregateMode = toolWindow.isAggregateMode();
                        toolWindow.setAggregateMode(true);

                        ToolWindowAnchor dragAnchor = getOnAnchor();
                        try {
                            if (dragAnchor != null) {
                                switch (dragAnchor) {
                                    case LEFT:
                                        if (onToolWindow != null) {
                                            toolWindow.setAnchor(dragAnchor, anchorIndex != -1 ? anchorIndex - 1 : -1);
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.LEFT);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.setAnchor(dragAnchor, 0);
                                                toolWindow.aggregate(AggregationPosition.LEFT);
                                            }
                                        }
                                        break;
                                    case RIGHT:
                                        if (onToolWindow != null) {
                                            toolWindow.setAnchor(dragAnchor, anchorIndex != -1 ? anchorIndex + 1 : -1);
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.setAnchor(dragAnchor);
                                                toolWindow.aggregate(AggregationPosition.RIGHT);
                                            }
                                        }
                                        break;
                                    case BOTTOM:
                                        if (onToolWindow != null) {
                                            toolWindow.setAnchor(dragAnchor, anchorIndex != -1 ? anchorIndex + 1 : -1);
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.setAnchor(dragAnchor);
                                                toolWindow.aggregate(AggregationPosition.BOTTOM);
                                            }
                                        }
                                        break;
                                    case TOP:
                                        if (onToolWindow != null) {
                                            toolWindow.setAnchor(dragAnchor, anchorIndex != -1 ? anchorIndex - 1 : -1);
                                            toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                        } else {
                                            if (checkCondition(toolWindow)) {
                                                toolWindow.setAnchor(dragAnchor, 0);
                                                toolWindow.aggregate(AggregationPosition.TOP);
                                            }
                                        }
                                        break;
                                }
                            } else {
                                if (onToolWindow != null && toolWindow != onToolWindow) {
                                    onToolWindow.addToolWindowTab(toolWindow).setSelected(true);
                                } else {
                                    toolWindow.aggregate();
                                }
                            }
                            toolWindow.setActive(true);
                        } finally {
                            toolWindow.setAggregateMode(oldAggregateMode);
                        }

                        return true;
                    } else
                        return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)) {
                try {
                    Content content = toolWindowManager.getContentManager().getContent(
                            transferable.getTransferData(MyDoggyTransferable.CONTENT_ID_DF)
                    );

                    if (content != null) {
                        toolWindowManager.getContentManager().removeContent(content);

                        if (content.getDockableDelegator() != null) {
                            Dockable delegator = content.getDockableDelegator();

                            if (delegator instanceof ToolWindow) {
                                ToolWindow toolWindow = (ToolWindow) delegator;
                                ToolWindow onToolWindow = (ToolWindow) getOnDockable();
                                int anchorIndex = (onToolWindow != null) ? onToolWindow.getAnchorIndex() : -1;

                                if (toolWindow == onToolWindow)
                                    return false;

                                boolean oldAggregateMode = toolWindow.isAggregateMode();
                                toolWindow.setAggregateMode(true);
                                ToolWindowAnchor dragAnchor = getOnAnchor();
                                try {
                                    toolWindow.setAnchor(dragAnchor, anchorIndex);

                                    if (dragAnchor != null) {
                                        switch (dragAnchor) {
                                            case LEFT:
                                                if (onToolWindow != null)
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.LEFT);
                                                else
                                                    toolWindow.aggregate(AggregationPosition.LEFT);
                                                break;
                                            case RIGHT:
                                                if (onToolWindow != null)
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.RIGHT);
                                                else
                                                    toolWindow.aggregate(AggregationPosition.RIGHT);
                                                break;
                                            case BOTTOM:
                                                if (onToolWindow != null)
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.BOTTOM);
                                                else
                                                    toolWindow.aggregate(AggregationPosition.BOTTOM);
                                                break;
                                            case TOP:
                                                if (onToolWindow != null)
                                                    toolWindow.aggregate(onToolWindow, AggregationPosition.TOP);
                                                else
                                                    toolWindow.aggregate(AggregationPosition.TOP);
                                                break;
                                        }
                                    } else {
                                        if (onToolWindow != null) {
                                            onToolWindow.addToolWindowTab(toolWindow).setSelected(true);
                                        } else
                                            toolWindow.aggregate();
                                    }
                                    toolWindow.setActive(true);
                                } finally {
                                    toolWindow.setAggregateMode(oldAggregateMode);
                                }
                                return true;
                            }
                        } else {
                            return false;
                        }

                    } else
                        return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        protected boolean checkCondition(ToolWindow toolWindow) {
            return true;    // TODO: it's always true........is it right?
        }
    }
    
}
