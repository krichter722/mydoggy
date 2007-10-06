package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.itest.*;
import org.noos.xing.mydoggy.itest.impl.NamedComponentFilter;
import org.noos.xing.mydoggy.ToolWindowAnchor;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class AbstractInteractiveTest implements InteractiveTest {
    private Container root;

    protected AbstractInteractiveTest(Container root) {
        this.root = root;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public Container setup() {
        return root;
    }

    public void dispose() {
    }

    public String toString() {
        return getName();
    }

    protected ComponentAdapter clickOn(InteractiveUI interactiveUI, String componentName) {
        return interactiveUI.getComponentLookuper().lookup(new NamedComponentFilter(componentName)).moveToCenter(500).click(ComponentAdapter.MouseButton.LEFT, 500);
    }

    protected ComponentAdapter drag(InteractiveUI interactiveUI, String from, String to) {
        interactiveUI.getComponentLookuper().lookup(new NamedComponentFilter(from)).moveToCenter(500).press(ComponentAdapter.MouseButton.LEFT);
        return interactiveUI.getComponentLookuper().lookup(new NamedComponentFilter(to)).moveToCenter(500).release(ComponentAdapter.MouseButton.LEFT, 500);
    }

    protected ComponentAdapter moveToAnchor(InteractiveUI interactiveUI, String componentName, ToolWindowAnchor anchor) {
        return drag(interactiveUI, componentName, "toolWindowManager.bar." + anchor.toString());
    }

}
