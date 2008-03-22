package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGesture;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class CustomDockableDescriptor implements DockableDescriptor {

    protected MyDoggyToolWindowManager manager;

    protected String id;
    protected boolean available;
    protected ToolWindowAnchor anchor;
    protected JComponent representativeAnchor;
    protected int anchorIndex;


    public CustomDockableDescriptor(MyDoggyToolWindowManager manager) {
        this(manager, LEFT);
    }

    protected CustomDockableDescriptor(MyDoggyToolWindowManager manager, ToolWindowAnchor anchor) {
        this.manager = manager;
        this.anchor = anchor;
        this.anchorIndex = -1;
        this.id = "CustomDockableDescriptor" + System.identityHashCode(this);
        this.manager.putDockableDescriptor(id, this);
        this.available = false;
    }

    public void setAnchor(ToolWindowAnchor anchor, int index) {
        setAvailable(false);

        this.anchor = anchor;
        this.anchorIndex = index;

        setAvailable(true);
    }

    public ToolWindowAnchor getAnchor() {
        return anchor;
    }

    public void setAvailable(boolean available) {
        if (this.available == available)
            return;

        if (available) {
            manager.propertyChange(
                    new UserPropertyChangeEvent(this, "available", false, true,
                                                new Object[]{anchorIndex, false}
                    )
            );
        } else {
            manager.propertyChange(
                    new UserPropertyChangeEvent(this, "available", true, false,
                                                new Object[]{-1, false}
                    )
            );
        }
        this.available = available;
    }

    public boolean isAvailable(boolean available) {
        return available;
    }

    public DockableType getDockableType() {
        return DockableType.CUSTOM;
    }

    public Dockable getDockable() {
        return null;
    }

    public JComponent getRepresentativeAnchor() {
        return representativeAnchor;
    }

    public void resetRepresentativeAnchor() {
        representativeAnchor = null;
    }

    public int getRepresentativeAnchorIndex() {
        if (representativeAnchor == null)
            return anchorIndex;

        return getToolBar().getRepresentativeAnchorIndex(representativeAnchor);
    }



    public ResourceManager getResourceManager() {
        return manager.getResourceManager();
    }

    public MyDoggyToolWindowManager getManager() {
        return manager;
    }

    public MyDoggyToolWindowBar getToolBar() {
        return manager.getBar(getAnchor());
    }

    public MyDoggyToolWindowBar getToolBar(ToolWindowAnchor anchor) {
        return manager.getBar(anchor);
    }

    public boolean isDragImageAvailable() {
        return false;
    }

    public Component getComponentForDragImage() {
        return null;
    }


    protected void registerDragGesture(Component c, DragGesture dragGesture) {
        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_MOVE, dragGesture);
        dragSource.addDragSourceMotionListener(dragGesture);
    }

    protected class RepresentativeAnchorUIDragGesture extends DragGestureAdapter {
         private ToolWindowAnchor lastAnchor;

         public RepresentativeAnchorUIDragGesture(DockableDescriptor descriptor) {
             super(descriptor);
         }

         public void dragGestureRecognized(DragGestureEvent dge) {
             // Acquire locks
             acquireLocks();

             // Start Drag
             dge.startDrag(Cursor.getDefaultCursor(),
                           new MyDoggyTransferable(manager,
                                                   MyDoggyTransferable.BAR2BAR_DF,
                                                   id),
                           this);

             // Fire startDrag Event
             descriptor.getToolBar().propertyChange(new PropertyChangeEvent(representativeAnchor, "startDrag", null, dge));

             // Setup ghostImage
             if (getResourceManager().getBoolean("drag.icon.useDefault", false)) {
                 setGhostImage(dge.getDragOrigin(),
                               getResourceManager().getBufferedImage(MyDoggyKeySpace.DRAG));
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
             ToolWindowAnchor newAnchor = manager.getToolWindowAnchor(
                     SwingUtil.convertPointFromScreen(dsde.getLocation(), manager)
             );

             // Produce updatedGhostImage
             if (newAnchor != lastAnchor) {
                 if (!getResourceManager().getBoolean("drag.icon.useDefault", false)) {
                     resetGhostImage();

                     if (newAnchor == null) {
                         updatedGhostImage = ghostImage;
                         manager.getBar(lastAnchor).setTempShowed(false);
                     } else {
                         if (manager.getBar(newAnchor).getAvailableTools() == 0)
                             manager.getBar(newAnchor).setTempShowed(true);

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

             updateGhostImage(dsde.getLocation(), updatedGhostImage);
         }

         public void dragDropEnd(DragSourceDropEvent dsde) {
             if (!checkStatus())
                 return;

             releaseLocksOne();

             // Restore graphics
             if (lastAnchor != null)
                 manager.getBar(lastAnchor).setTempShowed(false);
             else {
                 manager.getBar(LEFT).setTempShowed(false);
                 manager.getBar(RIGHT).setTempShowed(false);
                 manager.getBar(TOP).setTempShowed(false);
                 manager.getBar(BOTTOM).setTempShowed(false);
             }

             if (lastAnchor != null)
                 descriptor.getToolBar(lastAnchor).propertyChange(new PropertyChangeEvent(representativeAnchor, "endDrag", null, dsde));
             else
                 descriptor.getToolBar().propertyChange(new PropertyChangeEvent(representativeAnchor, "endDrag", null, dsde));             

             // cleanup glassPane
             cleanupGhostImage();
             lastAnchor = null;

             releaseLocksTwo();
         }

     }

}
