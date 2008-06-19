package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.drag.DragGesture;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureInitiator;
import org.noos.xing.mydoggy.plaf.ui.util.MouseEventDispatcher;

import java.awt.*;
import java.util.EventListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowTabContainer extends DragGestureInitiator {

    DragGesture getDragGesture();

    MouseEventDispatcher getMouseEventDispatcher();

    void removeEventDispatcherlListener(EventListener eventListener);

    void addEventDispatcherlListener(EventListener eventListener);

    void ensureVisible(Rectangle bounds);
}
