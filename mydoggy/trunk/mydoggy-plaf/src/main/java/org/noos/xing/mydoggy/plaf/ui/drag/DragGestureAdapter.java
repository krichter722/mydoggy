package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.drag.DragAndDropLock;
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
public abstract class DragGestureAdapter implements DragGesture {
    protected ToolWindowDescriptor descriptor;
    protected BufferedImage ghostImage;
    protected BufferedImage updatedGhostImage;

    protected DragGestureAdapter(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
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
        if (DragAndDropLock.isLocked()) {
            DragAndDropLock.setDragAndDropStarted(false);
            return false;
        }
        DragAndDropLock.setLocked(true);
        DragAndDropLock.setDragAndDropStarted(true);
        return true;
    }

    protected boolean checkStatus() {
        return DragAndDropLock.isDragAndDropStarted() && ghostImage != null;
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
        GlassPanel glassPane = descriptor.getManager().getGlassPanel();
        glassPane.setVisible(true);
        glassPane.setPoint(SwingUtil.convertPointFromScreen(point, glassPane));
        glassPane.setDraggingImage(ghostImage);
        glassPane.repaint();
        this.updatedGhostImage = ghostImage;
    }

    protected void updateGhostImage(Point point) {
        GlassPanel glassPane = descriptor.getManager().getGlassPanel();
        glassPane.setPoint(SwingUtil.convertPointFromScreen(point, glassPane));
        glassPane.repaint();
    }

    protected void resetGhostImage() {
        GlassPanel glassPane = descriptor.getManager().getGlassPanel();
        Rectangle dirtyRegion = glassPane.getRepaintRect();
        glassPane.setDraggingImage(null);
        glassPane.repaint(dirtyRegion);
    }

    protected void cleanupGhostImage() {
        GlassPanel glassPane = descriptor.getManager().getGlassPanel();
        glassPane.setDraggingImage(null);
        glassPane.setVisible(false);
        ghostImage = null;
        SwingUtilities.getWindowAncestor(descriptor.getManager()).repaint();
    }

}
