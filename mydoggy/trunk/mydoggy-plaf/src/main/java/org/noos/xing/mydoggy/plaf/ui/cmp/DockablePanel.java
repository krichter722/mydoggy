package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.DockableManagerListener;
import org.noos.xing.mydoggy.event.DockableManagerEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * TODO: introduce UI class
 */
public class DockablePanel extends JPanel implements PropertyChangeListener,
                                                     ActionListener,
                                                     DockableManagerListener {

    protected static Border flashingBorder = new LineBorder(Color.RED, 3);

    protected Dockable dockable;

    protected boolean flashingEnabled;
    protected Timer flashingTimer;
    protected int flashingDuration = -1;
    protected boolean flashingState;
    protected long startingTime = 0;

    
    public DockablePanel(Dockable dockable, Component component) {
        this.dockable = dockable;
        this.flashingEnabled = dockable instanceof Content;

        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        setFocusable(false);
        setFocusCycleRoot(true);
        add(component, "0,0,FULL,FULL");

        dockable.addPropertyChangeListener(this);
        dockable.getDockableManager().addDockableManagerListener(this);
    }


    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();

        if ("flash".equals(propertyName)) {
            if (!flashingEnabled)
                return;

            if (evt.getNewValue() == Boolean.TRUE) {
                if (!dockable.isSelected()) {
                    putClientProperty("oldBorder", getBorder());
                    flashingDuration = -1;
                    flashingTimer = new Timer(600, this);
                    flashingTimer.start();
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    setBorder((Border) getClientProperty("oldBorder"));
                }
            }
        } else if ("flash.duration".equals(propertyName)) {
            if (!flashingEnabled)
                return;

            if (evt.getNewValue() == Boolean.TRUE) {
                if (!dockable.isSelected()) {
                    putClientProperty("oldBorder", getBorder());
                    flashingDuration = (Integer) evt.getNewValue();
                    flashingTimer = new Timer(600, this);
                    flashingTimer.start();
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    setBorder((Border) getClientProperty("oldBorder"));
                }
            }
        } else if ("selected".equals(propertyName)) {
            if (evt.getNewValue() == Boolean.TRUE)
                dockable.setFlashing(false);            
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (startingTime == 0)
            startingTime = System.currentTimeMillis();

        flashingState = !flashingState;

        if (flashingState) {
            setBorder(flashingBorder);
        } else {
            setBorder((Border) getClientProperty("oldBorder"));
        }

        if (flashingDuration != -1 && System.currentTimeMillis() - startingTime > flashingDuration)
            dockable.setFlashing(false);
    }

    public void removeNotify() {
        super.removeNotify();
        
        dockable.removePropertyChangeListener(this);
    }

    public void dockableAdded(DockableManagerEvent event) {
    }

    public void dockableRemoved(DockableManagerEvent event) {
        if (event.getDockable() == dockable) {
            dockable.getDockableManager().removeDockableManagerListener(this);
            dockable.removePropertyChangeListener(this);
            dockable = null;

            if (flashingTimer != null)
                flashingTimer.stop();
            flashingTimer = null;
        }
    }

    public Dockable getDockable() {
        return dockable;
    }

    public Component getComponent() {
        return getComponent(0);
    }

    public void setComponent(Component component) {
        removeAll();
        add(component, "0,0,FULL,FULL");
    }

}
