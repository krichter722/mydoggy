package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.ToolWindowTab;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowTab implements ToolWindowTab {
    private String title;
    private Icon icon;
    private Component component;
    private boolean selected;

    public MyDoggyToolWindowTab(String title, Icon icon, Component component) {
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.selected = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
