package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * TODO: add cleanup
*/
public class ToolWindowRepresentativeAnchor extends JLabel {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowRepresentativeAnchorUI";


    protected ToolWindowDescriptor toolWindowDescriptor;


    public ToolWindowRepresentativeAnchor(ToolWindowDescriptor toolWindowDescriptor, Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        this.toolWindowDescriptor = toolWindowDescriptor;

        updateUI();
    }

    public ToolWindowRepresentativeAnchor(ToolWindowDescriptor toolWindowDescriptor, String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        this.toolWindowDescriptor = toolWindowDescriptor;

        updateUI();
    }


    public void updateUI() {
        if (toolWindowDescriptor != null)
            super.updateUI();
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }


    public ToolWindowDescriptor getToolWindowDescriptor() {
        return toolWindowDescriptor;
    }
}
