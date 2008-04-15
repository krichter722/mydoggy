package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeEventSource;
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

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class MyDoggyContentManagerUI extends PropertyChangeEventSource {
    protected ContentManagerUI contentManagerUI;
    protected MyDoggyToolWindowManager toolWindowManager;
    protected ContentManager contentManager;
    protected ResourceManager resourceManager;

    protected boolean closeable, detachable, minimizable;
    protected boolean installed;
    protected boolean uninstalling;

    protected PropertyChangeSupport internalPropertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;
    protected PropertyChangeListener contentUIListener;
    
    protected Content maximizedContent;
    protected Content lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;


    public MyDoggyContentManagerUI() {
        contentManagerUIListeners = new EventListenerList();
        this.closeable = this.detachable = this.minimizable = true;
    }


    public boolean isCloseable() {
        return closeable;
    }

    public boolean isDetachable() {
        return detachable;
    }

    public boolean isMinimizable() {
        return minimizable;
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

    public void setContentManagerUI(ContentManagerUI contentManagerUI) {
        this.contentManagerUI = contentManagerUI;
    }

    public Content getMaximizedContent() {
        for (Content content : contentManager.getContents()) {
            if (content.isMaximized())
                return content;
        }
        return null;
    }


    protected boolean isContentManagerEnabled() {
        return contentManager.isEnabled();
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

    
    protected class ContentDialogFocusListener implements WindowFocusListener {
        protected Content content;

        public ContentDialogFocusListener(Content content) {
            this.content = content;
        }

        public void windowGainedFocus(WindowEvent e) {
            if (!valueAdjusting && !contentValueAdjusting) {
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
    
    protected class ContentUIListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ContentUI contentUI = (ContentUI) evt.getSource();

            if (contentUI.getContent().isDetached()) {
                if ("detachedBounds".equals(evt.getPropertyName())) {
                    Window window = SwingUtilities.windowForComponent(contentUI.getContent().getComponent());
                    window.setBounds((Rectangle) evt.getNewValue());
                } else if ("addToTaskBar".equals(evt.getPropertyName())) {
                    Content content = contentUI.getContent();
                    Window oldWindow = SwingUtilities.windowForComponent(contentUI.getContent().getComponent());
                    Frame parentFrame = (toolWindowManager.getWindowAnchestor() instanceof Frame) ? (Frame) toolWindowManager.getWindowAnchestor() : null;
                    Component focusOwner = oldWindow.getFocusOwner();

                    // Init new window
                    Window dialog;
                    if ((Boolean) evt.getNewValue()) {
                        dialog = new ContentFrame(resourceManager,
                                                  content, contentUI,
                                                  parentFrame, oldWindow.getBounds());
                    } else {
                        dialog = new ContentDialog(resourceManager,
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
}