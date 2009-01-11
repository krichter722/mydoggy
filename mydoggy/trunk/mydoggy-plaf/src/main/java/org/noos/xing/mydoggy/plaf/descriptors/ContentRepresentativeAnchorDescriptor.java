package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.RepresentativeAnchorDescriptor;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentRepresentativeAnchorDescriptor extends PropertyChangeEventSource implements RepresentativeAnchorDescriptor<Content> {

    protected Content content;

    protected boolean previewEnabled;
    protected int previewDelay;
    protected float previewTransparentRatio;

    protected Set<ToolWindowAnchor> lockingAnchors;

    protected String title;
    protected boolean visible;


    public ContentRepresentativeAnchorDescriptor(Content content) {
        this.content = content;
        this.previewEnabled = true;
        this.previewDelay = 1000;
        this.previewTransparentRatio = 0.65f;
        this.lockingAnchors = new HashSet<ToolWindowAnchor>();
        this.visible = true;
        this.title = content.getId();
    }

    public Content getDockable() {
        return content;
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

    public void showMessage(String message) {
        firePropertyChangeEvent("showMessage", null, message);
    }

    public void setVisible(boolean visible) {
    }

    public boolean isVisible() {
        return content.isMinimized();
    }

    public void setTitle(String title) {
        if (title == null)
            title = content.getId();

        if (title != null && title.equals(this.title))
            return;

        String old = this.title;
        this.title = title;

        firePropertyChangeEvent("title", old, title);
    }

    public String getTitle() {
        return title;
    }

}