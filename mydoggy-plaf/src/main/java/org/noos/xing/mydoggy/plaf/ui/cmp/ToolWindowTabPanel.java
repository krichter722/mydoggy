package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureInitiator;
import org.noos.xing.mydoggy.plaf.ui.drag.DragListener;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTabPanelUI;
import org.noos.xing.mydoggy.plaf.ui.util.MouseEventDispatcher;

import javax.swing.*;
import java.awt.*;
import java.util.EventListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabPanel extends JPanel implements Cleaner, DragGestureInitiator {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowTabPanelUI";


    protected ToolWindowDescriptor toolWindowDescriptor;


    public ToolWindowTabPanel(ToolWindowDescriptor descriptor) {
        this.toolWindowDescriptor = descriptor;
        descriptor.getCleaner().addCleaner(this);
        updateUI();
    }


    public void cleanup() {
        toolWindowDescriptor = null;
    }

    public void updateUI() {
        if (toolWindowDescriptor != null)
            setUI((ToolWindowTabPanelUI) UIManager.getUI(this));
    }

    public void setUI(ToolWindowTabPanelUI ui) {
        super.setUI(ui);
    }

    public ToolWindowTabPanelUI getUI() {
        return (ToolWindowTabPanelUI) super.getUI();
    }

    public String getUIClassID() {
        return uiClassID;
    }


    public ToolWindowDescriptor getToolWindowDescriptor() {
        return toolWindowDescriptor;
    }

    public void setDragListener(DragListener dragListener) {
        getUI().setDragListener(dragListener);
    }

    public DragListener getDragListener() {
        return getUI().getDragListener();
    }

    public MouseEventDispatcher getMouseEventDispatcher() {
        return getUI().getMouseEventDispatcher();
    }

    public void addEventDispatcherlListener(EventListener eventListener) {
        getUI().addEventDispatcherlListener(eventListener);
    }

    public void removeEventDispatcherlListener(EventListener eventListener) {
        getUI().removeEventDispatcherlListener(eventListener);
    }

    public void ensureVisible(Rectangle bounds) {
        getUI().ensureVisible(bounds);
    }

}
