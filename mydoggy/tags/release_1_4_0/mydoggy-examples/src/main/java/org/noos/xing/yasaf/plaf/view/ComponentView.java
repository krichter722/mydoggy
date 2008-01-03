package org.noos.xing.yasaf.plaf.view;

import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;

import java.awt.*;
import java.awt.event.HierarchyListener;
import java.awt.event.HierarchyEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class ComponentView implements View {
    protected Component component;
    protected ViewContext viewContext;
    protected boolean first = false;

    protected ComponentView() {
        this(new MapViewContext());
    }

    protected ComponentView(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.component = initComponent();
        this.component.addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    if (!first) {
                        onFirstVisible();
                        first = true;
                    }
                }
            }
        });
        initListeners();
    }

    public Component getComponent() {
        return component;
    }

    protected abstract Component initComponent();

    protected void initListeners() {
    }

    protected void onFirstVisible() {
    }

}
