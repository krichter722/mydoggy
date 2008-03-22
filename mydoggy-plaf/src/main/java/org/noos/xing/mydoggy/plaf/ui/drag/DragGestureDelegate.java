package org.noos.xing.mydoggy.plaf.ui.drag;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DragGestureDelegate implements DragGesture {
    protected DragGesture dragGesture;

    protected DragGestureDelegate(DragGesture dragGesture) {
        this.dragGesture = dragGesture;
    }

    public DragGestureDelegate() {
    }

    public DragGesture getDragGesture() {
        return dragGesture;
    }

    public void setDragGesture(DragGesture dragGesture) {
        this.dragGesture = dragGesture;
        if (dragGesture == null)
            this.dragGesture = new DummyDragGesture();
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        dragGesture.dragGestureRecognized(dge);
    }

    public void dragMouseMoved(DragSourceDragEvent dsde) {
        dragGesture.dragMouseMoved(dsde);
    }

    public void dragEnter(DragSourceDragEvent dsde) {
        dragGesture.dragEnter(dsde);
    }

    public void dragOver(DragSourceDragEvent dsde) {
        dragGesture.dragOver(dsde);
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
        dragGesture.dropActionChanged(dsde);
    }

    public void dragExit(DragSourceEvent dse) {
        dragGesture.dragExit(dse);
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        dragGesture.dragDropEnd(dsde);
    }

    protected class DummyDragGesture implements DragGesture {

        public void dragGestureRecognized(DragGestureEvent dge) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragEnter(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragOver(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dropActionChanged(DragSourceDragEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragExit(DragSourceEvent dse) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}