package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ToolWindowRepresentativeAnchor extends JLabel {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowRepresentativeAnchorUI";


    public ToolWindowRepresentativeAnchor(ToolWindowDescriptor toolWindowDescriptor, Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        putClientProperty(ToolWindowDescriptor.class, toolWindowDescriptor);
        updateUI();
    }

    public ToolWindowRepresentativeAnchor(ToolWindowDescriptor toolWindowDescriptor, String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        putClientProperty(ToolWindowDescriptor.class, toolWindowDescriptor);
        updateUI();
    }


    public void updateUI() {
        if (getClientProperty(ToolWindowDescriptor.class) != null)
            super.updateUI();
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }


}
