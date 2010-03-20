package org.noos.xing.mydoggy.plaf.ui.drag;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DragListenerDelegate implements DragListener {
    protected DragListener dragListener;


    public DragListenerDelegate(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public DragListenerDelegate() {
//        System.out.println(System.identityHashCode(this));
    }


    public DragListener getDragListener() {
        return dragListener;
    }


    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;

        if (dragListener == null)
            this.dragListener = new DummyDragListener();
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        dragListener.dragGestureRecognized(dge);
    }

    public void dragMouseMoved(DragSourceDragEvent dsde) {
        dragListener.dragMouseMoved(dsde);
    }

    public void dragEnter(DragSourceDragEvent dsde) {
        dragListener.dragEnter(dsde);
    }

    public void dragOver(DragSourceDragEvent dsde) {
        dragListener.dragOver(dsde);
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
        dragListener.dropActionChanged(dsde);
    }

    public void dragExit(DragSourceEvent dse) {
        dragListener.dragExit(dse);
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        dragListener.dragDropEnd(dsde);
    }


    public class DummyDragListener implements DragListener {

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
    }

}