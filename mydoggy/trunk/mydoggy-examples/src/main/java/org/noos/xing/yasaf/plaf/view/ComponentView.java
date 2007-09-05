package org.noos.xing.yasaf.plaf.view;

import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class ComponentView implements View {
    protected Component component;
    protected ViewContext viewContext;

    protected ComponentView(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.component = initComponent();
        initListeners();
    }

    public Component getComponent() {
        return component;
    }

    protected abstract Component initComponent();

    protected void initListeners() {
    }

}
