package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureInitiator;

import java.util.EventListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface TitleBarTabs extends DragGestureInitiator {

    void removeEventDispatcherlListener(EventListener eventListener);

    void addEventDispatcherlListener(EventListener eventListener);
}
