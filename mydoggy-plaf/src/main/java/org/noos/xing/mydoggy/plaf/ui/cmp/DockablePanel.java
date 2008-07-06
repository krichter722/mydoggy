package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.plaf.ui.look.DockablePanelUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DockablePanel extends JPanel {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "DockablePanelUI";


    protected Dockable dockable;


    public DockablePanel(Dockable dockable, Component component) {
        this.dockable = dockable;

        setFocusable(false);
        setFocusCycleRoot(true);

        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        add(component, "0,0,FULL,FULL");
    }


    public void updateUI() {
        if (dockable != null)
            setUI((DockablePanelUI) UIManager.getUI(this));
    }

    public String getUIClassID() {
        return uiClassID;
    }

    public void setUI(DockablePanelUI ui) {
        super.setUI(ui);
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
