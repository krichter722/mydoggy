package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.persistence.InternalPersistenceDelegateFilterAdapter;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentDialog;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentFrame;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class MyDoggyContentManagerUI<T extends ContentUI> extends PropertyChangeEventSource implements PropertyChangeListener {
    protected ContentManagerUI contentManagerUI;
    protected MyDoggyToolWindowManager toolWindowManager;
    protected ContentManager contentManager;
    protected ResourceManager resourceManager;
    protected Map<Content, T> contentUIMap;

    protected boolean closeable, detachable, minimizable, maximizable;
    protected boolean installed;
    protected boolean uninstalling;
    protected boolean popupMenuEnabled;

    protected PropertyChangeSupport internalPropertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;
    protected PropertyChangeListener contentUIListener;

    protected Content maximizedContent;
    protected Content previousLastSelected;
    protected Content lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;


    public MyDoggyContentManagerUI() {
        contentManagerUIListeners = new EventListenerList();
        this.closeable = this.detachable = this.minimizable = this.maximizable = true;
        this.popupMenuEnabled = true;
        this.contentUIMap = new Hashtable<Content, T>();
        this.previousLastSelected = null;
    }


    public void setCloseable(boolean closeable) {
        boolean old = this.closeable;
        this.closeable = closeable;

        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setCloseable(closeable);
        }

        fireContentManagerUIProperty("closeable", old, closeable);
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void setDetachable(boolean detachable) {
        boolean old = this.detachable;
        this.detachable = detachable;

        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setDetachable(detachable);
        }

        fireContentManagerUIProperty("detachable", old, detachable);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setMinimizable(boolean minimizable) {
        boolean old = this.minimizable;
        this.minimizable = minimizable;

        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setMinimizable(minimizable);
        }

        fireContentManagerUIProperty("minimizable", old, minimizable);
    }

    public boolean isMinimizable() {
        return minimizable;
    }

    public void setMaximizable(boolean maximizable) {
        boolean old = this.maximizable;
        this.maximizable = maximizable;

        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setMaximizable(maximizable);
        }

        fireContentManagerUIProperty("maximizable", old, maximizable);
    }

    public boolean isMaximizable() {
        return maximizable;
    }

    public boolean isPopupMenuEnabled() {
        return popupMenuEnabled;
    }

    public void setPopupMenuEnabled(boolean popupMenuEnabled) {
        if (this.popupMenuEnabled == popupMenuEnabled)
            return;

        boolean old = this.popupMenuEnabled;
        this.popupMenuEnabled = popupMenuEnabled;

        fireContentManagerUIProperty("popupMenuEnabled", old, popupMenuEnabled);
    }

    public T getContentUI(Content content) {
        return contentUIMap.get(content);
    }

    public void addContentManagerUIListener(ContentManagerUIListener listener) {
        contentManagerUIListeners.add(ContentManagerUIListener.class, listener);
    }

    public void removeContentManagerUIListener(ContentManagerUIListener listener) {
        contentManagerUIListeners.remove(ContentManagerUIListener.class, listener);
    }

    public ContentManagerUIListener[] getContentManagerUiListener() {
        return contentManagerUIListeners.getListeners(ContentManagerUIListener.class);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        internalPropertyChangeSupport.firePropertyChange(evt);
    }


    public boolean isInstalled() {
        return installed;
    }

    public void addContent(PlafContent content, Object... constraints) {
        if (maximizedContent != null) {
            maximizedContent.setMaximized(false);
            maximizedContent = null;
        }

        // Add the content to the ui...
        addUIForContent(content, constraints);

        // Register a plaf listener
        content.addPlafPropertyChangeListener(this);
    }

    public void removeContent(PlafContent content) {
        try {
            if (content.isDetached()) {
                propertyChange(new PropertyChangeEvent(content, "detached.dispose", true, false));
            } else if (content.isMinimized()) {
                toolWindowManager.getDockableDescriptor(content.getId()).setAvailable(false);
            } else {
                removeUIForContent(content);
            }
        } finally {
            // Remove listeners
            content.getContentUI().removePropertyChangeListener(contentUIListener);
            content.removePlafPropertyChangeListener(this);

            // Clean desccriptor for minimization
            toolWindowManager.removeDockableDescriptor(content.getId());

            // Remove the contentUI part
            contentUIMap.remove(content);
            if (lastSelected == content)
                lastSelected = null;
        }
    }

    public boolean isSelected(Content content) {
        return content == lastSelected;
    }


    protected abstract Object addUIForContent(Content content, Object... constraints);

    protected abstract void removeUIForContent(Content content);


    protected void setContentManagerUI(ContentManagerUI contentManagerUI) {
        this.contentManagerUI = contentManagerUI;
    }

    protected Content getMaximizedContent() {
        for (Content content : contentManager.getContents()) {
            if (content.isMaximized())
                return content;
        }
        return null;
    }

    protected boolean isContentManagerEnabled() {
        return contentManager.isEnabled();
    }

    protected boolean isFocusAncestor(Component c) {
        return SwingUtil.isAncestor(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), c);
    }

    protected Component findAndRequestFocus(Component component) {
        Container container;
        if (component instanceof JDialog) {
            container = ((JDialog) component).getContentPane();
        } else if (component instanceof Container)
            container = (Container) component;
        else
            return null;

        Component focusRequester = SwingUtil.findFocusable(container);
        if (focusRequester == null) {
            focusRequester = container;
        }
        SwingUtil.requestFocus(focusRequester);
        return focusRequester;
    }

    protected boolean fireContentUIRemoving(ContentUI contentUI) {
        ContentManagerUIEvent event = new ContentManagerUIEvent(contentManagerUI, ContentManagerUIEvent.ActionId.CONTENTUI_REMOVING, contentUI);

        for (ContentManagerUIListener listener : contentManagerUIListeners.getListeners(ContentManagerUIListener.class)) {
            if (!listener.contentUIRemoving(event))
                return false;
        }
        return true;
    }

    protected void fireContentUIDetached(ContentUI contentUI) {
        ContentManagerUIEvent event = new ContentManagerUIEvent(contentManagerUI, ContentManagerUIEvent.ActionId.CONTENTUI_DETACHED, contentUI);
        for (ContentManagerUIListener listener : contentManagerUIListeners.getListeners(ContentManagerUIListener.class)) {
            listener.contentUIDetached(event);
        }
    }

    protected void fireContentManagerUIProperty(String property, Object oldValue, Object newValue) {
        firePropertyChangeEvent(new PropertyChangeEvent(contentManagerUI, property, oldValue, newValue));
    }


    public class ContentDialogFocusListener implements WindowFocusListener {
        protected Content content;

        public ContentDialogFocusListener(Content content) {
            this.content = content;
        }

        public void windowGainedFocus(WindowEvent e) {
            if (!valueAdjusting && !contentValueAdjusting /*&& e.getComponent().isValid()*/) {
                System.out.println(e.getComponent());
                Content newSelected = content;

                if (newSelected == lastSelected)
                    return;

                if (lastSelected != null) {
                    try {
                        lastSelected.setSelected(false);
                    } catch (Exception ignoreIt) {
                    }
                }

                newSelected.setSelected(true);
                lastSelected = newSelected;
            }
        }

        public void windowLostFocus(WindowEvent e) {
        }
    }

    public class ContentUIListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ContentUI contentUI = (ContentUI) evt.getSource();

            if (contentUI.getContent().isDetached()) {
                if ("detachedBounds".equals(evt.getPropertyName())) {
                    Window window = SwingUtilities.windowForComponent(contentUI.getContent().getComponent());
                    window.setBounds((Rectangle) evt.getNewValue());
                } else if ("addToTaskBar".equals(evt.getPropertyName())) {
                    Content content = contentUI.getContent();
                    Window oldWindow = SwingUtilities.windowForComponent(contentUI.getContent().getComponent());
                    Frame parentFrame = (toolWindowManager.getWindowAncestor() instanceof Frame) ? (Frame) toolWindowManager.getWindowAncestor() : null;
                    Component focusOwner = oldWindow.getFocusOwner();

                    // Init new window
                    Window dialog;
                    if ((Boolean) evt.getNewValue()) {
                        dialog = new ContentFrame(
                                content, contentUI,
                                parentFrame, oldWindow.getBounds());
                    } else {
                        dialog = new ContentDialog(
                                content, contentUI,
                                parentFrame, oldWindow.getBounds());
                    }

                    dialog.setBounds(oldWindow.getBounds());
                    dialog.addWindowFocusListener(new ContentDialogFocusListener(content));
                    dialog.toFront();

                    // Dispose old
                    oldWindow.setVisible(false);
                    oldWindow.dispose();

                    // Show new
                    dialog.setVisible(true);

                    if (focusOwner != null)
                        SwingUtil.requestFocus(focusOwner);
                }
            }
        }
    }

    public class ContentManagerUIPersistenceDelegateFilter extends InternalPersistenceDelegateFilterAdapter {
        public boolean saveSelectedContent() {
            return false;
        }
    }
}
