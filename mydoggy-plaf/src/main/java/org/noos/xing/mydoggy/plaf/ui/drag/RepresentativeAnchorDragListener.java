package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RepresentativeAnchorDragListener extends DragListenerAdapter {
    protected ToolWindowAnchor lastAnchor;
    protected Component component;


    public RepresentativeAnchorDragListener(DockableDescriptor descriptor, Component component) {
        super(descriptor);
        this.component = component;
    }


    public void dragGestureRecognized(DragGestureEvent dge) {
        super.dragGestureRecognized(dge);

        // Acquire locks
        if (!acquireLocks())
            return;

        // Start Drag
        dge.startDrag(DragSource.DefaultMoveDrop,
                      createTransferable(),
                      this);

        // Fire startDrag Event
        descriptor.getToolBar().propertyChange(new PropertyChangeEvent(getComponent(), "startDrag", null, dge));

        // Setup ghostImage
        if (SwingUtil.getBoolean(MyDoggyKeySpace.DRAG_USE_DEFAULT_ICON, false)) {
            setGhostImage(dge.getDragOrigin(),
                          SwingUtil.getImage(MyDoggyKeySpace.DRAG));
        } else {
            JComponent representativeAnchor = descriptor.getRepresentativeAnchor();
            BufferedImage ghostImage = new BufferedImage(representativeAnchor.getWidth(),
                                                         representativeAnchor.getHeight(),
                                                         BufferedImage.TYPE_INT_RGB);
            representativeAnchor.print(ghostImage.createGraphics());

            setGhostImage(dge.getDragOrigin(), ghostImage);
        }

        lastAnchor = null;
    }

    public void dragMouseMoved(DragSourceDragEvent dsde) {
        if (!checkStatus())
            return;

        // Obtain anchor for location
        ToolWindowAnchor newAnchor = manager.getToolWindowAnchor(dsde.getLocation());

        // updated the ghost image
        if (newAnchor != lastAnchor) {
            if (!SwingUtil.getBoolean(MyDoggyKeySpace.DRAG_USE_DEFAULT_ICON, false)) {
                resetGhostImage();

                if (newAnchor == null) {
                    updatedGhostImage = ghostImage;
                    manager.getBar(lastAnchor).setTemporarilyVisible(false);
                } else {
                    if (manager.getBar(newAnchor).getAvailableTools() == 0)
                        manager.getBar(newAnchor).setTemporarilyVisible(true);

                    switch (newAnchor) {
                        case LEFT:
                            switch (descriptor.getAnchor()) {
                                case LEFT:
                                    updatedGhostImage = ghostImage;
                                    break;
                                case RIGHT:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, Math.PI);
                                    break;
                                default:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, 1.5 * Math.PI);
                                    break;
                            }
                            break;
                        case RIGHT:
                            switch (descriptor.getAnchor()) {
                                case LEFT:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, Math.PI);
                                    break;
                                case RIGHT:
                                    updatedGhostImage = ghostImage;
                                    break;
                                default:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, -1.5 * Math.PI);
                                    break;
                            }
                            break;
                        case TOP:
                        case BOTTOM:
                            switch (descriptor.getAnchor()) {
                                case LEFT:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, -1.5 * Math.PI);
                                    break;
                                case RIGHT:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, 1.5 * Math.PI);
                                    break;
                                default:
                                    updatedGhostImage = ghostImage;
                                    break;
                            }
                            break;
                    }
                }
            } else
                updatedGhostImage = ghostImage;

            lastAnchor = newAnchor;
        }

        // Update ghost image
        updateGhostImage(dsde.getLocation(), updatedGhostImage);

        // Update drop target
        updateDropTarget(dsde);
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        try {
            if (!checkStatus())
                return;

            releaseLocksOne();

            // Restore graphics
            manager.setBarsTemporarilyVisible(false);

            // Fire endDrag event
            descriptor.getToolBar().propertyChange(new PropertyChangeEvent(getComponent(), "endDrag", null, dsde));

            // cleanup glassPane
            cleanupGhostImage();
            lastAnchor = null;

            releaseLocksTwo();
        } finally {
            dockableDropDragEnd();
        }
    }


    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }


    protected Transferable createTransferable() {
        return new MyDoggyTransferable(manager, MyDoggyTransferable.CUSTOM_DESCRIPTOR_ID, descriptor.getDockable().getId()
        );
    }

}
