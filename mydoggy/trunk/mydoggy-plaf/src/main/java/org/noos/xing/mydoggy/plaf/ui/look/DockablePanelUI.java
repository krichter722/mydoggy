package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.DockableManagerListener;
import org.noos.xing.mydoggy.event.DockableManagerEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.DockablePanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DockablePanelUI extends BasicPanelUI implements PropertyChangeListener,
                                                             ActionListener,
                                                             DockableManagerListener {

    protected static Border FLASHING_BORDER = new LineBorder(Color.RED, 3);


    public static ComponentUI createUI(JComponent c) {
        return new DockablePanelUI();
    }


    protected DockablePanel dockablePanel;
    protected Dockable dockable;

    protected boolean flashingEnabled;
    protected Timer flashingTimer;
    protected int flashingDuration = -1;
    protected boolean flashingState;
    protected Border flashingBorder;
    protected long startingTime = 0;
    protected int flashingInterval = 500;


    public DockablePanelUI() {
    }


    public void installUI(JComponent c) {
        //  Init fields
        this.dockablePanel = (DockablePanel) c;
        this.dockable = dockablePanel.getDockable();
        this.flashingEnabled = dockable instanceof Content;

        super.installUI(c);

        installListeners();
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        uninstallListeners();

        // Reset fields
        this.dockablePanel = null;
        this.dockable = null;
    }

    protected void installDefaults(JPanel p) {
        super.installDefaults(p);
        
        LookAndFeel.installColorsAndFont(p,
                                         "DockablePanelUI.background",
                                         "DockablePanelUI.foreground",
                                         "DockablePanelUI.font");
        LookAndFeel.installBorder(p, "DockablePanelUI.border");
        
        this.flashingBorder = SwingUtil.getBorder("DockablePanelUI.border.flashing", FLASHING_BORDER);
    }

    protected void installListeners() {
        dockable.addPropertyChangeListener(this);

        dockable.getDockableManager().addDockableManagerListener(this);
    }

    protected void uninstallListeners() {
        dockable.removePropertyChangeListener(this);
        
        dockable.getDockableManager().removeDockableManagerListener(this);
    }


    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();

        if ("flash".equals(propertyName)) {
            if (!flashingEnabled)
                return;

            if (evt.getNewValue() == Boolean.TRUE) {
                if (!dockable.isSelected()) {
                    dockablePanel.putClientProperty("oldBorder", dockablePanel.getBorder());
                    flashingDuration = SwingUtil.getInt(evt, -1);
                    flashingTimer = new Timer(flashingInterval, this);
                    flashingTimer.start();
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;

                    dockablePanel.setBorder((Border) dockablePanel.getClientProperty("oldBorder"));
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
            dockablePanel.setBorder(flashingBorder);
        } else {
            dockablePanel.setBorder((Border) dockablePanel.getClientProperty("oldBorder"));
        }

        if (flashingDuration != -1 && System.currentTimeMillis() - startingTime > flashingDuration)
            dockable.setFlashing(false);
    }

    public void dockableAdded(DockableManagerEvent event) {
    }

    public void dockableRemoved(DockableManagerEvent event) {
        if (event.getDockable() == dockable) {
            if (flashingTimer != null)
                flashingTimer.stop();

            uninstallUI(dockablePanel);
        }
    }
}
