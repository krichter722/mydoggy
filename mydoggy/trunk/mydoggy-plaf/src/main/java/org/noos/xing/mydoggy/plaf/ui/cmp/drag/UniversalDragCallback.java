package org.noos.xing.mydoggy.plaf.ui.cmp.drag;

import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.ToolWindow;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public interface UniversalDragCallback {

    boolean accept(DragGestureEvent dge);

    boolean startDrag(DragGestureEvent dge, DragSourceListener dragSourceListener);

    GlassPanel getGlassPanel();

    Image getGhostImage();

    MyDoggyToolWindowManager getManager();

    ToolWindow getToolWindow();
}
