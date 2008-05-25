package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.DesktopContentUI;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

/**
 * @TODO: introduce common super class with Tabbed
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyDesktopContentUI extends PropertyChangeEventSource implements DesktopContentUI {

    protected ContentManager contentManager;
    protected MyDoggyContentManagerUI contentManagerUI;

    protected JInternalFrame internalFrame;

    protected Content content;
    protected boolean closable;
    protected boolean detachable;
    protected boolean minimizable;
    protected boolean maximizable;
    protected boolean transparentMode;
    protected float transparentRatio;
    protected int transparentDelay;
    protected Rectangle detachedBounds;
    protected boolean addToTaskBar;


    public MyDoggyDesktopContentUI(ContentManager contentManager,
                                   MyDoggyContentManagerUI contentManagerUI,
                                   Content content) {
        this.contentManager = contentManager;
        this.contentManagerUI = contentManagerUI;
        this.content = content;
        this.closable = contentManagerUI.isCloseable();
        this.detachable = contentManagerUI.isDetachable();
        this.minimizable = contentManagerUI.isMinimizable();
        this.transparentMode = true;
        this.transparentRatio = 0.7f;
        this.transparentDelay = 0;
        this.addToTaskBar = false;


        initInternalFrame();
    }


    public Content getContent() {
        return content;
    }

    public boolean isCloseable() {
        return closable;
    }

    public void setCloseable(boolean closable) {
        if (this.closable == closable)
            return;

        boolean old = this.closable;
        this.closable = closable;
        internalFrame.setClosable(closable);

        firePropertyChangeEvent("closable", old, closable);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        if (this.detachable == detachable)
            return;

        boolean old = this.detachable;
        this.detachable = detachable;

        firePropertyChangeEvent("detachable", old, detachable);
    }

    public boolean isMinimizable() {
        return minimizable;
    }

    public void setMinimizable(boolean minimizable) {
        if (this.minimizable == minimizable)
            return;

        boolean old = this.minimizable;
        this.minimizable = minimizable;
        internalFrame.setMaximizable(minimizable);

        firePropertyChangeEvent("minimizable", old, minimizable);
    }

    public boolean isMaximizable() {
        return maximizable;
    }

    public void setMaximizable(boolean maximizable) {
        if (this.maximizable == maximizable)
            return;

        boolean old = this.maximizable;
        this.maximizable = maximizable;

        firePropertyChangeEvent("maximizable", old, maximizable);
    }
    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        if (this.transparentMode == transparentMode)
            return;

        boolean old = this.transparentMode;
        this.transparentMode = transparentMode;

        firePropertyChangeEvent("transparentMode", old, transparentMode);
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        if (this.transparentRatio == transparentRatio)
            return;

        float old = this.transparentRatio;
        this.transparentRatio = transparentRatio;

        firePropertyChangeEvent("transparentRatio", old, transparentRatio);
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public void setTransparentDelay(int transparentDelay) {
        if (this.transparentDelay == transparentDelay)
            return;

        int old = this.transparentDelay;
        this.transparentDelay = transparentDelay;

        firePropertyChangeEvent("transparentDelay", old, transparentDelay);
    }

    public void setConstraints(Object... constraints) {
        if (constraints.length > 0) {
            if (constraints[0] instanceof Point) {
                internalFrame.setLocation((Point) constraints[0]);
            } else if (constraints[0] instanceof Rectangle) {
                internalFrame.setBounds((Rectangle) constraints[0]);
            }
        }
    }

    public Rectangle getDetachedBounds() {
        return detachedBounds;
    }

    public void setDetachedBounds(Rectangle detachedBounds) {
        if ((this.detachedBounds != null && this.detachedBounds.equals(detachedBounds)) || detachedBounds == null)
            return;

        this.detachedBounds = detachedBounds;
        firePropertyChangeEvent("detachedBounds", null, detachedBounds);
    }

    public void setAddToTaskBarWhenDetached(boolean addToTaskBar) {
        if (this.addToTaskBar == addToTaskBar)
            return;

        boolean old = this.addToTaskBar;
        this.addToTaskBar = addToTaskBar;

        firePropertyChangeEvent("addToTaskBar", old, addToTaskBar);
    }

    public boolean isAddToTaskBarWhenDetached() {
        return addToTaskBar;
    }

    public void setLocation(int x, int y) {
        Point old = internalFrame.getLocation();
        Point location = new Point(x, y);

        if (old.equals(location))
            return;

        internalFrame.setLocation(location);

        firePropertyChangeEvent("location", old, location);
    }

    public Point getLocation() {
        return internalFrame.getLocation();
    }

    public void setSize(int width, int height) {
        Dimension old = internalFrame.getSize();
        Dimension size = new Dimension(width, height);

        if (old.equals(size))
            return;

        internalFrame.setSize(size);

        firePropertyChangeEvent("size", old, size);
    }

    public Dimension getSize() {
        return internalFrame.getSize();
    }

    public boolean isIconified() {
        return internalFrame.isIcon();
    }

    public void setIconified(boolean iconified) {
        boolean old = internalFrame.isIcon();
        if (old == iconified)
            return;

        try {
            internalFrame.setIcon(iconified);
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }

        firePropertyChangeEvent("iconified", old, iconified);
    }

    public void cleanup() {
        super.cleanup();

        content = null;
        contentManager = null;
        contentManagerUI = null;
    }


    public JInternalFrame getInternalFrame() {
        return internalFrame;
    }


    protected void initInternalFrame() {
        internalFrame = new JInternalFrame(content.getTitle(), true, true, true, true);
        internalFrame.setFrameIcon(content.getIcon());
        internalFrame.getContentPane().add(content.getComponent());
        internalFrame.setClosable(closable);
        internalFrame.setMaximizable(true);

        initInternalFrameListeners();
    }

    protected void initInternalFrameListeners() {
        internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosed(InternalFrameEvent e) {
                contentManager.removeContent(content);
            }
        });

        internalFrame.addVetoableChangeListener(new VetoableChangeListener() {
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                if (JInternalFrame.IS_CLOSED_PROPERTY.equals(evt.getPropertyName())) {
                    if (Boolean.TRUE.equals(evt.getNewValue())) {
                        if (!contentManagerUI.fireContentUIRemoving(content.getContentUI()))
                            throw new PropertyVetoException("Cannot remove.", evt);
                    }
                }
            }
        });

        internalFrame.addPropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (!contentManagerUI.valueAdjusting && !contentManagerUI.contentValueAdjusting) {
                    ContentUI contentUI = content.getContentUI();
                    if (contentUI != null) {
                        Content content = contentUI.getContent();
                        if (content == null)
                            return;

                        boolean value = (Boolean) evt.getNewValue();
                        if (value) {
                            if (contentManagerUI.lastSelected != null) {
                                if (contentManagerUI.lastSelected.isDetached())
                                    contentManagerUI.lastSelected.setSelected(false);
                            }
                            content.setSelected(true);
                            contentManagerUI.lastSelected = content;
                        } else
                            content.setSelected(false);
                    }
                }
            }
        });

    }

}