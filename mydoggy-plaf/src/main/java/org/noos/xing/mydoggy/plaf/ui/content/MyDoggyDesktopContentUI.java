package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyDesktopContentUI extends MyDoggyContentUI implements DesktopContentUI {

    protected JInternalFrame internalFrame;
    protected MyDoggyContentManagerUI myDoggyContentManagerUI;


    public MyDoggyDesktopContentUI(ContentManager contentManager,
                                   ContentManagerUI contentManagerUI,
                                   Content content) {
        super(contentManager, contentManagerUI, content);
        this.myDoggyContentManagerUI = (MyDoggyContentManagerUI) contentManagerUI;

        initInternalFrame();
    }


    public void setMinimizable(boolean minimizable) {
        if (this.minimizable == minimizable)
            return;

        boolean old = this.minimizable;
        this.minimizable = minimizable;
        internalFrame.setMaximizable(minimizable);

        firePropertyChangeEvent("minimizable", old, minimizable);
    }

    public void setCloseable(boolean closable) {
        if (this.closable == closable)
            return;

        boolean old = this.closable;
        this.closable = closable;
        internalFrame.setClosable(closable);

        firePropertyChangeEvent("closable", old, closable);
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

        uninstallInternalFrameListeners();
        myDoggyContentManagerUI = null;
        internalFrame = null;
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

        installInternalFrameListeners();
    }


    protected InternalFrameAdapter internalFrameAdapter;
    protected VetoableChangeListener vetoableChangeListener;
    protected PropertyChangeListener propertyChangeListener;

    protected void installInternalFrameListeners() {
        internalFrame.addInternalFrameListener(internalFrameAdapter = new InternalFrameAdapter() {
            public void internalFrameClosed(InternalFrameEvent e) {
                contentManager.removeContent(content);
            }
        });
        internalFrame.addVetoableChangeListener(vetoableChangeListener = new VetoableChangeListener() {
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                if (JInternalFrame.IS_CLOSED_PROPERTY.equals(evt.getPropertyName())) {
                    if (Boolean.TRUE.equals(evt.getNewValue())) {
                        if (!myDoggyContentManagerUI.fireContentUIRemoving(content.getContentUI()))
                            throw new PropertyVetoException("Cannot remove.", evt);
                    }
                }
            }
        });
        internalFrame.addPropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (!myDoggyContentManagerUI.valueAdjusting && !myDoggyContentManagerUI.contentValueAdjusting) {
                    ContentUI contentUI = content.getContentUI();
                    if (contentUI != null) {
                        Content content = contentUI.getContent();
                        if (content == null)
                            return;

                        boolean value = (Boolean) evt.getNewValue();
                        if (value) {
                            if (myDoggyContentManagerUI.lastSelected != null) {
                                if (myDoggyContentManagerUI.lastSelected.isDetached())
                                    myDoggyContentManagerUI.lastSelected.setSelected(false);
                            }
                            content.setSelected(true);
                            myDoggyContentManagerUI.lastSelected = content;
                        } else
                            content.setSelected(false);
                    }
                }
            }
        });
    }

    protected void uninstallInternalFrameListeners() {
        internalFrame.removeInternalFrameListener(internalFrameAdapter);
        internalFrame.removeVetoableChangeListener(vetoableChangeListener);
        internalFrame.removePropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, propertyChangeListener);
    }

}