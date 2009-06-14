package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.RepresentativeAnchorDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowRepresentativeAnchorDescriptor extends PropertyChangeEventSource implements RepresentativeAnchorDescriptor<ToolWindow> {

    protected ToolWindowDescriptor toolWindowDescriptor;
    protected ToolWindow toolWindow;

    protected boolean previewEnabled;
    protected int previewDelay;
    protected float previewTransparentRatio;

    protected Set<ToolWindowAnchor> lockingAnchors;

    protected String title;
    protected Icon icon;
    protected boolean visible;


    public ToolWindowRepresentativeAnchorDescriptor(ToolWindowDescriptor toolWindowDescriptor) {
        super(toolWindowDescriptor.getManager().getFirePublicEvent());

        this.toolWindowDescriptor = toolWindowDescriptor;
        this.toolWindow = toolWindowDescriptor.getToolWindow();
        this.previewEnabled = true;
        this.previewDelay = 1000;
        this.previewTransparentRatio = 0.65f;
        this.lockingAnchors = new HashSet<ToolWindowAnchor>();
        this.visible = true;
        this.title = toolWindow.getId();
        this.icon = toolWindow.getIcon();

        toolWindowDescriptor.getCleaner().addCleaner(this);
    }


    public ToolWindow getDockable() {
        return toolWindow;
    }

    public ToolWindowAnchor getAnchor() {
        return toolWindow.getAnchor();
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        if (this.previewEnabled == previewEnabled)
            return;

        boolean old = this.previewEnabled;
        this.previewEnabled = previewEnabled;
        firePropertyChangeEvent("previewEnabled", old, previewEnabled);
    }

    public int getPreviewDelay() {
        return previewDelay;
    }

    public void setPreviewDelay(int previewDelay) {
        if (this.previewDelay == previewDelay)
            return;

        int old = this.previewDelay;
        this.previewDelay = previewDelay;
        firePropertyChangeEvent("previewDelay", old, previewDelay);
    }

    public float getPreviewTransparentRatio() {
        return previewTransparentRatio;
    }

    public void setPreviewTransparentRatio(float previewTransparentRatio) {
        if (this.previewTransparentRatio == previewTransparentRatio)
            return;

        float old = this.previewTransparentRatio;
        this.previewTransparentRatio = previewTransparentRatio;
        firePropertyChangeEvent("previewTransparentRatio", old, previewTransparentRatio);
    }

    public void addLockingAnchor(ToolWindowAnchor anchor) {
        lockingAnchors.add(anchor);
    }

    public void removeLockingAnchor(ToolWindowAnchor anchor) {
        lockingAnchors.remove(anchor);
    }

    public void removeAllLockingAnchor() {
        lockingAnchors.clear();
    }

    public ToolWindowAnchor[] getLockingAnchors() {
        return lockingAnchors.toArray(new ToolWindowAnchor[lockingAnchors.size()]);
    }

    public boolean containsLockingAnchor(ToolWindowAnchor anchor) {
        return lockingAnchors.contains(anchor);
    }

    public void ensureVisible() {
        toolWindowDescriptor.getToolBar().ensureVisible(toolWindowDescriptor.getRepresentativeAnchor());
    }

    public void showMessage(Icon icon, String message) {
        ensureRepresentativeAnchor();
        ensureVisible();

        firePropertyChangeEvent("showMessage", null, new Object[]{icon, message});
    }

    public void setVisible(boolean visible) {
        if (toolWindow.getType() == ToolWindowType.FLOATING_FREE)
            return;

/*
        if (!toolWindow.isAvailable())
            return;
*/

        if (this.visible == visible)
            return;

        boolean old = this.visible;
        this.visible = visible;

        firePropertyChangeEvent("visible", old, visible);
    }

    public boolean isVisible() {
//        if (!toolWindow.isAvailable())
//            return visible && toolWindow.getDockableManager().getToolWindowManagerDescriptor().isShowUnavailableTools();

        return toolWindow.getType() != ToolWindowType.FLOATING_FREE && visible;
    }

    public void setTitle(String title) {
        if (title == null)
            title = toolWindow.getId();

        if (title != null && title.equals(this.title))
            return;

        String old = this.title;
        this.title = title;

        firePropertyChangeEvent("title", old, title);
    }

    public String getTitle() {
        return title;
    }

    public void setIcon(Icon icon) {
        if (icon == null)
            icon = toolWindow.getIcon();

        if (icon != null && icon.equals(this.icon))
            return;

        Icon old = this.icon;
        this.icon = icon;

        firePropertyChangeEvent("icon", old, icon);
    }

    public Icon getIcon() {
        return icon;
    }


    @Override
    public void cleanup() {
        super.cleanup();

        this.toolWindow = null;
        this.toolWindowDescriptor = null;
    }


    protected void ensureRepresentativeAnchor() {
        if (toolWindowDescriptor.getRepresentativeAnchor() == null)
            setVisible(true);
    }

}
