package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.DockableDropPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowScrollBar;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.image.BufferedImage;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class DragListenerAdapter implements DragListener, Cleaner {
    protected MyDoggyToolWindowManager manager;
    protected DockableDescriptor descriptor;

    // Fields for ghost image during the drag...
    protected BufferedImage ghostImage;
    protected BufferedImage updatedGhostImage;

    // Fields for the drop support...
    protected DockableDropPanel lastDropPanel;
    protected ToolWindowAnchor lastBarAnchor;


    protected DragListenerAdapter(DockableDescriptor descriptor) {
        this.descriptor = descriptor;
        this.manager = descriptor.getManager();
    }

    protected DragListenerAdapter(MyDoggyToolWindowManager manager) {
        this.manager = manager;
    }


    public void cleanup() {
        descriptor = null;
        manager = null;
    }


    public void dragGestureRecognized(DragGestureEvent dge) {
        lastDropPanel = null;
        lastBarAnchor = null;
    }

    public void dragMouseMoved(DragSourceDragEvent dsde) {
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
    }


    protected boolean acquireLocks() {
        if (isDragEnabled()) {
            if (DragAndDropLock.isLocked()) {
                DragAndDropLock.setDragAndDropStarted(false);
                return false;
            }
            DragAndDropLock.setLocked(true);
            DragAndDropLock.setDragAndDropStarted(true);
            return true;
        }

        return false;
    }

    protected void releaseLocks() {
        releaseLocksOne();
        releaseLocksTwo();
    }

    protected void releaseLocksOne() {
        DragAndDropLock.setDragAndDropStarted(false);
    }

    protected void releaseLocksTwo() {
        DragAndDropLock.setLocked(false);
    }

    protected boolean checkStatus() {
        return DragAndDropLock.isDragAndDropStarted() && ghostImage != null;
    }


    protected boolean isDragEnabled() {
        return SwingUtil.getBoolean(MyDoggyKeySpace.DRAG_ENABLED, true); 
    }

    protected void setGhostImage(Point point, BufferedImage ghostImage) {
        updateGhostImage(point, ghostImage);
        this.ghostImage = ghostImage;
    }

    protected void updateGhostImage(Point point, BufferedImage ghostImage) {
        GlassPanel glassPane = manager.getGlassPanel();
        glassPane.setVisible(true);
        glassPane.setPoint(SwingUtil.convertPointFromScreen(point, glassPane));
        glassPane.setDraggingImage(ghostImage);
        glassPane.repaint();
        this.updatedGhostImage = ghostImage;
    }

    protected void updateGhostImage(Point point) {
        GlassPanel glassPane = manager.getGlassPanel();
        glassPane.setPoint(SwingUtil.convertPointFromScreen(point, glassPane));
        glassPane.repaint();
    }

    protected void resetGhostImage() {
        GlassPanel glassPane = manager.getGlassPanel();
        Rectangle dirtyRegion = glassPane.getRepaintRect();
        glassPane.setDraggingImage(null);
        glassPane.repaint(dirtyRegion);
    }

    protected void cleanupGhostImage() {
        GlassPanel glassPane = manager.getGlassPanel();
        glassPane.setDraggingImage(null);
        glassPane.setVisible(false);
        ghostImage = null;
        SwingUtilities.getWindowAncestor(manager).repaint();
    }


    protected void updateDropTarget(DragSourceDragEvent event) {
        Point location = event.getLocation();
        Container source = manager.getLayeredPane();
        Component deepestCmp = null;

        // Check is the point is on a mydoggy window. (FloatingWindow or ContentWindow)
        for (Window window : SwingUtil.getMyDoggyTopContainers()) {
            if (!window.isVisible())
                continue;

            location = event.getLocation();

            SwingUtilities.convertPointFromScreen(location, window);
            deepestCmp = SwingUtilities.getDeepestComponentAt(window, location.x, location.y);

            if (deepestCmp != null)
                break;
        }

        // Check is the point is on the layered pane of the manager...for FLOATING_LIVE
        if (deepestCmp == null && source.getComponentCount() > 0) {
            SwingUtilities.convertPointFromScreen(location, source);
            deepestCmp = SwingUtilities.getDeepestComponentAt(source, location.x, location.y);
        }

        // Check is the point is on the manager
        if (deepestCmp == null) {
            location = event.getLocation();
            SwingUtilities.convertPointFromScreen(location, manager);
            deepestCmp = SwingUtilities.getDeepestComponentAt(manager, location.x, location.y);
        }

//        System.out.println("deepestCmp = " + deepestCmp);

        if (deepestCmp != null) {
            DockableDropPanel dockableDropPanel = SwingUtil.getParent(deepestCmp, DockableDropPanel.class);

            if (dockableDropPanel != null) {
                // the point is on a dockable drop panel...


                if (lastDropPanel != dockableDropPanel) {
                    if (lastDropPanel != null)
                        lastDropPanel.dragExit();

                    if (dockableDropPanel.dragStart(event.getDragSourceContext().getTransferable(),
                                                    event.getDragSourceContext().getSourceActions())) {
                        lastDropPanel = dockableDropPanel;
                    } else
                        lastDropPanel = null;
                } else {
                    location = event.getLocation();
                    SwingUtilities.convertPointFromScreen(location, dockableDropPanel);

                    dockableDropPanel.dragOver(location);
                }
                lastBarAnchor = null;
            } else {
                dockableDropDragExit();

                // Check if the point is on a ToolWindowBar
                ToolWindowScrollBar toolWindowScrollBar = SwingUtil.getParent(deepestCmp, ToolWindowScrollBar.class);
                if (toolWindowScrollBar != null)  {
                    lastBarAnchor = toolWindowScrollBar.getToolWindowBar().getAnchor();
                } else
                    lastBarAnchor = null;
            }
        } else {
            dockableDropDragExit();
            lastBarAnchor = null;
        }
    }

    protected void dockableDropDragEnd() {
        if (lastDropPanel != null) {
            lastDropPanel.dragEnd();
        }
    }

    protected void dockableDropDragExit() {
        if (lastDropPanel != null) {
            lastDropPanel.dragExit();
            lastDropPanel = null;
        }
    }

}
