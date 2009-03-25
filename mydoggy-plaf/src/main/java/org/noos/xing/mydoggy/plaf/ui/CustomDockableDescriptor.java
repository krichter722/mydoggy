package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import static org.noos.xing.mydoggy.ToolWindowAnchor.LEFT;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.cleaner.CleanerAggregator;
import org.noos.xing.mydoggy.plaf.cleaner.DefaultCleanerAggregator;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.drag.DragListener;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.drag.RepresentativeAnchorDragListener;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDropEvent;
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
    protected boolean anchorPositionLocked = false;

    protected DragListener dragListener;
    protected CleanerAggregator cleaner;


    public CustomDockableDescriptor(MyDoggyToolWindowManager manager) {
        this(manager, LEFT);
    }

    public CustomDockableDescriptor(MyDoggyToolWindowManager manager, ToolWindowAnchor anchor) {
        this.manager = manager;
        this.anchor = anchor;
        this.anchorIndex = -1;
        this.id = "CustomDockableDescriptor" + System.identityHashCode(this);
        this.manager.putDockableDescriptor(id, this);
        this.available = false;
    }

    public CustomDockableDescriptor(MyDoggyToolWindowManager manager, ToolWindowAnchor anchor, String id) {
        this.manager = manager;
        this.anchor = anchor;
        this.anchorIndex = -1;
        this.id = id;
        this.manager.putDockableDescriptor(id, this);
        this.available = false;
    }


    public DockableType getDockableType() {
        return DockableType.CUSTOM;
    }

    public Dockable getDockable() {
        return null;
    }

    public void setAnchor(ToolWindowAnchor anchor, int index) {
        if (!isAvailable())
            return;

        setAvailable(false);

        this.anchor = anchor;
        this.anchorIndex = index;

        setAvailable(true);
    }

    public ToolWindowAnchor getAnchor() {
        return anchor;
    }

    public void setAnchorPositionLocked(boolean anchorPositionLocked) {
        this.anchorPositionLocked = anchorPositionLocked;
    }

    public boolean isAnchorPositionLocked() {
        return anchorPositionLocked;
    }

    public int getAnchorIndex() {
        if (representativeAnchor == null)
            return anchorIndex;

        return getToolBar().getRepresentativeAnchorIndex(representativeAnchor);
    }

    public void setAvailable(boolean available) {
        if (this.available == available)
            return;

        if (available) {
            manager.propertyChange(
                    new UserPropertyChangeEvent(this, "available", false, true,
                                                new Object[]{anchorIndex, true}
                    )
            );
        } else {
            manager.propertyChange(
                    new UserPropertyChangeEvent(this, "available", true, false,
                                                new Object[]{-1, true}
                    )
            );
        }
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isAvailableCountable() {
        return true;
    }


    public JComponent getRepresentativeAnchor() {
        return representativeAnchor;
    }

    public void resetRepresentativeAnchor() {
        representativeAnchor = null;
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

    public CleanerAggregator getCleaner() {
        if (cleaner == null)
            cleaner = new DefaultCleanerAggregator();
        return cleaner;
    }

    public boolean isDragImageAvailable() {
        return false;
    }

    public Component getComponentForDragImage() {
        return null;
    }


    public void cleanup() {
        getCleaner().cleanup();
        
        manager = null;
    }


    protected void registerDragListener(Component c) {
        if (dragListener == null)
            initDragListener();

        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_MOVE, dragListener);
        dragSource.addDragSourceMotionListener(dragListener);
    }

    protected void initDragListener() {
        this.dragListener = new CustomRepresentativeAnchorDragListener(this);
    }


    public class CustomRepresentativeAnchorDragListener extends RepresentativeAnchorDragListener {

        public CustomRepresentativeAnchorDragListener(DockableDescriptor descriptor) {
            super(descriptor, null);
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!checkStatus())
                return;

            releaseLocksOne();

            // Restore graphics
            manager.setBarsTemporarilyVisible(false);

            // Fire endDrag event
            if (lastAnchor != null && dsde.getDropSuccess())
                descriptor.getToolBar(lastAnchor).propertyChange(new PropertyChangeEvent(representativeAnchor, "endDrag", null, dsde));
            else
                descriptor.getToolBar().propertyChange(new PropertyChangeEvent(representativeAnchor, "endDrag", null, dsde));

            // cleanup glassPane
            cleanupGhostImage();
            lastAnchor = null;

            releaseLocksTwo();
        }


        public Component getComponent() {
            return representativeAnchor;
        }

        protected Transferable createTransferable() {
            return new MyDoggyTransferable(manager, MyDoggyTransferable.CUSTOM_DESCRIPTOR_ID, id);
        }
    }

}
