package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;
import org.noos.xing.mydoggy.plaf.ui.look.CornerPanelUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * TODO: create ui...
 */
public class CornerPanel extends JPanel {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "CornerPanelUI";


    public CornerPanel(ToolWindowManagerDescriptor.Corner corner) {
        putClientProperty(ToolWindowManagerDescriptor.Corner.class, corner);

        setLayout(new TableLayout(new double[][]{{-1}, {-1}}));
        setFocusable(false);
        updateUI();
    }


    public void updateUI() {
        if (getClientProperty(ToolWindowManagerDescriptor.Corner.class) != null)
            setUI((CornerPanelUI) UIManager.getUI(this));
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }


    public void setUI(CornerPanelUI ui) {
        super.setUI(ui);
    }

    public Component getComponent() {
        return (getComponentCount() == 0) ? null : getComponent(0);
    }

    public void setComponent(Component component) {
        removeAll();
        add(component, "0,0,FULL,FULL");
    }

    public void resetComponent() {
        removeAll();
    }

}
