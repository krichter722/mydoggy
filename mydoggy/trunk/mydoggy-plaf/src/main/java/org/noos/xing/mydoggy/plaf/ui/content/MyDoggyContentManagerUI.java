package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;

import javax.swing.event.EventListenerList;
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

    protected boolean closeable, detachable;
    protected boolean installed;

    protected PropertyChangeSupport internalPropertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;
    protected PropertyChangeListener contentUIListener;
    
    protected Content maximizedContent;
    protected PlafContent lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;


    public MyDoggyContentManagerUI() {
        contentManagerUIListeners = new EventListenerList();
        this.closeable = this.detachable = true;
    }


    public boolean isCloseable() {
        return closeable;
    }

    public boolean isDetachable() {
        return detachable;
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
        protected PlafContent plafContent;

        public ContentDialogFocusListener(PlafContent plafContent) {
            this.plafContent = plafContent;
        }

        public void windowGainedFocus(WindowEvent e) {
            if (!valueAdjusting && !contentValueAdjusting) {
                PlafContent newSelected = plafContent;

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
    
}
