package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ContentManagerListener;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.plaf.ui.content.ContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.content.tabbed.TabbedContentManagerUI;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyContentManager implements ContentManager {
    private static final Content[] EMPTY_CONTENT_ARRAY = new Content[0];

    private MyDoggyToolWindowManager toolWindowManager;

    private List<Content> contents;
    private Map<Object, Content> contentMap;
    private ContentManagerUI contentManagerUI;

    private EventListenerList listeners;

    MyDoggyContentManager(MyDoggyToolWindowManager windowManager) {
        this.toolWindowManager = windowManager;
        this.contents = new ArrayList<Content>();
        this.contentMap = new Hashtable<Object, Content>();
        this.listeners = new EventListenerList();
        this.contentManagerUI = new TabbedContentManagerUI(this);
    }

    public int getContentCount() {
        return contents.size();
    }

    public Content addContent(Object key, String title, Icon icon, Component component, String tip) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null.");
        if (component == null)
            throw new IllegalArgumentException("Component cannot be null.");

        MyDoggyContent content = new MyDoggyContent(this, key, title, icon, component, tip);
        contents.add(content);
        contentMap.put(key, content);
        contentManagerUI.addContent(content);

        fireContentAdded(content);

        return content;
    }

    public Content addContent(Object key, String title, Icon icon, Component component) {
        return addContent(key, title, icon, component, null);
    }

    public boolean removeContent(Content content) {
        if (content == null)
            throw new IllegalArgumentException("Content cannot be null");
        
        contentManagerUI.removeContent((MyDoggyContent) content);
        boolean result = contents.remove(content);

        if (result) {
            contentMap.remove(content.getKey());
            fireContentRemoved(content);
        }

        return result;
    }

    public boolean removeContent(int index) {
        Content content = contents.get(index);
        return removeContent(content);
    }

    public Content getContent(int index) {
        return contents.get(index);
    }

    public Content getContent(Object key) {
        return contentMap.get(key);
    }

    public Content[] getContents() {
        return contents.toArray(EMPTY_CONTENT_ARRAY);
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        contentManagerUI.setPopupMenu(popupMenu);
    }

    public JPopupMenu getPopupMenu() {
        return contentManagerUI.getPopupMenu();
    }

    public void addContentManagerListener(ContentManagerListener listener) {
        listeners.add(ContentManagerListener.class, listener);
    }

    public void removeContentManagerListener(ContentManagerListener listener) {
        listeners.remove(ContentManagerListener.class, listener);
    }

    public ContentManagerListener[] getContentManagerListeners() {
        return listeners.getListeners(ContentManagerListener.class);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(PropertyChangeListener.class, listener);
    }


    public void removeContent(Component component) {
        removeContent(getContent(component));
    }

    public Content getContent(Component component) {
        for (Content content : contents) {
            if (content.getComponent() == component)
                return content;
        }
        throw new IllegalArgumentException("Cannot found content for component. [cmp : " + component + ']'); 
    }

    public MyDoggyToolWindowManager getToolWindowManager() {
        return toolWindowManager;
    }

    public void updateUI() {
        for (Content content : contents) {
            SwingUtilities.updateComponentTreeUI(content.getComponent());
            if (content.getPopupMenu() != null)
                SwingUtilities.updateComponentTreeUI(content.getPopupMenu());
        }

        if (getPopupMenu() != null)
            SwingUtilities.updateComponentTreeUI(getPopupMenu());

        contentManagerUI.updateUI();
    }

    public ContentManagerUI getContentManagerUI() {
        return contentManagerUI;
    }

    
    protected void firePropertyChange(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);

        for (PropertyChangeListener listener : listeners.getListeners(PropertyChangeListener.class)) {
            listener.propertyChange(event);
        }
    }

    protected void fireContentAdded(Content content) {
        ContentManagerEvent event = new ContentManagerEvent(this, ContentManagerEvent.ActionId.CONTENT_ADDED, content);
        for (ContentManagerListener listener : listeners.getListeners(ContentManagerListener.class)) {
            listener.contentAdded(event);
        }
    }

    protected void fireContentRemoved(Content content) {
        ContentManagerEvent event = new ContentManagerEvent(this, ContentManagerEvent.ActionId.CONTENT_REMOVED, content);
        for (ContentManagerListener listener : listeners.getListeners(ContentManagerListener.class)) {
            listener.contentRemoved(event);
        }
    }
}
