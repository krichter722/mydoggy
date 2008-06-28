package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGesture;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureInitiator;
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


    public ToolWindowTabPanel(ToolWindowDescriptor descriptor, DockedContainer dockedContainer) {
        putClientProperty(ToolWindowDescriptor.class, descriptor);
        putClientProperty(DockedContainer.class, dockedContainer);
        descriptor.getCleaner().addCleaner(this);
        updateUI();
    }


    public void cleanup() {
        putClientProperty(ToolWindowDescriptor.class, null);
    }

    public void updateUI() {
        if (getClientProperty(ToolWindowDescriptor.class) != null)
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


    public void setDragGesture(DragGesture dragGesture) {
        getUI().setDragGesture(dragGesture);
    }

    public DragGesture getDragGesture() {
        return getUI().getDragGesture();
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
