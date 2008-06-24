package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.util.MyDoggyUtil;
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
public abstract class DragGestureAdapter implements DragGesture, Cleaner {
    protected DockableDescriptor descriptor;
    protected MyDoggyToolWindowManager manager;
    protected BufferedImage ghostImage;
    protected BufferedImage updatedGhostImage;


    protected DragGestureAdapter(DockableDescriptor descriptor) {
        this.descriptor = descriptor;
        this.manager = descriptor.getManager();
    }

    protected DragGestureAdapter(MyDoggyToolWindowManager manager) {
        this.manager = manager;
    }


    public void cleanup() {
        descriptor = null;
        manager = null;
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
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
        if  (isDragEnabled()) {
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

    protected boolean checkStatus() {
        return DragAndDropLock.isDragAndDropStarted() && ghostImage != null;
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

    protected boolean isDragEnabled() {
        return MyDoggyUtil.getBoolean("drag.enabled", true); 
    }

}
